package edu.nd.paradigms.hw4

class Enrollment(
    initialEnrollmentCapacity: Int,
    initialWaitListCapacity: Int,
    enrolledStudents: MutableSet<Student> = mutableSetOf(),
    waitListedStudents: MutableList<Student> = mutableListOf(),
    initialEnrollmentStatus: EnrollmentStatus = EnrollmentStatus.OPEN
) {
    var enrollmentStatus = initialEnrollmentStatus
    var enrollmentCapacity = initialEnrollmentCapacity
        set(value) {
            require(value >= 0){"Enrollment capacity cannot be negative"}
            field = value
        }
    
    private val _enrolledStudents = enrolledStudents
    /**
     * Returns the set of students enrolled in the section
     * @return an unmodifiable [Set] of students enrolled in the course.
     */
    val enrolledStudents
        get() = _enrolledStudents.toSet()
    
    private val _waitListedStudents = waitListedStudents
    /**
     * Returns the set of students wait listed in the section
     * @return an unmodifiable [List] of students enrolled in the course in-order of wait list priority
     */
    val waitListedStudents
        get() = _waitListedStudents.toList()
    
    var waitListCapacity = initialWaitListCapacity
        set(value) {
            require(value >= 0){"Wait list capacity cannot be negative"}
            field = value
        }
    
    val isEnrollmentClosed
        get() = enrollmentStatus == EnrollmentStatus.CLOSED
    
    val isEnrollmentFull
        get() = _enrolledStudents.size >= enrollmentCapacity
    
    /**
     * Checks if the wait list is full
     * @return true if the wait list is full or over capacity.
     */
    val isWaitListFull
        get() = _waitListedStudents.size >= waitListCapacity
    
    val enrolledSize
        get() = _enrolledStudents.size
    
    
    val waitListSize
        get() = _waitListedStudents.size
    
    /**
     * Adds the student to the section enrollment if there is space.
     * @param student the student to add to enrollment
     * @throws IllegalStateException if the section enrollment is already full.
     * @throws IllegalArgumentException if the student is already enrolled in the section.
     */
    fun addStudentToEnrolled(student: Student){
        require(!isEnrollmentClosed) {"Enrollment closed"}
        require(!isEnrollmentFull) {"Enrollment full. Cannot add student: $student to enrollment for $this"}
        require(!_enrolledStudents.contains(student)) {"Student already enrolled in section."}
        _enrolledStudents.add(student)
    }
    
    /**
     * Add a student to the wait list if the section enrollment is already full
     * @param student the student to add to the wait list.
     * @throws IllegalArgumentException if the section's enrollment is not full (that is, the student can enroll directly)
     * OR the wait list is already full.
     * @throws IllegalArgumentException if the student is already enrolled or waitlisted in that section.
     */
    fun addStudentToWaitList(student: Student){
        require(!isEnrollmentClosed) {"Enrollment closed"}
        require(!isWaitListFull) {"Wait list full. Cannot add student: $student to enrollment for $this"}
        require(isEnrollmentFull) { "Enrollment not full. Cannot add student: $student to wait list for $this"}
        require(!_enrolledStudents.contains(student)) {"Student is already enrolled and can't be wait listed "}
        require(!_waitListedStudents.contains(student)) {"Student already enrolled in section. "}
        _waitListedStudents.add(student)
    }
    
    /**
     * Checks if a student is enrolled
     * @param student the [Student]
     * @return true if the student is enrolled, false if not enrolled.
     */
    fun isStudentEnrolled(student: Student) = _enrolledStudents.contains(student)
    
    /**
     * Checks if a student is waitlisted
     * @param student the [Student]
     * @return true if the student is wait listed, false if wait listed.
     */
    fun isStudentWaitListed(student: Student) = _waitListedStudents.contains(student)
    
    /**
     * Removes the student from the section enrollment.
     * @param student the student to be removed from the section enrollment
     */
    fun removeEnrolledStudent(student: Student){
        require(_enrolledStudents.contains(student)) {"Student $student is not enrolled for $this"}
        _enrolledStudents.remove(student)
    }
    
    fun removeWaitListedStudent(student: Student){
        require(_waitListedStudents.contains(student)) {"Student $student is not wait listed for $this"}
        _waitListedStudents.remove(student)
    }
    
    fun getFirstStudentOnWaitList(): Student {
        require(!_waitListedStudents.isNotEmpty()) { "Wait List is empty" }
        return _waitListedStudents[0]
    }
}