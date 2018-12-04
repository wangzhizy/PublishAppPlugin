package com.wangzhi

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PublishAppTask extends DefaultTask {
    PublishAppTask() {
        group = 'wangzhi'
        description = 'publish your app'
        dependsOn("build")
    }

    @TaskAction
    void doAction() {
        def oldApkPath = project.fileTree("${project.buildDir}/outputs/apk/release/").find {
            return it.name.endsWith(".apk")
        }

        def qhJar = project.extensions.publishAppInfo.qihuPath
        def keyStorePath = project.extensions.publishAppInfo.keyStorePath
        def keyStorePass = project.extensions.publishAppInfo.keyStorePass
        def keyStoreKeyAlias = project.extensions.publishAppInfo.keyStoreKeyAlias
        def keyStoreKeyAliasPass = project.extensions.publishAppInfo.keyStoreKeyAliasPass
        execCmd("java -jar ${qhJar} -login 15811354035 wangzhi1")
        execCmd("java -jar ${qhJar}  -importsign ${keyStorePath} ${keyStorePass} ${keyStoreKeyAlias} ${keyStoreKeyAliasPass}")
        execCmd("java -jar ${qhJar} -importmulpkg ${project.extensions.publishAppInfo.channelPath}")
        def outputPath = project.extensions.publishAppInfo.outputPath
        if (!outputPath.endsWith("/")) {
            outputPath += "/"
        }
        def projectName = project.extensions.publishAppInfo.projectName
        def projectVersion = project.extensions.publishAppInfo.projectVersion
        def apkOutputDir = outputPath
        if (projectName){
            apkOutputDir += projectName
        }
        if (projectVersion){
            apkOutputDir += projectVersion
        }
        def tmp = new File(apkOutputDir)
        if (!tmp.exists()) {
            tmp.mkdirs()
        }
        execCmd("java -jar ${qhJar} -jiagu ${oldApkPath} ${apkOutputDir} -autosign  -automulpkg")
        println "加固完成"
    }

    void execCmd(cmd) {
        project.exec {
            executable 'bash'
            args '-c', cmd
        }
    }
}
