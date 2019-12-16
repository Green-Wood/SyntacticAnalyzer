package yacc

import kotlin.test.Test
import kotlin.test.assertEquals

internal class ProductionTest {
    @Test
    fun testProduction() {
        val symbol = "{E}"
        val deriveString = "{E} * {E}"
        val production = Production(symbol, deriveString)
        assertEquals(production.symbol, Symbol.NonterminalSymbol("E"))
        assertEquals(3, production.derive.size)
        assertEquals("E", production.derive[0].content)
    }
}