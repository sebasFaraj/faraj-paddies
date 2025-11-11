package edu.nd.paradigms.hw4

class Section(
    val courseRegistrationNumber: Int,
    val sectionNumber: Int,
    val course: Course,
    val semester: Semester,
    val location: Location,
    val timeSlot: TimeSlot,
    val lecturer: Lecturer,
    val enrollment: Enrollment
) {
    init {
        require(1 <= courseRegistrationNumber && courseRegistrationNumber <= 99999) {
            "Invalid CRN: $courseRegistrationNumber - must be between (00001-99999) inclusive"
        }
        require (1 <= sectionNumber && sectionNumber <= 999) {
            "Invalid section number: $sectionNumber - must be between (001-999) inclusive"
        }
        require(enrollment.enrollmentCapacity <= location.roomCapacity) {
            "Enrollment capacity: ${enrollment.enrollmentCapacity} exceeds fire code limit for $location"
        }
    }
    
    fun getEnrollmentCapacity(): Int {
        return enrollment.enrollmentCapacity
    }
    
    /**
     * Changes the enrollmentCapacity of the course. Note that if this number is smaller than the current number of
     * enrolled students, no students will be removed from enrollment. However, no one can add the class while
     * the number of enrolled students is greater than or equal to the capacity.
     * @param enrollmentCapacity the new enrollmentCapacity
     * @throws IllegalArgumentException if the new enrollment capacity is larger than the [Location]'s fire code
     * capacity
     * @see Section.addStudentToEnrolled
     */
    fun setEnrollmentCapacity(enrollmentCapacity: Int) {
        require(enrollmentCapacity >= 0) { "Enrollment Capacity cannot be negative" }
        require(location.roomCapacity >= enrollmentCapacity) {
            "New enrollment capacity: " + enrollmentCapacity +
                " is too large for " + location + ". Cannot change enrollment capacity for: " + this
        }
        enrollment.enrollmentCapacity = enrollmentCapacity
    }
    
    /**
     * Get the current number of enrolled students
     * @return the number of students currently enrolled.
     */
    fun getEnrollmentSize(): Int {
        return enrollment.enrolledSize
    }
    
    fun isEnrollmentFull(): Boolean {
        return enrollment.isEnrollmentFull
    }
    
    /**
     * Returns the set of students enrolled in the section
     * @return an unmodifiable [Set] of students enrolled in the course.
     */
    fun getEnrolledStudents(): Set<Student> {
        return enrollment.enrolledStudents
    }
    
    /**
     * Adds the student to the section enrollment if there is space.
     * @param student the student to add to enrollment
     * @throws IllegalStateException if the section enrollment is already full.
     * @throws IllegalArgumentException if the student is already enrolled in the section.
     */
    fun addStudentToEnrolled(student: Student) {
        enrollment.addStudentToEnrolled(student)
    }
    
    /**
     * Checks if a student is enrolled
     * @param student the [Student]
     * @return true if the student is enrolled, false if wait listed or not enrolled at all.
     */
    fun isStudentEnrolled(student: Student): Boolean {
        return enrollment.isStudentEnrolled(student)
    }
    
    /**
     * Removes the student from the section enrollment.
     * @param student the student to be removed from the section enrollment
     */
    fun removeStudentFromEnrolled(student: Student) {
        enrollment.removeEnrolledStudent(student)
    }
    
    /**
     * Get the waitlisted capacity for the section
     * @return the number of students which can be waitlisted in the course.
     */
    fun getWaitListCapacity(): Int {
        return enrollment.waitListCapacity
    }
    
    /**
     * Get the current number of students on the wait list
     * @return the number of students currently wait listed.
     */
    fun getWaitListSize(): Int {
        return enrollment.waitListSize
    }
    
    /**
     * Checks if the wait list is full
     * @return true if the wait list is full or over capacity.
     */
    fun isWaitListFull(): Boolean {
        return enrollment.isWaitListFull
    }
    
    /**
     * Changes the waitListCapacity of the course. This does not remove students already on the wait list if the
     * capacity is less than the size.
     * @param waitListCapacity the new wait list capacity for the course.
     */
    fun setWaitListCapacity(waitListCapacity: Int) {
        enrollment.waitListCapacity = waitListCapacity
    }
    
    /**
     * Returns the list of students enrolled in the section, in order of their wait list priority
     * @return an unmodifiable [List] of students waitListed in the course.
     */
    fun getWaitListedStudents(): List<Student> {
        return enrollment.waitListedStudents
    }
    
    /**
     * Returns the first student on the wait-list (the next student to be added if space opens up)
     * @return the first student on the wait list.
     */
    fun getFirstStudentOnWaitList(): Student {
        return enrollment.getFirstStudentOnWaitList()
    }
    
    /**
     * Add a student to the wait list if the section enrollment is already full
     * @param student the student to add to the wait list.
     * @throws IllegalStateException if the section's enrollment is not full (that is, the student can enroll directly)
     * OR the wait list is already full.
     * @throws IllegalArgumentException if the student is already enrolled or waitlisted in that section.
     */
    fun addStudentToWaitList(student: Student) {
        enrollment.addStudentToWaitList(student)
    }
    
    /**
     * Checks if a student is wait listed
     * @param student the [Student]
     * @return true if the student is wait-listed, false if enrolled or not enrolled at all.
     */
    fun isStudentWaitListed(student: Student): Boolean {
        return enrollment.isStudentWaitListed(student)
    }
    
    /**
     * Removes a student from the wait list.
     * @param student the student to be removed from the wait list.
     * @throws IllegalArgumentException if the student is not on the wait list.
     */
    fun removeStudentFromWaitList(student: Student) {
        enrollment.removeWaitListedStudent(student)
    }
    
    fun isEnrollmentClosed(): Boolean {
        return enrollment.isEnrollmentClosed
    }
    
    fun getEnrollmentStatus(): EnrollmentStatus {
        return enrollment.enrollmentStatus
    }
    
    fun setEnrollmentStatus(enrollmentStatus: EnrollmentStatus) {
        enrollment.enrollmentStatus = enrollmentStatus
    }
    
    fun overlapsWith(otherTimeSlot: TimeSlot): Boolean {
        return otherTimeSlot.overlapsWith(timeSlot)
    }
    
    
    override fun toString(): String {
        return "Section(courseRegistrationNumber=$courseRegistrationNumber, sectionNumber=$sectionNumber, course=$course)"
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as Section
        
        if (sectionNumber != other.sectionNumber) return false
        if (course != other.course) return false
        if (semester != other.semester) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = sectionNumber
        result = 31 * result + course.hashCode()
        result = 31 * result + semester.hashCode()
        return result
    }
    
    
}