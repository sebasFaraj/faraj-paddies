package edu.nd.paradigms.hw4

/**
 * Represents a student's schedule, including their enrolled and waitlisted {@link Section Sections}.
 * @see Student
 */
class Schedule(
    enrolledSections: MutableSet<Section> = mutableSetOf(),
    waitListedSections: MutableSet<Section> = mutableSetOf()
) {
    private val _enrolledSections: MutableSet<Section> = enrolledSections
    /**
     * Get the sections the student is enrolled in
     * @return An unmodifiable {@link Set} of {@link Section Sections} the student is enrolled in.
     */
    val enrolledSections: Set<Section>
        get() = _enrolledSections.toSet()
    
    private val _waitListedSections: MutableSet<Section> = waitListedSections
    
    /**
     * Get the sections the student is wait-listed in
     * @return An unmodifiable {@link Set} of {@link Section Sections} the student is waitlisted in.
     */
    val waitListedSections: Set<Section>
        get() = _waitListedSections.toSet()
    
    /**
     * Adds a section to the students schedule of enrolled sections
     * @param section the section to be added to the students enrolled sections..
     * @return true if the section was added to the enrolled sections. False if the student was already enrolled in
     * that section.
     */
    fun addEnrolledSection(section: Section) = _enrolledSections.add(section)
    
    /**
     * Remove a section from the student's schedule of enrolled sections.
     * @param section the section to be removed from the enrolled sections.
     * @return true if the sections was removed. False if the student was not enrolled in that section.
     */
    fun removeEnrolledSection(section: Section) = _enrolledSections.remove(section)
    
    /**
     * Returns true if the student is enrolled in the section, false otherwise
     */
    fun isEnrolledInSection(section: Section) = _enrolledSections.contains(section)
    
    /**
     * Returns true if the student is enrolled in any {@link Section} that is an offering of course
     * @param course a {@link Course}
     * @return true if the student is enrolled in a {@link Section} associated with the course.
     */
    fun isEnrolledInCourse(course: Course) = _enrolledSections.map { it.course }.any { it == course }
    
    /**
     * Adds a section to the student's schedule of waitlisted courses
     * @param section the section to be added to the waitlist of.
     * @return true if the section was added to the waitlisted sections. False if the student was already waitlisted in
     * that section.
     */
    fun addWaitListedSection(section: Section) = _waitListedSections.add(section)
    
    /**
     * Remove a section from the student's schedule of waitlisted sections.
     * @param section the section to be removed from the waitlisted sections.
     * @return true if the sections was removed. False if the student was not waitlisted in that section.
     */
    fun removeWaitListedSection(section: Section) = _waitListedSections.remove(section)
    
    /**
     * Returns true if the student is waitlisted in any {@link Section} that is an offering of the course
     * @param course a [Course]
     * @return true if the student is enrolled in a {@link Section} associated with the course.
     */
    fun isWaitListedInSection(section: Section) = _waitListedSections.contains(section)
    
    /**
     * Returns true if the student is waitListed in the section, false otherwise
     */
    fun isWaitListedInCourse(course: Course) = _waitListedSections.map { it.course }.any { it == course}
}