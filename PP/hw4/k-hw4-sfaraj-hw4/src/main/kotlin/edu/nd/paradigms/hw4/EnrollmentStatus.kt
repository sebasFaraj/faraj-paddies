package edu.nd.paradigms.hw4

/**
 * Represents whether a {@link Section} is open to enrollment.
 */
enum class EnrollmentStatus {
    /**
     * The course can add students to enrollment or wait list
     */
    OPEN,
    
    /**
     * The add deadline has passed, so the course cannot add any more students.
     * Additionally, no students can move from the wait list to the enrolled list.
     */
    CLOSED;
}