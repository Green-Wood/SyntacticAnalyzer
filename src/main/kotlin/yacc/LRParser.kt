package yacc

import yacc.AnalyzeTable

class LRParser(grammarString: String) {
    val analyzeTable: AnalyzeTable = AnalyzeTable(grammarString)
}