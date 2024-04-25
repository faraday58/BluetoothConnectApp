package com.mexiti.bluetoothconnectapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mexiti.bluetoothconnectapp.controller.connectHC05
import com.mexiti.bluetoothconnectapp.data.DataExchange
import com.mexiti.bluetoothconnectapp.ui.theme.BluetoothConnectAppTheme
import com.mexiti.bluetoothconnectapp.ui.views.BluetoothUI

const val CONNECTION_FAIlED: Int = 0
const val CONNECTION_SUCCESS: Int =1
var dataExchangeInstance: DataExchange? = null


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
                    BluetoothUI(connectStatus = status)

                }
            }
        }
    }

}





