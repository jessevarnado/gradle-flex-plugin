package com.nola.gradle.plugins.flex

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

import org.gradle.api.tasks.Copy;

/*
import com.jvoegele.gradle.enhancements.FlexdocEnhancement
import com.jvoegele.gradle.tasks.flex.AdbExec
import com.jvoegele.gradle.tasks.flex.FlexPackageTask
import com.jvoegele.gradle.tasks.flex.ProGuard
*/

import com.nola.gradle.tasks.flex.*
import org.apache.tools.ant.filters.ReplaceTokens

/**
 * Gradle plugin that extends the Flex plugin for Flex development.
 *
 * @author Jesse Varnado (Jesse.Varnado@gmail.com)
 */
class FlexPlugin implements Plugin<Project> {
    private static final FLEX_GROUP = "Flex";

    private static final INIT_TASK_NAME = 'initFlexProject'
    private static final COMPILE_FLEX_PROJECT_TASK_NAME = 'compileFlexProject'
    private static final BUILD_WRAPPER_TASK_NAME = 'buildWrapper'
    private static final BUILD_CUSTOM_WRAPPER_TASK_NAME = 'buildCustomWrapper'
    private static final COPY_NON_EMBEDDED_FILES_TASK_NAME = 'copyNonEmbeddedFiles'
    private static final CLEAN_DEPLOY_SWCS_TASK = 'cleanDeploySWCsTask'
    private static final COPY_PRECOMPILED_SWCS_TASK_NAME = 'copyPrecompiledSWCs'
    private static final COMPILE_LIBRARY_TASK = 'compileLibrary'

    private static final PROPERTIES_FILES = ['local', 'build', 'default']

    private FlexPluginConvention flexConvention

    private Project project
    private logger

    private initTask
    private compileFlexProjectTask
    private buildWrapperTask
    private buildCustomWrapperTask
    private copyNonEmbeddedFilesTask
    private cleanDeploySWCsTask
    private copyPrecompiledSWCsTask
    private compileLibraryTask

    boolean verbose = false
    def ant

    @Override
    public void apply(Project project) {
        project.logger.info "Begin applying Flex Plugin..."
        this.project = project
        this.logger = project.logger
        this.ant = project.ant
        PROPERTIES_FILES.each { ant.property(file: "${it}.properties") }
        flexConvention = new FlexPluginConvention(project)
        project.convention.plugins.flex = flexConvention

        flexSetup()
        defineTasks()
        configureEnhancements()
        configureCompile()
        logger.info "Finished applying Flex Plugin"
    }

    private void flexSetup() {
        logger.info "Begin setting up flex environment..."
        //TODO setup intergration for mavenized flex dependancies
        /*
        project.configurations {
            flex
        }
        project.repositories {
            mavenRepo urls: "http://repository.sonatype.org/content/groups/flexgroup"
        }
        project.dependencies {
            flex group: 'com.adobe.flex.framework', name: 'flex-framework', version: project.flexVersion
        }
        */
        ant.taskdef(resource: "flexTasks.tasks", classpath: flexConvention.flexTasksFile)
        logger.info "Finished setting up flex environment"
    }

    private void defineTasks() {
        logger.info "Begin defining tasks..."
        defineInitTask()
        defineCompileFlexProjectTask()
        defineBuildWrapperTask()
        defineBuildCustomWrapperTask()
        defineTaskDependencies()
        defineCopyNonEmbeddedFilesTask()
        defineCleanDeploySWCsTask()
        defineCopyPrecompiledSWCsTask()
        defineCompileLibraryTask()
        configureTaskLogging()
        logger.info "Finished defining tasks"
        /*
      defineFlexProcessAssetsTask()
      defineProguardTask()
      defineFlexPackageTask()
      defineFlexInstallTask()
      defineFlexUninstallTask()
      */
    }

    private void defineInitTask() {
        initTask = project.task(INIT_TASK_NAME,
                description: "Compiles the project", type: InitFlexProject)
        initTask.group = FLEX_GROUP
    }

    private void defineCompileFlexProjectTask() {
        compileFlexProjectTask = project.task(COMPILE_FLEX_PROJECT_TASK_NAME,
                description: "Compiles the project", type: CompileFlexProject)
        compileFlexProjectTask.group = FLEX_GROUP
    }

