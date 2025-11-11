package edu.nd.paradigms.hw4

import org.junit.jupiter.api.Test
import kotlin.test.*

class TimeSlotTest {
    @Test
    fun constructorTest() {
        val timeSlot = TimeSlot(
            TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            1, 2,
            3, 4
        )
        assertEquals(timeSlot.startTimeHour, 1)
        assertEquals(timeSlot.startTimeMinute, 2)
        assertEquals(timeSlot.endTimeHour, 3)
        assertEquals(timeSlot.endTimeMinute, 4)
        assertEquals(timeSlot.days, TimeSlot.MONDAY_WEDNESDAY_FRIDAY)
    }
    
    @Test
    fun invalidConstructor_earlierEndTime() {
        assertFailsWith<IllegalArgumentException> {
            TimeSlot(TimeSlot.TUESDAY_THURSDAY, 12, 0, 11, 59)
        }
    }
    
    @Test
    fun isOverlap_noTimeOverlap() {
        val timeSlotA = TimeSlot(
            TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            12, 0, 12, 50
        )
        val timeSlotB = TimeSlot(
            TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            13, 0, 13, 50
        )
        assertFalse(timeSlotA.overlapsWith(timeSlotB))
    }
    
    @Test
    fun isOverlap_timeOverlap_differentDays() {
        val timeSlotA = TimeSlot(
            TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            12, 0, 12, 50
        )
        val timeSlotB = TimeSlot(
            TimeSlot.TUESDAY_THURSDAY,
            12, 0, 12, 50
        )
        assertFalse(timeSlotA.overlapsWith(timeSlotB))
    }
    
    @Test
    fun isOverlap_sameTimeSlot() {
        val timeSlotA = TimeSlot(
            TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            12, 0, 12, 50
        )
        assertTrue(timeSlotA.overlapsWith(timeSlotA))
    }
    
    @Test
    fun isOverlap_oneMinuteOverlap() {
        val timeSlotA = TimeSlot(
            TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            12, 0, 12, 50
        )
        val timeSlotB = TimeSlot(
            TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            11, 30, 12, 1
        )
        assertTrue(timeSlotA.overlapsWith(timeSlotB))
    }
    
    @Test
    fun isOverlap_oneMinuteOverlap_mirrored() {
        val timeSlotA = TimeSlot(
            TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            12, 0, 12, 50
        )
        val timeSlotB = TimeSlot(
            TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            11, 30, 12, 1
        )
        assertTrue(timeSlotB.overlapsWith(timeSlotA))
    }
}