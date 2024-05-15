package com.mexiti.bluetoothconnectapp.ui.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mexiti.bluetoothconnectapp.dataExchangeInstance
import com.mexiti.bluetoothconnectapp.ui.theme.BluetoothConnectAppTheme

@Composable
fun BluetoothUI(connectStatus: MutableState<String>){
    var checked by remember {
        mutableStateOf(false)
    }
    val sensor = remember {
        mutableStateOf("Sin mensaje")
    }
    var switchControl by remember {
      mutableStateOf("LED OFF")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
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
            Text(text = switchControl)
            Switch(checked = checked ,
                onCheckedChange = {
                    if (!checked){
                        switchControl = "LED ON"
                        dataExchangeInstance?.write("A".toByteArray())
                        checked = true
                    }else{
                        switchControl = "LED OFF"
                        dataExchangeInstance?.write("B".toByteArray())
                        checked = false

                    }
                }
            )

        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                val str = dataExchangeInstance?.read()
                if(str != null){
                    sensor.value = str + " [°C]"
                }else{
                    connectStatus.value = "Sin mensaje"
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

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun BluetoothUIPreview(){
    val state = mutableStateOf("Correcto")
BluetoothConnectAppTheme {
    Surface {
        BluetoothUI(connectStatus =state )
    }

}

}