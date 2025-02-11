package com.djc.ft_testingapp.ui.facetrack

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.djc.ft_testingapp.R
import com.djc.ft_testingapp.databinding.FragmentRecognitionBinding

class RecognitionFragment : Fragment() {


    private var _binding: FragmentRecognitionBinding? = null

    val binding get() = _binding!!

    private val viewModel by activityViewModels<RecognitionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecognitionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private fun initListeners(){

    }


}