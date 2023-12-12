import kotlin.properties.Delegates

fun main() {

    fun parseInput(input: List<String>): Day12Input {
        return Day12Input(
            springs = input.map { line ->
                line.split(' ').let { (states, groupsString) ->
                    SpringRow(
                        states = states,
                        groups = groupsString.splitToInts(','),
                    )
                }
            }
        )
    }

        fun part1(input: List<String>): Long {
        val memoizedSolution = memoized(::day12CountSolutions)

        return parseInput(input).springs
            .asSequence()
            .map { memoizedSolution(it.states, it.groups) }
            .sum()
    }

    fun part2(input: List<String>): Long {
        val memoizedSolution = memoized(::day12CountSolutions)

        return parseInput(input)
            .springs
            .asSequence()
            .map { row ->
                SpringRow(
                    states = (List(5) { row.states }).joinToString(separator = "?"),
                    groups = List(5) { row.groups }.flatten(),
                )
            }
            .map { memoizedSolution(it.states, it.groups) }
            .sum()
    }

    testAll(
        day = 12,
        part1Fn = ::part1,
        part1TestSolutions = listOf(21L),
        part2Fn = ::part2,
        part2TestSolutions = listOf(525152L),
    )
}

private fun day12CountSolutions(input: String, groups: List<Int>, recurse: (String, List<Int>) -> Long): Long {
    when {
        input.isBlank() && groups.isEmpty() -> return 1L
        input.isBlank() && groups.isNotEmpty() -> return 0L
        input.isNotEmpty() && groups.isEmpty() -> return (if (input.any { it == '#' }) 0L else 1L)
        input.length < groups.first() -> return 0L
        input.length < groups.sum() + groups.size - 1 -> return 0L
    }

    return when (input.first()) {
        '#' -> {
            val neededInRow = groups.first()

            (0 until neededInRow).forEach { if (input[it] == '.') return 0L }
            if (input.getOrNull(neededInRow) == '#') return 0L

            recurse(input.drop(neededInRow + 1), groups.drop(1))
        }
        '.' -> recurse(input.drop(1), groups)
        '?' -> recurse("#${input.drop(1)}", groups) + recurse(".${input.drop(1)}", groups)
        else -> error("unknown input")
    }
}

private data class Day12Input(
    val springs: List<SpringRow>,
)

private data class SpringRow(
    val states: String,
    val groups: List<Int>,
)

fun <P1, P2, R> memoized(fn: (P1, P2, (P1, P2) -> R) -> R): ((P1, P2) -> R) {
    val cache = mutableMapOf<Pair<P1, P2>, R>()
    var recursiveCall: ((P1, P2) -> R) by Delegates.notNull()
    recursiveCall = { p1, p2 ->
        cache.getOrPut(p1 to p2) { fn(p1, p2, recursiveCall) }
    }
    return recursiveCall
}
