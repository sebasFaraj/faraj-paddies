package edu.nd.paradigms.hw4

import java.util.*

class Student(
    val id: Long,
    val netId: String,
    var firstName: String,
    var lastName: String?,
    var year: Int,
    val transcript: Transcript = Transcript(),
    val schedule: Schedule = Schedule()
) {
    
    /**
     * Get the set of enrolled sections.
     * @return an immutable copy of the students enrolled sections as a [Set]
     */
    fun getEnrolledSections(): Set<Section> = schedule.enrolledSections
    
    /**
     * Add a section to the student's enrolled sections
     * @return false if the student is already enrolled in the section. True otherwise.
     */
    fun addEnrolledSection(section: Section): Boolean = schedule.addEnrolledSection(section)
    
    /**
     * Removes a section from the student's enrolled sections
     * @param section The [Section] to be removed
     * @return false if the student was not enrolled in the section. True otherwise.
     */
    fun removeEnrolledSection(section: Section): Boolean = schedule.removeEnrolledSection(section)
    
    /**
     * Check if student is enrolled in a particular section
     * @param section the [Section] to check
     * @return true if the student is enrolled, false if not
     */
    fun isEnrolledInSection(section: Section): Boolean = schedule.isEnrolledInSection(section)
    
    fun isEnrolledInCourse(course: Course): Boolean = schedule.isEnrolledInCourse(course)
    
    /**
     * Get the set of wait listed sections.
     * @return an immutable copy of the students wait listed sections as a [Set]
     */
    fun getWaitListedSections(): Set<Section> = schedule.waitListedSections
    
    /**
     * Add a section to the student's wait listed sections
     * @return false if the student is already enrolled in the section. True otherwise.
     */
    fun addWaitListedSection(section: Section): Boolean = schedule.addWaitListedSection(section)
    
    /**
     * Remove a section to the student's wait listed sections.
     * @param section the [Section] to remove
     * @return false if the student was not enrolled in the section. True otherwise.
     */
    fun removeWaitListedSection(section: Section): Boolean = schedule.removeWaitListedSection(section)
    
    /**
     * Check if student is wait listed in a particular section
     * @param section the [Section] to check
     * @return true if the student is wait listed in that section, false if not
     */
    fun isWaitListedInSection(section: Section): Boolean = schedule.isWaitListedInSection(section)
    
    /**
     * Get the grade the student received in section
     * @param section a [Section] a student has received a grade for.
     * @throws IllegalArgumentException if the student hasn't taken a section.
     */
    fun getGrade(section: Section): Grade = transcript.getGrade(section)
    
    /**
     * Gets the best grade a student has for a course. Used to check [Prerequisite]
     * @param course the course to get the grade for.
     * @return An [Optional] of a [Grade]. Returns [Optional.empty] if the student hasn't taken the
     * course.
     */
    fun getBestGrade(course: Course) = transcript.getBestGrade(course)
    
    /**
     * Add a grade to the student's transcript
     * @param section the [Section] the student received the grade in
     * @param grade the [Grade] received
     */
    fun addGrade(section: Section?, grade: Grade?) {
        transcript.add(section!!, grade!!)
    }
    
    /**
     * Get the list of Sections the student has taken
     * @return an unmodifiable [Set] of the [Sections][Section] the student has credit for.
     */
    fun getTranscriptSections(): Set<Section> = transcript.sections
    
    /**
     * Get the student's current GPA, or grade point average.
     * @return the student's GPA as a double
     */
    fun getGPA(): Double = transcript.gpa
    
    /**
     * Determine if the student's GPA puts them on probation.
     * @return true if the student's GPA is below the probation threshold, false otherwise.
     * @see Transcript.PROBATION_GPA_THRESHOLD
     */
    fun isOnProbation(): Boolean = transcript.isOnProbation
    
    /**
     * Gets the students credit limit (the maximum number of credits they can be enrolled and waitlisted in). Student's
     * on Probation receive a reduced credit limit.
     * @return the credit limit as an int
     */
    fun getCreditLimit(): Int {
        return if (isOnProbation()) {
            PROBATION_CREDIT_LIMIT
        } else DEFAULT_CREDIT_LIMIT
    }
    
    override fun toString(): String {
        return "Student(id=$id, netId='$netId', firstName='$firstName', lastName=$lastName, year=$year)"
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as Student
        
        return id == other.id
    }
    
    override fun hashCode(): Int {
        return id.hashCode()
    }
}