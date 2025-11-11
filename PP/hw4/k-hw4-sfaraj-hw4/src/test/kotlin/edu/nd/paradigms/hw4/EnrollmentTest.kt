package edu.nd.paradigms.hw4


import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EnrollmentTest {
    lateinit var enrollment: Enrollment
    
    @MockK
    lateinit var enrolledStudents: MutableSet<Student>
    
    @MockK
    lateinit var waitListedStudents: MutableList<Student>
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        enrollment = Enrollment(
            245,
            199,
            enrolledStudents,
            waitListedStudents,
            EnrollmentStatus.OPEN
        )
    }
    
    @Test
    fun constructorTest() {
        assertEquals(245, enrollment.enrollmentCapacity)
        assertEquals(199, enrollment.waitListCapacity)
        assertEquals(EnrollmentStatus.OPEN, enrollment.enrollmentStatus)
    }
    
    @Test
    fun isEnrollmentFull_true() {
        every { enrolledStudents.size } returns enrollment.enrollmentCapacity
        
        assertTrue(enrollment.isEnrollmentFull)
    }
    
    @Test
    fun isEnrollmentFull_overCapacity() {
        every { enrolledStudents.size } returns enrollment.enrollmentCapacity + 1
        
        assertTrue(enrollment.isEnrollmentFull)
    }
    
    @Test
    fun isEnrollmentFull_underCapacity() {
        every { enrolledStudents.size } returns enrollment.enrollmentCapacity - 1
        
        assertFalse(enrollment.isEnrollmentFull)
    }
    
    @Test
    fun enrolledSize() {
        every { enrolledStudents.size } returns 123
        assertEquals(123, enrollment.enrolledSize)
    }
    
    @Test
    fun enrollStudent_equivalence() {
        val student = mockk<Student>()
        every { enrolledStudents.size } returns 100
        every { enrolledStudents.contains(student) } returns false
        every { enrolledStudents.add(student) } returns true
        
        enrollment.addStudentToEnrolled(student)
        
        verify { enrolledStudents.add(student) }
    }
    
    @Test
    fun enrollStudent_exception_Closed() {
        val student = mockk<Student>(relaxed = true)
        every { enrolledStudents.size } returns 0
        every { enrolledStudents.contains(student) } returns false
        every { enrolledStudents.add(student) } returns false
        
        enrollment.enrollmentStatus = EnrollmentStatus.CLOSED
        assertFailsWith<IllegalArgumentException> {
            enrollment.addStudentToEnrolled(student)
        }
        verify(exactly = 0) {enrolledStudents.add(student)}
    }
    
    @Test
    fun enrollStudent_exception_Full() {
        val student = mockk<Student>(relaxed = true)
        every { enrolledStudents.size } returns enrollment.enrollmentCapacity
        every { enrolledStudents.contains(student) } returns false
        every { enrolledStudents.add(student) } returns false
        
        assertFailsWith<IllegalArgumentException> {
            enrollment.addStudentToEnrolled(student)
        }
        verify(exactly = 0){ enrolledStudents.add(student) }
    }
    
    @Test
    fun enrollStudent_exception_alreadyEnrolled() {
        val student = mockk<Student>(relaxed = true)
        every { enrolledStudents.size } returns 0
        every { enrolledStudents.contains(student) } returns true
        every { enrolledStudents.add(student) } returns false
        
        assertFailsWith<IllegalArgumentException> {
            enrollment.addStudentToEnrolled(student)
        }
        verify(exactly = 0){ enrolledStudents.add(student) }
    }
    
    @Test
    fun waitListStudent_equivalence() {
        val student = mockk<Student>()
        every { enrolledStudents.size } returns enrollment.enrollmentCapacity
        every { enrolledStudents.contains(student) } returns false
        every { waitListedStudents.size } returns 100
        every { waitListedStudents.contains(student) } returns false
        every { waitListedStudents.add(student) } returns true
        
        enrollment.addStudentToWaitList(student)
        
        verify { waitListedStudents.add(student) }
    }
    
    @Test
    fun waitListedStudent_exception_Closed() {
        val student = mockk<Student>(relaxed = true)
        every { enrolledStudents.size } returns enrollment.enrollmentCapacity
        every { enrolledStudents.contains(student) } returns false
        every { waitListedStudents.size } returns 0
        every { waitListedStudents.contains(student) } returns false
        every { waitListedStudents.add(student) } returns false
        
        enrollment.enrollmentStatus = EnrollmentStatus.CLOSED
        assertFailsWith<IllegalArgumentException> {
            enrollment.addStudentToWaitList(student)
        }
        verify(exactly = 0) {waitListedStudents.add(student)}
    }
    
    @Test
    fun waitListedStudent_exception_Full() {
        val student = mockk<Student>(relaxed = true)
        every { enrolledStudents.size } returns enrollment.enrollmentCapacity
        every { enrolledStudents.contains(student) } returns false
        every { waitListedStudents.size } returns enrollment.waitListCapacity
        every { waitListedStudents.contains(student) } returns false
        every { waitListedStudents.add(student) } returns false
        
        assertFailsWith<IllegalArgumentException> {
            enrollment.addStudentToWaitList(student)
        }
        verify(exactly = 0){ waitListedStudents.add(student) }
    }
    
    @Test
    fun waitListedStudent_exception_EnrollmentNotFull() {
        val student = mockk<Student>(relaxed = true)
        every { enrolledStudents.size } returns enrollment.enrollmentCapacity - 1
        every { enrolledStudents.contains(student) } returns false
        every { waitListedStudents.size } returns 0
        every { waitListedStudents.contains(student) } returns false
        every { waitListedStudents.add(student) } returns false
        
        assertFailsWith<IllegalArgumentException> {
            enrollment.addStudentToWaitList(student)
        }
        verify(exactly = 0){ waitListedStudents.add(student) }
    }
    
    @Test
    fun waitListStudent_exception_alreadyEnrolled() {
        val student = mockk<Student>(relaxed = true)
        every { enrolledStudents.size } returns enrollment.enrollmentCapacity
        every { enrolledStudents.contains(student) } returns true
        every { waitListedStudents.size } returns 0
        every { waitListedStudents.contains(student) } returns false
        every { waitListedStudents.add(student) } returns false
        
        assertFailsWith<IllegalArgumentException> {
            enrollment.addStudentToWaitList(student)
        }
        verify(exactly = 0){ waitListedStudents.add(student) }
    }
    
    @Test
    fun waitListStudent_exception_alreadyWaitListed() {
        val student = mockk<Student>(relaxed = true)
        every { enrolledStudents.size } returns enrollment.enrollmentCapacity
        every { enrolledStudents.contains(student) } returns false
        every { waitListedStudents.size } returns 0
        every { waitListedStudents.contains(student) } returns true
        every { waitListedStudents.add(student) } returns false
        
        assertFailsWith<IllegalArgumentException> {
            enrollment.addStudentToWaitList(student)
        }
        verify(exactly = 0){ waitListedStudents.add(student) }
    }
    
    @Test
    fun isStudentEnrolled_true() {
        val student = mockk<Student>()
        every { enrolledStudents.contains(student) } returns true
        
        assertTrue(enrollment.isStudentEnrolled(student))
    }
    
    @Test
    fun isStudentEnrolled_false() {
        val student = mockk<Student>()
        every { enrolledStudents.contains(student) } returns false
        
        assertFalse(enrollment.isStudentEnrolled(student))
    }
    
    @Test
    fun isStudentWaitListed_true() {
        val student = mockk<Student>()
        every { waitListedStudents.contains(student) } returns true
        
        assertTrue(enrollment.isStudentWaitListed(student))
    }
    
    @Test
    fun isStudentWaitListed_false() {
        val student = mockk<Student>()
        every { waitListedStudents.contains(student) } returns false
        
        assertFalse(enrollment.isStudentWaitListed(student))
    }
    
    @Test
    fun removeStudentFromEnrolled() {
        val student = mockk<Student>()
        every { enrolledStudents.contains(student) } returns true
        every { enrolledStudents.remove(student) } returns true
        
        enrollment.removeEnrolledStudent(student)
        
        verify{ enrolledStudents.remove(student) }
    }
    
    @Test
    fun removeStudentFromEnrolled_exception() {
        val student = mockk<Student>()
        every { waitListedStudents.contains(student) } returns false
        
        assertFailsWith<IllegalArgumentException> { enrollment.removeWaitListedStudent(student) }
        
        verify(exactly = 0){ waitListedStudents.remove(student) }
    }
    
    @Test
    fun removeStudentFromWaitList() {
        val student = mockk<Student>()
        every { enrolledStudents.contains(student) } returns true
        every { enrolledStudents.remove(student) } returns true
        
        enrollment.removeEnrolledStudent(student)
        
        verify{ enrolledStudents.remove(student) }
    }
    
    @Test
    fun removeStudentFromWaitList_exception() {
        val student = mockk<Student>()
        every { waitListedStudents.contains(student) } returns false
        
        assertFailsWith<IllegalArgumentException> { enrollment.removeWaitListedStudent(student) }
        
        verify(exactly = 0){ waitListedStudents.remove(student) }
    }
    
    @Test
    fun isWaitListFull_true() {
        every { waitListedStudents.size } returns enrollment.waitListCapacity
        assertTrue(enrollment.isWaitListFull)
    }
    
    @Test
    fun isWaitListFull_false() {
        every { waitListedStudents.size } returns enrollment.waitListCapacity - 1
        assertFalse(enrollment.isWaitListFull)
    }
    
    @Test
    fun isWaitListFull_true_overCapacity() {
        every { waitListedStudents.size } returns enrollment.waitListCapacity + 1
        assertTrue(enrollment.isWaitListFull)
    }
    
    @Test
    fun setEnrollmentCapacity_valid() {
        enrollment.enrollmentCapacity = 50
        assertEquals(50, enrollment.enrollmentCapacity)
    }
    
    @Test
    fun setEnrollmentCapacity_invalid_negative() {
        val initialCapacity = enrollment.enrollmentCapacity
        assertFailsWith<IllegalArgumentException> {  enrollment.enrollmentCapacity = -1}
        assertEquals(initialCapacity, enrollment.enrollmentCapacity)
    }
    
    @Test
    fun setWaitListCapacity_valid() {
        enrollment.waitListCapacity = 50
        assertEquals(50, enrollment.waitListCapacity)
    }
    
    @Test
    fun setWaitListCapacity_invalid_negative() {
        val initialCapacity = enrollment.waitListCapacity
        assertFailsWith<IllegalArgumentException> {  enrollment.waitListCapacity = -1}
        assertEquals(initialCapacity, enrollment.waitListCapacity)
    }
    
    @Test
    fun getFirstStudentOnWaitList_equivalence() {
        val student = mockk<Student>()
        every { waitListedStudents.isNotEmpty() } returns true
        every { waitListedStudents[0] } returns student
        assertEquals(student, enrollment.getFirstStudentOnWaitList())
    }
    
    @Test
    fun getFirstStudentOnWaitList_exception() {
        every { waitListedStudents.isNotEmpty() } returns false
        assertFailsWith<IllegalArgumentException> {  enrollment.getFirstStudentOnWaitList() }
    }
    
    @Test
    fun isEnrollmentClosed_true() {
        assertFalse(enrollment.isEnrollmentClosed)
    }
    
    @Test
    fun isEnrollmentClosed_false() {
        enrollment.enrollmentStatus = EnrollmentStatus.CLOSED
        assertTrue(enrollment.isEnrollmentClosed)
    }
}