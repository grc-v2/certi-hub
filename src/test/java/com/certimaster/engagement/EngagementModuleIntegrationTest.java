package com.certimaster.engagement;

import com.certimaster.CertiMasterApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import static org.assertj.core.api.Assertions.assertThat;

class EngagementModuleIntegrationTest {

    @Test
    void engagementModuleIsRegisteredWithExpectedBasePackage() {
        var modules = ApplicationModules.of(CertiMasterApplication.class);
        var module = modules.getModuleByName("engagement");

        assertThat(module).isPresent();
        assertThat(module.orElseThrow().getBasePackage().getName()).isEqualTo("com.certimaster.engagement");
        modules.verify();
    }
}
