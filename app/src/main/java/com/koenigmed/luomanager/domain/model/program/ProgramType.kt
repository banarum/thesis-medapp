package com.koenigmed.luomanager.domain.model.program

enum class ProgramType(val number: Int) {
    MANUAL(1),
    LITE(2),
    STANDARD(3),
    HARD(4);

    fun isSchedule() = this != MANUAL

    companion object {
        fun fromNumber(number: Int?): ProgramType {
            return ProgramType.values().find { it.number == number } ?: MANUAL
        }
    }
}