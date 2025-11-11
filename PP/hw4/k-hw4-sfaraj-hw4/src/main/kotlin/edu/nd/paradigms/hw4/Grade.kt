package edu.nd.paradigms.hw4

/**
 * Represents a Grade a student received in a class.
 * @param gradePoints - The number of gradePoints the grade is worth. Set to {@link Double#NaN} if the grade doesn't affect GPA.
 * @param prerequisiteScore - A comparison score as related to prerequisites. Specifically, a grade is "as good or better"
 *      than another grade with relation to prerequisites if the comparison score is greater than or equal to another.
 * @param isWorthCredit - Whether the grade is worth graduation credit. If false, this grade does not give graduation credit.
 */


enum class Grade(
    val gradePoints: Double,
    val prerequisiteScore: Int,
    val isWorthCredit: Boolean
) {
    A_PLUS(4.0, 12, true),
    A(4.0, 11, true),
    A_MINUS(3.7, 10, true),
    B_PLUS(3.3, 9, true),
    B(3.0, 8, true),
    B_MINUS(2.7, 7, true),
    C_PLUS(2.3, 6, true),
    C(2.0, 5, true),
    C_MINUS(1.7, 4, true),
    D_PLUS(1.3, 3, true),
    D(1.0, 2, true),
    D_MINUS(0.7, 1, true),
    F(0.0, -1, false),
    DROP(GRADE_DOESNT_AFFECT_GPA, -1, false),
    W(GRADE_DOESNT_AFFECT_GPA, -1, false),
    PASS(GRADE_DOESNT_AFFECT_GPA, 2, true),
    FAIL(GRADE_DOESNT_AFFECT_GPA, -1, false);
    
    fun greaterThanOrEqualTo(minimumRequirement: Grade): Boolean =
         (this.prerequisiteScore - minimumRequirement.prerequisiteScore) >= 0

    override fun toString(): String {
        var name = this.name
        name = name.replace("_PLUS", "+")
        name = name.replace("_MINUS", "-")
        return name;
    }
    
    companion object {
    
    }
}