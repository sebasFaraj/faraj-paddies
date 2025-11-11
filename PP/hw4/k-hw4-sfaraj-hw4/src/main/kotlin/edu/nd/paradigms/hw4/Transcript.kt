package edu.nd.paradigms.hw4

/**
 * Represents a student's transcript: for each section the student has taken as well as the grade for that section.
 * @see Student
 * @see Section
 * @see Grade
 * @param history the directly injected section grade history
 */

class Transcript(
    val history: MutableMap<Section, Grade> = mutableMapOf()
) {
    /**
     * Get the grade the student received in a particular section of a course.
     * @param section the section to get the grade for
     * @return the [Grade] the student received.
     * @throws IllegalArgumentException if student has not grade for the section
     */
    fun getGrade(section: Section): Grade {
        require(history.containsKey(section)) {"Transcript doesn't contain section: $section"}
        return history[section]!!
    }
    
    /**
     * Gets the best grade a student has for a course. Used to check {@link Prerequisite}
     * @param course the course to get the grade for.
     * @return the best [Grade] a student has in the course. Returns null if the student hasn't taken any sections of the course
     */
    fun getBestGrade(course: Course): Grade? {
        return history.keys
            .filter { it.course == course }
            .map { history[it]!! }
            .maxByOrNull { it.prerequisiteScore }
    }
    
    /**
     * Adds a section/grade entry to the student's transcript. This method is also used to update an existing grade
     * in this case a grade needs to be changed.
     * @param section the {@link Section} the student is receiving a grade for.
     * @param grade the {@link Grade} the student received
     */
    fun add(section: Section, grade: Grade) {
        history[section] = grade
    }
    
    /**
     * Get the list of Sections the student has taken
     * @return an unmodifiable [Set] of the [Section]s the student has credit for.
     */
    val sections
        get() = history.keys.toSet()
    
    /**
     * Calculates and returns the student's grade point average (GPA)
     */
    val gpa: Double
        get() {
            var gradedCredits = 0
            var creditPoints = 0.0
            for (section in history.keys) {
                if (history[section]!!.gradePoints != GRADE_DOESNT_AFFECT_GPA) {
                    gradedCredits += section.course.creditHours
                    creditPoints += section.course.creditHours * history[section]!!.gradePoints
                }
            }
            return creditPoints/gradedCredits
        }
    
    val isOnProbation
        get() = gpa <= PROBATION_GPA_THRESHOLD
}