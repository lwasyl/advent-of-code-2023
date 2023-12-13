fun main() {

    fun parseInput(input: Pattern): Day13Input {
        return Day13Input(
            input.splitWhen { it.isBlank() }
        )
    }

    fun findSolution(input: List<String>, smudges: Int): Int {
        return parseInput(input).patterns
            .sumOf { pattern ->
                findSymmetryWithSmudges(pattern, smudges)?.times(100)
                    ?: findSymmetryWithSmudges(pattern.transpose(), smudges)
                    ?: error("no symmetry")
            }
    }

    fun part1(input: List<String>): Int {
        return findSolution(input = input, smudges = 0)
    }

    fun part2(input: List<String>): Int {
        return findSolution(input = input, smudges = 1)
    }

    testAll(
        day = 13,
        part1Fn = ::part1,
        part1TestSolutions = listOf(405),
        part2Fn = ::part2,
        part2TestSolutions = listOf(400),
    )
}

fun findSymmetryWithSmudges(pattern: Pattern, smudges: Int = 0): Int? {
    return (0..pattern.size - 2).firstNotNullOfOrNull { symmetryLine ->
        var leftEdge = symmetryLine
        var rightEdge = symmetryLine + 1
        var smudgesCount = 0
        while (leftEdge >= 0 && rightEdge < pattern.size && smudgesCount <= smudges) {
            smudgesCount += pattern[leftEdge].diffCount(pattern[rightEdge])

            leftEdge -= 1
            rightEdge += 1
        }
        if (smudgesCount == smudges) symmetryLine + 1 else null
    }
}

fun String.diffCount(other: String) = this.zip(other).count { it.first != it.second }

fun List<String>.transpose() = List(first().length) { row ->
    List(size) { col ->
        this@transpose[col][row]
    }
        .joinToString(separator = "")
}

private data class Day13Input(
    val patterns: List<Pattern>,
)

typealias Pattern = List<String>

fun <T> Collection<T>.splitWhen(predicate: (T) -> Boolean) =
    fold(mutableListOf(mutableListOf<T>())) { acc, line ->
        if (predicate(line)) {
            acc.add(mutableListOf())
        } else {
            acc.last().add(line)
        }

        acc
    }
        .filter { it.isNotEmpty() }
