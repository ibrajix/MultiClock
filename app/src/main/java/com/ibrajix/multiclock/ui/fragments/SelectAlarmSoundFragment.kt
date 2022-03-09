package com.ibrajix.multiclock.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ibrajix.multiclock.databinding.FragmentSelectAlarmSoundBinding
import com.ibrajix.multiclock.ui.adapters.DeviceSoundAdapter
import com.ibrajix.multiclock.utils.AlarmUtility.getNotificationSounds

class SelectAlarmSoundFragment : Fragment() {

    private var _binding: FragmentSelectAlarmSoundBinding? = null
    private val binding get() = _binding!!
    lateinit var deviceSoundAdapter: DeviceSoundAdapter

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

        deviceSoundAdapter = DeviceSoundAdapter(onClickListener = DeviceSoundAdapter.OnDeviceSoundClickListener{
            //do something when sound is clicked

        })

        binding.rcvDeviceSounds.apply {
            adapter = deviceSoundAdapter
        }

        deviceSoundAdapter.submitList(getNotificationSounds(requireContext()))

        handleClicks()

    }

    private fun handleClicks(){
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}