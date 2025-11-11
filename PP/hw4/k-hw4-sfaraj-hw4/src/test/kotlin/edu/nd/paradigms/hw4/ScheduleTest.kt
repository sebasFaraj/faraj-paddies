package edu.nd.paradigms.hw4

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScheduleTest {
    lateinit var schedule: Schedule
    
    @MockK(relaxed = true)
    lateinit var enrolledSections: MutableSet<Section>
    
    @MockK(relaxed = true)
    lateinit var waitListedSections: MutableSet<Section>
    
    @MockK
    lateinit var paradigmsSection: Section
    
    @MockK
    lateinit var algorithmsSection: Section
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        schedule = Schedule(enrolledSections =  enrolledSections, waitListedSections = waitListedSections)
    }
    
    @Test
    fun getEnrolledSections() {
        enrolledSections = mutableSetOf(paradigmsSection, algorithmsSection)
        schedule = Schedule(enrolledSections = enrolledSections)
        
        val outputSections = schedule.enrolledSections
        
        assertEquals(2, outputSections.size)
        assertTrue(outputSections.contains(paradigmsSection))
        assertTrue(outputSections.contains(algorithmsSection))
    }
    
    @Test
    fun getWaitListSection() {
        waitListedSections = mutableSetOf(paradigmsSection, algorithmsSection)
        schedule = Schedule(waitListedSections = waitListedSections)
        
        val outputSections = schedule.waitListedSections
        
        assertEquals(2, outputSections.size)
        assertTrue(outputSections.contains(paradigmsSection))
        assertTrue(outputSections.contains(algorithmsSection))
    }
    
    @Test
    fun addToEnrolled_true() {
        every { enrolledSections.add(paradigmsSection) } returns true
        
        assertTrue(schedule.addEnrolledSection(paradigmsSection) )
        
        verify {enrolledSections.add(paradigmsSection) }
    }
    
    @Test
    fun addToEnrolled_false() {
        every { enrolledSections.add(paradigmsSection) } returns false
        
        assertFalse(schedule.addEnrolledSection(paradigmsSection) )
        
        verify {enrolledSections.add(paradigmsSection) }
    }
    
    @Test
    fun addToWaitList_true() {
        every { waitListedSections.add(paradigmsSection) } returns true
        
        assertTrue(schedule.addWaitListedSection(paradigmsSection) )
        
        verify {waitListedSections.add(paradigmsSection) }
    }
    
    @Test
    fun addToWaitList_false() {
        every { waitListedSections.add(paradigmsSection) } returns false
        
        assertFalse(schedule.addWaitListedSection(paradigmsSection) )
        
        verify {waitListedSections.add(paradigmsSection) }
    }
    
    @Test
    fun removeEnrolled_true() {
        every { enrolledSections.remove(paradigmsSection) } returns true
        
        assertTrue(schedule.removeEnrolledSection(paradigmsSection) )
        
        verify {enrolledSections.remove(paradigmsSection) }
    }
    
    @Test
    fun removeEnrolled_false() {
        every { enrolledSections.remove(paradigmsSection) } returns false
        
        assertFalse(schedule.removeEnrolledSection(paradigmsSection) )
        
        verify {enrolledSections.remove(paradigmsSection) }
    }
    
    @Test
    fun removeWaitList_true() {
        every { waitListedSections.remove(paradigmsSection) } returns true
        
        assertTrue(schedule.removeWaitListedSection(paradigmsSection) )
        
        verify {waitListedSections.remove(paradigmsSection) }
    }
    
    @Test
    fun removeWaitList_false() {
        every { waitListedSections.remove(paradigmsSection) } returns false
        
        assertFalse(schedule.removeWaitListedSection(paradigmsSection) )
        
        verify {waitListedSections.remove(paradigmsSection) }
    }
    
    @Test
    fun containsEnrolled_true() {
        every { enrolledSections.contains(paradigmsSection) } returns true
        
        assertTrue(schedule.isEnrolledInSection(paradigmsSection) )
        
        verify {enrolledSections.contains(paradigmsSection) }
    }
    
    @Test
    fun containsEnrolled_false() {
        every { enrolledSections.contains(paradigmsSection) } returns false
        
        assertFalse(schedule.isEnrolledInSection(paradigmsSection) )
        
        verify {enrolledSections.contains(paradigmsSection) }
    }
    
    @Test
    fun containsWaitList_true() {
        every { waitListedSections.contains(paradigmsSection) } returns true
        
        assertTrue(schedule.isWaitListedInSection(paradigmsSection) )
        
        verify {waitListedSections.contains(paradigmsSection) }
    }
    
    @Test
    fun containsWaitList_false() {
        every { waitListedSections.contains(paradigmsSection) } returns false
        
        assertFalse(schedule.isWaitListedInSection(paradigmsSection) )
        
        verify {waitListedSections.contains(paradigmsSection) }
    }
    
    @Test
    fun isEnrolledInCourse_true() {
        val paradigmsCourse = mockk<Course>()
        val algorithmsCourse = mockk<Course>(relaxed = true)
        every { paradigmsSection.course } returns paradigmsCourse
        every { algorithmsSection.course } returns algorithmsCourse
        enrolledSections = mutableSetOf(paradigmsSection, algorithmsSection)
        schedule = Schedule(enrolledSections = enrolledSections)
        
        assertTrue(schedule.isEnrolledInCourse(paradigmsCourse))
    }
    
    @Test
    fun isEnrolledInCourse_false() {
        val paradigmsCourse = mockk<Course>()
        val algorithmsCourse = mockk<Course>()
        every { algorithmsSection.course } returns algorithmsCourse
        enrolledSections = mutableSetOf(algorithmsSection)
        schedule = Schedule(enrolledSections = enrolledSections)
        
        assertFalse(schedule.isEnrolledInCourse(paradigmsCourse))
    }
    
    @Test
    fun isWaitListedInCourse_true() {
        val paradigmsCourse = mockk<Course>()
        val algorithmsCourse = mockk<Course>(relaxed = true)
        every { paradigmsSection.course } returns paradigmsCourse
        every { algorithmsSection.course } returns algorithmsCourse
        waitListedSections = mutableSetOf(paradigmsSection, algorithmsSection)
        schedule = Schedule(waitListedSections = waitListedSections)
        
        assertTrue(schedule.isWaitListedInCourse(paradigmsCourse))
    }
    
    @Test
    fun isWaitListedInCourse_false() {
        val paradigmsCourse = mockk<Course>()
        val algorithmsCourse = mockk<Course>()
        every { algorithmsSection.course } returns algorithmsCourse
        waitListedSections = mutableSetOf(algorithmsSection)
        schedule = Schedule(waitListedSections = waitListedSections)
        
        assertFalse(schedule.isWaitListedInCourse(paradigmsCourse))
    }
}