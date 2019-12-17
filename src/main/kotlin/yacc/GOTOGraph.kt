package yacc
import FINAL_SYMBOL
import yacc.Symbol.*
import java.lang.invoke.MutableCallSite
import java.util.*

data class DirectEdge(val from: Int, val to: Int, val symbol: Symbol)

class GOTOGraph(grammar: Grammar) {
    val idSetMap: MutableMap<Int, LRItemSet> = mutableMapOf()
    val adjacent: MutableMap<Int, MutableSet<DirectEdge>> = mutableMapOf()

    val itemSets: Iterable<LRItemSet>
        get() = idSetMap.values

    val edges: Iterable<DirectEdge>
        get() = adjacent.flatMap { it.value }

    init {
        var id = 0
        val initSet = LRItemSet(grammar, id)
        for (prod in grammar.startProds) {
            initSet.addItem(LRItem(prod, TerminalSymbol(FINAL_SYMBOL), 0))
        }
        initSet.closure()

        val setQueue: Deque<LRItemSet> = ArrayDeque()

        idSetMap[id] = initSet
        setQueue.add(initSet)

        while (setQueue.isNotEmpty()) {
            val currSet: LRItemSet = setQueue.poll()
            for (sym in currSet.symbolsBehindDot) {
                id += 1
                val reachable = currSet.goto(sym, id)
                if (idSetMap.containsValue(reachable)) {
                    id -= 1
                } else {
                    idSetMap[id] = reachable
                    setQueue.add(reachable)
                }
                // set relationship
                val adjSet = adjacent[currSet.id] ?: mutableSetOf()
                adjSet.add(DirectEdge(currSet.id, id, sym))
                adjacent[currSet.id] = adjSet
            }
        }

        assert(idSetMap.values.size == idSetMap.values.toSet().size) {
            "Duplicate set exits in graph"
        }
    }
}