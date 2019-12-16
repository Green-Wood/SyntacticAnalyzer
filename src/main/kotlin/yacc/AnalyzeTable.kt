package yacc

sealed class Action(val id: Int) {
    class Shift(id: Int): Action(id)
    class Reduce(id: Int): Action(id)
}

class AnalyzeTable(grammarString: String) {
    val grammar: Grammar = Grammar(grammarString)
}