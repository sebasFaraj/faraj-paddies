package edu.nd.paradigms.hw4

class RegistrationService {
    /**
     * Describes the result of [RegistrationService.register]. Either the student was:
     *
     *  * Successfully enrolled in the section
     *  * Successfully waited list in the section
     *  * Could not be enrolled for some reason
     *
     */
    enum class RegistrationResult {
        /**
         * The student was successfully enrolled into the section.
         */
        SUCCESS_ENROLLED,
        
        /**
         * The student was successfully added to the wait list for the section.
         */
        SUCCESS_WAIT_LISTED,
        
        /**
         * Unable to enroll student because the student is already either enrolled or wait listed in at least one
         * section of this course in the same semester as the one they are trying to register for.
         */
        FAILED_ALREADY_IN_COURSE,
        
        /**
         * Unable to enroll because the section is closed
         */
        FAILED_ENROLLMENT_CLOSED,
        
        /**
         * Unable to enroll because both the enrollment and wait list for the section is full
         */
        FAILED_SECTION_FULL,
        
        /**
         * Unable to enroll because the student is either enrolled or wait listed in a class with a time conflict
         */
        FAILED_SCHEDULE_CONFLICT,
        
        /**
         * Unable to enroll because the student does not meet the prerequisites for the class.
         */
        FAILED_PREREQUISITE_NOT_MET,
        
        /**
         * Unable to enroll because this would cause the student to exceed their credit limit (combining the credits
         * that they are enrolled AND waitlisted in).
         */
        FAILED_CREDIT_LIMIT_VIOLATION
    }
    
    /**
     * Attempts to enroll a [Student] in the a [Section]. If the student is successfully added to the
     * enrollment or the wait list for a given section, that student's [Schedule] should also be updated. However,
     * if the student fails to enroll or be wait listed in the section, no changes should occur.
     *
     * @param student the [Student] attempting to enroll in a section.
     * @param section the [Section] the student is attempting to enroll in
     * @return a [RegistrationResult] object indicating success or failure.
     * @see RegistrationResult
     *
     * @see Section.isStudentEnrolled
     * @see Section.isStudentWaitListed
     * @see Section.getEnrollmentStatus
     * @see Section.isEnrollmentFull
     * @see Section.isWaitListFull
     * @see Student.getEnrolledSections
     * @see Student.getWaitListedSections
     * @see Section.overlapsWith
     * @see Prerequisite.isSatisfiedBy
     * @see Student.getCreditLimit
     */
    fun register(student: Student, section: Section): RegistrationResult {
        //already in course (enrolled or waitlisted) for any section of the same course & semester
        val sameTermEnrolled = student.getEnrolledSections().any {
            it.course == section.course && it.semester == section.semester
        }
        val sameTermWaitlisted = student.getWaitListedSections().any {
            it.course == section.course && it.semester == section.semester
        }
        if (sameTermEnrolled || sameTermWaitlisted) {
            return RegistrationResult.FAILED_ALREADY_IN_COURSE
        }

        //enrollment closed
        if (section.getEnrollmentStatus() == EnrollmentStatus.CLOSED) {
            return RegistrationResult.FAILED_ENROLLMENT_CLOSED
        }

        //entire section full (including waitlist)
        if (section.isEnrollmentFull() && section.isWaitListFull()) {
            return RegistrationResult.FAILED_SECTION_FULL
        }

        //schedule conflict with any enrolled or waitlisted class this sem
        val hasConflict = sequenceOf(
            student.getEnrolledSections(),
            student.getWaitListedSections()
        ).flatten().any { s ->
            s.semester == section.semester && s.overlapsWith(section.timeSlot)
        }
        if (hasConflict) {
            return RegistrationResult.FAILED_SCHEDULE_CONFLICT
        }

        //prereqs
        val prereq = section.course.prerequisite
        if (!prereq.isSatisfiedBy(student)) {
            return RegistrationResult.FAILED_PREREQUISITE_NOT_MET
        }

        //credit limit. adding this section consumes its credit hours
        val currentCredits = (student.getEnrolledSections() + student.getWaitListedSections())
            .filter { it.semester == section.semester }
            .sumOf { it.course.creditHours }
        val proposed = currentCredits + section.course.creditHours
        if (proposed > student.getCreditLimit()) {
            return RegistrationResult.FAILED_CREDIT_LIMIT_VIOLATION
        }

        //passed all checks then try to enroll if space, else waitlist
        return if (!section.isEnrollmentFull()) {
            section.addStudentToEnrolled(student)
            student.addEnrolledSection(section)
            RegistrationResult.SUCCESS_ENROLLED
        } else {
            //enrollment full bot overall not section full, meaning waitlist has room
            section.addStudentToWaitList(student)
            student.addWaitListedSection(section)
            RegistrationResult.SUCCESS_WAIT_LISTED
        }
    }
    
    /**
     * Drop a [Student] from either the enrollment or wait list for a given {@list Section}. A successful drop
     * should also be reflected in the [Student]'s [Schedule]. This will also be added to the student's
     * transcript, adding a Grade of [Grade.DROP] for this section to their transcript. (Note that this should be
     * done even if the student already has a grade.)
     *
     * If the student was enrolled (i.e., not waitlisted), and this frees up an empty seat in the class's enrollment,
     * AND the [Section]'s enrollment is still [open][EnrollmentStatus.OPEN], then the first student
     * on the wait list should be removed from the wait list and added to the section's enrollment automatically. That
     * student's schedule should also be updated to reflect the change. However, if enrollment for the section is
     * [closed][EnrollmentStatus.CLOSED], no students should be added to enrollment even if the section is under
     * enrollment capacity.
     *
     * @param student the [Student] attempting to drop from a section's enrollment or wait list
     * @param section the [Section] to drop the student from.
     * @return true if the student was dropped successfully, false if they could not be dropped because they were
     * neither enrolled nor wait listed in the section.
     * @see Section.isStudentEnrolled
     * @see Section.isStudentWaitListed
     * @see Section.getEnrollmentStatus
     * @see Section.isEnrollmentFull
     * @see Section.removeStudentFromEnrolled
     * @see Section.removeStudentFromWaitList
     * @see Section.getFirstStudentOnWaitList
     * @see Student.removeEnrolledSection
     * @see Student.removeWaitListedSection
     */
    fun drop(student: Student, section: Section): Boolean {
        val isEnrolled = section.isStudentEnrolled(student)
        val isWaitlisted = section.isStudentWaitListed(student)

        if (!isEnrolled && !isWaitlisted) {
            return false
        }

        if (isEnrolled) {
            //remove from enrollment + schedule
            section.removeStudentFromEnrolled(student)
            student.removeEnrolledSection(section)

            //record DROP on transcript
            student.addGrade(section, Grade.DROP)

            //auto-promote first waitlisted student if enrollment open and seat available
            if (section.getEnrollmentStatus() == EnrollmentStatus.OPEN && !section.isEnrollmentFull()) {
                //only promote if someone is actually waitlisted
                val waitList = section.getWaitListedStudents()
                if (waitList.isNotEmpty()) {
                    val next = section.getFirstStudentOnWaitList()
                    section.removeStudentFromWaitList(next)
                    section.addStudentToEnrolled(next)
                    next.removeWaitListedSection(section)
                    next.addEnrolledSection(section)
                }
            }

            return true
        } else {
            //was waitlisted: remove from waitlist + schedule
            section.removeStudentFromWaitList(student)
            student.removeWaitListedSection(section)

            //still add DROP to transcript
            student.addGrade(section, Grade.DROP)

            return true
        }
    }
}