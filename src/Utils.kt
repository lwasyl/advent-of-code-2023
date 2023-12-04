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

fun TimedValue<Any>.println(prefix: String? = null) =
    kotlin.io.println("$prefix: $value [${duration.toString(unit = DurationUnit.MILLISECONDS, decimals = 4)}]")

internal fun testAll(
    day: Int,
    part1Fn: KFunction1<List<String>, Any>,
    part1TestSolution: Any,
    part2Fn: KFunction1<List<String>, Any>,
    part2TestSolution: Any?,
) {
    val dayString = "Day${String.format("%02d", day)}"
    readInput("${dayString}_test")
        .let { measureTimedValue { part1Fn(it) } }
        .let {
            it.println("Part 1 test")
            check(it.value == part1TestSolution)
        }

    readInput(dayString)
        .let { measureTimedValue { part1Fn(it) } }
        .println("Part 1 output")

    if (part2TestSolution == null) return

    readInput("${dayString}_2_test")
        .let { measureTimedValue { part2Fn(it) } }
        .let {
            it.println("Part 2 test")
            check(it.value == part2TestSolution)
        }

    readInput(dayString)
        .let { measureTimedValue { part2Fn(it) } }
        .println("Part 2 output")
}
