package edu.nd.paradigms.hw4

class FinalGradesService {
    /**
     * Stores final grades to each [student&#39;s][Student] [Transcript]. Be aware that this method can also be
     * used to *change* an existing grade. It is **not required** for a professor to upload all final grades
     * at the same time, so not all students enrolled in the section must be present in the finalGrades [Map]
     *
     * @param section the section the final grades are being uploaded for.
     * @param finalGrades a [Map] of [Student]s to their final [Grade] in the class.
     * @throws IllegalArgumentException if any student in finalGrades is not enrolled in the class. In this situation,
     * no post-conditions should occur (that is, no grades uploaded and no schedule changes). Be aware that students
     * on the wait list cannot receive grades in the Section.
     *
     * @see Section.isStudentEnrolled
     * @see Student.addGrade
     */
    fun processFinalGrades(section: Section, finalGrades: Map<Student, Grade>) {
        //validate atomically before side effects
        for ((student, _) in finalGrades) {
            if (!section.isStudentEnrolled(student)) {
                throw IllegalArgumentException("Student is not enrolled in the section: $student")
            }
        }

        //apply grades and clean up schedules
        for ((student, grade) in finalGrades) {
            student.addGrade(section, grade)
            student.removeEnrolledSection(section)
        }
    }
}