package com.nola.gradle.tasks.flex;


import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.TaskAction;

import com.nola.gradle.plugins.flex.FlexPluginConvention;


class BuildCustomWrapper extends ConventionTask {
  boolean verbose

  FlexPluginConvention flexConvention   

  public BuildCustomWrapper () {
    super()
    
    flexConvention = project.convention.plugins.flex
    inputs.file(flexConvention.templateFile)
	outputs.file(flexConvention.outputFile)
  }
  
  
  @TaskAction
  protected void process() {
    project.logger.info "Processing BuildCustomWrapper task..."
    println "Building Custom Wrapper..."
      ant.copy(file:flexConvention.templateFile, todir:flexConvention.outputFile, overwrite:"true"){
        filterchain{
            replacetokens{
                token (key:"version_major", value:flexConvention.flashMajorVersion)
                token (key:"version_minor", value:flexConvention.flashMinorVersion)
                token (key:"version_revision", value:flexConvention.flashRevision)
                token (key:"application", value:flexConvention.applicationName)
                token (key:"width", value:"100%")
                token (key:"height", value:"100%")
                token (key:"bgcolor", value:"#FFFFFF")
                token (key:"swf", value:flexConvention.applicationName)
            }
        } /*
          filter(ReplaceTokens, tokens: ["version_major":flexConvention.flashMajorVersion,
										"version_minor":flexConvention.flashMinorVersion,
										"version_revision":flexConvention.flashRevision,
										"application":project.applicationName,
										"width":"100%",
										"height":"100%",
										"bgcolor":"#FFFFFF",
										"swf":project.applicationName])*/
      }
        /*
	copy {
		from:flexConvention.templateFile
		into:flexConvention.outputFile
		overwrite:"true"
		filter(ReplaceTokens, tokens: ["version_major":flexConvention.flashMajorVersion,
										"version_minor":flexConvention.flashMinorVersion,
										"version_revision":flexConvention.flashRevision,
										"application":project.applicationName,
										"width":"100%",
										"height":"100%",
										"bgcolor":"#FFFFFF",
										"swf":project.applicationName])		
	}     */
	println "Custom Wrapper Built"
    project.logger.info "Finished BuildCustomWrapper task"
  }
}