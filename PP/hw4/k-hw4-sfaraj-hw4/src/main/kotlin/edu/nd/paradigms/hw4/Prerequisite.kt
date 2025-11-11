package edu.nd.paradigms.hw4

/**
 * Represents the requirements need to take a [Course].
 * @see Course
 * @see Grade
 * @param requiredCourses a [Map] of [Course]s to minimum required [Grade]s
 */
class Prerequisite(
    private val requiredCourses: MutableMap<Course, Grade> = mutableMapOf()
) {
    fun add(course: Course, minimumGrade: Grade) {
        requiredCourses[course] = minimumGrade
    }
    
    fun getMinimumGrade(course: Course): Grade {
        require(requiredCourses.contains(course)) {"Prerequisites doesn't include course: $course"}
        return requiredCourses[course]!!
    }
    
    fun remove(course: Course) {
        require(requiredCourses.contains(course)) {"Prerequisites doesn't include course: $course"}
        requiredCourses.remove(course)
    }
    
    val prerequisiteCourses
        get() = requiredCourses.keys.toSet()

    fun isSatisfiedBy(student: Student): Boolean {
        for ((course, minimumGrade) in requiredCourses) {
            val bestGrade = student.getBestGrade(course)

            if (bestGrade != null && bestGrade.greaterThanOrEqualTo(minimumGrade))
            {
                continue
            }

            //not satisfied with just transcript, need enrollment
            if (!student.isEnrolledInCourse(course))
            {
                return false
            }

            // check if student is enrolled in any section that doesn't have a transcript entry (currently taking it)
            // they DON'T yet have a transcript entry (i.e., currently taking it).
            val enrolledForCourse = student.getEnrolledSections().filter { it.course == course }

            val transcriptSections = student.getTranscriptSections()

            val enrolledWithoutRecordedGrade = enrolledForCourse.any { enrolledSection ->
                !transcriptSections.contains(enrolledSection)
            }

            if (enrolledWithoutRecordedGrade) {
                //treat as satisfying prerequisite
                continue

            } else {
                //they are enrolled, but have a grade recorded for the enrolled section
                return false
            }
        }
        return true
    }
}
