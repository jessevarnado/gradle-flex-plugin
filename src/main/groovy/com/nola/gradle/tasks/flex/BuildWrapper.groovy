package com.nola.gradle.tasks.flex;

import groovy.lang.MetaClass;

import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.TaskAction;

import com.nola.gradle.plugins.flex.FlexPluginConvention;

class BuildWrapper extends ConventionTask {
  boolean verbose

  FlexPluginConvention flexConvention   

  public BuildWrapper () {
    super()
    
    flexConvention = project.convention.plugins.flex
    
  }
  
  
  @TaskAction
  protected void process() {
    println "Building Wrapper..."
	logger.info "Major Version:  ${project.convention.plugins.flex.flashMajorVersion}\nMinor Revision: ${project.convention.plugins.flex.flashMinorVersion}\nRevision: ${project.convention.plugins.flex.flashRevision}"

	ant."html-wrapper"(title:project.name, 
					 height:"100%", 
					 width:"100%", 
					 bgcolor:"#FFFFFF", 	
					 file:"${project.name}.html", 
					 application:"${project.name}",
					 swf:"${project.name}",
					 "version-major":project.convention.plugins.flex.flashMajorVersion ,
					 "version-minor":project.convention.plugins.flex.flashMinorVersion ,
					 flashReviflashRevisionject.convention.plugins.flex.flashRevision ,
					 history:"true" ,					 
					 output:project.buildDir )
	println "Wrapper Built"
	
  }
}