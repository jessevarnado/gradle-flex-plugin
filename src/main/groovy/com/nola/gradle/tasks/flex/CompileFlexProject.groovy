package com.nola.gradle.tasks.flex;

import groovy.lang.MetaClass;

import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.TaskAction;

import com.nola.gradle.plugins.flex.FlexPluginConvention;

class CompileFlexProject extends ConventionTask {
  boolean verbose

  FlexPluginConvention flexConvention

  public CompileFlexProject () {
    super()
    
    flexConvention = project.convention.plugins.flex
    
  }
  
  
  @TaskAction
  protected void process() {
    project.logger.info "Processing CompileFlexProject task..."
    project.logger.info "Finished CompileFlexProject task"
  }
}