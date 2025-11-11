package edu.nd.paradigms.hw4

import io.mockk.MockKAnnotations
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TranscriptTest {
    @RelaxedMockK
    lateinit var history: MutableMap<Section, Grade>
    
    
    /** Section Mocks */
    @MockK
    lateinit var mockSection: Section
    
    @MockK
    lateinit var sectionFundamentalsFail: Section
    
    @MockK
    lateinit var sectionFundamentalsPass: Section
    
    @MockK
    lateinit var sectionDiscrete: Section
    
    @MockK
    lateinit var sectionSystems: Section
    
    
    /** Course mocks */
    @MockK
    lateinit var fundamentals: Course
    
    @MockK
    lateinit var discreteMath: Course
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }
    
    @Test
    fun getGrade() {
        every { history.contains(mockSection) } returns true
        every { history.get(mockSection) } returns Grade.A
        val transcript = Transcript(history)
        
        assertEquals(Grade.A, transcript.getGrade(mockSection))
    }
    
    @Test
    fun getGrade_notPresent() {
        every { history.containsKey(mockSection) } returns false
        val transcript = Transcript(history)
        
        assertFailsWith<IllegalArgumentException> {
            transcript.getGrade(mockSection)
        }
    }
    
    @Test
    fun add() {
        val transcript = Transcript(history)
        
        transcript.add(mockSection, Grade.C)
        
        verify {history.put(mockSection, Grade.C)}
        confirmVerified(history)
    }
    
    @Test
    fun getSections() {
        history = mutableMapOf(mockSection to Grade.B_PLUS)
        val transcript = Transcript(history)
        
        val sectionSet = transcript.sections
        
        assertEquals(1, sectionSet.size)
        assertTrue(sectionSet.contains(mockSection))
    }
    
    @Test
    fun getBestGrade() {
        //setup section history
        history = mutableMapOf(
            sectionFundamentalsFail to Grade.F,
            sectionFundamentalsPass to Grade.B_PLUS,
            sectionDiscrete to Grade.A
        )
        //get course for each mock section
        every {sectionFundamentalsFail.course} returns fundamentals
        every {sectionFundamentalsPass.course} returns fundamentals
        every {sectionDiscrete.course} returns discreteMath
        val transcript = Transcript(history)
        
        val bestGrade = transcript.getBestGrade(fundamentals)
        
        assertEquals(Grade.B_PLUS, bestGrade)
    }
    
    @Test
    fun getBestGrade_notPresent() {
        //setup section history
        history = mutableMapOf(
            sectionFundamentalsFail to Grade.F,
            sectionFundamentalsPass to Grade.B_PLUS
        )
        //get course for each mock section
        every {sectionFundamentalsFail.course} returns fundamentals
        every {sectionFundamentalsPass.course} returns fundamentals
        val transcript = Transcript(history)
        
        val bestGrade = transcript.getBestGrade(discreteMath)
        
        assertNull(bestGrade)
    }
    
    @Test
    fun getGPA() {
        history = mutableMapOf(
            sectionFundamentalsFail to Grade.F,
            sectionFundamentalsPass to Grade.C,
            sectionDiscrete to Grade.A,
            sectionSystems to Grade.PASS
        )
        
        every { sectionFundamentalsFail.course } returns fundamentals
        every { sectionFundamentalsPass.course } returns fundamentals
        every { sectionDiscrete.course} returns discreteMath
        
        every { fundamentals.creditHours } returns 4
        every { discreteMath.creditHours } returns 3
        
        val transcript = Transcript(history)
        val expectedGradePoints = (4 * 0.0) + (4 * 2.0) + (3 * 4.0)
        val expectedGradeCredits = 4 + 4 + 3
        val expectedGPA = expectedGradePoints / expectedGradeCredits
        assertEquals(expectedGPA, transcript.gpa, 1e-14)
    }
}