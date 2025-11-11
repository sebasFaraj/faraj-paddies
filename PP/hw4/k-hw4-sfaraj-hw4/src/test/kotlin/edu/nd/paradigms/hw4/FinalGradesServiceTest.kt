package edu.nd.paradigms.hw4

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FinalGradesServiceTest {

    private val service = FinalGradesService()

    //happy path test (partial upload, enrolled student)
    @Test
    fun processFinalGrades_partialUpload_success() {
        val section = mockk<Section>()
        val s1 = mockk<Student>(relaxed = true)    // will receive a grade
        val s2 = mockk<Student>(relaxed = true)    // enrolled, but not in upload map

        //both are enrolled in this section
        every { section.isStudentEnrolled(s1) } returns true
        every { section.isStudentEnrolled(s2) } returns true

        val grades = mapOf(s1 to Grade.B_PLUS)     // only s1 gets uploaded now

        service.processFinalGrades(section, grades)

        //s1 updated + removed from enrolled schedule
        verify { s1.addGrade(section, Grade.B_PLUS) }
        verify { s1.removeEnrolledSection(section) }

        //s2 unchanged (not part of upload)
        verify(exactly = 0) { s2.addGrade(any(), any()) }
        verify(exactly = 0) { s2.removeEnrolledSection(any()) }
        verify(exactly = 0) { s2.removeWaitListedSection(any()) }
    }


    //Failure test (given student is not enrolled or is waitlisted)
    @Test
    fun processFinalGrades_throwsIfAnyStudentNotEnrolled_andDoesNothing() {
        val section = mockk<Section>()
        val s1 = mockk<Student>(relaxed = true)

        // s1 is NOT enrolled (could be waitlisted or not in class)
        every { section.isStudentEnrolled(s1) } returns false

        val grades = mapOf(s1 to Grade.C)

        assertFailsWith<IllegalArgumentException> {
            service.processFinalGrades(section, grades)
        }

        // No side effects
        verify(exactly = 0) { s1.addGrade(any(), any()) }
        verify(exactly = 0) { s1.removeEnrolledSection(any()) }
        verify(exactly = 0) { s1.removeWaitListedSection(any()) }
    }

    //Grade change is allowed (re-upload)
    fun processFinalGrades_allowsChangingExistingGrade() {
        val section = mockk<Section>()
        val s = mockk<Student>(relaxed = true)

        every { section.isStudentEnrolled(s) } returns true

        // Instructor re-uploads a different final grade
        val grades = mapOf(s to Grade.A_MINUS)

        service.processFinalGrades(section, grades)

        verify { s.addGrade(section, Grade.A_MINUS) }
        verify { s.removeEnrolledSection(section) }
    }
}