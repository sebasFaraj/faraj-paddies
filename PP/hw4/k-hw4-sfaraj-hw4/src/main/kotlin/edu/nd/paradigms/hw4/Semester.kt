package edu.nd.paradigms.hw4

/**
 * Represents a particular Semester, which is a combination of a {@link Term} and a year, i.e., SPRING 2024
 * @param term the {@link Term} of the semester
 * @param year the integer year of the semester - must be >= 1950
 */
data class Semester(
    val term: Term,
    val year: Int
) {
    init {
        require(year >= 1950) {"Invalid year: $year - year must be >= 1950"}
    }
}
