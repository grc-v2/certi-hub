package com.certimaster.learning;

import com.certimaster.CertiMasterApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import static org.assertj.core.api.Assertions.assertThat;

class LearningModuleIntegrationTest {

    @Test
    void learningModuleIsRegisteredWithExpectedBasePackage() {
        var modules = ApplicationModules.of(CertiMasterApplication.class);
        var module = modules.getModuleByName("learning");

        assertThat(module).isPresent();
        assertThat(module.orElseThrow().getBasePackage().getName()).isEqualTo("com.certimaster.learning");
        modules.verify();
    }
}
