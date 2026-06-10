package dev.sijan.focii.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.edit
import dev.sijan.focii.accessibility.BlockerService
import dev.sijan.focii.accessibility.FocusModeService
import dev.sijan.focii.services.routines.RoutineAlarmScheduler
import dev.sijan.focii.services.routines.RoutineSessionManager
import dev.sijan.focii.util.NotificationHelper
import dev.sijan.focii.util.isAccessibilityServiceEnabledForBlocker
import dev.sijan.focii.util.isPrefsInitialized
import dev.sijan.focii.util.prefs

class BootReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val safeContext =
            context.createDeviceProtectedStorageContext()

        if (!isPrefsInitialized) {
            prefs = safeContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        }

        Log.d("BootReceiver", "Action received: ${intent.action}")

        when (intent.action) {
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_USER_PRESENT -> {
                refreshServices(safeContext)

                RoutineSessionManager.evaluateAndSync(safeContext)
                NotificationHelper.syncRoutineNotification(safeContext)
                RoutineAlarmScheduler.scheduleAll(
                    safeContext,
                    dev.sijan.focii.routine.Routines.getAll()
                )

                if (prefs.getBoolean("daily_summary", false)) {
                    DailySummaryScheduler.scheduleDailySummary(safeContext)
                }

                if (intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
                    prefs.edit { putBoolean("show_dialog", true) }
                }
            }
        }
    }

    private fun refreshServices(context: Context) {
        if (context.isAccessibilityServiceEnabledForBlocker()) {
            val accessibilityIntent = Intent(context, BlockerService::class.java)
            try {
                context.startService(accessibilityIntent)
            } catch (e: Exception) {
                Log.e("BootReceiver", "Could not nudge BlockerService", e)
            }
        }

        if (prefs.getBoolean("focus_mode", false)) {
            val serviceIntent = Intent(context, FocusModeService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}
