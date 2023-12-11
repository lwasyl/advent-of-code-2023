import kotlin.math.abs

fun main() {

    fun parseInput(input: List<String>): Day11Input {
        return Day11Input(
            sky = input.map { line -> line.map { it } }
        )
    }

    fun distancesSum(expandedGalaxies: List<Coords>) = expandedGalaxies
        .asSequence()
        .flatMapIndexed { index: Int, first: Coords ->
            expandedGalaxies.asSequence().drop(index + 1).map { second -> first to second }
        }
        .map { abs(it.first.row.toLong() - it.second.row) + abs(it.first.col.toLong() - it.second.col) }
        .sum()

    fun part1(input: List<String>): Long {
        return parseInput(input)
            .sky
            .expand()
            .findCoordsMatching { it == '#' }
            .let(::distancesSum)
    }

    fun part2(input: List<String>): Long {
        val sky = parseInput(input).sky

        val galaxies = sky.findCoordsMatching { it == '#' }
        val allRows = List(sky.size) { it }.toSet()
        val allColumns = List(sky.first().size) { it }.toSet()

        val rowsWithGalaxies = galaxies.map { it.row }.toSet()
        val columnsWithGalaxies = galaxies.map { it.col }.toSet()

        val rowsToExpand = (allRows - rowsWithGalaxies).sorted().toSet()
        val columnsToExpand = (allColumns - columnsWithGalaxies).sorted().toSet()

        val expandedGalaxies = galaxies.map { (row, col) ->
            val newRow = row + (rowsToExpand.count { it < row } * 999_999)
            val newCol = col + (columnsToExpand.count { it < col } * 999_999)
            Coords(newRow, newCol)
        }
        return distancesSum(expandedGalaxies)
    }

    testAll(
        day = 11,
        part1Fn = ::part1,
        part1TestSolutions = listOf(374L),
        part2Fn = ::part2,
        part2TestSolutions = listOf(82000210L),
    )
}

private fun Sky.expand(): Sky {
    val galaxies = findCoordsMatching { it == '#' }
    val allRows = List(size) { it }.toSet()
    val allColumns = List(first().size) { it }.toSet()

    val rowsWithGalaxies = galaxies.map { it.row }.toSet()
    val columnsWithGalaxies = galaxies.map { it.col }.toSet()

    val rowsToExpand = allRows - rowsWithGalaxies
    val columnsToExpand = allColumns - columnsWithGalaxies

    return flatMapIndexed { row: Int, line: List<Char> ->
        val mappedLine = line.flatMapIndexed { col: Int, char: Char ->
            if (col in columnsToExpand) listOf(char, char) else listOf(char)
        }

        if (row in rowsToExpand) listOf(mappedLine, mappedLine) else listOf(mappedLine)
    }
}

data class Day11Input(
    val sky: Sky,
)

typealias Sky = List<List<Char>>

fun <T> List<List<T>>.findCoordsMatching(predicate: (T) -> Boolean) = flatMapIndexed { row: Int, line: List<T> ->
    line.mapIndexedNotNull { col, item -> if (predicate(item)) Coords(row, col) else null }
}

fun <T> List<List<T>>.println() {
    kotlin.io.println(
        joinToString(separator = "\n") { line ->
            line.joinToString(separator = "") { " $it " }
        }
    )
}
