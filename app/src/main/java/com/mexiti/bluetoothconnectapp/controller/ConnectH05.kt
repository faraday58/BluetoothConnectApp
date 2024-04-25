package com.mexiti.bluetoothconnectapp.controller

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.os.Handler

@SuppressLint("MissingPermission")
fun connectHC05(bluetoothAdapter: BluetoothAdapter?, handler: Handler):String{
    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
    val hc05Device = pairedDevices?.find { it.name == "HC-05" }
    if (hc05Device != null){
        ConnectThread(hc05Device, handler = handler).start()
        return ""
    }else{
        return "HC-05 No asociado\n"
    }


}

