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
        println '打包成功 正在复制APK...'
        def apkDir = project.fileTree("${project.buildDir}/outputs/apk/release/")
        String targetDir = project.extensions.publishAppInfo.qihuPath
        if (!targetDir.endsWith("/")) {
            targetDir += "/"
        }
        String apkName = project.extensions.publishAppInfo.apkName
        if (apkName == null || "".equalsIgnoreCase(apkName)) {
            apkName = System.currentTimeMillis() + ".apk"
            project.extensions.publishAppInfo.apkName = apkName
        }
        def oldApkPath = targetDir + apkName
        apkDir.each { File item ->
            if (item.getName().endsWith("apk")) {
                item.renameTo(new File("${oldApkPath}"))
            }
        }
        println '复制APK成功'
        def juguJar = targetDir + "jiagu.jar"
        def keyStorePath = project.extensions.publishAppInfo.keyStorePath
        def keyStorePass = project.extensions.publishAppInfo.keyStorePass
        def keyStoreKeyAlias = project.extensions.publishAppInfo.keyStoreKeyAlias
        def keyStoreKeyAliasPass = project.extensions.publishAppInfo.keyStoreKeyAliasPass
        execCmd("java -jar ${juguJar} -login 15811354035 wangzhi1")
        execCmd("java -jar ${juguJar}  -importsign ${keyStorePath} ${keyStorePass} ${keyStoreKeyAlias} ${keyStoreKeyAliasPass}")
        execCmd("java -jar ${juguJar} -importmulpkg ${project.extensions.publishAppInfo.channelPath}")
        def outputPath = project.extensions.publishAppInfo.outputPath
        if (!outputPath.endsWith("/")) {
            outputPath += "/"
        }
        def projectName = project.extensions.publishAppInfo.projectName
        def projectVersion = project.extensions.publishAppInfo.projectVersion
        def apkOutputDir = outputPath + projectName + projectVersion
        def tmp = new File(apkOutputDir)
        if (!tmp.exists()) {
            tmp.mkdirs()
        }
        execCmd("java -jar ${juguJar} -jiagu ${oldApkPath} ${apkOutputDir} -autosign  -automulpkg")
        println "加固完成"
    }

    void execCmd(cmd) {
        project.exec {
            executable 'bash'
            args '-c', cmd
        }
    }
}
