package dev.sijan.focii.accessibility

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import dev.sijan.focii.services.routines.RoutineSessionManager
import dev.sijan.focii.util.NotificationHelper
import dev.sijan.focii.util.isPrefsInitialized
import dev.sijan.focii.util.prefs

class RoutinesService: Service() {

    companion object {
        private const val TAG = "RoutinesService"

        fun start(context: Context) {
            try {
                context.startService(Intent(context, RoutinesService::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start RoutinesService", e)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, RoutinesService::class.java))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isPrefsInitialized) {
            val deviceContext = createDeviceProtectedStorageContext()
            prefs = deviceContext.getSharedPreferences("prefs", MODE_PRIVATE)
        }
        try {
            RoutineSessionManager.evaluateAndSync(this)
            NotificationHelper.syncRoutineNotification(this)
        } catch (e: Exception) {
            Log.e(TAG, "Error during routine evaluation", e)
        }
        stopSelf(startId)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
