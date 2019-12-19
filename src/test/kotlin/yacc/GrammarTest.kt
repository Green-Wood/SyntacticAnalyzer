package yacc

import ALL_START_SYMBOL
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import yacc.Symbol.*

internal class GrammarTest {

    @Test
    fun testGrammar1() {
        val grammarString = "E : E '*' E | E '/' E | E '-' E | E '+' E"
        val grammar = Grammar(grammarString)
        assertEquals(5, grammar.prods.size)
        assertEquals(ALL_START_SYMBOL, grammar.prods[0].symbol.content)
        assertEquals("'*'", grammar.prods[1].derive[1].content)
        assertEquals("E", grammar.prods[1].derive[2].content)
        assertTrue(grammar.prods[1].derive[1] is TerminalSymbol)
        assertTrue(grammar.prods[2].derive[0] is NonterminalSymbol)
    }
}