package com.nola.gradle.plugins.flex;

import org.gradle.api.Project

class FlexPluginConvention {
    Project project

    def antPropertyMapper = ["sdkDirName": [resolver: [withoutBaseDir: "true"], ant: [name: "FLEX_HOME"]],
            "flexTasksFileName": [resolver: [withBaseDir: "flexTasksDirName"], ant: [name: "FlexTasks.file"]],
            "flexTasksDirName": [resolver: [withBaseDir: "sdkDirName"], ant: [name: "FlexTasks.dir"]],
            "deployDirName": [resolver: [withBaseDir: "buildDir"], ant: [name: "Deploy.dir"]],
            "srcDirName": [ant: [name: "Src.dir"]],
            "mainSourceSetName": [resolver: [withBaseDir: "srcDirName"]],
            "flexSrcDirName": [resolver: [withBaseDir: "mainSourceSetName"], ant: [name: "FlexSrc.dir"]],
            "debug": [ant: [name: "Debug.Boolean"]],
            "applicationName": [ant: [name: "Application.name"]],
            "flashMajorVersion": [ant: [name: "Major.version"]],
            "flashMinorVersion": [ant: [name: "Minor.version"]],
            "flashRevision": [ant: [name: "Revision.version"]],
            "templateFileName": [ant: [name: "Template.file"], resolver: [withBaseDir: "flexSrcDirName"]],
            "outputFileName": [ant: [name: "Output.file"], resolver: [withBaseDir: "deployDirName"]],
            "assetsDirName": [resolver: [withBaseDir: "flexSrcDirName"], ant: [name: "Assets.dir"]],
            "deployAssetsDirName": [ant: [name: "DeployAssets.dir"], resolver: [withBaseDir: "deployDirName"]],
            "deploySWCDirName": [ant: [name: "DeploySWC.dir"], resolver: [withBaseDir: "deployDirName"]],
            "libraryDirName": [ant: [name: "Library.dir"]],
            "libraryLibsDirName": [ant: [name: "LibraryLibs.dir", resolver: [withBaseDir: "libraryDirName"]]],
            "librarySrcDirName": [ant: [name: "LibrarySrc.dir", resolver: [withBaseDir: "libraryDirName"]]],
            "libraryName": [ant: [name: "Library.name"]],
            "applicationLibsDirName": [ant: [name: "ApplicationLibs.dir"]]]
    def sdkDirName
    def debug = "false"
    def applicationName
    def version = "0.0.0"
    def srcDirName = "src"
    def mainSourceSetName = "main"
    def flexSrcDirName = "flex"
    def flashMajorVersion = "9"
    def flashMinorVersion = "0"
    def flashRevision = "124"
    def templateFileName = "index.template.html"
    def outputFileName = "index.html"
    def deployDirName = "deploy"
    def assetsDirName = "assets"
    def deployAssetsDirName = "assets"
    def deploySWCDirName = "deploySWC"
    def flexTasksFileName = "flexTasks.jar"
    def flexTasksDirName = "ant/lib/"
    def libraryDirName
    def libraryLibsDirName = "libs"
    def librarySrcDirName = "src"
    def libraryName
    def applicationLibsDirName = "libs"
    def flexVersion = '3.2.0.3958'
    def flashVersion = '9.0.124'

    FlexPluginConvention(Project project) {
        project.logger.debug "Begin Initializing FlexPluginConvention..."
        this.project = project
        applicationName = project.name
        libraryName = applicationName + "Library"
        libraryDirName = "../" + libraryName
        getPropertyByName("assetsDirName")
        if (!sdkDirName) {
            setupSDK()
        }
        try {
            flashVersion = project.flashVersion
        } catch (MissingPropertyException e){
        } finally {
            def versionTokens = flashVersion.tokenize(".")
            switch (versionTokens.size()) {
                case 3:
                    flashRevision = versionTokens[2]
                case 2:
                    flashMinorVersion = versionTokens[1]
                case 1:
                    flashMajorVersion = versionTokens[0]
            }
        }
        project.logger.debug "End Initializing FlexPluginConvention"
    }

