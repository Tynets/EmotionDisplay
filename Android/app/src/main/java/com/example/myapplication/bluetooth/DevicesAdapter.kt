package com.example.myapplication.bluetooth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class DevicesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val devices: LinkedHashSet <BluetoothDeviceDO> = linkedSetOf()

    inner class TextFieldViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(device: BluetoothDeviceDO) {
            val item = itemView.findViewById<TextView>(R.id.deviceName)
            item.text = device.name
            itemView.findViewById<TextView>(R.id.deviceAddress).text = device.address
            return
        }
    }

    fun addDevice(device: BluetoothDeviceDO) {
        this.devices.add(device)
        notifyDataSetChanged()
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
        val device: BluetoothDeviceDO = this.devices.toArray()[position] as BluetoothDeviceDO
        when (holder) {
            is TextFieldViewHolder -> holder.bind(device)
        }
        return
    }

}