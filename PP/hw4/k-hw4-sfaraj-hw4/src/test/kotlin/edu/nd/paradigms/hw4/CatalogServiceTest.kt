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
import kotlin.test.BeforeTest

class CatalogServiceTest {
    private lateinit var catalog: Catalog
    private lateinit var service: CatalogService
    private val sem = Semester(Term.FALL, 2025)

    @BeforeTest
    fun setup() {
        catalog = mockk()
        every {catalog.semester} returns sem
        service = CatalogService(catalog)
    }


    // add() Tests
    @Test
    fun add_fails_whenSemesterMismatch() {
        val section = mockk<Section>()
        every { section.semester } returns Semester(Term.SPRING, 2025)

        val result = service.add(section)

        assertEquals(CatalogService.AddSectionResult.FAILED_SEMESTER_MISMATCH, result)
        verify(exactly = 0) { catalog.add(any()) }
    }

    @Test
    fun add_fails_whenLocationConflict() {
        val section = mockk<Section>()
        val existing = mockk<Section>()
        val room = Location("DBRT", "101", 60)
        val ts = mockk<TimeSlot>()

        //conflict checks
        every { section.semester } returns sem
        every { catalog.contains(section) } returns false
        every { section.courseRegistrationNumber } returns 12345
        every { catalog.getSectionByCRN(12345) } returns null
        every { catalog.sections } returns setOf(existing)

        //conflict: same room + overlapping times
        every { section.location } returns room
        every { section.timeSlot } returns ts
        every { existing.location } returns room
        every { existing.overlapsWith(ts) } returns true

        val result = service.add(section)

        assertEquals(CatalogService.AddSectionResult.FAILED_LOCATION_CONFLICT, result)
        verify(exactly = 0) { catalog.add(any()) }
    }

    @Test
    fun add_successful_whenAllRulesPass() {
        val section = mockk<Section>()
        every { section.semester } returns sem
        every { catalog.contains(section) } returns false
        every { section.courseRegistrationNumber } returns 22222
        every { catalog.getSectionByCRN(22222) } returns null
        every { catalog.sections } returns emptySet()
        every { section.getEnrollmentSize() } returns 0
        every { section.getWaitListSize() } returns 0
        every { catalog.add(section) } returns true

        val result = service.add(section)

        assertEquals(CatalogService.AddSectionResult.SUCCESSFUL, result)
        verify { catalog.add(section) }
    }


    //remove() Tests

    @Test
    fun remove_throws_ifSectionNotInCatalog() {
        val section = mockk<Section>()
        every { catalog.contains(section) } returns false

        assertFailsWith<IllegalArgumentException> { service.remove(section) }
        verify(exactly = 0) { catalog.remove(any()) }
    }

    @Test
    fun remove_updatesStudentSchedules_andRemovesFromCatalog() {
        val section = mockk<Section>()
        val enrolled = setOf(mockk<Student>(relaxed = true), mockk<Student>(relaxed = true))
        val waitlisted = listOf(mockk<Student>(relaxed = true))

        every { catalog.contains(section) } returns true
        every { section.getEnrolledStudents() } returns enrolled
        every { section.getWaitListedStudents() } returns waitlisted
        every { catalog.remove(section) } returns true

        service.remove(section)

        enrolled.forEach { verify { it.removeEnrolledSection(section) } }
        waitlisted.forEach { verify { it.removeWaitListedSection(section) } }
        verify { catalog.remove(section) }
    }


    // closeAllSections() test
    @Test
    fun closeAllSections_setsEverySectionClosed() {
        val s1 = mockk<Section>(relaxed = true)
        val s2 = mockk<Section>(relaxed = true)

        every { catalog.sections } returns setOf(s1, s2)

        service.closeAllSections()

        verify { s1.setEnrollmentStatus(EnrollmentStatus.CLOSED) }
        verify { s2.setEnrollmentStatus(EnrollmentStatus.CLOSED) }
    }

}