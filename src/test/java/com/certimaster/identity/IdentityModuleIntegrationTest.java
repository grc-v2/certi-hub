package com.certimaster.identity;

import com.certimaster.CertiMasterApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import static org.assertj.core.api.Assertions.assertThat;

class IdentityModuleIntegrationTest {

    @Test
    void identityModuleIsRegisteredWithExpectedBasePackage() {
        var modules = ApplicationModules.of(CertiMasterApplication.class);
        var module = modules.getModuleByName("identity");

        assertThat(module).isPresent();
        assertThat(module.orElseThrow().getBasePackage().getName()).isEqualTo("com.certimaster.identity");
        modules.verify();
    }
}
