package com.likandr.gradle.tasks

import com.likandr.gradle.config.DownloadConfig
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

class DownloadStrings extends DefaultTask {
    def separator = File.separator
    @Input
    String lokalise_token
    @Input
    String lokalise_id
    @Input
    DownloadConfig config
    @Input
    Project project

    @TaskAction
    def handle() {
        def dirRes = project.android.sourceSets.findByName("main").res.srcDirs.first()

        def nameTempLokaliseDir = "lokalise"
        def locoBuildDir = new File("$project.buildDir.path$separator$nameTempLokaliseDir")
        def zipPath = "$locoBuildDir${separator}lang-file.zip"


        // https://lokalise.co/api2docs/curl/#transition-download-files-post

        // curl --request POST \
        //  --url https://api.lokalise.co/api2/projects/{project_id}/files/download \
        //  --header 'content-type: application/json' \
        //  --header 'x-api-token: {project_api_token}' \
        //  --data '{"format":"json","original_filenames":true}'

        def url = new URL("https://api.lokalise.co/api2/projects/${lokalise_id}/files/download")
        def connection = url.openConnection()
        connection.setRequestMethod("POST")
        connection.addRequestProperty("content-type", "application/json")
        connection.addRequestProperty("x-api-token", lokalise_token)
        connection.doOutput = true

        def queryString = """{
                |"format": "xml", 
                |"original_filenames": ${config.originalFilenames},
                |"directory_prefix": "values-%LANG_ISO%",
                |"export_sort": "${config.order.value}",
                |"export_empty_as": "${config.emptyTranslationStrategy.value}"
                |}""".stripMargin()

        println "Sending request\n\t$queryString"
        def writer = new OutputStreamWriter(connection.outputStream)
        writer.write(queryString)
        writer.flush()
        writer.close()
        connection.connect()

        if (connection.getResponseCode() != 200) {
            throw new IllegalStateException(
                    "An error occurred while trying to export from lokalise API: \n\n" +
                            connection.errorStream.text
            )
        }

        String response = connection.inputStream.text

        println "got response\n\t$response"
        def responseJson = new JsonSlurper().parseText(response)
        String bundleUrl = responseJson.bundle_url

        println "Create temp dirs:\n\t$locoBuildDir"
        locoBuildDir.mkdirs()

        println "Start download bundle: $bundleUrl"
        saveUrlContentToFile(zipPath, bundleUrl)

        println "Unzipping downloaded bundle into res folder ($dirRes.path)..."
        unzipReceivedZipFile(zipPath, dirRes)

        println "Delete temp dirs for zip file."
        locoBuildDir.deleteDir()

        println "Done."
    }

    private void saveUrlContentToFile(String zipPath, String filePathUrl) {
        try {
            new File(zipPath).withOutputStream { out ->
                out << new URL(filePathUrl).openStream()
            }
        } catch (Exception exception) {
            project.logger.error("Failed to download translations zip file", exception)
            throw new TaskExecutionException(this, exception)
        }
    }

    private void unzipReceivedZipFile(String fullZipFilePath, dirForUnzipped) {
        project.copy {
            from project.zipTree(new File(fullZipFilePath))
            into dirForUnzipped
        }
    }

    private void copyToRes(dirForUnzipped, dirRes) {
        project.copy {
            from dirForUnzipped
            into dirRes
        }
    }
}
