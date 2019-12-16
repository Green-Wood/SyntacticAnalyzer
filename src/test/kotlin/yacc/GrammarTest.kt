package yacc

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import yacc.Symbol.*

internal class GrammarTest {

    @Test
    fun testGrammar1() {
        val grammarString = "{E} -> {E} * {E} | {E} / {E} | {E} - {E} |{E} + {E}"
        val grammar = Grammar(grammarString)
        assertEquals(5, grammar.prods.size)
        assertEquals("START", grammar.prods[0].symbol.content)
        assertEquals("*", grammar.prods[1].derive[1].content)
        assertEquals("E", grammar.prods[1].derive[2].content)
        assertTrue(grammar.prods[1].derive[1] is TerminalSymbol)
        assertTrue(grammar.prods[2].derive[0] is NonterminalSymbol)
    }

    @Test
    fun testGrammar2() {
        val grammarString = """
            {E} -> {E} * {E} | {E} / {E} | {E} - {E} |{E} + {E}
            {STMT} -> if {EXPR} then {STMT} else {STMT} | if {EXPR} then {STMT}
        """.trimIndent()
        val grammar = Grammar(grammarString)
        assertEquals(8, grammar.prods.size)
        assertEquals("*", grammar.prods[2].derive[1].content)
        assertEquals("E", grammar.prods[2].derive[2].content)
        assertEquals("EXPR", grammar.prods[6].derive[1].content)
        assertTrue(grammar.prods[2].derive[1] is TerminalSymbol)
        assertTrue(grammar.prods[3].derive[0] is NonterminalSymbol)
        assertTrue(grammar.prods[6].derive[2] is TerminalSymbol)
    }

    @Test
    fun testFirst1() {
        val grammarString = """
            {S} -> i {E} t {S} {S'} | a
            {S'} -> e {S} | _
            {E} -> b
        """.trimIndent()
        val grammar = Grammar(grammarString)
        val set = grammar.first(listOf(NonterminalSymbol("S")))
        assertEquals(setOf(TerminalSymbol("a"), TerminalSymbol("i")), set)
    }

    @Test
    fun testFirst2() {
        val grammarString = """
            {S} -> i {E} t {S} {S'} | a
            {S'} -> e {S} | _
            {E} -> b
        """.trimIndent()
        val grammar = Grammar(grammarString)
        val set = grammar.first(listOf(NonterminalSymbol("S'")))
        assertEquals(setOf(TerminalSymbol("e"), TerminalSymbol("_")), set)
    }
}