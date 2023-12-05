import java.util.ArrayDeque
import kotlin.math.pow

fun main() {

    fun parseCard(input: String): Card {
        val (cardText, numberText) = input.split(':')
        val (winningNumbers, ownNumbers) = numberText.split('|')

        return Card(
            number = cardText.substringAfter(' ').trimToInt(),
            winningNumbers = winningNumbers.splitToInts().toSet(),
            ownNumbers = ownNumbers.splitToInts().toSet(),
        )
    }

    fun Card.score() = winningNumbers.intersect(ownNumbers).count()

    fun part1(input: List<String>): Int {
        return input
            .asSequence()
            .map(::parseCard)
            .map(Card::score)
            .filter { it >= 1 }
            .map { 2.0.pow(it - 1.0).toInt() }
            .sum()
    }

    fun part2(input: List<String>): Int {
        var score = 0
        val cardNumbersToProcess = ArrayDeque(List(input.size) { it + 1 })
        val scores = input.asSequence()
            .map(::parseCard)
            .associate { it.number to it.score() }

        while (cardNumbersToProcess.isNotEmpty()) {
            val number = cardNumbersToProcess.removeLast()
            val cardScore = scores.getValue(number)
            cardNumbersToProcess.addAll(List(cardScore) { number + 1 + it })

            score += 1
        }

        return score
    }

    testAll(
        day = 4,
        part1Fn = ::part1,
        part1TestSolution = 13,
        part2Fn = ::part2,
        part2TestSolution = 30,
    )
}

data class Card(
    val number: Int,
    val winningNumbers: Set<Int>,
    val ownNumbers: Set<Int>,
)
