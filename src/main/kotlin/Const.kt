val ALL_START_SYMBOL = "START"    // cannot be used in user grammar definition
val FINAL_SYMBOL = "$"
val OPERATORS = mapOf(       // 最后没用用到，因为优先级是直接在GCF中定义的，并没有出现二义文法
        "*" to 2,
        "/" to 2,
        "+" to 1,
        "-" to 1
)