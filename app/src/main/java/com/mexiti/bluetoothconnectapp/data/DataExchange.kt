package com.mexiti.bluetoothconnectapp.data

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

class DataExchange(mmSocket: BluetoothSocket): Thread(){
    private val length = 2

    private val mmInStream: InputStream = mmSocket.inputStream
    private val mmOutStream: OutputStream = mmSocket.outputStream
    private val mmBuffer: ByteArray = ByteArray(length)

    fun write(bytes:ByteArray){
        try {
            mmOutStream.write(bytes)
        }catch (error: Exception){
            Log.e("Byte Error","Message didn't send")
        }
    }

    fun read():String{
        try {
            mmOutStream.write("C".toByteArray())
        }catch (error: Exception){
            Log.e("Byte Error","Message didn't receive")
        }
        var numBytesReaded = 0
        try{
            while (numBytesReaded < length){

                val number = mmInStream.read(mmBuffer,numBytesReaded,length-numBytesReaded )

                if(number == -1 )
                {
                    break
                }
                numBytesReaded += number
            }
            Log.d("TAGByte","Number of reading bytes: " + numBytesReaded + "\nDato: " + mmBuffer[0])
            return mmBuffer[0].toString()

        }
        catch (e: IOException){
            return "error"
        }
    }
}