    private void defineBuildWrapperTask() {
        buildWrapperTask = project.task(BUILD_WRAPPER_TASK_NAME,
                description: "Creates a html wrapper for the swf", type: BuildWrapper)
        buildWrapperTask.group = FLEX_GROUP
    }

    private void defineBuildCustomWrapperTask() {
        buildCustomWrapperTask = project.task(BUILD_CUSTOM_WRAPPER_TASK_NAME,
                description: "Creates a custom html wrapper for the swf", overwrite: "true", type: Copy) {
            from flexConvention.templateFile
            into flexConvention.deployDir
            rename '.*', flexConvention.outputFileName
            filter(ReplaceTokens, tokens: ["version_major": flexConvention.flashMajorVersion,
                    "version_minor": flexConvention.flashMinorVersion,
                    "version_revision": flexConvention.flashRevision,
                    "application": project.applicationName,
                    "width": "100%",
                    "height": "100%",
                    "bgcolor": "#FFFFFF",
                    "swf": project.applicationName])
        }
        buildCustomWrapperTask.group = FLEX_GROUP
        buildCustomWrapperTask.doFirst {
            project.logger.info "Processing BuildCustomWrapper task..."
            println "Building Custom Wrapper..."
        }
        buildCustomWrapperTask.doLast {
            println "Custom Wrapper Built"
            project.logger.info "Finished BuildCustomWrapper task"
        }
    }

    private void defineCopyPrecompiledSWCsTask() {
        copyPrecompiledSWCsTask = project.task(COPY_PRECOMPILED_SWCS_TASK_NAME,
                description: "Creates a custom html wrapper for the swf", type: Copy,
                includeemptydirs: true, overwrite:true) {
            from flexConvention.applicationLibsDir
            into flexConvention.deploySWCDir
        }
        copyPrecompiledSWCsTask.group = FLEX_GROUP
        copyPrecompiledSWCsTask.doFirst {
            project.logger.info "Processing CopyPrecompiledSWCs task..."
            println "Copying to Deploy SWC Directory..."
        }
        copyPrecompiledSWCsTask.doLast {
            println "Copied to Deploy SWC Directory"
            project.logger.info "Finished CopyPrecompiledSWCs task"
        }
    }

    private void defineCopyNonEmbeddedFilesTask() {
        copyNonEmbeddedFilesTask = project.task(COPY_NON_EMBEDDED_FILES_TASK_NAME, type: CopyNonEmbeddedFiles,
                description: "Copies Nonembedded Resources To Deployment Directory")
        copyNonEmbeddedFilesTask.group = FLEX_GROUP
    }

    private void defineCleanDeploySWCsTask() {
        cleanDeploySWCsTask = project.task(CLEAN_DEPLOY_SWCS_TASK, type: CleanDeploySWCs,
                description: "Copies Nonembedded Resources To Deployment Directory")
        cleanDeploySWCsTask.group = FLEX_GROUP
    }

    private void defineCompileLibraryTask() {
        compileLibraryTask = project.task(COMPILE_LIBRARY_TASK, type: CompileLibrary,
                description: "Compiles Library")
        compileLibraryTask.group = FLEX_GROUP
    }

    private void defineTaskDependencies() {
        compileFlexProjectTask.dependsOn(buildWrapperTask)
        /*
      project.tasks.compileFlex.dependsOn(flexProcessAssetsTask)
      proguardTask.dependsOn(project.tasks.jar)
      flexPackageTask.dependsOn(proguardTask)
      project.tasks.assemble.dependsOn(flexPackageTask)
      flexInstallTask.dependsOn(project.tasks.assemble)
      */
    }

    private void configureTaskLogging() {
        compileFlexProjectTask.logging.captureStandardOutput(LogLevel.INFO)
    }

    /**
     * Configure enhancements to other Gradle plugins so that they work better in
     * concert with the Flex plugin.
     */
    private void configureEnhancements() {
        /*
      new FlexdocEnhancement(project).apply()
      */
    }

    private void configureCompile() {
        /*
      def mainSource = project.tasks.compileFlex.source
      project.tasks.compileFlex.source = [flexConvention.genDir, mainSource]
      project.sourceSets.main.compileClasspath +=
      project.files(project.ant.references['flex.target.classpath'].list())
      project.compileFlex.options.bootClasspath = project.ant.references['flex.target.classpath']
      */
    }

}