import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.reflect.KFunction1
import kotlin.time.DurationUnit
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println(prefix: String? = null) = kotlin.io.println("$prefix: $this")

fun TimedValue<out Any>.println(prefix: String? = null) =
    kotlin.io.println("$prefix: $value [${duration.toString(unit = DurationUnit.MILLISECONDS, decimals = 4)}]")

internal fun testAll(
    day: Int,
    part1Fn: KFunction1<List<String>, Any>,
    part1TestSolution: Any,
    part2Fn: KFunction1<List<String>, Any>,
    part2TestSolution: Any?,
) {
    testAll(
        day = day,
        part1Fn = part1Fn,
        part1TestSolutions = listOf(part1TestSolution),
        part2Fn = part2Fn,
        part2TestSolutions = listOfNotNull(part2TestSolution),
    )
}

internal fun testAll(
    day: Int,
    part1Fn: KFunction1<List<String>, Any>,
    part1TestSolutions: List<Any>,
    part2Fn: KFunction1<List<String>, Any>,
    part2TestSolutions: List<Any>,
) {
    val dayString = "Day${String.format("%02d", day)}"
    sequence {
        var index = 0
        while (true) {
            yield(index to "${dayString}_test${if (index == 0) "" else "_${index + 1}"}")
            index++
        }
    }
        .map { it.first to runCatching { readInput(it.second) }.getOrNull() }
        .takeWhile { it.second != null }
        .forEach { (idx, input) ->
            measureTimedValue { part1Fn(input!!) }
                .let {
                    it.println("Part 1 test [$idx]")
                    check(it.value == part1TestSolutions[idx])
                }
        }

    readInput(dayString)
        .let { measureTimedValue { part1Fn(it) } }
        .println("Part 1 output")

    if (part2TestSolutions.isEmpty()) return
    sequence {
        var index = 0
        while (true) {
            yield(index to "${dayString}_2_test${if (index == 0) "" else "_${index + 1}"}")
            index++
        }
    }
        .map { it.first to runCatching { readInput(it.second) }.getOrNull() }
        .takeWhile { it.second != null }
        .forEach { (idx, input) ->
            measureTimedValue { part2Fn(input!!) }
                .let {
                    it.println("Part 2 test [$idx]")
                    check(it.value == part2TestSolutions[idx])
                }
        }

    readInput(dayString)
        .let { measureTimedValue { part2Fn(it) } }
        .println("Part 2 output")
}

fun String.splitToLongs(delimiter: Char = ' ') = split(delimiter).mapNotNull { it.trimToLongOrNull() }
fun String.splitToInts(delimiter: Char = ' ') = split(delimiter).mapNotNull { it.trimToIntOrNull() }

fun String.trimToInt() = trim().toInt()
fun String.trimToIntOrNull() = trim().toIntOrNull()
fun String.trimToLong() = trim().toLong()
fun String.trimToLongOrNull() = trim().toLongOrNull()

fun lcm(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}

fun lcm(numbers: List<Long>): Long {
    var result = numbers[0]
    for (i in 1 until numbers.size) {
        result = lcm(result, numbers[i])
    }
    return result
}
