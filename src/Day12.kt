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
        return parseInput(input).springs
            .asSequence()
            .map { day12CountSolutions(it.states, it.groups) }
            .sum()
    }

    fun part2(input: List<String>): Long {
        return parseInput(input)
            .springs
            .asSequence()
            .map { row ->
                SpringRow(
                    states = (List(5) { row.states }).joinToString(separator = "?"),
                    groups = List(5) { row.groups }.flatten(),
                )
            }
            .map { day12CountSolutions(it.states, it.groups) }
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

private val day12Cache = mutableMapOf<Pair<String, List<Int>>, Long>()
private fun day12CountSolutions(input: String, groups: List<Int>): Long {
    day12Cache[input to groups]?.let { return it }
    fun Long.alsoCache() = this.also { day12Cache[input to groups] = it }

    when {
        input.isBlank() && groups.isEmpty() -> return 1L.alsoCache()
        input.isBlank() && groups.isNotEmpty() -> return 0L.alsoCache()
        input.isNotEmpty() && groups.isEmpty() -> return (if (input.any { it == '#' }) 0L else 1L).alsoCache()
        input.length < groups.first() -> return 0L.alsoCache()
        input.length < groups.sum() + groups.size - 1 -> return 0L.alsoCache()
    }

    return when (input.first()) {
        '#' -> {
            val neededInRow = groups.first()

            (0 until neededInRow).forEach { if (input[it] == '.') return 0L.alsoCache() }
            if (input.getOrNull(neededInRow) == '#') return 0L.alsoCache()

            day12CountSolutions(input.drop(neededInRow + 1), groups.drop(1))
        }
        '.' -> day12CountSolutions(input.drop(1), groups)
        '?' -> day12CountSolutions("#${input.drop(1)}", groups) + day12CountSolutions(".${input.drop(1)}", groups)
        else -> error("unknown input")
    }.alsoCache()
}

private data class Day12Input(
    val springs: List<SpringRow>,
)

private data class SpringRow(
    val states: String,
    val groups: List<Int>,
)
