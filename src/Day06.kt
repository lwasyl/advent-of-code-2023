import kotlin.math.nextDown
import kotlin.math.sqrt

fun main() {

    fun parseInput(input: List<String>) = input.first().substringAfter(':').splitToLongs()
        .zip(input[1].substringAfter(':').splitToLongs())
        .map { Race(it.first, it.second) }
        .toSet()
        .let(::RacesData)

    fun parseInputForPartTwo(input: List<String>) = RacesData(
        setOf(
            Race(
                input.first().substringAfter(':').replace(" ", "").toLong(),
                input[1].substringAfter(':').replace(" ", "").toLong()
            )
        )
    )

    fun Race.winningRacesCountNaive(): Long {
        // Could be half the range and the result * 2, but even durations add non-duplicated result in the middle so meh.
        val possibleScores = (1 until duration)
            .asSequence()
            .map { it * (duration - it) }

        return possibleScores
            .filter { it > record }
            .count()
            .toLong()
    }

    // Alternative analytical solution
    fun Race.winningRacesCountAnalytical(): Long {
        val maxPressDuration = ((sqrt(duration.toDouble() * duration.toDouble() - 4 * record) + duration) / 2.0).nextDown().toLong()
        val minPressedDuration = duration - maxPressDuration

        return maxPressDuration - minPressedDuration + 1
    }

    fun part1(input: List<String>): Long {
        val data = parseInput(input)

        return data.races.asSequence()
            .map { race ->
                val analytical = race.winningRacesCountAnalytical()
                // val naive = race.winningRacesCountNaive()
                // check(naive == analytical)
                analytical
            }
            .fold(1) { acc, value -> acc * value }
    }

    fun part2(input: List<String>): Long {
        return parseInputForPartTwo(input)
            .races
            .single()
            .let { race ->
                val analytical = race.winningRacesCountAnalytical()
                // val naive = race.winningRacesCountNaive()
                // check(naive == analytical)
                analytical
            }
    }

    testAll(
        day = 6,
        part1Fn = ::part1,
        part1TestSolution = 288L,
        part2Fn = ::part2,
        part2TestSolution = 71503L,
    )
}

private data class RacesData(
    val races: Set<Race>,
)

private data class Race(
    val duration: Long,
    val record: Long,
)
