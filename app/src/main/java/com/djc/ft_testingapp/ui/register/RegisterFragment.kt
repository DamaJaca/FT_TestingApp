package com.djc.ft_testingapp.ui.register

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.djc.ft_testingapp.R
import com.djc.ft_testingapp.databinding.FragmentFaceRecordBinding
import com.djc.ft_testingapp.databinding.FragmentRegisterBinding
import com.djc.ft_testingapp.ui.facerecord.FaceRecordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {


    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<FaceRecordViewModel>()
    private val regviewModel by activityViewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            findNavController().popBackStack(R.id.mainFragment, false)
        }
    }


    private fun initUI() {
        viewModel.face.observe(super.getViewLifecycleOwner(), {
            binding.imageFace.setImageBitmap(it)
            regviewModel.getFaceInfo(it!!)
            }
        )
        regviewModel.error.observe(super.getViewLifecycleOwner(), {
            if (!it.isNullOrEmpty()){
                errorMessage(it)
            }
        })
        regviewModel.user.observe(super.getViewLifecycleOwner(), {
            if (it!=null){
                binding.editAge.setText(it.age)
                binding.editName.setText(it.name)
                val posicion = when (it.sex){
                    "Man"-> 0
                    "Woman"-> 1
                    else-> 2
                }
                binding.spinnerSex.setSelection(posicion)
            }
        })
        binding.spinnerSex.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.sex,androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)

        binding.btnSave.setOnClickListener {
            if(binding.editAge.text.isNullOrEmpty() || binding.editName.text.isNullOrEmpty()){
                Toast.makeText(requireContext(), "Please, complete all fields", Toast.LENGTH_LONG).show()
            }
            else{

                regviewModel.saveUserInfo(binding.editName.text.toString(), binding.editAge.text.toString(), binding.spinnerSex.selectedItem.toString())
                findNavController().popBackStack(R.id.mainFragment, false)
            }
        }

    }


    fun errorMessage (message :String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }


}