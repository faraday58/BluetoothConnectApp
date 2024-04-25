package com.mexiti.bluetoothconnectapp.controller

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import com.mexiti.bluetoothconnectapp.CONNECTION_FAIlED
import com.mexiti.bluetoothconnectapp.CONNECTION_SUCCESS
import com.mexiti.bluetoothconnectapp.data.DataExchange
import com.mexiti.bluetoothconnectapp.dataExchangeInstance
import java.lang.Exception
import java.util.UUID

private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

@SuppressLint("MissingPermission")
class ConnectThread(private val monDevice: BluetoothDevice,
                    private val handler: Handler
): Thread(){
    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        monDevice.createRfcommSocketToServiceRecord(MY_UUID)
    }

    override fun run() {
        mmSocket?.let {
                socket ->
            try {
                socket.connect()
                handler.obtainMessage(CONNECTION_SUCCESS).sendToTarget()

            }
            catch (e: Exception){
                handler.obtainMessage(CONNECTION_FAIlED).sendToTarget()
            }
            dataExchangeInstance = DataExchange(socket)
        }
    }


}

