package com.koenigmed.luomanager.presentation.mvp.program

data class ProgramsPresentation(
        var programs: List<MyoProgramPresentation>,
        var userPrograms: List<MyoProgramPresentation>,
        val query: String?) {
    init {
        programs = programs
                .filter {
                    filter(query, it)
                }
                .filter {
                    !userPrograms.contains(it)
                }

        userPrograms = userPrograms.filter {
            filter(query, it)
        }
    }

    private fun filter(query: String?, it: MyoProgramPresentation): Boolean {
        if (query != null) {
            return it.name.toLowerCase().contains(query.trim().toLowerCase())
        }
        return true
    }

    fun filterDeleted(multiSelect: Boolean, programIdsToDelete: MutableSet<String>): Boolean {
        if (!multiSelect) {
            return true
        }
        return userPrograms.find { programIdsToDelete.contains(it.id) } != null
    }
}