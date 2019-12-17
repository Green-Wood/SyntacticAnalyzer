package yacc

import ALL_START_CONTENT
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


internal class LRItemSetTest {

    fun getInitSet(grammarString: String): LRItemSet {
        val grammar = Grammar(grammarString)
        var id = 0
        val initSet = LRItemSet(grammar, id)
        for (prod in grammar.startProds) {
            initSet.addItem(LRItem(prod, Symbol.TerminalSymbol("$"), 0))
        }
        initSet.closure()
        return initSet
    }

    @Test
    fun testClosure1() {
//        val grammarString = """
//            {E} -> {E} * {E} | {E} / {E} | {E} - {E} |{E} + {E}
//            {E} -> id
//        """.trimIndent()
//        val set = getInitSet(grammarString)
//        assertEquals(26, set.itemSet.size)
//        set.itemSet.removeIf { it.production.symbol.content == ALL_START_CONTENT || it.production.derive.size == 1 }
//        with(set.itemSet) {
//            assertEquals(5, count { it.production.derive[1].content == "+" })
//            assertEquals(5, count { it.production.derive[1].content == "-" })
//            assertEquals(5, count { it.production.derive[1].content == "*" })
//            assertEquals(5, count { it.production.derive[1].content == "/" })
//        }
    }

    @Test
    fun testGOTO1() {
//        val grammarString = "{E} -> {E} * {E} | {E} / {E} | {E} - {E} |{E} + {E}"
//        val set = getInitSet(grammarString)
//        val toSet = set.goto(Symbol.NonterminalSymbol("E"), 1)
//        assertTrue(toSet.itemSet.all { it.dotIndex == 1 })
//        assertEquals(setOf(Symbol.TerminalSymbol("$") to 0), toSet.reduceAbleSet())
//
//        val toMulSet = toSet.goto(Symbol.TerminalSymbol("*"), 2)
//        assertEquals(5, toMulSet.itemSet.size)
//        assertTrue(toMulSet.itemSet.all { it.dotIndex == 2 })
    }
}