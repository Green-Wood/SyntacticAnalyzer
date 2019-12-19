package yacc

import FINAL_SYMBOL
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AnalyzeTableTest {
    @Test
    fun testInitTable() {
        val grammarString = """
            {S} -> {A} a | b {A} c | d c | b d a
            {A} -> d
        """.trimIndent()
        val analyzeTable = AnalyzeTable(grammarString)
        assertEquals(Action.Shift(3), analyzeTable.table[0 to Symbol.TerminalSymbol("b")])
        assertEquals(Action.GOTO(2), analyzeTable.table[0 to Symbol.NonterminalSymbol("A")])
        assertEquals(Action.ACC(), analyzeTable.table[1 to Symbol.TerminalSymbol(FINAL_SYMBOL)])
        assertEquals(Action.Reduce(5), analyzeTable.table[4 to Symbol.TerminalSymbol("a")])
    }

    @Test
    fun testTable2() {
        val grammarString = """
            {S} -> {A} a | b {A} c | {B} c | b {B} a
            {A} -> d
            {B} -> d
        """.trimIndent()
        val analyzeTable = AnalyzeTable(grammarString)
        assertEquals(Action.Shift(3), analyzeTable.table[0 to Symbol.TerminalSymbol("b")])
        assertEquals(Action.GOTO(2), analyzeTable.table[0 to Symbol.NonterminalSymbol("A")])
        assertEquals(Action.ACC(), analyzeTable.table[1 to Symbol.TerminalSymbol(FINAL_SYMBOL)])
        assertEquals(Action.Reduce(5), analyzeTable.table[5 to Symbol.TerminalSymbol("a")])
        assertEquals(Action.Reduce(6), analyzeTable.table[5 to Symbol.TerminalSymbol("c")])
        assertEquals(Action.Reduce(6), analyzeTable.table[9 to Symbol.TerminalSymbol("a")])
        assertEquals(Action.Reduce(5), analyzeTable.table[9 to Symbol.TerminalSymbol("c")])
    }

    @Test
    fun testTable3() {
        val grammarString = """
            {S} -> {S} {A} | {A}
            {A} -> a
        """.trimIndent()
        val analyzeTable = AnalyzeTable(grammarString)
        assertEquals(12, analyzeTable.table.size)
        assertEquals(Action.Shift(3), analyzeTable.table[0 to Symbol.TerminalSymbol("a")])
        assertEquals(Action.GOTO(2), analyzeTable.table[0 to Symbol.NonterminalSymbol("A")])
        assertEquals(Action.ACC(), analyzeTable.table[1 to Symbol.TerminalSymbol(FINAL_SYMBOL)])
        assertEquals(Action.Reduce(2), analyzeTable.table[2 to Symbol.TerminalSymbol("a")])
        assertEquals(Action.Reduce(3), analyzeTable.table[3 to Symbol.TerminalSymbol("a")])
        assertEquals(Action.Reduce(1), analyzeTable.table[4 to Symbol.TerminalSymbol("$")])
    }

    @Test
    fun testTable4() {
        val grammarString = """
            {E} -> {E} * {E} | {E} + {E} | id
        """.trimIndent()
        val analyzeTable = AnalyzeTable(grammarString)
        assertEquals(18, analyzeTable.table.size)
        assertEquals(Action.Reduce(3), analyzeTable.table[2 to Symbol.TerminalSymbol("+")])
        assertEquals(Action.Shift(3), analyzeTable.table[6 to Symbol.TerminalSymbol("*")])
        assertEquals(Action.Reduce(2), analyzeTable.table[6 to Symbol.TerminalSymbol("+")])
    }

    @Test
    fun testReduce() {
        val grammarString = """
            {E} -> {E} * {E} | {E} + {E} | id
        """.trimIndent()
        val analyzeTable = AnalyzeTable(grammarString)
        val res = analyzeTable.reduce(listOf("id", "+", "id", "*", "id", "$"))
        val except = """
            sentence: id + id * id ${'$'}
            reduce: E -> id
            sentence: E + id * id ${'$'}
            reduce: E -> id
            sentence: E + E * id ${'$'}
            reduce: E -> id
            sentence: E + E * E ${'$'}
            reduce: E -> E * E
            sentence: E + E ${'$'}
            reduce: E -> E + E
            result: E ${'$'}
        """.trimIndent()
        assertEquals(except.lines(), res)
    }

    @Test
    fun testReduceDecimal() {
        val grammarString = """
            {E} -> {E} * {E} | {E} + {E} | ( {E} ) | id
        """.trimIndent()
        val analyzeTable = AnalyzeTable(grammarString)
        val res = analyzeTable.reduce(listOf("(", "id", "+", "id", ")", "*", "id", "$"))
        val except = """
            sentence: ( id + id ) * id ${'$'}
            reduce: E -> id
            sentence: ( E + id ) * id ${'$'}
            reduce: E -> id
            sentence: ( E + E ) * id ${'$'}
            reduce: E -> E + E
            sentence: ( E ) * id ${'$'}
            reduce: E -> ( E )
            sentence: E * id ${'$'}
            reduce: E -> id
            sentence: E * E ${'$'}
            reduce: E -> E * E
            result: E ${'$'}
        """.trimIndent()
        assertEquals(except.lines(), res)
    }
}