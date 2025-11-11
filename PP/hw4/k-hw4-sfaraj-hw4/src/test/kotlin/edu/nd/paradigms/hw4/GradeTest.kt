package edu.nd.paradigms.hw4

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GradeTest {
    @Test
    fun getGradePoints() {
        assertEquals(3.7, Grade.A_MINUS.gradePoints)
    }
    
    @Test
    fun getGradePoints_noPoints() {
        assertEquals(GRADE_DOESNT_AFFECT_GPA, Grade.W.gradePoints)
    }
    
    @Test
    fun getPrerequisiteScore() {
        assertEquals(1, Grade.D_MINUS.prerequisiteScore)
    }
    
    @Test
    fun isWorthCredit_true() {
        assertTrue(Grade.D_MINUS.isWorthCredit)
    }
    
    @Test
    fun isWorthCredit_false() {
        assertFalse(Grade.F.isWorthCredit)
    }
    
    @Test
    fun meetsMinimumRequirement_higher() {
        assertTrue(Grade.B_PLUS.greaterThanOrEqualTo(Grade.B))
    }
    
    @Test
    fun meetsMinimumRequirement_equal() {
        assertTrue(Grade.B.greaterThanOrEqualTo(Grade.B))
    }
    
    @Test
    fun meetsMinimumRequirement_lower() {
        assertFalse(Grade.B_MINUS.greaterThanOrEqualTo(Grade.B))
    }
    
    @Test
    fun testToString_plus() {
        assertEquals("C+", Grade.C_PLUS.toString())
    }
    
    @Test
    fun testToString_minus() {
        assertEquals("C-", Grade.C_MINUS.toString())
    }
}