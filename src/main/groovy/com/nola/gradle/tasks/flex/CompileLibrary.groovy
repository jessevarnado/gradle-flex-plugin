package com.nola.gradle.tasks.flex;


import com.nola.gradle.plugins.flex.FlexPluginConvention
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.TaskAction

class CompileLibrary extends ConventionTask {
    boolean verbose

    FlexPluginConvention flexConvention

    public CompileLibrary() {
        super()

        flexConvention = project.convention.plugins.flex
        inputs.dir(flexConvention.librarySrcDir)
        outputs.dir(flexConvention.library)
    }

    @TaskAction
    protected void process() {
        project.logger.info "Processing CompileLibrary task..."
        println "Compiling Library SWC To Deploy SWC Folder..."

        ant.compc(debug:flexConvention.debug, output:flexConvention.library){
            "source-path"("path-element":flexConvention.librarySrcDir)
            "include-sources"(dir:flexConvention.librarySrcDir, includes:"*")
            "compiler.library-path"(dir:flexConvention.libraryLibsDir, append:"true"){
                include (name:"*.swc")
            }
            metadata{
                creator{
                    "Jesse Varnado"
                }
                publisher {
                    "techNOLAgy"
                }
                language {
                    "EN"
                }
            }
        }
        println "Compiled Library SWC To Deploy SWC Folder"
        project.logger.info "Finished CompileLibrary task"
    }
}