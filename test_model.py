import tensorflow as tf
import numpy as np
import cv2

model_path = "model.tflite"
interpreter = tf.lite.Interpreter(model_path=model_path)
interpreter.allocate_tensors()
    
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()
    
print("Detalles de entrada:")
for detail in input_details:
    print(detail)
print("\nDetalles de salida:")
for detail in output_details:
    print(detail)
    
img_path = "D:/NewFacenet/imagenes de prueba/mujer.jpeg"
img = cv2.imread(img_path)
if img is None:
    raise ValueError("No se pudo cargar la imagen. Verifica la ruta: " + img_path)
    
img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
img = cv2.resize(img, (160, 160))
img = img.astype("float32") / 255.0
input_data = np.expand_dims(img, axis=0)

# Establecer el tensor de entrada
interpreter.set_tensor(input_details[0]['index'], input_data)

# Ejecutar la inferencia
interpreter.invoke()

embedding = interpreter.get_tensor(output_details[1]['index'])
age = interpreter.get_tensor(output_details[0]['index'])
gender = interpreter.get_tensor(output_details[2]['index'])

print("\n--- Resultados ---")
print("Embedding:", embedding)
print("Edad (normalizada):", age)
print("Género (probabilidades):", gender)

edad_estimada = age[0][0] * 100
print("Edad estimada:", edad_estimada)
    
clase_predicha = np.argmax(gender[0])
genero_predicho = "Hombre" if clase_predicha == 0 else "Mujer"
print("Género predicho:", genero_predicho)