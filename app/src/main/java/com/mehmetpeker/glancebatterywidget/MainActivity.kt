package com.mehmetpeker.glancebatterywidget

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import com.mehmetpeker.glancebatterywidget.service.GlanceForegroundService
import com.mehmetpeker.glancebatterywidget.ui.theme.GlanceTodoListTheme

@ExperimentalUnitApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serviceIntent = Intent(applicationContext,GlanceForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        }else{
            startService(serviceIntent)
        }
        setContent {
            GlanceTodoListTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Mobiler.dev")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Merhaba $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GlanceTodoListTheme {
        Greeting("Android")
    }
}