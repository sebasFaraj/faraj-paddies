package edu.nd.paradigms.hw4

import java.time.DayOfWeek

/**
 * Represents a time slot for a class, such as MWF 14:00-14:50 (Monday, Wednesday, and Friday starting at 14:00 [2p.m.])
 * All times represented in 24 time. Example, 2:00 p.m. -> 14:00
 */
data class TimeSlot(
    val days: Set<DayOfWeek>,
    val startTimeHour: Int,
    val startTimeMinute: Int,
    val endTimeHour: Int,
    val endTimeMinute: Int
)  {
    init {
        require(days.isNotEmpty()) { "The class must meet on at least one day of the week" }
        require(0 <= startTimeHour && startTimeHour < 24) {"Invalid startTimeHour: $startTimeHour - must be 0-23 inclusive"}
        require(0 <= endTimeHour && endTimeHour < 24) {"Invalid startTimeHour: $endTimeHour - must be 0-23 inclusive"}
        require(0 <= startTimeMinute && startTimeMinute < 60) {"Invalid startTimeMinut: $startTimeMinute - must be 0-59 inclusive"}
        require(0 <= endTimeMinute && endTimeMinute < 60) {"Invalid startTimeMinut: $endTimeMinute - must be 0-59 inclusive"}
        require(getTimeInMinutes(startTimeHour, startTimeMinute) < getTimeInMinutes(endTimeHour, endTimeMinute)) { """
            Invalid time slot, start time before end time.
                Start Time: $startTimeHour:$startTimeMinute
                End Time: $endTimeHour:$endTimeMinute
            """.trimIndent()
        }
    }
    
    fun overlapsWith(other: TimeSlot): Boolean {
        val thisDays = HashSet<DayOfWeek>(this.days)
        val otherDays = HashSet<DayOfWeek>(other.days)
        thisDays.retainAll(otherDays)
        if (thisDays.isEmpty()) {
            return false
        }
        val thisStartTime: Int = getTimeInMinutes(startTimeHour, startTimeMinute)
        val thisEndTime: Int = getTimeInMinutes(endTimeHour, endTimeMinute)
        val otherStartTime: Int = getTimeInMinutes(other.startTimeHour, other.startTimeMinute)
        val otherEndTime: Int = getTimeInMinutes(other.endTimeHour, other.endTimeMinute)
        
        return (otherStartTime <= thisStartTime && thisStartTime < otherEndTime) ||
            (thisStartTime <= otherStartTime && otherStartTime < thisEndTime)
    }
    
    companion object {
        val MONDAY_WEDNESDAY_FRIDAY = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        val MONDAY_WEDNESDAY = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
        val TUESDAY_THURSDAY = setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)
        
        fun getTimeInMinutes(hour: Int, minutes: Int): Int {
            return 60 * hour + minutes
        }
    }
}
