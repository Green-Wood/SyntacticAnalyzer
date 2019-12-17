package yacc

import FINAL_SYMBOL
import OPERATORS
import java.lang.Exception
import java.util.*
import kotlin.math.min

sealed class Action(val id: Int) {
    class Shift(id: Int): Action(id)
    class Reduce(id: Int): Action(id)
    class GOTO(id: Int): Action(id)
    class ACC: Action(0)

    override fun toString(): String {
        return when(this) {
            is Shift -> "S$id"
            is Reduce -> "R$id"
            is GOTO -> "GOTO$id"
            is ACC -> "ACC"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Action) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
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
                if (prodId == 0) table[key] = Action.ACC()
                else table[key] = Action.Reduce(min(prodId, table[key]?.id ?: Int.MAX_VALUE))
            }
        }

        for ((from, to, sym) in graph.edges) {
            val key = from to sym
            when(sym) {
                is Symbol.TerminalSymbol -> {
                    val isBeforeOp = key in table.keys && grammar.getOperator(table[key]!!.id) in OPERATORS.keys
                    val isAfterOp = sym.content in OPERATORS.keys
                    if (isBeforeOp && isAfterOp) {
                        val firstOp = grammar.getOperator(table[key]!!.id)
                        val secondOp = sym.content
                        if (OPERATORS.getValue(secondOp) > OPERATORS.getValue(firstOp)) {
                            table[key] = Action.Shift(to)
                        }
                    } else {
                        table[key] = Action.Shift(to)
                    }
                }
                is Symbol.NonterminalSymbol -> table[key] = Action.GOTO(to)
            }
        }
    }

    /**
     * replace target first in old list with new string
     */
    private fun replaceFirst(old: List<String>, target: List<String>, new: String): List<String> {
        val index = old
                .withIndex()
                .find { old.subList(it.index, it.index + target.size) == target }
        return old.subList(0, index!!.index) + listOf(new) + old.subList(index.index + target.size, old.size)
    }

    /**
     * w$ which is the final symbol
     */
    fun reduce(tokens: List<String>): List<String> {
        require(tokens.last() == FINAL_SYMBOL) {
            "token should be append with $FINAL_SYMBOL"
        }

        val res: MutableList<String> = mutableListOf()

        val stateStack: Deque<Int> = ArrayDeque()
        val symStack: Deque<String> = ArrayDeque()

        var scanner = 0
        var currSym = tokens.first()
        stateStack.push(0)

        var tempTokens: List<String> = tokens.toMutableList()

        var isAcc = false
        while (!isAcc) {
            val s = stateStack.peek()
            when(val action = table[s to Symbol.TerminalSymbol(currSym)]) {
                is Action.Shift -> {
                    stateStack.push(action.id)
                    symStack.push(currSym)
                    // move to next symbol
                    scanner += 1
                    currSym = tokens[scanner]
                }
                is Action.Reduce -> {
                    // A -> B
                    val prod = grammar.prods[action.id]
                    // pop num |B|
                    res.add("sentence: " + tempTokens.joinToString(separator = " "))
                    val deleteTokens = mutableListOf<String>()
                    for (symbol in prod.derive) {
                        stateStack.pop()
                        deleteTokens.add(symStack.pop())
                    }
                    tempTokens = replaceFirst(tempTokens, deleteTokens, prod.symbol.content)
                    // t is stack peek, so push(GOTO[t, A])
                    stateStack.push(table[stateStack.peek() to prod.symbol]!!.id)
                    symStack.push(prod.symbol.content)
                    res.add("reduce: " + prod.getHumanString())
                }
                is Action.ACC -> {
                    res.add("result: " + tempTokens.joinToString(separator = " "))
                    isAcc = true
                }
                null -> throw Exception("坏了")
            }
        }
        return res
    }
}