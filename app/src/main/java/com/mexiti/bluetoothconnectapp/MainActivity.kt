package com.mexiti.bluetoothconnectapp

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mexiti.bluetoothconnectapp.ui.theme.BluetoothConnectAppTheme
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.UUID

const val CONNECTION_FAIlED: Int = 0
const val CONNECTION_SUCCESS: Int =1
private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")


class MainActivity : ComponentActivity() {

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         bluetoothManager = getSystemService(BluetoothManager::class.java)
         bluetoothAdapter  = bluetoothManager.adapter

        val status = mutableStateOf("Bluetooth & Arduino \n")

        val handler = Handler(Looper.getMainLooper()){
            msg ->
            when(msg.what){
                CONNECTION_FAIlED -> {
                    status.value+= "Conexión Rechazada"
                    true
                }
                CONNECTION_SUCCESS -> {
                    status.value += "Conexión Exitosa"
                    true
                }else -> false

            }
        }

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted ->
            if (isGranted){
                status.value += "Permisos Aceptados \n Intentando Conexión\n"
                status.value += connectHC05(bluetoothAdapter,handler)
                Toast.makeText(applicationContext,"Permission Granted",Toast.LENGTH_LONG).show()
            }
            else{
                status.value += "=====> Permiso Denegado"
                Toast.makeText(applicationContext,"This permission is required to comunicate with Arduino",Toast.LENGTH_LONG).show()
            }

        }

        when{
            ContextCompat.checkSelfPermission(
                applicationContext,Manifest.permission.BLUETOOTH) ==
                PackageManager.PERMISSION_GRANTED ->{
                   status.value += connectHC05(bluetoothAdapter,handler)
                    Toast.makeText(applicationContext,"Todo en orden a conectar con el bluetooth",Toast.LENGTH_LONG).show()

                }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,Manifest.permission.BLUETOOTH
            ) ->{
                Toast.makeText(applicationContext,"Debes de darme los permisos idiota, sino como quieres que funcione",Toast.LENGTH_LONG).show()
            }

            else ->{
                requestPermissionLauncher.launch(
                    Manifest.permission.BLUETOOTH
                )
            }


        }



        setContent {
            BluetoothConnectAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyUI(connectStatus = status)

                }
            }
        }
    }

}





@SuppressLint("MissingPermission")
private fun connectHC05(bluetoothAdapter: BluetoothAdapter?, handler: Handler):String{
    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
    val hc05Device = pairedDevices?.find { it.name == "HC-05" }
    if (hc05Device != null){
        ConnectThread(hc05Device, handler = handler).start()
        return ""
    }else{
        return "HC-05 No asociado\n"
    }


}

@SuppressLint("MissingPermission")
class ConnectThread(private val monDevice:BluetoothDevice,
    private val handler: Handler): Thread(){
        private val mmSocket:BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            monDevice.createRfcommSocketToServiceRecord(MY_UUID)
        }

    override fun run() {
        mmSocket?.let {
            socket ->
            try {
                socket.connect()
                handler.obtainMessage(CONNECTION_SUCCESS).sendToTarget()

            }
            catch (e:Exception){
                handler.obtainMessage(CONNECTION_FAIlED).sendToTarget()
            }
            dataExchangeInstance= DataExchange(socket)
        }
    }


}


var dataExchangeInstance: DataExchange? = null
class DataExchange(mmSocket:BluetoothSocket): Thread(){
    private val length = 5
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
        }catch (error:Exception){
            Log.e("Byte Error","Message didn't receive")
        }
        var numBytesReaded = 0
        try{
            while (numBytesReaded < length){
                val number = mmInStream.read(mmBuffer,numBytesReaded,length -numBytesReaded)
                if(number == -1 )
                {
                    break
                }
                numBytesReaded += number
            }
            return String(mmBuffer,0,numBytesReaded)

        }
        catch (e:IOException){
            return "error"
        }
    }
}

@Composable
fun MyUI(connectStatus: MutableState<String>){
    val sensor = remember {
        mutableStateOf("Nada")
    }

    Column {
        Text(
            text = connectStatus.value,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .background(Color(0x80E2EBEA))
                .padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
            ){
            Button(
                onClick = {
                    dataExchangeInstance?.write("A".toByteArray())
                }) {
                Text(text = "  LED ON  ")
            }
            Button(onClick = {
                dataExchangeInstance?.write("B".toByteArray())
            }) {
                Text(text = " LED OFF  ")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                val str = dataExchangeInstance?.read()
                if(str != null){
                    sensor.value = str
                }else{
                    connectStatus.value = "Nada"
                }
            },
                modifier = Modifier.padding(start = 48.dp)
                ) {
                Text(text = " READ ")
            }
            Text(
                text = sensor.value,
                modifier = Modifier
                    .padding(start = 96.dp)
                    .background(Color(0x80E2EBEA))
                    .padding(horizontal = 16.dp)
            )
        }

    }

}