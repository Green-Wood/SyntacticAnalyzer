package yacc

import kotlin.test.Test
import kotlin.test.assertEquals


internal class GOTOGraphTest {
    @Test
    fun testInitGraph() {
        val grammarString = """
            {S} -> {A} a | b {A} c | d c | b d a
            {A} -> d
        """.trimIndent()
        val grammar = Grammar(grammarString)
        val graph = GOTOGraph(grammar)
        assertEquals(11, graph.setList.size)
        assertEquals(10, graph.adjacent.values.flatten().size)
        assertEquals(1, graph.setList[10]!!.itemSet.size)
        assertEquals(1, graph.adjacent[6]!!.size)
    }

    @Test
    fun testLRGraph() {
        val grammarString = """
            {S} -> {A} a | b {A} c | {B} c | b {B} a
            {A} -> d
            {B} -> d
        """.trimIndent()
        val grammar = Grammar(grammarString)
        val graph = GOTOGraph(grammar)
        assertEquals(13, graph.setList.size)
        assertEquals(12, graph.adjacent.values.flatten().size)
    }

    @Test
    fun testLRGraphRecursive() {
        val grammarString = """
            {S} -> {S} {A} | {A}
            {A} -> a
        """.trimIndent()
        val grammar = Grammar(grammarString)
        val graph = GOTOGraph(grammar)
        assertEquals(5, graph.setList.size)
        assertEquals(5, graph.adjacent.values.flatten().size)
        val testSet: Set<DirectEdge> = setOf(DirectEdge(1, 3, Symbol.TerminalSymbol("a")),
                DirectEdge(1, 4, Symbol.NonterminalSymbol("A")))
        assertEquals(testSet, graph.adjacent[1]!!)
    }

    @Test
    fun testLRGraphAmbiguous() {
        val grammarString = """
            {E} -> {E} * {E} | {E} + {E} | id
        """.trimIndent()
        val grammar = Grammar(grammarString)
        val graph = GOTOGraph(grammar)
        assertEquals(7, graph.setList.size)
        assertEquals(12, graph.adjacent.values.flatten().size)
        val testSet: Set<DirectEdge> = setOf(DirectEdge(5, 3, Symbol.TerminalSymbol("*")),
                DirectEdge(5, 4, Symbol.NonterminalSymbol("+")))
        assertEquals(testSet, graph.adjacent[5]!!)
    }
}