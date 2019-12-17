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
        return "Prod(symbol=${symbol.content}, derive=${derive.map { it.content }})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Production) return false

        if (symbol != other.symbol) return false
        if (derive != other.derive) return false

        return true
    }

    override fun hashCode(): Int {
        var result = symbol.hashCode()
        result = 31 * result + derive.hashCode()
        return result
    }


}