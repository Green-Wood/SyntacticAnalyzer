package yacc
import FINAL_SYMBOL
import yacc.Symbol.*
import java.util.*

data class DirectEdge(val from: Int, val to: Int, val symbol: Symbol)

class GOTOGraph(grammar: Grammar) {
    val setList: MutableList<LRItemSet> = mutableListOf()
    val adjacent: MutableMap<Int, MutableSet<DirectEdge>> = mutableMapOf()

    val itemSets: Iterable<LRItemSet>
        get() = setList

    val edges: Iterable<DirectEdge>
        get() = adjacent.flatMap { it.value }

    init {
        val initSet = LRItemSet(grammar)
        for (prod in grammar.startProds) {
            initSet.addItem(LRItem(prod, TerminalSymbol(FINAL_SYMBOL), 0))
        }
        initSet.closure()

        val setQueue: Deque<LRItemSet> = ArrayDeque(setOf(initSet))

        initSet.id = setList.size
        setList.add(initSet)

        while (setQueue.isNotEmpty()) {
            val currSet: LRItemSet = setQueue.poll()
            val adjSet = adjacent[currSet.id] ?: mutableSetOf()

            for (sym in currSet.symbolsBehindDot) {
                val reachable = currSet.goto(sym)
                if (reachable !in setList) {
                    reachable.id = setList.size
                    setList.add(reachable)
                    setQueue.add(reachable)
                }
                adjSet.add(DirectEdge(currSet.id, setList.indexOf(reachable), sym))
            }

            adjacent[currSet.id] = adjSet
        }

        assert(setList.size == setList.toSet().size) {
            "Duplicate set exits in graph"
        }
    }
}