    private void setupSDK() {
        // Determine the sdkDir value.
        // First, let's try the sdk.dir property in local.properties file.
        try {
            sdkDirName = project.ant['FLEX_HOME']
        } catch (MissingPropertyException e) {
            sdkDirName = null
        }
        if (sdkDirName == null || sdkDirName.length() == 0) {
            // No local.properties and/or no sdk.dir property: let's try FLEX_HOME
            sdkDirName = System.getenv("FLEX_HOME")
            // Propagate it to the Gradle's Ant environment
            if (sdkDirName != null) {
                project.ant.setProperty("sdk.dir", sdkDirName)
            }
        }
        // Check for sdkDir correctly valued, and in case throw an error
        if (sdkDirName == null || sdkDirName.length() == 0) {
            throw new MissingPropertyException("Unable to find location of Flex SDK. Please read documentation.")
        }
    }

    private def getPropertyByName(String name) {
        project.logger.debug "Begin getPropertyByName($name)..."
        def prop = antPropertyMapper[name]
        project.logger.debug "End getPropertyByName($name) return $prop"
        return prop
    }

    private File getFileResolverForProperty(String name) {
        project.logger.debug "Begin getFileResolverForProperty($name)..."

        File resolver
        def prop = getPropertyByName(name)
        def antProp = prop?.ant ? getAntProperty(prop.ant?.name) : null
        if (antProp && antProp.size() > 0) {
            project.logger.debug "  resolved with Ant and withBaseDir(project.projectDir)"
            resolver = project.fileResolver.withBaseDir(project.projectDir).resolve(antProp)
        } else if (prop?.resolver?.withBaseDir && prop.resolver.withBaseDir.size() > 0) {
            project.logger.debug "  resolved with Property and BaseDir: ${prop.resolver.withBaseDir}"
            resolver = project.fileResolver.withBaseDir(getFileResolverForProperty(prop.resolver.withBaseDir)).resolve(project[name])
        } else if (prop?.resolver?.withoutBaseDir && prop.resolver.withoutBaseDir == "true") {
            project.logger.debug "  resolved with Property and WithoutBaseDir"
            resolver = project.fileResolver.resolve(project[name])
        } else {
            project.logger.debug "  resolved with Property and withBaseDir(project.projectDir)"
            resolver = project.fileResolver.withBaseDir(project.projectDir).resolve(project[name])
        }
        project.logger.debug "End getFileResolverForProperty($name) return $resolver"
        return resolver
    }

    private def getAntProperty(String name) {
        project.logger.debug "Begin getAntProperty($name)..."
        def prop
        try {
            prop = project.ant[name]
        } catch (Exception e) {
            project.logger.debug "Could not find project.ant[$name]"
        }
        project.logger.debug "End getAntProperty($name) return $prop"
        return prop
    }

    File getSdkDir() {
        //project.fileResolver.withBaseDir(project.projectDir).resolve(srcDirName)
        getFileResolverForProperty("sdkDirName")
    }

    File getSrcDir() {
        //project.fileResolver.withBaseDir(project.projectDir).resolve(srcDirName)
        getFileResolverForProperty("srcDirName")
    }

    File getFlexSrcDir() {
        //project.fileResolver.withBaseDir(project.projectDir).resolve(srcDirName)
        getFileResolverForProperty("flexSrcDirName")
    }

    File getDeployDir() {
        //project.fileResolver.withBaseDir(project.buildDir).resolve(deployDirName)
        getFileResolverForProperty("deployDirName")
    }

    File getDeployAssetsDir() {
        //project.fileResolver.withBaseDir(project.buildDir).resolve(deployAssetsDirName)
        getFileResolverForProperty("deployAssetsDirName")
    }

    File getAssetsDir() {
        //project.fileResolver.withBaseDir(srcDir).resolve(assetsDirName)
        getFileResolverForProperty("assetsDirName")
    }

    File getTemplateFile() {
        getFileResolverForProperty("templateFileName")
    }

    File getOutputFile() {
        getFileResolverForProperty("outputFileName")
    }

    File getDeploySWCDir() {
        getFileResolverForProperty("deploySWCDirName")
    }

    File getFlexTasksFile() {
        getFileResolverForProperty("flexTasksFileName")
    }

    File getFlexTasksDir() {
        getFileResolverForProperty("flexTasksDirName")
    }

    File getLibraryDir() {
        getFileResolverForProperty("libraryDirName")
    }

    File getLibraryLibsDir() {
        getFileResolverForProperty("libraryLibsDirName")
    }

    File getLibrarySrcDir() {
        getFileResolverForProperty("librarySrcDirName")
    }

    File getApplicationLibsDir() {
        getFileResolverForProperty("applicationLibsDirName")
    }

    File getLibrary() {
        new File(getDeploySWCDir(), "${libraryName}.swc")
    }

}