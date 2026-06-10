package dev.sijan.focii.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sijan.focii.services.routines.RoutineSessionManager
import dev.sijan.focii.util.NotificationHelper
import dev.sijan.focii.util.isPrefsInitialized
import dev.sijan.focii.util.prefs

class RoutineAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val safeContext = context.createDeviceProtectedStorageContext()
        if (!isPrefsInitialized) {
            prefs = safeContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        }
        RoutineSessionManager.evaluateAndSync(safeContext)
        NotificationHelper.syncRoutineNotification(safeContext)
    }
}
