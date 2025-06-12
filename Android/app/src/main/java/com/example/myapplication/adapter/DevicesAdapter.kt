package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.BluetoothDeviceDO

class DevicesAdapter(val listener: (BluetoothDeviceDO) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var devices: List<BluetoothDeviceDO> = listOf()

    inner class TextFieldViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(device: BluetoothDeviceDO) {
            itemView.findViewById<TextView>(R.id.deviceName).text = device.name
            itemView.findViewById<TextView>(R.id.deviceAddress).text = device.address
            itemView.setOnClickListener { listener(device) }
            return
        }
    }

    fun updateDevices(devices: List<BluetoothDeviceDO>) {
        this.devices = devices
        this.notifyDataSetChanged()
        return
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.devices_layout, parent, false)
        return TextFieldViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.devices.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val device: BluetoothDeviceDO = this.devices[position]
        when (holder) { is TextFieldViewHolder -> holder.bind(device) }
        return
    }

}