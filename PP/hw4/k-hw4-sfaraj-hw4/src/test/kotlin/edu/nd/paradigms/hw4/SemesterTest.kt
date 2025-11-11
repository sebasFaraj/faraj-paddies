package edu.nd.paradigms.hw4

import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals


class SemesterTest {
    lateinit var semester: Semester
    
    @BeforeEach
    fun setup() {
        semester = Semester(Term.FALL, 2023)
    }
    
    @Test
    fun term() {
        assertEquals(Term.FALL, semester.term)
    }
    
    @Test
    fun year() {
        assertEquals(2023, semester.year)
    }
}