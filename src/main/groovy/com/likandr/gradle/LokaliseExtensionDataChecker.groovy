package com.likandr.gradle

class LokaliseExtensionDataChecker {
    static def validateExtensionDataIsCorrect(LokalisePluginExtension extension, String projectName) {

        if (!extension) {
            throw new RuntimeException("Missing 'lokalise' closure in $projectName/build.gradle.")
        }

        if (!extension.id && !extension.token) {
            throw new RuntimeException("Missing 'id' and 'token' property in 'lokalise' closure in $projectName/build.gradle.")
        }

        if (!extension.id) {
            throw new RuntimeException("Missing 'id' property in 'lokalise' closure in $projectName/build.gradle.")
        }

        if (!extension.token) {
            throw new RuntimeException("Missing 'token' property in 'lokalise' closure in $projectName/build.gradle.")
        }
    }
}
