package edu.nd.paradigms.hw4


/**
 * Represents a lecturer, or professor.
 * @param id a unique integer id for each professor
 * @param netId a unique [String] id of the form abcdefg or abcdef1
 * @param firstName a first name as a [String]
 * @param lastName a last name as a [String]
 */
data class Lecturer(
    val id: Int,
    val netId: String,
    val firstName: String,
    val lastName: String? // mononym lecturers will only have first name
)
