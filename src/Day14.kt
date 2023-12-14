fun main() {

    fun List<String>.moveRocksNorth() = transpose()
        .map { line ->
            line.split("#")
                .joinToString(separator = "#") { segment ->
                    segment.toCharArray()
                        .sortedBy(terrainCharComparator)
                        .joinToString(separator = "")
                }
        }
        .toList()
        .transpose()

    fun List<String>.calculateLoad() = transpose().asSequence()
        .map { line ->
            line.foldIndexed(0) { idx, acc, char ->
                acc + if (char == 'O') line.length - idx else 0
            }
        }
        .sum()

    fun part1(input: List<String>): Int {

        return input.moveRocksNorth().calculateLoad()
    }

    fun part2(input: List<String>): Int {
        val previousStates = mutableListOf(input)
        var newState = performCycle(input)
        var cycleCount = 1

        while (newState !in previousStates) {
            previousStates += newState
            newState = performCycle(newState)
            cycleCount++
        }

        val cycleSize = (cycleCount - previousStates.indexOf(newState))

        return previousStates
            .takeLast(cycleSize)
            .get((1_000_000_000 - cycleCount).mod(cycleSize))
            .calculateLoad()
    }

    testAll(
        day = 14,
        part1Fn = ::part1,
        part1TestSolutions = listOf(136),
        part2Fn = ::part2,
        part2TestSolutions = listOf(64),
    )
}

fun performCycle(terrain: List<String>): List<String> {
    // move north
    return terrain
        .transpose()
        .map { line ->
            line.split("#")
                .joinToString(separator = "#") { segment ->
                    segment.toCharArray()
                        .sortedBy(terrainCharComparator)
                        .joinToString(separator = "")
                }
        }
        .transpose()
        // move east
        .map { line ->
            line.split("#")
                .joinToString(separator = "#") { segment ->
                    segment.toCharArray()
                        .sortedBy(terrainCharComparator)
                        .joinToString(separator = "")
                }
        }
        // move south
        .transpose()
        .map { line ->
            line.split("#")
                .joinToString(separator = "#") { segment ->
                    segment.toCharArray()
                        .sortedByDescending(terrainCharComparator)
                        .joinToString(separator = "")
                }
        }
        .transpose()
        // move west
        .map { line ->
            line.split("#")
                .joinToString(separator = "#") { segment ->
                    segment.toCharArray()
                        .sortedByDescending(terrainCharComparator)
                        .joinToString(separator = "")
                }
        }
}

val terrainCharComparator = { char: Char ->
    when (char) {
        'O' -> 0
        '.' -> 1
        else -> error("unknown character")
    }
}
