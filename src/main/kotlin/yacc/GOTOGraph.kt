package yacc

data class DirectEdge(val from: Int, val to: Int, val symbol: Symbol)

class GOTOGraph {
    val idSetMap: Map<Int, LRItemSet> = mutableMapOf()
    val adjacent: Map<Int, DirectEdge> = mutableMapOf()

    companion object {
        fun of(grammar: Grammar) {

        }
    }
}