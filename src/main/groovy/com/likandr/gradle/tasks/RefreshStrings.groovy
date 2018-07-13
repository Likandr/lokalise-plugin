package com.likandr.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class RefreshStrings extends DefaultTask {
    @Input DefaultTask uploadTask
    @Input DefaultTask downloadTask

    @TaskAction
    def build() {
        println "--------------------------------------------------------------------------------------------------------------"
        println "UploadStrings"
        uploadTask.execute()
        println "--------------------------------------------------------------------------------------------------------------"
        println "DownloadStrings"
        downloadTask.execute()
        println "--------------------------------------------------------------------------------------------------------------"
        println "RefreshStrings done"
    }

}
