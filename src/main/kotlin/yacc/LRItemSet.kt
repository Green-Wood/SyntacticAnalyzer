package yacc

import FINAL_SYMBOL
import yacc.Symbol.*
import java.util.*

data class LRItemSet(private val grammar: Grammar, val id: Int) {

    val itemSet: MutableSet<LRItem> = mutableSetOf()

    // symbols can be used in goto
    val symbolsBehindDot: Set<Symbol>
        get() = itemSet
                .map { it.symbolBehindDot }
                .filter { it.content != FINAL_SYMBOL }
                .toSet()

    fun addItem(item: LRItem) {
        itemSet.add(item)
    }

    // in place function
    fun closure(): LRItemSet {
        // A -> a.Bc, d
        val itemQueue: Deque<LRItem> = ArrayDeque(itemSet)
        while (itemQueue.isNotEmpty()) {
            val item = itemQueue.poll()
            val symbol = item.symbolBehindDot
            if (symbol is TerminalSymbol) continue
            // B -> y
            for (prod in grammar.prodsDeriveFrom(symbol as NonterminalSymbol)) {
                // b in FIRST(cd)
                for (termSym in grammar.first(listOf(item.symbolAcrossDot, item.lookAheadSymbol))) {
                    // B -> .y, b
                    val newItem = LRItem(prod, termSym, 0)
                    if (newItem in itemSet) continue
                    addItem(newItem)
                    itemQueue.add(newItem)
                }
            }
        }
        return this
    }

    fun goto(sym: Symbol, id: Int): LRItemSet {
        require(sym in symbolsBehindDot) {
            "$sym is not behind dot"
        }
        val itemSet = LRItemSet(grammar, id)
        for (item in this.itemSet) {
            if (item.symbolBehindDot == sym) {
                itemSet.addItem(LRItem(item.production, item.lookAheadSymbol, item.dotIndex + 1))
            }
        }
        return itemSet.closure()
    }

    /**
     * find items that can be reduce, return lookAheadSymbol and production index
     */
    fun reduceAbleSet(): Set<Pair<Symbol, Int>> =
        itemSet
                .filter { it.reduceAble }
                .map { it.lookAheadSymbol to grammar.indexOf(it.production) }
                .toSet()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LRItemSet) return false

        if (itemSet != other.itemSet) return false

        return true
    }

    override fun hashCode(): Int {
        return itemSet.hashCode()
    }


}