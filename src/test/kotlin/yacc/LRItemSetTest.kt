package yacc

import ALL_START_CONTENT
import FINAL_SYMBOL
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


internal class LRItemSetTest {

    fun getInitSet(grammarString: String): LRItemSet {
        val grammar = Grammar(grammarString)
        val initSet = LRItemSet(grammar)
        for (prod in grammar.startProds) {
            initSet.addItem(LRItem(prod, Symbol.TerminalSymbol(FINAL_SYMBOL), 0))
        }
        initSet.closure()
        return initSet
    }

    @Test
    fun testClosure1() {
        val grammarString = """
            {S} -> {S} {A} | {A}
            {A} -> a
        """.trimIndent()
        val set = getInitSet(grammarString)
         assertEquals(7, set.itemSet.size)
        assertEquals(3, set.itemSet.count { it.lookAheadSymbol == Symbol.TerminalSymbol("a") })
        assertEquals(4, set.itemSet.count { it.lookAheadSymbol == Symbol.TerminalSymbol("$") })
        assertEquals(2, set.itemSet.count { it.production.symbol == Symbol.NonterminalSymbol("A") })
    }
}