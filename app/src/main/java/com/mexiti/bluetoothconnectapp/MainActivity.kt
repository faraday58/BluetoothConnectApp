package com.mexiti.bluetoothconnectapp

import android.Manifest
import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import com.mexiti.bluetoothconnectapp.ui.theme.BluetoothConnectAppTheme


class MainActivity : ComponentActivity() {

    val REQUEST_ENABLE_BT =100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        var bluetoothAvailable = "Available"
        if (bluetoothAdapter == null){
            bluetoothAvailable = "Not Available"
        }

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted ->
            if (isGranted){
                Toast.makeText(applicationContext,"Permission Granted",Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(applicationContext,"This permission is required to comunicate with Arduino",Toast.LENGTH_LONG).show()
            }

        }

        when{
            ContextCompat.checkSelfPermission(
                applicationContext,android.Manifest.permission.BLUETOOTH) ==
                PackageManager.PERMISSION_GRANTED ->{
                    Toast.makeText(applicationContext,"Todo en orden a prender el bluetooth",Toast.LENGTH_LONG).show()

                }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,android.Manifest.permission.BLUETOOTH
            ) ->{
                Toast.makeText(applicationContext,"Debes de darme los permisos idiota, sino como quieres que funcione",Toast.LENGTH_LONG).show()
            }

            else ->{
                requestPermissionLauncher.launch(
                    android.Manifest.permission.BLUETOOTH
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
                    if (bluetoothAdapter != null) {
                        if (savedInstanceState != null) {
                            Disposible(bluetoothAvailable,bluetoothAdapter,savedInstanceState)
                        }
                    }
                }
            }
        }
    }


}


@Composable
fun Disposible(bluetooothAvailable:String,bluetoothAdapter: BluetoothAdapter, bundle: Bundle){

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center


    ) {
        Text(text = bluetooothAvailable,
            fontSize = 25.sp
        )
        Button(onClick = {
            if (bluetoothAdapter?.isEnabled == false){
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

                startActivityForResult(Activity(),enableBtIntent,200,bundle)
            }
        }) {
            Text(text = "On")

        }

    }
}



