package yacc

import kotlin.math.min

sealed class Action(val id: Int) {
    class Shift(id: Int): Action(id)
    class Reduce(id: Int): Action(id)

    override fun toString(): String {
        return when(this) {
            is Shift -> "S$id"
            is Reduce -> "R$id"
        }
    }
}

class AnalyzeTable(grammarString: String) {
    val table: MutableMap<Pair<Int, Symbol>, Action> = mutableMapOf()
    val grammar: Grammar = Grammar(grammarString)

    init {
        val graph = GOTOGraph(grammar)
        for (itemSet in graph.itemSets) {
            for ((sym, prodId) in itemSet.reduceAbleSet()) {
                val key = itemSet.id to sym
                table[key] = Action.Reduce(min(prodId, table[key]?.id ?: Int.MAX_VALUE))
            }
        }

        for ((from, to, sym) in graph.edges) {
            table[from to sym] = Action.Shift(to)
        }
    }
}