package com.ibrajix.multiclock.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibrajix.multiclock.database.DeviceSound
import com.ibrajix.multiclock.databinding.RcvLytDeviceSoundsBinding

class DeviceSoundAdapter(private val onClickListener: OnDeviceSoundClickListener) : ListAdapter<DeviceSound, DeviceSoundAdapter.DeviceSoundViewHolder>(DeviceSoundDiffCallback()) {

    class DeviceSoundViewHolder private constructor(private val binding: RcvLytDeviceSoundsBinding) : RecyclerView.ViewHolder(binding.root){

        fun bindDeviceSound(deviceSound: DeviceSound){
            binding.txtSoundName.text = deviceSound.name
        }

        companion object {
            fun from(parent: ViewGroup) : DeviceSoundViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RcvLytDeviceSoundsBinding.inflate(layoutInflater, parent, false)
                return DeviceSoundViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceSoundViewHolder {
        return DeviceSoundViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DeviceSoundViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null){
            holder.bindDeviceSound(item)
        }
        holder.itemView.setOnClickListener {
            if (item != null){
                onClickListener.onClickAlarmSound(item)
            }
        }
    }

    class DeviceSoundDiffCallback : DiffUtil.ItemCallback<DeviceSound>(){
        override fun areItemsTheSame(oldItem: DeviceSound, newItem: DeviceSound): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: DeviceSound, newItem: DeviceSound): Boolean {
            return oldItem == newItem
        }
    }

    class OnDeviceSoundClickListener(val clickListener: (deviceSound: DeviceSound) -> Unit){
        fun onClickAlarmSound(deviceSound: DeviceSound) = clickListener(deviceSound)
    }

}