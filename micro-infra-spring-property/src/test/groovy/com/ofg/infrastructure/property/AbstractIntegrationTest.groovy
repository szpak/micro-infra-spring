package com.ofg.infrastructure.property

import com.ofg.infrastructure.spock.ClassLevelRestoreSystemProperties
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

@RestoreSystemProperties
@ClassLevelRestoreSystemProperties
abstract class AbstractIntegrationTest extends Specification {

    def setupSpec() {
        System.setProperty(AppCoordinates.CONFIG_FOLDER, findConfigDirInTestResources())
        System.setProperty(AppCoordinates.APP_ENV, "prod")
        System.setProperty("spring.cloud.config.server.enabled", "false")
    }

    private String findConfigDirInTestResources() {
        URL resourceInSrcTestRoot = getClass().getResource("/logback-test.groovy")
        String srcTestResources = new File(resourceInSrcTestRoot.file).parent
        return new File(srcTestResources, 'test-config-dir').absolutePath
    }
}
