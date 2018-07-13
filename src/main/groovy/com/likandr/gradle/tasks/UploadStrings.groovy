package com.likandr.gradle.tasks

import com.likandr.gradle.MultipartUtility
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.util.regex.Pattern

class UploadStrings extends DefaultTask {
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

        for (String item in listOfPaths) {
            def asd = item.split(Pattern.quote('\\'))
            println asd.toString()

            for (eeee in asd) {
                if (eeee.contains("value")) {
                    def zzz = eeee.split("-")
                    String lang = "en"

                    if (zzz.size() == 2)
                        lang = zzz[1]
                    else
                        lang = getDefaultLang(lokalise_token, lokalise_id)

                    println lang
                    ////
                    String charset = "ISO-8859-1"
                    File   uploadFile1 = new File(item)
                    String requestURL = "https://api.lokalise.co/api/project/import"

                    try {
                        MultipartUtility multipart = new MultipartUtility(requestURL, charset)
                        multipart.addFormField("api_token", lokalise_token)
                        multipart.addFormField("id", lokalise_id)
                        multipart.addFormField("lang_iso", lang)
                        multipart.addFormField("replace", "0")

                        multipart.addFilePart("fileUpload", uploadFile1)

                        List<String> response = multipart.finish()
                        System.out.println("SERVER REPLIED:")
                        for (String line : response) {
                            System.out.println(line)
                        }
                    } catch (IOException ex) {
                        System.out.println("ERROR: " + ex.getMessage())
                        ex.printStackTrace()
                    }

                    ///////
//                    def response = ['curl', '-X', 'POST', 'https://api.lokalise.co/api/project/import',
//                                    '-F', "api_token=" + lokalise_token,
//                                    '-F', "id=" + lokalise_id,
//                                    '-F', "file=@" + item,
//                                    '-F', "replace=0",
//                                    '-F', "lang_iso=" + lang
//                    ].execute().text
//                    println response
                    ///////
                    break
                }
            }
        }
    }

    private String getDefaultLang(lokalise_token, lokalise_id) {
        println "Get default lang from project..."
        def jsonSlurper = new JsonSlurper()

        def headers = [Accept: 'application/json']
        def response = new URL("https://api.lokalise.co/api/language/list?" +
                "api_token=" + lokalise_token +
                "&id=" + lokalise_id).getText(requestProperties: headers)

        ///////
//    def response = ['curl', 'GET', 'https://api.lokalise.co/api/language/list?' +
//            'api_token=' + lokalise_token +
//            '&id=' + lokalise_id
//    ].execute().text
        ///////
        println response

        def langsJson = jsonSlurper.parseText(response)
        def lang = ""
        for (item in langsJson.languages) {
            if (item.is_default == "1")
                lang = item.iso
        }
        println "result: " + lang
        return lang
    }
}