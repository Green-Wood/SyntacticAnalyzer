package yacc

import java.io.File

class LRParser(yaccFileName: String) {
    private val analyzeTable: AnalyzeTable

    init {
        val cgfFile = File(yaccFileName).inputStream()
        val cgfString = cgfFile.bufferedReader().use { it.readText() }
        analyzeTable = AnalyzeTable(cgfString)
    }

    fun reduce(tokenFileName: String): List<String> {
        val lineList = mutableListOf<String>()

        File(tokenFileName).useLines { lines ->
            lines.forEach {
                lineList.add('\'' + it.split("\'".toRegex())[1] + '\'')
            }
        }

        return analyzeTable.reduce(lineList + listOf("$"))
    }
}