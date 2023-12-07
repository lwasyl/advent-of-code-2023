fun main() {

    fun parseInput(input: List<String>) = input
        .map {
            val (hand, bid) = it.split(' ')
            HandWithBid(
                hand = Hand(cards = hand.map(PlayingCard.Companion::fromChar)),
                bid = bid.toInt(),
            )
        }

    fun part1(input: List<String>): Int {
        fun Hand.getRank(): HandRank {
            val rankString = cards
                .groupBy { it }
                .values
                .map { it.size }
                .sortedDescending()
                .joinToString(separator = "")

            return when (rankString) {
                "5" -> HandRank.FiveOfKind
                "41" -> HandRank.FourOfKind
                "32" -> HandRank.FullHouse
                "311" -> HandRank.ThreeOfKind
                "221" -> HandRank.TwoPairs
                "2111" -> HandRank.OnePair
                "11111" -> HandRank.HighCard
                else -> error("Shouldn't happen")
            }
        }

        return parseInput(input)
            .sortedWith { left, right ->
                left.hand.getRank().compareTo(right.hand.getRank()).takeIf { it != 0 }
                    ?: left.hand.cards.zip(right.hand.cards)
                        .firstNotNullOf { cards -> cards.first.compareTo(cards.second).takeIf { it != 0 } }
            }
            .asReversed()
            .mapIndexed { index, handWithBid ->
                handWithBid.bid * (input.size - index)
            }
            .sum()
    }

    fun part2(input: List<String>): Int {
        fun PlayingCard.strength() = if (this == PlayingCard.J) -1 else this.ordinal

        fun Hand.getRank(): HandRank {
            val rankString = cards
                .groupBy { it }
                .mapValues { it.value.size }
                .toList()
                .let { handCards ->
                    val handJ = handCards.firstOrNull { it.first == PlayingCard.J }
                    if (handJ == null) return@let handCards

                    val mostCommonNotJ = handCards
                        .sortedByDescending { it.second }
                        .firstOrNull { it.first != PlayingCard.J }

                    if (mostCommonNotJ != null) {
                        handCards.minus(handJ)
                            .minus(mostCommonNotJ)
                            .plus(mostCommonNotJ.first to mostCommonNotJ.second + handJ.second)
                    } else {
                        handCards
                    }
                }
                .map { it.second }
                .sortedDescending()
                .joinToString(separator = "")

            return when (rankString) {
                "5" -> HandRank.FiveOfKind
                "41" -> HandRank.FourOfKind
                "32" -> HandRank.FullHouse
                "311" -> HandRank.ThreeOfKind
                "221" -> HandRank.TwoPairs
                "2111" -> HandRank.OnePair
                "11111" -> HandRank.HighCard
                else -> error("Shouldn't happen ($rankString)")
            }
        }

        return parseInput(input)
            .sortedWith(
                compareBy<HandWithBid>(selector = { it.hand.getRank() })
                    .thenComparing(
                        compareBy<HandWithBid>(selector = { it.hand.getRank() })
                            .thenComparing(
                                { handWithBid -> handWithBid.hand.cards.map { it.strength() } },
                                { left, right ->
                                    left.zip(right)
                                        .map { it.first.compareTo(it.second) }
                                        .firstOrNull { it != 0 }
                                        ?: 0
                                },
                            )
                    )
            )
            .asReversed()
            .mapIndexed { index, handWithBid ->
                handWithBid.bid * (input.size - index)
            }
            .sum()
    }

    testAll(
        day = 7,
        part1Fn = ::part1,
        part1TestSolution = 6440,
        part2Fn = ::part2,
        part2TestSolution = 5905,
    )
}

private data class HandWithBid(
    val hand: Hand,
    val bid: Int,
)

private data class Hand(val cards: List<PlayingCard>)

private enum class HandRank {
    HighCard,
    OnePair,
    TwoPairs,
    ThreeOfKind,
    FullHouse,
    FourOfKind,
    FiveOfKind,
}

private enum class PlayingCard {
    V2,
    V3,
    V4,
    V5,
    V6,
    V7,
    V8,
    V9,
    T,
    J,
    Q,
    K,
    A;

    companion object {

        fun fromChar(char: Char): PlayingCard {
            val name = if (char.isDigit()) "V$char" else "$char"
            return PlayingCard.entries.first { it.name == name }
        }
    }
}
