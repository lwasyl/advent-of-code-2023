fun main() {

    fun parseAlmanac(input: List<String>): Almanac {
        fun parseConversionRange(line: String): LayerMap.ConversionRange {
            val (destinationStart, sourceStart, rangeLength) = line.splitToLongs()

            return LayerMap.ConversionRange(
                sourceRange = sourceStart until sourceStart + rangeLength,
                offset = destinationStart - sourceStart
            )
        }

        return input
            .fold(mutableListOf<MutableList<LayerMap.ConversionRange>>()) { allMaps, line ->
                when {
                    line.isBlank() -> Unit
                    line.contains("map") -> allMaps.add(mutableListOf())
                    else -> allMaps.last().add(parseConversionRange(line))
                }

                allMaps
            }
            .map { LayerMap(it.toSet()) }
            .let(::Almanac)
    }

    fun parseSeeds(input: String) = input.substringAfter(": ").splitToLongs()

    fun part1(input: List<String>): Long {
        val seeds = parseSeeds(input.first())
        val almanac = parseAlmanac(input.drop(1))

        return seeds.asSequence()
            .map { almanac.getDestinationFor(it) }
            .min()
    }

    fun part2(input: List<String>): Long {
        val seedsInput = parseSeeds(input.first())
        val almanac = parseAlmanac(input.drop(1))

        val seedsInputRanges = seedsInput
            .windowed(size = 2, step = 2)
            .map { (rangeStart, rangeLength) -> rangeStart until rangeStart + rangeLength }

        val numbersOfInterest = almanac.maps.flatMapIndexed { index, conversionMap ->
            conversionMap.ranges.flatMap { listOf(it.sourceRange.first, it.sourceRange.last) }
                .let {
                    if (index == 0) {
                        it
                    } else {
                        val partialAlmanacUpToMap = Almanac(almanac.maps.take(index))
                        it.map(partialAlmanacUpToMap::getSourceFor)
                    }
                }
        }
            .filter { number -> seedsInputRanges.any { number in it } }
            .plus(seedsInputRanges.flatMap { listOf(it.first, it.last) })

        return numbersOfInterest
            .asSequence()
            .map { almanac.getDestinationFor(it) }
            .min()
    }

    testAll(
        day = 5,
        part1Fn = ::part1,
        part1TestSolution = 35L,
        part2Fn = ::part2,
        part2TestSolution = 46L,
    )
}

private data class Almanac(val maps: List<LayerMap>) {

    fun getDestinationFor(source: Long) = maps.fold(source) { src, conversionMap -> conversionMap.getTargetFor(src) }

    fun getSourceFor(destination: Long) = maps
        .reversed()
        .fold(destination) { dest, conversionMap -> conversionMap.getSourceFor(dest) }
}

private data class LayerMap(
    val ranges: Set<ConversionRange>,
) {

    data class ConversionRange(
        val sourceRange: LongRange,
        val offset: Long,
    ) {

        fun getTargetFor(source: Long) = if (source in sourceRange) source + offset else null

        fun getSourceFor(destination: Long): Long? {
            val possibleSource = destination - offset

            return possibleSource.takeIf { it in sourceRange }
        }
    }

    fun getTargetFor(source: Long) = ranges
        .firstNotNullOfOrNull { it.getTargetFor(source) }
        ?: source

    fun getSourceFor(destination: Long) = ranges
        .firstNotNullOfOrNull { it.getSourceFor(destination) }
        ?: destination
}
