package yacc

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val parser = LRParser("resources/CGF.y")
    val res = parser.reduce("resources/input1.txt")
    val file = Paths.get("resources/output1.txt")
    Files.write(file, res, StandardCharsets.UTF_8)
}