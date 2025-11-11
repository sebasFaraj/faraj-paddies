package edu.nd.paradigms.hw4

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RegistrationIntegrationTest {
    @Test
    fun register_returnsSUCCESS_ENROLLED_andUpdatesBothSides() {
        val fall = Semester(Term.FALL, 2025)
        val catalog = Catalog(fall)


        val sde = Course(200, "CSE", 67410, "FakeClass", creditHours = 3, prerequisite = Prerequisite())

        val room = Location("DBRT", "101", 100)
        val ts = TimeSlot(
            days = TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            startTimeHour = 10, startTimeMinute = 0,
            endTimeHour = 10, endTimeMinute = 50
        )
        val prof = Lecturer(10, "hkakashi", "Hatake", "Kakashi")

        //1 seat to guarantee enroll
        val enr = Enrollment(initialEnrollmentCapacity = 1, initialWaitListCapacity = 10, initialEnrollmentStatus = EnrollmentStatus.OPEN)
        val sec = Section(20001, 1, sde, fall, room, ts, prof, enr)
        catalog.add(sec)

        val stu = Student(1L, "sfaraj", "Sebastian", "Faraj", 3)

        val svc = RegistrationService()
        val res = svc.register(stu, sec)

        assertEquals(RegistrationService.RegistrationResult.SUCCESS_ENROLLED, res)

        //both sides reflect enrollment
        assertTrue(sec.isStudentEnrolled(stu), "Section should show student enrolled")
        assertTrue(stu.isEnrolledInSection(sec), "Student's schedule should include the section")
    }

    @Test
    fun register_returnsFAILED_PREREQUISITE_NOT_MET_whenStudentLacksRequiredCourse() {
        val fall = Semester(Term.FALL, 2025)

        // Prereq course (must have Intro with >= C-)
        val intro = Course(101, "CSE", 10100, "FakeClass", 3, Prerequisite())
        val prereq = Prerequisite().apply { add(intro, Grade.C_MINUS) }

        val dsa = Course(201, "CSE", 20200, "DSA", 3, prereq)

        val room = Location("DBRT", "103", 100)
        val ts = TimeSlot(
            days = TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            startTimeHour = 11, startTimeMinute = 0,
            endTimeHour = 11, endTimeMinute = 50
        )
        val prof = Lecturer(11, "jsannin", "Jiraiya", "sannin")

        val enr = Enrollment(initialEnrollmentCapacity = 30, initialWaitListCapacity = 10, initialEnrollmentStatus = EnrollmentStatus.OPEN)
        val sec = Section(30001, 1, dsa, fall, room, ts, prof, enr)

        // Student without Intro on transcript and not currently enrolled in Intro
        val stu = Student(2L, "sfaraj", "Sebastian", "Faraj", 2)

        val svc = RegistrationService()
        val res = svc.register(stu, sec)

        assertEquals(RegistrationService.RegistrationResult.FAILED_PREREQUISITE_NOT_MET, res)

        //ensure nothing changed
        assertFalse(sec.isStudentEnrolled(stu), "Student should not be enrolled")
        assertFalse(stu.isEnrolledInSection(sec), "Student schedule should remain unchanged")
    }

    @Test
    fun drop_returnsTRUE_andRemovesFromBothSides_andRecordsDrop() {
        val fall = Semester(Term.FALL, 2025)
        val course = Course(301, "CSE", 30100, "FakeClass", 3, Prerequisite())

        val room = Location("DBRT", "104", 100)
        val ts = TimeSlot(
            days = TimeSlot.MONDAY_WEDNESDAY_FRIDAY,
            startTimeHour = 12, startTimeMinute = 0,
            endTimeHour = 12, endTimeMinute = 50
        )
        val prof = Lecturer(12, "mnamikaze", "Minato", "Namikaze")

        val enr = Enrollment(initialEnrollmentCapacity = 30, initialWaitListCapacity = 10, initialEnrollmentStatus = EnrollmentStatus.OPEN)
        val sec = Section(40001, 1, course, fall, room, ts, prof, enr)

        val stu = Student(3L, "sfaraj", "Sebastian", "Faraj", 3)

        //enroll first using the real service
        val svc = RegistrationService()
        assertEquals(RegistrationService.RegistrationResult.SUCCESS_ENROLLED, svc.register(stu, sec))

        //now drop
        val ok = svc.drop(stu, sec)
        assertTrue(ok, "Drop should succeed")

        //ensure removed from section & schedule, and DROP grade recorded
        assertFalse(sec.isStudentEnrolled(stu), "Student should no longer be enrolled in section")
        assertFalse(stu.isEnrolledInSection(sec), "Student schedule should no longer include the section")
        assertEquals(Grade.DROP, stu.getGrade(sec), "DROP grade should be recorded on transcript")
    }


}