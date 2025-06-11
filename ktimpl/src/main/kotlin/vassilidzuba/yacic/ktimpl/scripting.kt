package vassilidzuba.yacic.ktimpl

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import javax.script.ScriptEngineManager

class Scripting {
    fun runScript() {
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
            ret.run()
        } else {
            println("bad type")
        }
    }
}