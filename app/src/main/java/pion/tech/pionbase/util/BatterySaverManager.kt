package pion.tech.pionbase.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

import androidx.core.content.ContextCompat

object BatterySaverManager {

    private const val LOW_BATTERY_THRESHOLD_PERCENT = 20

    fun isPowerSaveMode(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        return powerManager?.isPowerSaveMode == true
    }

    fun getBatteryPercentage(context: Context): Int {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = ContextCompat.registerReceiver(context, null, iFilter, ContextCompat.RECEIVER_NOT_EXPORTED)
            ?: return 100
        val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level == -1 || scale == -1) return 100
        return ((level.toFloat() / scale.toFloat()) * 100).toInt()
    }

    fun isLowBatteryOrPowerSave(context: Context): Boolean {
        val isLowBattery = getBatteryPercentage(context) < LOW_BATTERY_THRESHOLD_PERCENT
        val isPowerSave = isPowerSaveMode(context)
        val active = isLowBattery || isPowerSave
        Timber.d("BatterySaverManager: lowBattery=$isLowBattery, powerSave=$isPowerSave -> active=$active")
        return active
    }

    fun observeBatterySaverState(context: Context): Flow<Boolean> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (ctx != null) {
                    trySend(isLowBatteryOrPowerSave(ctx))
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        }

        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        trySend(isLowBatteryOrPowerSave(context))

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
                Timber.e(e, "Error unregistering BatterySaverReceiver")
            }
        }
    }
}
