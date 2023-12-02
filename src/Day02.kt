import java.util.regex.Pattern
import kotlin.streams.asSequence

fun main() {
    val redPattern = "(\\d+) red".toPattern()
    val greenPatter = "(\\d+) green".toPattern()
    val bluePattern = "(\\d+) blue".toPattern()

    fun String.findCount(pattern: Pattern) = pattern.matcher(this).run {
        results()
            .asSequence()
            .map { it.group(1).toInt() }
            .sum()
    }

    fun parseGame(input: String): Game {
        val (gameText, drawsText) = input.split(':')

        return Game(
            id = gameText.substringAfter(' ').toInt(),
            draws = drawsText.split(';')
                .mapTo(mutableSetOf()) { drawText ->
                    Draw(
                        red = drawText.findCount(redPattern),
                        green = drawText.findCount(greenPatter),
                        blue = drawText.findCount(bluePattern),
                    )
                }
        )
    }

    fun part1(input: List<String>): Int {
        val drawPossible = { draw: Draw ->
            draw.red <= 12 && draw.green <= 13 && draw.blue <= 14
        }

        return input
            .asSequence()
            .map(::parseGame)
            .filter { game -> game.draws.all(drawPossible) }
            .sumOf { it.id }
    }

    fun part2(input: List<String>): Int {
        return input
            .asSequence()
            .map(::parseGame)
            .map(Game::draws)
            .map { draws -> draws.maxOf { it.red } * draws.maxOf { it.green } * draws.maxOf { it.blue } }
            .sum()
    }

    testAll(
        day = 2,
        part1Fn = ::part1,
        part1TestSolution = 8,
        part2Fn = ::part2,
        part2TestSolution = 2286
    )
}

private data class Draw(
    val red: Int,
    val green: Int,
    val blue: Int,
)

private data class Game(
    val id: Int,
    val draws: Set<Draw>,
)
