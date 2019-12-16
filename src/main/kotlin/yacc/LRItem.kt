package yacc

class LRItem(
        val production: Production,
        val lookAheadSymbol: Symbol,
        val dotIndex: Int = 0
        ) {
    val lookAheadList = mutableListOf(lookAheadSymbol)

    fun addSymbol(symbol: Symbol) {
        lookAheadList.add(symbol)
    }
}