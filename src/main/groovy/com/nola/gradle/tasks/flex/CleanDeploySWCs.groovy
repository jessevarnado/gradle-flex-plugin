package com.nola.gradle.tasks.flex;


import com.nola.gradle.plugins.flex.FlexPluginConvention
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.TaskAction

class CleanDeploySWCs extends ConventionTask {
    boolean verbose

    FlexPluginConvention flexConvention

    public CleanDeploySWCs() {
        super()

        flexConvention = project.convention.plugins.flex
    }

    @TaskAction
    protected void process() {
        project.logger.info "Processing CopyNonEmbeddedFiles task..."
        println "Deleting Deploy Assets Directory..."
        println "Deleting Deploy SWC Directory ..."
        ant.delete(dir: flexConvention.deploySWCDir, failOnError: "false", includeEmptyDirs: "true")
        println "Deleted Deploy SWC Directory"
        println "Creating Deploy SWC Directory ..."
        flexConvention.deploySWCDir.mkdir()
        println "Created Deploy SWC Directory"
    }
}