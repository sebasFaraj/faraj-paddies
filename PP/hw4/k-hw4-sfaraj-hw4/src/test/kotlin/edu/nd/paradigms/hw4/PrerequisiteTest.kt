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

class PrerequisiteTest {
    lateinit var prerequisite: Prerequisite
    
    @RelaxedMockK
    lateinit var requiredCourses: MutableMap<Course, Grade>
    
    @MockK
    lateinit var fundamentals: Course
    
    @MockK
    lateinit var discreteMath: Course
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        prerequisite = Prerequisite(requiredCourses)
    }
    
    @Test
    fun add() {
        every { requiredCourses.put(fundamentals, Grade.C_MINUS) } returns null
        
        prerequisite.add(fundamentals, Grade.C_MINUS)
        
        verify{ requiredCourses.put(fundamentals, Grade.C_MINUS) }
    }
    
    @Test
    fun getMinimumGrade() {
        every { requiredCourses.containsKey(fundamentals) } returns true
        every { requiredCourses[fundamentals] } returns Grade.C_MINUS
        
        assertEquals(Grade.C_MINUS, prerequisite.getMinimumGrade(fundamentals))
    }
    
    @Test
    fun getMinimumGrade_exception() {
        every { requiredCourses.containsKey(fundamentals) } returns false
        
        assertFailsWith<IllegalArgumentException>{ prerequisite.getMinimumGrade(fundamentals) }
    }
    
    @Test
    fun remove() {
        every { requiredCourses.containsKey (fundamentals) } returns true
        every { requiredCourses.remove(fundamentals) } returns Grade.C_MINUS
        
        prerequisite.remove(fundamentals)
        
        verify { requiredCourses.remove(fundamentals) }
    }
    
    @Test
    fun remove_exception() {
        every { requiredCourses.containsKey (fundamentals) } returns false
        every { requiredCourses.remove(fundamentals) } returns null
        
        assertFailsWith<IllegalArgumentException> {  prerequisite.remove(fundamentals) }
        
        verify(exactly = 0) { requiredCourses.remove(fundamentals) }
    }
    
    @Test
    fun getPrerequisiteCourses() {
        requiredCourses = mutableMapOf(discreteMath to Grade.C_MINUS, fundamentals to Grade.C_MINUS)
        prerequisite = Prerequisite(requiredCourses)
        
        val prerequisiteCourses = prerequisite.prerequisiteCourses
        
        assertEquals(2, prerequisiteCourses.size)
        assertTrue(prerequisiteCourses.contains(discreteMath))
        assertTrue(prerequisiteCourses.contains(fundamentals))
    }
    
    @Test
    fun isSatisfiedBy_true() {
        val student = mockk<Student>()
        every { student.getBestGrade(fundamentals) } returns Grade.A
        every { student.getBestGrade(discreteMath) } returns Grade.C_MINUS
        
        requiredCourses = mutableMapOf(discreteMath to Grade.C_MINUS, fundamentals to Grade.C_MINUS)
        prerequisite = Prerequisite(requiredCourses)
        
        assertTrue(prerequisite.isSatisfiedBy(student))
    }
    
    @Test
    fun isSatisfiedBy_false_lowGrade() {
        val student = mockk<Student>()
        every { student.getBestGrade(fundamentals) } returns Grade.A
        every { student.getBestGrade(discreteMath) } returns Grade.D_PLUS

        every { student.isEnrolledInCourse(fundamentals) } returns false
        every { student.isEnrolledInCourse(discreteMath) } returns false
        
        requiredCourses = mutableMapOf(discreteMath to Grade.C_MINUS, fundamentals to Grade.C_MINUS)
        prerequisite = Prerequisite(requiredCourses)
        
        assertFalse(prerequisite.isSatisfiedBy(student))
    }
    
    @Test
    fun isSatisfiedBy_missing_class() {
        val student = mockk<Student>()
        every { student.getBestGrade(fundamentals) } returns null
        every { student.getBestGrade(discreteMath) } returns Grade.A

        every { student.isEnrolledInCourse(fundamentals) } returns false
        every { student.isEnrolledInCourse(discreteMath) } returns false
        
        requiredCourses = mutableMapOf(discreteMath to Grade.C_MINUS, fundamentals to Grade.C_MINUS)
        prerequisite = Prerequisite(requiredCourses)
        
        assertFalse(prerequisite.isSatisfiedBy(student))
    }

    @Test
    fun isSatisfiedBy_true_enrolledNoRecordedGrade() {
        //prereq: Fundamentals C-
        requiredCourses = mutableMapOf(fundamentals to Grade.C_MINUS)
        prerequisite = Prerequisite(requiredCourses)

        val student = mockk<Student>()

        //no sufficient transcript grade yet
        every { student.getBestGrade(fundamentals) } returns null

        //student is currently enrolled in Fundamentals
        every { student.isEnrolledInCourse(fundamentals) } returns true

        //mock a section of Fundamentals they are enrolled in
        val currentSection = mockk<Section>()
        every { currentSection.course } returns fundamentals
        every { student.getEnrolledSections() } returns setOf(currentSection)

        //transcript has no recorded grade yet
        every { student.getTranscriptSections() } returns emptySet()

        assertTrue(prerequisite.isSatisfiedBy(student))
    }

    @Test
    fun isSatisfiedBy_false_enrolledButRecordedGradeForThatSection() {
        //prereq: Discrete Math with min C-
        requiredCourses = mutableMapOf(discreteMath to Grade.C_MINUS)
        prerequisite = Prerequisite(requiredCourses)

        val student = mockk<Student>()
        //best grade is insufficient or missing
        every { student.getBestGrade(discreteMath) } returns Grade.D_PLUS

        //student is currently in Discrete
        every { student.isEnrolledInCourse(discreteMath) } returns true

        //enrolled in one section of Discrete
        val currentSection = mockk<Section>()
        every { currentSection.course } returns discreteMath
        every { student.getEnrolledSections() } returns setOf(currentSection)

        //and transcript already has a grade entry for section
        every { student.getTranscriptSections() } returns setOf(currentSection)

        //since recorded grade already exists, fallback to larger grade(D+ < C-)
        assertFalse(prerequisite.isSatisfiedBy(student))
    }

    @Test
    fun isSatisfiedBy_true_multipleCourses_mixedSources() {
        //prereqs: Fundamentals (min C-) and Discrete Math (min C-)
        requiredCourses = mutableMapOf(fundamentals to Grade.C_MINUS, discreteMath to Grade.C_MINUS)
        prerequisite = Prerequisite(requiredCourses)

        val student = mockk<Student>()

        //fundamentals satisfied by transcript
        every { student.getBestGrade(fundamentals) } returns Grade.A

        //No grade yet, enrolled without a recorded grade
        every { student.getBestGrade(discreteMath) } returns null
        every { student.isEnrolledInCourse(discreteMath) } returns true

        val discSection = mockk<Section>()
        every { discSection.course } returns discreteMath

        //student is enrolled in section, not in transcript
        every { student.getEnrolledSections() } returns setOf(discSection)
        every { student.getTranscriptSections() } returns emptySet()

        assertTrue(prerequisite.isSatisfiedBy(student))
    }
}