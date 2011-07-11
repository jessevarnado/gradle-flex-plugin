package com.nola.gradle.tasks.flex;


import com.nola.gradle.plugins.flex.FlexPluginConvention
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.TaskAction

class InitFlexProject extends ConventionTask {
    boolean verbose

    FlexPluginConvention flexConvention

    public InitFlexProject() {
        super()

        flexConvention = project.convention.plugins.flex

    }


    @TaskAction
    protected void process() {
        project.logger.info "Processing InitFlexProject task..."
        println "Create source directories..."
        if (flexConvention.flexSrcDir.mkdirs()) {
            project.logger.info "Created flex source directory: ${flexConvention.flexSrcDir}"
        }
        if (flexConvention.assetsDir.mkdirs()) {
            project.logger.info "Created assets source directory: ${flexConvention.assetsDir}"
        }
        println "Finished creation source directories..."
        project.logger.info "Finished InitFlexProject task"
    }
}