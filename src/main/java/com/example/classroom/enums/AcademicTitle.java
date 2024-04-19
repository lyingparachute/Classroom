package com.example.classroom.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AcademicTitle {
    BACH("", "Bachelor", 6),
    ENG("eng.", "Engineer", 7),
    MGR("mgr", "Master", 3),
    MGR_ENG("mgr eng.", "Master Engineer", 3),
    DR("dr", "Doctor", 3),
    DR_ENG("dr eng.", "Doctor Engineer", 3),
    DR_HAB("dr hab.", "Habilitated Doctor", 3),
    DR_HAB_ENG("dr hab. eng.", "Habilitated Doctor Engineer", 3),
    PROF("prof. dr hab.", "Professor Habilitated Doctor", 3),
    PROF_ENG("prof. dr hab. eng.", "Professor Habilitated Doctor Engineer", 3);

    private final String prefix;
    private final String fullTitle;
    private final Integer numberOfSemesters;
}
