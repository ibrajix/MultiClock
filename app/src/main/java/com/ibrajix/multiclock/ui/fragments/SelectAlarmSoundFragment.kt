package com.ibrajix.multiclock.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.databinding.FragmentSelectAlarmSoundBinding

class SelectAlarmSoundFragment : Fragment() {

    private var _binding: FragmentSelectAlarmSoundBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSelectAlarmSoundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView(){
        handleClicks()
    }

    private fun handleClicks(){
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}