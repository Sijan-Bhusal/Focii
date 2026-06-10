package dev.sijan.focii.services.routines

import dev.sijan.focii.data.Routine
import dev.sijan.focii.data.RoutineSchedule
import java.time.LocalDateTime

object RoutineEvaluator {

    data class ActivationInfo(
        val routine: Routine,
        val sessionStartMs: Long,
        val sessionEndMs: Long
    )

    fun findRoutinesToActivate(
        routines: List<Routine>,
        activeSessionIds: Set<String>,
        now: LocalDateTime
    ): List<ActivationInfo> {
        val toActivate = mutableListOf<ActivationInfo>()

        for (routine in routines) {
            if (!routine.isEnabled) continue
            if (routine.schedule.type == RoutineSchedule.ScheduleType.MANUAL) continue
            if (routine.id in activeSessionIds) continue

            val window = RoutineTimeCalculator.getSessionWindow(routine.schedule, now)
            if (window != null) {
                toActivate.add(
                    ActivationInfo(
                        routine = routine,
                        sessionStartMs = window.startEpochMs,
                        sessionEndMs = window.endEpochMs
                    )
                )
            }
        }

        return toActivate
    }
}
