package com.mehmetpeker.glancebatterywidget

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.mehmetpeker.glancebatterywidget.R
import com.mehmetpeker.glancebatterywidget.service.GlanceForegroundService

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

const val BATTERY_LEVEL_KEY = "battery_level"

@ExperimentalUnitApi
class GlanceBatteryWidgetReceiver : GlanceAppWidgetReceiver() {
    var glanceId: GlanceId? = null
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "android.appwidget.action.APPWIDGET_UPDATE" -> {

            }
            "android.intent.action.BATTERY_CHANGED" -> {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                MainScope().launch {
                    glanceId =
                        GlanceAppWidgetManager(context).getGlanceIds(GlanceBatteryWidget::class.java)
                            .firstOrNull()
                    glanceId?.let { id ->
                        updateAppWidgetState(
                            context,
                            PreferencesGlanceStateDefinition,
                            id
                        ) { preferences ->
                            preferences.toMutablePreferences().apply {
                                this[intPreferencesKey(BATTERY_LEVEL_KEY)] = level
                            }
                        }
                        GlanceBatteryWidget().update(context, id)
                    }


                }

            }
            "android.intent.action.BOOT_COMPLETED" -> {
                val serviceIntent = Intent(context, GlanceForegroundService::class.java)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }


            }
        }


        super.onReceive(context, intent)
    }

    override val glanceAppWidget: GlanceAppWidget = GlanceBatteryWidget()
}


@ExperimentalUnitApi
class GlanceBatteryWidget : GlanceAppWidget() {


    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    @Composable
    override fun Content() {
        val preferences = currentState<Preferences>()
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.White)
                .appWidgetBackground()
                .cornerRadius(16.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val batteryLevel = preferences[intPreferencesKey(BATTERY_LEVEL_KEY)] ?: -1

            Text(
                "Batarya Seviyesi", style = TextStyle(
                    fontSize = TextUnit(16f, TextUnitType.Sp),
                    color = ColorProvider(Color.Black),
                    textAlign = TextAlign.Center
                ), modifier = GlanceModifier.fillMaxWidth()
            )
            Spacer(GlanceModifier.size(8.dp))
            if (batteryLevel == -1) {
                LoadingContent()
            } else {
                ProgressContent(batteryLevel = batteryLevel)
            }

        }
    }

    @Composable
    fun ProgressContent(batteryLevel: Int) {
        val progress: Float = batteryLevel / 100f
        val boxBackgroundColor = when (batteryLevel) {
            in 80..100 -> Color.Green
            in 25..80 -> MaterialTheme.colors.primary
            else -> Color.Red
        }
        // Note:CornerRadius only works on Android S+.
        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier.size(96.dp)
                .background(boxBackgroundColor)
                .cornerRadius(48.dp)
        ) {
            Text(
                text = "$batteryLevel", style = TextStyle(
                    fontSize = TextUnit(24f, TextUnitType.Sp),
                    color = ColorProvider(Color.White),
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(GlanceModifier.size(8.dp))
        LinearProgressIndicator(progress)
    }

    @Composable
    fun LoadingContent() {
        Image(
            provider = ImageProvider(R.drawable.ic_baseline_timer_24),
            contentDescription = "",
            modifier = GlanceModifier.size(24.dp)
        )
    }
}

