fun main() {

    fun holidayHash(source: String) = source.fold(0) { acc, char ->
        ((acc + char.code) * 17).rem(256)
    }

    fun part1(input: List<String>): Int {
        return input.joinToString("").split(',')
            .sumOf(::holidayHash)
    }

    fun parseStep(step: String): Step {
        val parts = step.split('-', '=').filter { it.isNotBlank() }

        return Step(
            label = parts[0],
            op = parts.getOrNull(1)?.let { Step.Op.Add(it.toInt()) } ?: Step.Op.Remove
        )
    }

    fun part2(input: List<String>): Int {
        val hashMap = mutableMapOf<Int, MutableList<Lens>>()
        input.joinToString("").split(',')
            .asSequence()
            .map(::parseStep)
            .forEach { step ->
                val key = holidayHash(step.label)
                when (step.op) {
                    is Step.Op.Add -> {
                        if (hashMap.contains(key)) {
                            val existingLenses = hashMap.getValue(key)
                            existingLenses.firstOrNull { it.label == step.label }
                                ?.let { it.focalLength = step.op.focalLength }
                                ?: run { existingLenses.add(Lens(step.label, step.op.focalLength)) }
                        } else {
                            hashMap[key] = mutableListOf(Lens(step.label, step.op.focalLength))
                        }
                    }
                    Step.Op.Remove -> if (hashMap.contains(key)) {
                        hashMap.getValue(key).removeIf { it.label == step.label }
                    }
                }
            }

        return hashMap.entries.asSequence()
            .flatMap { (box, lenses) ->
                lenses.mapIndexed { idx, lens ->
                    (box + 1) * (idx + 1) * lens.focalLength
                }
            }
            .sum()
    }

    testAll(
        day = 15,
        part1Fn = ::part1,
        part1TestSolutions = listOf(1320),
        part2Fn = ::part2,
        part2TestSolutions = listOf(145),
    )
}

data class Step(
    val label: String,
    val op: Op,
) {

    sealed class Op {
        data object Remove : Op()
        data class Add(val focalLength: Int) : Op()
    }
}

data class Lens(val label: String, var focalLength: Int) {

    override fun toString() = "[$label $focalLength]"
}