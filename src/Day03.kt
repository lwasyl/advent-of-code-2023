import kotlin.streams.asSequence

fun main() {

    val numberPattern = "\\d+".toPattern()

    fun getAllNumbers(input: List<String>) = input.mapIndexed { rowIdx, line ->
        numberPattern.matcher(line).results()
            .asSequence()
            .map {
                NumberOnSchematic(
                    pos = Pos(row = rowIdx, col = it.start()),
                    length = it.end() - it.start(),
                    value = it.group().toInt()
                )
            }

    }
        .asSequence()
        .flatMap { it }

    fun NumberOnSchematic.getAdjacentSymbols(input: List<String>) = sequence {
        ((pos.row - 1)..(pos.row + 1)).forEach { row ->
            ((pos.col - 1)..(pos.col + length)).forEach { col ->
                val symbolAtPosition = input.getOrNull(row)?.getOrNull(col)

                if (symbolAtPosition != null) {
                    yield(
                        SymbolOnSchematic(
                            pos = Pos(row, col),
                            symbol = symbolAtPosition
                        )
                    )
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        return getAllNumbers(input)
            .filter { number ->
                number.getAdjacentSymbols(input)
                    .map { it.symbol }
                    .any { symbol -> !symbol.isDigit() && symbol != '.' }
            }
            .sumOf { it.value }
    }

    fun part2(input: List<String>): Int {
        return getAllNumbers(input)
            .flatMap { number ->
                number
                    .getAdjacentSymbols(input).filter { it.symbol == '*' }
                    .map { it.pos to number.value }
            }
            .groupBy(
                keySelector = { it.first },
                valueTransform = { it.second },
            )
            .map { it.value }
            .filter { it.size == 2 }
            .sumOf { it[0] * it[1] }
    }

    testAll(
        day = 3,
        part1Fn = ::part1,
        part1TestSolution = 4361,
        part2Fn = ::part2,
        part2TestSolution = 467835,
    )
}

private data class Pos(val row: Int, val col: Int)

private data class SymbolOnSchematic(val pos: Pos, val symbol: Char)

private data class NumberOnSchematic(val pos: Pos, val length: Int, val value: Int)
