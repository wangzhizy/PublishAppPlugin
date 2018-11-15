package com.wangzhi

import org.gradle.api.Plugin
import org.gradle.api.Project

class PublishAppPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("publishAppInfo", PublishAppInfoExtension)
        project.tasks.create("publishApp", PublishAppTask)

    }
}