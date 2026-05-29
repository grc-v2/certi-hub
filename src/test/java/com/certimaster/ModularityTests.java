package com.certimaster;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

    @Test
    void verifyModuleStructure() {
        ApplicationModules.of(CertiMasterApplication.class).verify();
    }

    @Test
    void generateDocumentation() {
        var modules = ApplicationModules.of(CertiMasterApplication.class);
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }
}
