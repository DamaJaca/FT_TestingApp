package com.djc.ft_testingapp.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.djc.ft_testingapp.R
import com.djc.ft_testingapp.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get () = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        val assetFiles = context?.assets?.list("") ?: arrayOf()
        Log.d("ASSETS", "Assets disponibles: ${assetFiles.joinToString(", ")}")
        binding.cvRegistration.setOnClickListener{
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToFaceRecord2())
        }
        binding.cvRecognition.setOnClickListener{
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToRecognitionFragment2())
        }
    }


}