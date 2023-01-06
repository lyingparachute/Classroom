package com.example.classroom.enums;

public enum AcademicTitle {
    NONE("", "Bachelor"),
    ENG("eng.", "Engineer"),
    MGR("mgr", "Master"),
    MGR_ENG("mgr eng.", "Master Engineer"),
    DR("dr", "Doctor"),
    DR_ENG("dr eng.", "Doctor Engineer"),
    DR_HAB("dr hab.", "Habilitated Doctor"),
    DR_HAB_ENG("dr hab. eng.", "Habilitated Doctor Engineer"),
    PROF("prof. dr hab.", "Professor Habilitated Doctor"),
    PROF_ENG("prof. dr hab. eng.", "Professor Habilitated Doctor Engineer");

    private final String prefix;
    private final String fullTitle;

    AcademicTitle(String value, String fullTitle) {
        this.prefix = value;
        this.fullTitle = fullTitle;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getFullTitle() {
        return fullTitle;
    }
}
