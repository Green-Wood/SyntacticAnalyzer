package yacc

import yacc.Symbol.NonterminalSymbol
import yacc.Symbol.TerminalSymbol

class Production(symbol: String, deriveString: String) {
    val symbol: NonterminalSymbol = NonterminalSymbol(symbol.substring(1, symbol.length - 1))
    val derive: MutableList<Symbol> = mutableListOf()
    // E -> {E} + {E}
    init {

        require(symbol.first() == '{' && symbol.last() == '}') {
            "First element of production must be nonterminal symbol."
        }

        for (sym in deriveString.split("\\s+".toRegex())) {
            val deriveSymbol = when {
                sym.first() == '{' && sym.last() == '}'
                    -> NonterminalSymbol(sym.substring(1, sym.length - 1))
                else
                    -> TerminalSymbol(sym)
            }
            derive.add(deriveSymbol)
        }
    }

    override fun toString(): String {
        return "yacc.Production(symbol=$symbol, derive=$derive)"
    }
}