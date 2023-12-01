fun main() {
    fun part1(input: List<String>): Int {
        return input
            .asSequence()
            .map { line ->
                10 * line.first { it.isDigit() }.digitToInt() + line.last { it.isDigit() }.digitToInt()
            }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val digits = mapOf(
            "0" to 0, "1" to 1, "2" to 2, "3" to 3, "4" to 4, "5" to 5, "6" to 6, "7" to 7, "8" to 8, "9" to 9,
            "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5, "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9,
        )
        return input
            .asSequence()
            .map { line ->
                val firstDigit = digits
                    .map { (spelledOut, digit) -> line.indexOf(spelledOut) to digit }
                    .filterNot { it.first == -1 }
                    .minBy { it.first }
                    .second

                val secondDigit = digits
                    .map { (spelledOut, digit) -> line.lastIndexOf(spelledOut) to digit }
                    .filterNot { it.first == -1 }
                    .maxBy { it.first }
                    .second

                firstDigit * 10 + secondDigit
            }
            .sum()
    }

    testAll(
        day = 1,
        part1Fn = ::part1,
        part1TestSolution = 142,
        part2Fn = ::part2,
        part2TestSolution = 281
    )
}
