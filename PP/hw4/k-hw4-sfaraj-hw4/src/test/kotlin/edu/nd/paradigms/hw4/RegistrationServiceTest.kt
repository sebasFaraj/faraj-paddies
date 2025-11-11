package edu.nd.paradigms.hw4

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.Runs
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.BeforeTest


class RegistrationServiceTest {
    private val service = RegistrationService()
    private val sem = Semester(Term.FALL, 2025)

    @BeforeTest
    fun setup()
    {
        MockKAnnotations.init(this)
    }

    //register() Tests
    fun register_failsAlreadyInCourse() {
        val student = mockk<Student>(relaxed = true)
        val section = mockk<Section>()
        val existing = mockk<Section>()
        val course = mockk<Course>()

        every { section.course } returns course
        every { section.semester } returns sem
        every { existing.course } returns course
        every { existing.semester } returns sem
        every { student.getEnrolledSections() } returns setOf(existing)
        every { student.getWaitListedSections() } returns emptySet()

        val result = service.register(student, section)

        assertEquals(RegistrationService.RegistrationResult.FAILED_ALREADY_IN_COURSE, result)
        verify(exactly = 0) { section.addStudentToEnrolled(any()) }
        verify(exactly = 0) { section.addStudentToWaitList(any()) }
        verify(exactly = 0) { student.addEnrolledSection(any()) }
        verify(exactly = 0) { student.addWaitListedSection(any()) }
    }

    @Test
    fun register_successEnroll_whenOpenNotFull_noConflicts_prereqsOk_andUnderCreditLimit() {
        val student = mockk<Student>(relaxed = true)
        val section = mockk<Section>()
        val course = mockk<Course>()
        val prereq = mockk<Prerequisite>()
        val ts = mockk<TimeSlot>()

        every { section.course } returns course
        every { section.semester } returns sem
        every { section.timeSlot } returns ts
        every { section.getEnrollmentStatus() } returns EnrollmentStatus.OPEN
        every { section.isEnrollmentFull() } returns false
        every { section.isWaitListFull() } returns false
        every { section.addStudentToEnrolled(student) } just Runs

        every { course.creditHours } returns 3
        every { course.prerequisite } returns prereq
        every { prereq.isSatisfiedBy(student) } returns true

        every { student.getEnrolledSections() } returns emptySet()
        every { student.getWaitListedSections() } returns emptySet()
        every { student.getCreditLimit() } returns 18
        every { student.addEnrolledSection(section) } returns true

        val result = service.register(student, section)

        assertEquals(RegistrationService.RegistrationResult.SUCCESS_ENROLLED, result)
        verify { section.addStudentToEnrolled(student) }
        verify { student.addEnrolledSection(section) }
    }


    //drop() tests
    @Test
    fun drop_returnsFalse_whenNeitherEnrolledNorWaitlisted() {
        val student = mockk<Student>(relaxed = true)
        val section = mockk<Section>()

        every { section.isStudentEnrolled(student) } returns false
        every { section.isStudentWaitListed(student) } returns false

        assertFalse(service.drop(student, section))

        verify(exactly = 0) { section.removeStudentFromEnrolled(any()) }
        verify(exactly = 0) { section.removeStudentFromWaitList(any()) }
        verify(exactly = 0) { student.removeEnrolledSection(any()) }
        verify(exactly = 0) { student.removeWaitListedSection(any()) }
        verify(exactly = 0) { student.addGrade(any(), any()) }
    }

    @Test
    fun drop_enrolled_promotesFirstWaitlisted_whenOpen_andRecordsDrop() {
        val dropping = mockk<Student>(relaxed = true)
        val next = mockk<Student>(relaxed = true)
        val section = mockk<Section>()

        // dropping student is enrolled
        every { section.isStudentEnrolled(dropping) } returns true
        every { section.isStudentWaitListed(dropping) } returns false

        // after removal: section OPEN and not full → promotion should occur
        every { section.getEnrollmentStatus() } returns EnrollmentStatus.OPEN
        every { section.isEnrollmentFull() } returns false

        // waitlist present
        every { section.getWaitListedStudents() } returns listOf(next)
        every { section.getFirstStudentOnWaitList() } returns next

        every { section.removeStudentFromEnrolled(dropping) } just Runs
        every { section.removeStudentFromWaitList(next) } just Runs
        every { section.addStudentToEnrolled(next) } just Runs

        // schedule operations are relaxed on students

        val ok = service.drop(dropping, section)
        assertTrue(ok)

        // dropping student side effects
        verify { section.removeStudentFromEnrolled(dropping) }
        verify { dropping.removeEnrolledSection(section) }
        verify { dropping.addGrade(section, Grade.DROP) }

        // promotion side effects for first waitlisted
        verify { section.removeStudentFromWaitList(next) }
        verify { section.addStudentToEnrolled(next) }
        verify { next.removeWaitListedSection(section) }
        verify { next.addEnrolledSection(section) }
    }



}