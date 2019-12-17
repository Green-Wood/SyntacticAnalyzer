package yacc

import FINAL_SYMBOL
import yacc.Symbol.*

data class LRItem(
        val production: Production,
        val lookAheadSymbol: Symbol,
        val dotIndex: Int = 0
        ) {

    /**
     * A -> a.Bc   this will return B
     */
    val symbolBehindDot: Symbol
        get() = when(dotIndex) {
            production.derive.size -> TerminalSymbol(FINAL_SYMBOL)
            else -> production.derive[dotIndex]
        }

    val reduceAble: Boolean
        get() = dotIndex == production.derive.size

    /**
     * A -> a.Bc   this will return c
     */
    val symbolAcrossDot: Symbol?
        get() {
            require(dotIndex < production.derive.size) {
                "cannot get across symbol when dot is last like iSaS."
            }
            return when(dotIndex) {
                production.derive.size - 1 -> null
                else -> production.derive[dotIndex + 1]
            }
        }

    init {
        require(dotIndex in 0..production.derive.size) {
            "dot should be initialize in [0, derive.size]"
        }
    }

    override fun toString(): String {
        return "LRItem(production=$production, lookAheadSymbol=${lookAheadSymbol.content}, dotIndex=$dotIndex)"
    }


}