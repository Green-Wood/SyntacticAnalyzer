# Yacc实验报告

## 学号：   171250649              姓名：  赵文祺

## 本次实验使用Kotlin语言实现，并使用Gradle进行测试和管理

[TOC]

## 截图

### 源文件目录截图

![截屏2019-12-1918.38.43](https://tva1.sinaimg.cn/large/006tNbRwly1ga27ybyq01j30fy11ewgq.jpg)

### GCF.y资源文件截图

简单定义了一个支持加减乘除、括号、一元运算符、布尔运算、if/else控制语句、while控制语句的上下文无关语法，详见 GCF.y 文件

![截屏2019-12-1918.41.41](https://tva1.sinaimg.cn/large/006tNbRwly1ga27ykiv97j30u00v7n1w.jpg)

### 输入文件code1.txt截图

支持简单的原始类型声明，if/else，while语句

![截屏2019-12-1918.40.20](https://tva1.sinaimg.cn/large/006tNbRwly1ga27ypk16fj313e0ekt9r.jpg)

### 输入Token截图(input1.txt)

通过上一次实验得到的Lex，将输入源文件转为token的形式

![截屏2019-12-1918.41.32](https://tva1.sinaimg.cn/large/006tNbRwly1ga27yvp30ij30u00v711b.jpg)

### 输出规约表达式的截图（output1.txt）

以空白行分割两次规约。

sentence指的是未规约之前的语句，reduce指的是此次规约所使用的产生式，result指的是最后一次产生的结果

最后将文法规约为grammar即结束

![截屏2019-12-1918.40.59](https://tva1.sinaimg.cn/large/006tNbRwly1ga27z370r0j30u00v7dlv.jpg)



## 1.实现LRParser需要的具体步骤

1. 从CGF.y中读取上下文无关文法
2. 获得拓展文法
3. 根据拓展文法构造整个LR项集
4. 由LR项集组成的GOTO图构建预测分析表
5. 根据预测分析表进行规约



## 2.重要的转化步骤的实现

### 2.1 从CGF.y中读取上下文无关文法

#### 2.1.1 确定终结符与非终结符

因为采用了yacc和lex松耦合的设计，因此yacc读入流将是lex生成的Token序列，因此将所有的Token模式名都定义为终结符。在书写时，终结符需要用两个单引号包围来表示。否则表示一个非终结符。

比如 `'number'` 是一个终结符，而 `expression` 是一个非终结符。

实践中，先定义了一个密封类Symbol，以及Symbol的三个子类TerminalSymbol, NonterminalSymbol, EpsilonSymbol，这样便于使用when表达式进行Symbol的实际类型判断。



### 2.2 获得拓展文法

为了确定哪些非终结符是开始符，并添加额外的START产生式。

我才用的方法是：如果一个非终结符没有出现在除以自身为产生式左部的产生式右部中，那么这个这个非终结符是开始符

```kotlin
// should not exist in other production's derivation part
startProds = tempProds
	.map { it.symbol }
	.toSet()
	.filter { symbol ->
		tempProds.filter { it.symbol != symbol  } // except it self
		.all { !it.derive.contains(symbol) }
	}
	.map { Production(ALL_START_SYMBOL, it.content) }
```

- 将产生式映射为左部符号的集合
- 过滤以自身为左部的产生式
- 判断是否出现在产生式的右部
- 将符合条件的符号映射到 START -> a上

这里借助kotlin的集合操作，较为简洁地实现了需求。



### 2.3 根据拓展文法构造整个LR项集

#### 2.3.1 first()方法（详见Grammar.kt）

```kotlin
    fun first(symbols: List<Symbol>): Set<TerminalSymbol> {
        require(symbols.isNotEmpty()) {
            "Cannot find first() for an empty list<yacc.Symbol>"
        }

        // take first symbol of list
        val firstSym = symbols.first()

        // return if first symbol is terminal symbol
        if (firstSym is TerminalSymbol) return setOf(firstSym)

        // find first(firstSym)
        val termSet = mutableSetOf<TerminalSymbol>()
        for (prod in prods.filter { it.symbol == firstSym }) {
            termSet += first(prod.derive)
        }

        // if symbol set of first symbol does not contains epsilon or this is the last
        if (!termSet.contains(EpsilonSymbol()) || symbols.size == 1) return termSet

        val leftTermSet = first(symbols.drop(1))

        // not all set contains epsilon, remove epsilon
        if (!leftTermSet.contains(EpsilonSymbol())) {
            termSet.remove(EpsilonSymbol())
        }
        return termSet + leftTermSet
    }
```

- 取出列表头部元素，判断是否是终结符，如果是则直接返回单元素的集合
- 递归调用`first()` 方法，获得头部元素的first集合
- 如果该first集合不包含 $\epsilon$ 终结符，或者这是列表中最后一个元素，直接返回first集合
- 递归调用first求除头部元素以外的first集合
- 去掉 $\epsilon$ 除非所有集合都含有 $\epsilon$



#### 2.3.2 closure()方法 （详见LRItemSet.kt）

```kotlin
    // in place function
    fun closure(): LRItemSet {
        // A -> a.Bc, d
        val itemQueue: Deque<LRItem> = ArrayDeque(itemSet)
        while (itemQueue.isNotEmpty()) {
            val item = itemQueue.poll()
            val symbol = item.symbolBehindDot
            if (symbol is TerminalSymbol) continue
            // B -> y
            for (prod in grammar.prodsDeriveFrom(symbol as NonterminalSymbol)) {
                // b in FIRST(cd)
                val symList: List<Symbol> =
                        if (item.symbolAcrossDot == null)
                            listOf(item.lookAheadSymbol)
                        else
                            listOf(item.symbolAcrossDot!!, item.lookAheadSymbol)
                for (termSym in grammar.first(symList)) {
                    // B -> .y, b
                    val newItem = LRItem(prod, termSym, 0)
                    if (newItem in itemSet) continue
                    addItem(newItem)
                    itemQueue.add(newItem)
                }
            }
        }
        return this
    }
```

在LRItem类中定义 `lookAheadSymbol` 来表示向前看符，定义 `symbolBehindDot` 表示点之后存在的符号，如果点在产生式的最后，则返回 `$` 。

定义 `symbolAcrossDot` 表示点之后第二个符号 $A \to a . Bc$ 中的 c。

在 `closure()` 方法中使用一个队列来保存待处理的产生式，如果生成的新产生式不在总的 `itemSet` 中，则将其加入。



#### 2.3.3 goto()方法 （详见LRItemSet.kt）

```kotlin
    fun goto(sym: Symbol): LRItemSet {
        require(sym in symbolsBehindDot) {
            "$sym is not behind dot"
        }
        val itemSet = LRItemSet(grammar)
        for (item in this.itemSet) {
            if (item.symbolBehindDot == sym) {
                itemSet.addItem(LRItem(item.production, item.lookAheadSymbol, item.dotIndex + 1))
            }
        }
        return itemSet.closure()
    }
```

先用grammar初始化一个LR项集，将本LR项中能够通过sym得到的新LRItem放入新的LR项集中



### 2.4 由LR项集组成的GOTO图构建预测分析表

#### 2.4.1 建立GOTO图

```kotlin
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
```

使用一个集合队列 `setQueue` 来保存所有的未处理LR项集，使用一个 `setList` 来保存所有产生的LR项集，对于一个LR项集，使用 `goto()` 方法产生所有能够到达的LR项集，如果新LR项集不存在 `setList` 中，则将其加入。并使用一个 `val adjacent: MutableMap<Int, MutableSet<DirectEdge>>` 邻接表来保存项集之间的有向边。



#### 2.4.2 建立预测分析表

```kotlin
sealed class Action(val id: Int) {
    class Shift(id: Int): Action(id)
    class Reduce(id: Int): Action(id)
    class GOTO(id: Int): Action(id)
    class ACC: Action(0)
}
```

使用 `val table: MutableMap<Pair<Int, Symbol>, Action>` 来表示一个二维表，以一个集合标号和Symbol的二元组来作为二维表的索引，其中Action是一个密封类，来让我们较为容易地判断预测分析表中的具体动作。

```kotlin
       for (itemSet in graph.itemSets) {
            for ((sym, prodId) in itemSet.reduceAbleSet()) {
                val key = itemSet.id to sym
                if (prodId == 0) table[key] = Action.ACC()
                else table[key] = Action.Reduce(min(prodId, table[key]?.id ?: Int.MAX_VALUE))
            }
        }
```

- 遍历GOTO图中所有的 LR项集
- 获得该项集中能够进行规约的二元组
- 如果是零号产生式，则向表中加入ACC，否则加入Reduce
- 在加入Reduce时，该表项可能会产生规约-规约冲突，此时选择产生式Id较小的那个，因为Id较小产生式比较靠前，优先级高

```kotlin
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
```

- 对GOTO图的有向边进行遍历
- 如果是一个非终结符，向表中加入GOTO
- 如果是一个终结符，如果没有产生操作符移入-规约冲突，则向表中加入Shift
- 如果产生操作符移入-规约冲突，则通过判断两个操作符的优先级，来决定最后是移入还是规约



### 2.5 根据预测分析表进行规约(详见analyzeTable.kt)

```kotlin
				while (!isAcc) {
            val s = stateStack.peek()
            when(val action = table[s to Symbol.TerminalSymbol(currSym)]) {
                is Action.Shift -> {
                    stateStack.push(action.id)
                    // move to next symbol
                    scanner += 1
                    tempPointer += 1
                    currSym = tokens[scanner]
                }
                is Action.Reduce -> {
                    // A -> B
                    val prod = grammar.prods[action.id]
                    // pop num |B|
                    res.add("sentence: " + tempTokens.joinToString(separator = " "))

                    for (symbol in prod.derive) {
                        tempPointer -= 1
                        stateStack.pop()
                        // replace E -> E * E with E
                        tempTokens.removeAt(tempPointer)
                    }
                    tempTokens.add(tempPointer, prod.symbol.content)
                    tempPointer += 1

                    // t is stack peek, so push(GOTO[t, A])
                    stateStack.push(table[stateStack.peek() to prod.symbol]!!.id)
                    res.add("reduce: " + prod.getHumanString())
                    res.add("")
                }
                is Action.ACC -> {
                    res.add("result: " + tempTokens.joinToString(separator = " "))
                    isAcc = true
                }
                null -> {
                    val reduced =
                            if(res.isNotEmpty())
                                "Part of input have been reduced\n" + res.joinToString("\n") + "\n"
                            else "\n"
                    val pointer = " ".repeat(tokens.take(scanner).sumBy { it.length + 1 }) + "^"
                    val error = """Reduce broken at: 
                        |   ${tokens.joinToString(" ")}
                        |   $pointer
                        |   please check your input!!!
                        |
                        """.trimMargin()
                    throw ReduceException(reduced + error)
                }
            }
        }
```

在规约的过程中，对规约产生式进行记录，并记录sentence变化的过程。

使用when语句来对查表得到的Action进行子类判断

- 移入：push当前状态入栈
- 规约：pop状态出栈，并将sentence中的字符串替换为产生式的左部
- ACC：成功，跳出循环
- null：查表失败，抛出异常



## 3 Error handling

### 3.1 规约Token序列

如果通过预测分析表无法找到action时，抛出规约异常么，并提供用户友好的指示。

例如，对于一个赋值语句 `int a = 5;` ，如果忘记输入赋值符号 `=` ，则会提醒用户输入错误

![截屏2019-12-1920.39.01](https://tva1.sinaimg.cn/large/006tNbRwly1ga2bc933irj313e0mkwhb.jpg)





## 4 感受和实验评价

在本次编译原理实验中，我使用kotlin语言实现了一个yacc。通过本次实验，我更加深入理解了从上下文无关文法到构造LR预测分析表的过程。对于first(), closure(), goto()等主要方法有了更好地掌握。