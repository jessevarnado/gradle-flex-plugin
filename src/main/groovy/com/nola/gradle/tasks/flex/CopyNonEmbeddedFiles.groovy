package com.nola.gradle.tasks.flex;

import groovy.lang.MetaClass;

import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.TaskAction;

import com.nola.gradle.plugins.flex.FlexPluginConvention;
import org.apache.tools.ant.filters.ReplaceTokens

class CopyNonEmbeddedFiles extends ConventionTask {
  boolean verbose

  FlexPluginConvention flexConvention   

  public CopyNonEmbeddedFiles () {
    super()
    
    flexConvention = project.convention.plugins.flex
    inputs.dir(flexConvention.assetsDir)
	outputs.dir(flexConvention.deployAssetsDir)
  }
  
  @TaskAction
  protected void process() {
    project.logger.info "Processing CopyNonEmbeddedFiles task..."
    println "Deleting Deploy Assets Directory..."
	//println flexConvention.deployDir
	ant.delete( dir:flexConvention.deployAssetsDir,
		failOnError:"false",
		includeEmptyDirs:"true"
	)
	println "Deleted Deploy Assets Directory"
	println "Creating Assets Folder"
	def deployAssetsDir = flexConvention.deployAssetsDir
	deployAssetsDir.mkdirs()
	println "Created Assets Folder"
	println "Copy Nonembedded Resources To Deploy..."
	ant.copy(todir:flexConvention.deployAssetsDir, includeemptydirs:"false", overwrite:"true"){
        fileset(dir:flexConvention.assetsDir)
    }
	println "Copied Nonembedded Resources To Deploy"
    project.logger.info "Finished CopyNonEmbeddedFiles task"
  }
}