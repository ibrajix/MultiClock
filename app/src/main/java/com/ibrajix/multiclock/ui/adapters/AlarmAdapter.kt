package com.ibrajix.multiclock.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibrajix.multiclock.database.Alarm
import com.ibrajix.multiclock.databinding.RcvLytAlarmsBinding

class AlarmAdapter(private val onClickListener: OnAlarmClickListener) : ListAdapter<Alarm, AlarmAdapter.AlarmViewHolder>(AlarmDiffCallback()) {

    class AlarmViewHolder private constructor(private val binding: RcvLytAlarmsBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(alarm: Alarm?){
            binding.alarm = alarm
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup) : AlarmViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RcvLytAlarmsBinding.inflate(layoutInflater, parent, false)
                return AlarmViewHolder(binding)
            }
        }

    }

    class AlarmDiffCallback : DiffUtil.ItemCallback<Alarm>(){

        override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        return AlarmViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null){
            holder.bind(item)
        }
        holder.itemView.setOnClickListener {
            if (item != null){
                onClickListener.onClickAlarm(item)
            }
        }
    }

    class OnAlarmClickListener(val clickListener: (alarm: Alarm) -> Unit){
        fun onClickAlarm(alarm: Alarm) = clickListener(alarm)
    }

}