package edu.nd.paradigms.hw4

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CatalogIntegrationTest {
    @Test
    fun add_returnsSUCCESSFUL_andCatalogContainsSection() {
        val fall = Semester(Term.FALL, 2025)
        val catalog = Catalog(fall)

        val course = Course(1, "CSE", 10100, "FakeClass", 3, Prerequisite())
        val room = Location("DBRT", "101", 100)
        val ts = TimeSlot(
            days = TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            startTimeHour = 9, startTimeMinute = 0,
            endTimeHour = 9, endTimeMinute = 50
        )
        val prof = Lecturer(id = 1, netId = "mroshi", firstName = "Master", lastName = "Roshi")
        val enr = Enrollment(initialEnrollmentCapacity = 30, initialWaitListCapacity = 10, initialEnrollmentStatus = EnrollmentStatus.OPEN)

        val s1 = Section(
            courseRegistrationNumber = 10001,
            sectionNumber = 1,
            course = course,
            semester = fall,
            location = room,
            timeSlot = ts,
            lecturer = prof,
            enrollment = enr
        )

        val svc = CatalogService(catalog)
        val result = svc.add(s1)

        assertEquals(CatalogService.AddSectionResult.SUCCESSFUL, result)
        assertTrue(catalog.contains(s1), "Catalog should contain the added section")
    }

    @Test
    fun add_returnsFAILED_LOCATION_CONFLICT_whenSameRoomOverlaps() {
        val fall = Semester(Term.FALL, 2025)
        val catalog = Catalog(fall)

        val c1 = Course(1, "CSE", 10100, "IntroFakeClass", 3, Prerequisite())
        val c2 = Course(2, "CSE", 20200, "FakeClass", 3, Prerequisite())

        val room = Location("DBRT", "101", 100)

        val prof1 = Lecturer(2, "suchiha", "Sasuke", "Uchiha")
        val prof2 = Lecturer(3, "nuzumaki", "Naruto", "Uzumaki")

        val tsA = TimeSlot(
            days = TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            startTimeHour = 9, startTimeMinute = 0,
            endTimeHour = 9, endTimeMinute = 50
        )
        val tsB = TimeSlot( // overlaps with tsA
            days = TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            startTimeHour = 9, startTimeMinute = 30,
            endTimeHour = 10, endTimeMinute = 20
        )

        val e1 = Enrollment(30, 10, initialEnrollmentStatus = EnrollmentStatus.OPEN)
        val e2 = Enrollment(30, 10, initialEnrollmentStatus = EnrollmentStatus.OPEN)

        val s1 = Section(10001, 1, c1, fall, room, tsA, prof1, e1)
        val s2 = Section(10002, 2, c2, fall, room, tsB, prof2, e2)

        val svc = CatalogService(catalog)

        catalog.add(s1)

        val r2 = svc.add(s2)

        assertEquals(CatalogService.AddSectionResult.FAILED_LOCATION_CONFLICT, r2)
        assertTrue(catalog.contains(s1), "Original non-conflicting section should remain")
        assertFalse(catalog.contains(s2), "Conflicting section should not be added")
    }
}