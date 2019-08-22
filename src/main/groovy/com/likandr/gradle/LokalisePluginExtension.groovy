package com.likandr.gradle

import com.likandr.gradle.config.DownloadConfig
import org.gradle.api.Project

class LokalisePluginExtension {
    private final Project project

    String id
    String token
    DownloadConfig downloadConfig

    LokalisePluginExtension(Project project) {
        this.project = project
    }

    DownloadConfig downloadConfig(Closure closure) {
        downloadConfig = new DownloadConfig()
        project.configure(downloadConfig, closure)
    }
}
