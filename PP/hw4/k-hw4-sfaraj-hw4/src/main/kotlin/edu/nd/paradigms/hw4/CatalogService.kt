package edu.nd.paradigms.hw4

class CatalogService(
    val catalog: Catalog
) {
    enum class AddSectionResult {
        /**
         * The section was successfully added to the Catalog
         */
        SUCCESSFUL,
        /**
         * The second could not be added because it's semester doesn't match the catalogs
         */
        FAILED_SEMESTER_MISMATCH,
        /**
         * Section not added to catalog, as it already exists in the catalog.
         */
        FAILED_SECTION_ALREADY_EXISTS,
        /**
         * Section not added because the CRN is already in-use by another course.
         */
        FAILED_CRN_CONFLICT,
        /**
         * Section not added to catalog, as the TimeSlot of this section conflicts with the time slot of another
         * section in the same location.
         */
        FAILED_LOCATION_CONFLICT,
        /**
         * Section not added to catalog, the lecturer is already teaching another class that conflicts with this
         * section's timeslot.
         */
        FAILED_LECTURER_CONFLICT,
        /**
         * At the time the section is added to the catalog, it's enrollment and wait-list must be empty (that is,
         * no students registered ahead of time).
         */
        FAILED_ENROLLMENT_NOT_EMPTY
    }
    
    /**
     * Attempts to add a section to the course catalog, ensuring the section doesn't break any rules.
     * The rules are:<br>
     * <ol>
     *     <li>A section can only be added to a catalog <b>if the semesters match!</b> If the section semester and
     *     the catalog semester do not match, then return {@link AddSectionResult#FAILED_SEMESTER_MISMATCH}</li>
     *     <li>A section can only be added to the catalog once. No duplicate sections. If the section is already
     *     present, then return {@link AddSectionResult#FAILED_SECTION_ALREADY_EXISTS}</li>
     *     <li>CRNs in a given catalog must be unique. If the CRN is not unique, this returns
     *     {@link AddSectionResult#FAILED_CRN_CONFLICT}</li>
     *     <li>No two sections can use the same {@link Location} in overlapping {@link TimeSlot}s. If this rule would
     *     be violated, return {@link AddSectionResult#FAILED_LOCATION_CONFLICT}</li>
     *     <li>No lecturer can teach two sections with overlapping {@link TimeSlot}s. If this rule would be
     *     violated, return {@link AddSectionResult#FAILED_LECTURER_CONFLICT}</li>
     *     <li>At the time a course is added to the catalog, it must have no students already registred for it. That
     *     is, the enrollment and waitlist must be empty. If this is violated, return
     *     {@link AddSectionResult#FAILED_ENROLLMENT_NOT_EMPTY}</li>
     * </ol>
     * If any of the above rules are violated, the section <b>should not</b> be added to the catalog. However, if none
     * of the rules are violated, the section <b>should</b> be added to the catalog, and {@link AddSectionResult#SUCCESSFUL}
     * should be returned.
     * @param section the section to attempt to add to catalog
     * @return a {@link AddSectionResult} enum indicating success or the reason for failure
     * @see Section.semester
     * @see Catalog.semester
     * @see Catalog.contains
     * @see Catalog.getSectionByCRN
     * @see Location.equals
     * @see Section.overlapsWith
     * @see Lecturer.equals
     * @see Section.getEnrollmentSize
     * @see Section.getWaitListSize
     */
    fun add(section: Section): AddSectionResult {
        //semester must match
        if (section.semester != catalog.semester) {
            return AddSectionResult.FAILED_SEMESTER_MISMATCH
        }

        //section must not already exist in catalog
        if (catalog.contains(section)) {
            return AddSectionResult.FAILED_SECTION_ALREADY_EXISTS
        }

        //CRN must be unique within catalog
        val existingByCrn = catalog.getSectionByCRN(section.courseRegistrationNumber)
        if (existingByCrn != null) {
            return AddSectionResult.FAILED_CRN_CONFLICT
        }

        //no location/time conflicts
        for (existing in catalog.sections) {
            if (existing.location == section.location && existing.overlapsWith(section.timeSlot)) {
                return AddSectionResult.FAILED_LOCATION_CONFLICT
            }
        }

        //no lecturer/time conflicts
        for (existing in catalog.sections) {
            if (existing.lecturer == section.lecturer && existing.overlapsWith(section.timeSlot)) {
                return AddSectionResult.FAILED_LECTURER_CONFLICT
            }
        }

        //enrollment and waitlist must be empty at add time
        if (section.getEnrollmentSize() > 0 || section.getWaitListSize() > 0) {
            return AddSectionResult.FAILED_ENROLLMENT_NOT_EMPTY
        }

        //passed all checks → add to catalog
        catalog.add(section)
        return AddSectionResult.SUCCESSFUL
    }
    
    /**
     * Remove a section from the course catalog. This should also remove the section from any of the enrolled/wait-list
     * Student's schedules.
     * @param section the section to be removed
     * @throws IllegalArgumentException if the section is not present in the catalog
     * @see Catalog.contains
     * @see Catalog.remove
     * @see Section.getEnrolledStudents
     * @see Section.getWaitListedStudents
     * @see Student.removeEnrolledSection
     * @see Student.removeWaitListedSection
     */
    fun remove(section: Section) {
        //check if exists
        if (!catalog.contains(section)) {
            throw IllegalArgumentException("Section not found in catalog: $section")
        }

        //remove the section from every student's schedule for both enrolled and waitlisted
        for (student in section.getEnrolledStudents()){
            student.removeEnrolledSection(section)
        }

        for (student in section.getWaitListedStudents()){
            student.removeWaitListedSection(section)
        }

        catalog.remove(section)
    }
    
    /**
     * Set all sections to closed enrollment. This method will be automatically called at the add deadline.
     * @see Section.setEnrollmentStatus
     * @see EnrollmentStatus
     */
    fun closeAllSections() {
        for (section in catalog.sections) {
            section.setEnrollmentStatus(EnrollmentStatus.CLOSED)
        }
    }
}