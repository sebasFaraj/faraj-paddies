package edu.nd.paradigms.hw4

/**
 * A list of {@link Section sections} offered in a given {@link Semester}.
 * @see Section
 * @see Semester
 */
class Catalog(
    val semester: Semester,
    sections: MutableSet<Section> = mutableSetOf()
) {
    private val _sections = sections
    /**
     * Get the Sections in this semester
     * @return an immutable copy of the {@link Set} of {@link Section}s in this catalog.
     */
    val sections
        get() = _sections.toSet()
    
    /**
     * Adds section to the Catalog
     * @param section the section to be added
     * @return true if the section is added, but false if the section was already in the catalog
     * @throws IllegalArgumentException if the section is not in the same {@link Semester} as the catalog
     */
    fun add(section: Section): Boolean {
        require(section.semester == semester) {"Section semester: ${section.semester} is different from the Catalog's semester: $semester"}
        return _sections.add(section)
    }
    
    /**
     * Removes a section from the catalog
     * @param section the {@link Section} to be removed
     * @return true if the section was present and was removed. False if the section was not present.
     */
    fun remove(section: Section) = _sections.remove(section)
    
    /**
     * Returns true if the section is in the Catalog
     * @param section the {@link Section} to look for
     * @return true of the section is in the catalog
     */
    fun contains(section: Section) = _sections.contains(section)
    
    /**
     * @return a section with the matching courseRegistrationNumber if one exists. If CRN isn't found, return null
     */
    fun getSectionByCRN(courseRegistrationNumber: Int): Section? =
        _sections.firstOrNull { it.courseRegistrationNumber == courseRegistrationNumber }
}