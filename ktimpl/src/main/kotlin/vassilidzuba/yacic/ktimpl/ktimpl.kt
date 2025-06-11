/**
Copyright 2025 Vassili Dzuba

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 **/

package vassilidzuba.yacic.ktimpl

import vassilidzuba.yacic.model.AbstractAction
import vassilidzuba.yacic.model.AbstractPipeline
import vassilidzuba.yacic.model.Node
import vassilidzuba.yacic.model.PipelineConfiguration
import vassilidzuba.yacic.model.PipelineStatus
import java.io.OutputStream
import java.nio.file.Path

abstract class KtStep : AbstractAction<KtPipelineConfiguration>() {
    var command: String = ""
    abstract val category: String

    override fun toString(): String {
        return "[$id $category - $description]"
    }

    open fun substitute(environment: Map<String, String>)  {
        command = substituteVariables(command, environment)
        command = substituteVariables(command, mapOf<String, String>("ACTIONID" to id!!))
    }

    open fun run() {
        println("running step $id ($category)")
        if (description != "") {
            println("        description: $description")
        }
        if (command != "") {
            println("        command: $command")
        }
    }

    override fun run(
        pconfig: KtPipelineConfiguration?,
        os: OutputStream?,
        nodes: List<Node?>?
    ): String? {
        TODO("Not yet implemented")
    }

    override fun setContext(data: String?) {
        TODO("Not yet implemented")
    }

    override fun getContext(): String? {
        TODO("Not yet implemented")
    }

    override fun setDataArea(data: Path?) {
        TODO("Not yet implemented")
    }

    override fun getDataArea(): Path? {
        TODO("Not yet implemented")
    }

    override fun getSkipWhen(): Set<String?>? {
        TODO("Not yet implemented")
    }

    override fun getOnlyWhen(): Set<String?>? {
        TODO("Not yet implemented")
    }
}

class KtPodmanStep : KtStep() {
    var image: String = ""
    var setup: String = ""
    override val category = "podman"

    override fun toString(): String {
        return "[$id $category '$image']"
    }

    override fun substitute(environment: Map<String, String>)  {
        super.substitute(environment)
        setup = substituteVariables(setup, environment)
        setup = substituteVariables(setup, mapOf<String, String>("ACTIONID" to id!!))
        command = substituteVariables(command, mapOf<String, String>("IMAGE" to image))

    }

    override fun run() {
        super.run()
        if (image != "") {
            println("        image: $image")
        }
        if (setup != "") {
            println("        setup: $setup")
        }
    }
}

class KtShellStep : KtStep() {
    override val category = "shell"

    override fun toString(): String {
        return "[$id $category]"
    }
}

class KtPipelineConfiguration : PipelineConfiguration() {

}


class KtPipeline : AbstractPipeline<KtPipelineConfiguration>() {
    var name: String = ""
    var desc: String = ""
    val steps = mutableListOf<KtStep>()
    var environment : Map<String, String> = mapOf()

    fun addStep(step: KtStep) {
        steps.add(step)
    }

    fun podmanstep(init: KtPodmanStep.() -> Unit) : Unit {
        var step = KtPodmanStep()
        step.init()
        steps.add(step)
    }

    fun shellstep(init: KtShellStep.() -> Unit) : Unit {
        var step = KtShellStep()
        step.init()
        steps.add(step)
    }

    fun substitute(environment: Map<String, String>) {
        steps.forEach { it.substitute(environment) }
    }

    override fun toString() : String {
        return "[$name($description) : $steps]"
    }

    fun run() {
        println("running pipeline $name")
        println("   description: $description")
        steps.forEach { r -> r.run() }
    }

    override fun getType(): String? {
        TODO("Not yet implemented")
    }

    override fun getId(): String? {
        TODO("Not yet implemented")
    }

//    override fun getDescription(): String? {
//       return desc
//    }

//    fun setDescription(d: String) {
//        desc = d;
//    }

    override fun run(
        pconfig: KtPipelineConfiguration?,
        logFile: Path?,
        nodes: List<Node?>?,
        flags: Set<String?>?
    ): PipelineStatus<KtPipelineConfiguration?>? {
        TODO("Not yet implemented")
    }

    override fun initialize(initialStep: String?): PipelineStatus<KtPipelineConfiguration?>? {
        TODO("Not yet implemented")
    }

    override fun runNextStep(
        ps: PipelineStatus<KtPipelineConfiguration?>?,
        pconfig: KtPipelineConfiguration?,
        logFile: Path?,
        nodes: List<Node?>?,
        flags: Set<String?>?
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun setActionContext(data: String?) {
        TODO("Not yet implemented")
    }

    override fun getActionContext(): String? {
        TODO("Not yet implemented")
    }

    override fun setDataArea(data: Path?) {
        TODO("Not yet implemented")
    }

    override fun getDataArea(): Path? {
        TODO("Not yet implemented")
    }
}

fun pipeline(environment: Map<String, String>,  init: KtPipeline.() -> Unit) : KtPipeline {
    var p = KtPipeline()
    p.environment = environment
    p.init()
    p.substitute(environment)
    return p
}

fun substituteVariables(data: String, environment: Map<String, String> ) : String {
    var s = data
    environment.forEach{ (k,v) ->
       s = s.replace("@{$k}", v)
    }

    return s;
}
