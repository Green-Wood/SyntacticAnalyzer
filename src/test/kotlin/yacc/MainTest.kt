package yacc

import yacc.exception.ReduceException
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MainTest {
    @Test
    fun testException() {
//        val cgfFile = File("resources/CGF.y").inputStream()
//        val cgfString = cgfFile.bufferedReader().use { it.readText() }
//        val analyzeTable = AnalyzeTable(cgfString)
//
//        val inputFile = File("resources/input2.txt").inputStream()
//        val inputString = inputFile.bufferedReader().use { it.readText() }
//
//        try {
//            val reduceResult = analyzeTable.reduce(inputString.split("\\s+".toRegex()))
//            val file = Paths.get("resources/output1.txt")
//            Files.write(file, reduceResult, StandardCharsets.UTF_8)
//        } catch (e: ReduceException) {
//            val except = """
//                Part of input have been reduced
//                sentence: ( id * ( i - id ) + id ) / ( id - id ) ${'$'}
//                reduce: E -> id
//                Reduce broken at:
//                   ( id * ( i - id ) + id ) / ( id - id )
//                            ^
//                   please check your input!!!
//
//            """.trimIndent()
//            assertEquals(except, e.message)
//        }

    }
}