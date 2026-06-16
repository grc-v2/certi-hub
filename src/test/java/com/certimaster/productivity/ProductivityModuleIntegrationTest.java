package com.certimaster.productivity;

import com.certimaster.CertiMasterApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import static org.assertj.core.api.Assertions.assertThat;

class ProductivityModuleIntegrationTest {

    @Test
    void productivityModuleIsRegisteredWithExpectedBasePackage() {
        var modules = ApplicationModules.of(CertiMasterApplication.class);
        var module = modules.getModuleByName("productivity");

        assertThat(module).isPresent();
        assertThat(module.orElseThrow().getBasePackage().getName()).isEqualTo("com.certimaster.productivity");
        modules.verify();
    }
}
