package yacc

sealed class Symbol(val content: String) {
    open class TerminalSymbol(content: String): Symbol(content)
    class NonterminalSymbol(content: String): Symbol(content)
    class EpsilonSymbol: TerminalSymbol("_")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Symbol) return false

        if (content != other.content) return false

        return true
    }

    override fun hashCode(): Int {
        return content.hashCode()
    }

    override fun toString(): String {
        return "Symbol('$content')"
    }

}