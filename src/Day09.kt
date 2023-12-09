fun main() {

    fun parseInput(input: List<String>) = Day9Input(
        values = input.map { it.splitToLongs() },
    )

    fun buildDifferences(valueHistory: List<Long>): MutableList<List<Long>> {
        val differences = mutableListOf(valueHistory)
        while (differences.last().any { it != 0L }) {
            differences += differences.last().windowed(2).map { (left, right) -> right - left }
        }
        return differences
    }

    fun part1(input: List<String>): Long {
        return parseInput(input).values
            .asSequence()
            .map { valueHistory ->
                val differences = buildDifferences(valueHistory)

                differences.asReversed()
                    .asSequence()
                    .map { it.last() }
                    .reduce { diff, value -> value + diff }
            }
            .sum()
    }

    fun part2(input: List<String>): Long {
        return parseInput(input).values
            .asSequence()
            .map { valueHistory ->
                val differences = buildDifferences(valueHistory)

                differences.asReversed()
                    .asSequence()
                    .map { it.first() }
                    .reduce { diff, value -> value - diff }
            }
            .sum()
    }

    testAll(
        day = 9,
        part1Fn = ::part1,
        part1TestSolution = 114L,
        part2Fn = ::part2,
        part2TestSolution = 2L,
    )
}

private data class Day9Input(
    val values: List<List<Long>>,
)
