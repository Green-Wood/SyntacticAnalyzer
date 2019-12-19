package yacc

import ALL_START_SYMBOL
import yacc.Symbol.*


class Grammar(grammarString: String) {
    val prods: List<Production>
    val startProds: List<Production>     // productions like {START} -> {S}
    private val alertMsg = "$ALL_START_SYMBOL cannot be used as grammar definition, please change to another one."

    init {
        fun checkSymbol(s: String) {
            check(s != ALL_START_SYMBOL) {
                alertMsg
            }
        }

        val tempProds = mutableListOf<Production>()
        for (line in grammarString.split("\\s*;\\s*".toRegex())) {
            // E -> {E} * {E} | {E} + {E}
            if (line.isEmpty()) continue
            require(":" in line) {
                "no ':' in the production"
            }
            val (symbol, derive) = line.split("\\s*:\\s*".toRegex())
            checkSymbol(symbol)

            for (gen in derive.split("\\s*\\|\\s*".toRegex())) {
                checkSymbol(gen)

                val production = Production(symbol, gen)
                tempProds.add(production)
            }
        }

        // should not exist in other production's derivation part
        startProds = tempProds
                .map { it.symbol }
                .toSet()
                .filter { symbol ->
                    tempProds.filter { it.symbol != symbol  } // except it self
                            .all { !it.derive.contains(symbol) }
                }
                .map { Production(ALL_START_SYMBOL, it.content) }

        prods = startProds + tempProds
    }

    fun indexOf(production: Production) = prods.indexOf(production)

    fun prodsDeriveFrom(symbol: NonterminalSymbol): List<Production> = prods.filter { it.symbol == symbol }

    fun getOperator(id: Int): String {
        require(prods.size > id && prods[id].derive.size > 1) {
            "No infix operator exist on id: $id"
        }
        return prods[id].derive[1].content
    }

    /**
     * find first terminal symbol of seq like (iSeS)
     */
    fun first(symbols: List<Symbol>): Set<TerminalSymbol> {
        require(symbols.isNotEmpty()) {
            "Cannot find first() for an empty list<yacc.Symbol>"
        }

        // take first symbol of list
        val firstSym = symbols.first()

        // return if first symbol is terminal symbol
        if (firstSym is TerminalSymbol) return setOf(firstSym)

        // find first(firstSym)
        val termSet = mutableSetOf<TerminalSymbol>()
        for (prod in prods.filter { it.symbol == firstSym }) {
            termSet += first(prod.derive)
        }

        // if symbol set of first symbol does not contains epsilon or this is the last
        if (!termSet.contains(EpsilonSymbol()) || symbols.size == 1) return termSet

        val leftTermSet = first(symbols.drop(1))

        // not all set contains epsilon, remove epsilon
        if (!leftTermSet.contains(EpsilonSymbol())) {
            termSet.remove(EpsilonSymbol())
        }
        return termSet + leftTermSet
    }

    override fun toString(): String {
        return "Grammar(prods=$prods)"
    }
}