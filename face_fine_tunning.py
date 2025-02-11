
import os
import shutil
import subprocess
import zipfile
import numpy as np
import cv2
from mtcnn import MTCNN
from tensorflow.keras import layers, models, optimizers
from tensorflow.keras.applications import InceptionResNetV2
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.callbacks import ModelCheckpoint, EarlyStopping
from sklearn.model_selection import train_test_split
from tensorflow.keras.utils import to_categorical



# El siguiente código comentado es para descargar el UTKFace y descomprimirlo:



# kaggle_config_dir = os.path.expanduser("~/.kaggle")
# if not os.path.exists(kaggle_config_dir):
#     os.makedirs(kaggle_config_dir)

# src_config = "kaggle.json"
# dst_config = os.path.join(kaggle_config_dir, "kaggle.json")
# if os.path.exists(src_config):
#     shutil.copy(src_config, dst_config)
#     # Cambia los permisos para que solo el usuario tenga acceso
#     os.chmod(dst_config, 0o600)
#     print("Configuración de Kaggle copiada a ~/.kaggle/")
# else:
#     print("No se encontró 'kaggle.json' en el directorio actual. Por favor, colócalo aquí.")


# dataset_identifier = "jangedoo/utkface-new"
# print("Descargando el dataset UTKFace desde Kaggle...")
# subprocess.run(["kaggle", "datasets", "download", "-d", dataset_identifier], check=True)


# zip_filename = "utkface-new.zip"
# extract_dir = "UTKFace"

# if os.path.exists(zip_filename):
#     print("Extrayendo el dataset...")
#     with zipfile.ZipFile(zip_filename, "r") as zip_ref:
#         zip_ref.extractall(extract_dir)
#     print("Extracción completada. Contenido del directorio '{}':".format(extract_dir))
#     print(os.listdir(extract_dir))
# else:
#     print("No se encontró el archivo '{}'. Verifica que la descarga se haya completado.".format(zip_filename))


def load_utkface_dataset(dataset_path):
    images = []
    ages = []
    genders = []
    image_size = 160 

    for filename in os.listdir(dataset_path):
        if filename.endswith('.jpg'):
            parts = filename.split('_')
            if len(parts) >= 4:
                age = int(parts[0])
                gender = int(parts[1])
                img_path = os.path.join(dataset_path, filename)
                img = cv2.imread(img_path)
                img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
                img = cv2.resize(img, (image_size, image_size))
                images.append(img)
                ages.append(age)
                genders.append(gender)

    images = np.array(images)
    ages = np.array(ages)
    genders = np.array(genders)
    return images, ages, genders

def preprocess_images(images):
    images = images.astype('float32') / 255.0
    return images

def preprocess_labels(ages, genders):
    ages = ages / 100.0  
    genders = to_categorical(genders, num_classes=2)
    return ages, genders

def create_model(input_shape):
    base_model = InceptionResNetV2(include_top=False, input_shape=input_shape)
    base_model.trainable = False 


    x = layers.GlobalAveragePooling2D()(base_model.output)


    embedding_output = layers.Dense(128, activation='linear', name='embedding')(x)


    age_output = layers.Dense(1, activation='sigmoid', name='age')(embedding_output)


    gender_output = layers.Dense(2, activation='softmax', name='gender')(embedding_output)


    model = models.Model(inputs=base_model.input, outputs=[embedding_output, age_output, gender_output])
    model.compile(
        optimizer=optimizers.Adam(),
        loss={
            'embedding': lambda y_true, y_pred: 0.0,
            'age': 'mean_squared_error',
            'gender': 'categorical_crossentropy'},
    loss_weights={
        'embedding': 0.0,
        'age': 1.0,
        'gender': 1.0
    },
        metrics={'age': 'mae', 'gender': 'accuracy'}
    )
    return model




# Cargar y preprocesar datos
dataset_path = './UTKFace/UTKFace'
images, ages, genders = load_utkface_dataset(dataset_path)
images = preprocess_images(images)
ages, genders = preprocess_labels(ages, genders)

# Dividir en conjuntos de entrenamiento y prueba
X_train, X_test, y_train_age, y_test_age, y_train_gender, y_test_gender = train_test_split(
    images, ages, genders, test_size=0.2, random_state=42
)
dummy_embedding_train = np.zeros((X_train.shape[0], 128))
dummy_embedding_val = np.zeros((X_test.shape[0], 128))
y_train = {
    'embedding': dummy_embedding_train,
    'age': y_train_age,
    'gender': y_train_gender
}
y_val = {
    'embedding': dummy_embedding_val,
    'age': y_test_age,
    'gender': y_test_gender
}

# Crear el modelo
import tensorflow as tf
input_shape = (160, 160, 3)
with tf.distribute.MirroredStrategy().scope():
    model = create_model(input_shape)
    history = model.fit(
    X_train,
    y_train,
    validation_data=(X_test, y_val),
    epochs=3,
    batch_size=32,
    callbacks=[
        ModelCheckpoint('model.keras', save_best_only=True),
        EarlyStopping(patience=3)
    ]
)

#Convertir a TfLite
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()
with open('model.tflite', 'wb') as f:
    f.write(tflite_model)
