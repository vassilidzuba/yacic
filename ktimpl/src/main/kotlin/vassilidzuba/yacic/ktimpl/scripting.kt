package vassilidzuba.yacic.ktimpl

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.script.ScriptEngineManager
import vassilidzuba.yacic.model.Node;

class Scripting {
    fun runScript() {
        val config = KtPipelineConfiguration()
        val logFile = Paths.get("target/test.log")
        val nodes : List<Node?> = ArrayList();
        val flags : Set<String?> = HashSet()

        val strPipeline = Files.readString(Path.of("../server/config/pipelines/java-maven.kts"), StandardCharsets.UTF_8)

        val prelim = """
        package vassilidzuba.yacic.ktimpl
        
        val environment = mapOf<String, String>(
                "PROJECT" to "hellojava",
                "BRANCHDIR" to "b0",
                "BRANCHNAME" to "feature/initial",
                "DATAAREA" to "/mnt/yacic",

                "DOCKERTAG" to "192.168.0.20:5000/hellojava:1.0"
                
                )
          
        """.trimIndent()

        val sb = StringBuilder()

        sb.append(prelim)
        sb.append(strPipeline);
        println(sb.toString())

        val engine = ScriptEngineManager().getEngineByExtension("kts")

        println("Going to run ze skript")
        val ret = engine.eval(sb.toString())

        println(ret.javaClass)
        println(ret)

        if (ret is KtPipeline) {
            ret.run(config, logFile, nodes, flags)
        } else {
            println("bad type")
        }
    }
}