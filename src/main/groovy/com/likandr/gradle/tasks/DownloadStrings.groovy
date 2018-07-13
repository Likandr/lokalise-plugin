package com.likandr.gradle.tasks

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException

class DownloadStrings  extends DefaultTask {
    def separator = File.separator
    @Input String lokalise_token
    @Input String lokalise_id
    @Input Project project

    @TaskAction
    def handle() {
        def dirRes = project.android.sourceSets.findByName("main").res.srcDirs.first()

        def nameTempLokaliseDir = "lokalise"
        def locoBuildDir = new File("$project.buildDir.path$separator$nameTempLokaliseDir")
        def zipPath = "$locoBuildDir${separator}lang-file.zip"
        def dirForUnzipped = "$project.buildDir.path$separator$nameTempLokaliseDir${separator}unzipped"

        def listOfPaths = project.fileTree("$dirRes").include("**/strings.xml").files.path
        println listOfPaths

        println "Sending request..."
        def jsonSlurper = new JsonSlurper()

        ///////
//    def response = ['curl', '-X', 'POST', 'https://api.lokalise.co/api/project/export',
//                    '-d', "api_token=" + lokalise_token,
//                    '-d', "id=" + lokalise_id,
//                    '-d', "type=xml"
//    ].execute().text
//    println response
        ///////

        def urlString = "https://api.lokalise.co/api/project/export"
        def queryString = "api_token=${lokalise_token}&id=${lokalise_id}&type=xml"
        def url = new URL(urlString)
        def connection = url.openConnection()
        connection.setRequestMethod("POST")
        connection.doOutput = true

        def writer = new OutputStreamWriter(connection.outputStream)
        writer.write(queryString)
        writer.flush()
        writer.close()
        connection.connect()

        String response = connection.content.text
        println response

        def langsJson = jsonSlurper.parseText(response)
        String filePathUrl = langsJson.bundle.full_file

        if (langsJson.response.code != "200") {
            throw new IllegalStateException(
                    "An error occurred while trying to export from lokalise API: \n\n" +
                            langsJson.toString()
            )
        } else {
            println "Response code 200: " + filePathUrl
        }

        locoBuildDir.mkdirs()
        println "Create temp dirs:"
        println locoBuildDir

        println dirForUnzipped
        def unzippedDir = new File(dirForUnzipped)
        unzippedDir.mkdirs()

        println "Start download zip file..."
        println zipPath
        saveUrlContentToFile(zipPath, filePathUrl)

        println "Unzipping downloaded file into res folder..."
        unzipReceivedZipFile(zipPath, dirRes)

        println "Move unzipped files to res dir..."
        copyToRes(dirForUnzipped, dirRes)

        println "Delete temp dirs for zip file."
        locoBuildDir.deleteDir()
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