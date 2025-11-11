package edu.nd.paradigms.hw4

/**
 * Represents a course, such as "CS 3140 - Software Development Essentials". Note that this describes the course
 * as a whole, not an individual offering. Individual offerings are described in {@link Section}.
 *
 * @see Section Section: an offering of a Course
 * @see Prerequisite Prerequisite: a collection of required minimum grades in specific courses need to take a Course
 * @param id the course's id
 * @param mnemonic the course mnemonic (i.e., "CSE") as a {@link String}
 * @param courseNumber the course number (i.e., "30332") as a {@link String}
 * @param title the course's name (i.e., "Software Development Essentials") as a {@link String}
 * @param creditHours the number of credit hours the course is worth
 * @param prerequisite a {@link Prerequisite} that includes the required courses (and their minimum grades)
 *        to take this course
 */
class Course(
    val id: Int,
    val mnemonic: String,
    val courseNumber: Int,
    val title: String,
    val creditHours: Int,
    val prerequisite: Prerequisite = Prerequisite()
) {
    init {
        require(id > 0) {"Course id must be > 0"}
        require(3 <= mnemonic.length && mnemonic.length <= 4) {"Course Mnemonic (such as CSE and MSCE) must be between 3 and 4 characters in length"}
        require(title.isNotEmpty()){"Course title cannot be empty"}
        require(creditHours >= 0){"A course cannot have negative credit hours"}
    }
    
    override fun toString(): String {
        return "Course(id=$id, mnemonic='$mnemonic', courseNumber=$courseNumber, title='$title', creditHours=$creditHours, prerequisite=$prerequisite)"
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as Course
        
        if (courseNumber != other.courseNumber) return false
        if (mnemonic != other.mnemonic) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = courseNumber
        result = 31 * result + mnemonic.hashCode()
        return result
    }
    
    
}