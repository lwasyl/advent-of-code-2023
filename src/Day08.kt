import kotlin.time.measureTimedValue

fun main() {

    fun parseInput(input: List<String>): Day8Input1 {
        val instructions = input.first().map(Direction.Companion::fromChar)

        val map = input.drop(2).associate { line ->
            val (node, edges) = line.split('=')
            val (leftNode, rightNode) = edges.trim().drop(1).dropLast(1).split(',')

            node.trim() to (leftNode.trim() to rightNode.trim())
        }

        return Day8Input1(
            instructions = instructions,
            nodesMap = map,
        )
    }

    fun part1(input: List<String>): Int {
        val parsedInput = parseInput(input)

        val instructionsIterator = parsedInput.repeatedInstructions.iterator()
        var currentNode = "AAA"
        var stepsNeeded = 0
        while (currentNode != "ZZZ") {
            currentNode = parsedInput.nodesMap.getValue(currentNode).getDirection(instructionsIterator.next())
            stepsNeeded++
        }

        return stepsNeeded
    }

    fun part2(input: List<String>): Long {
        val parsedInput = parseInput(input)

        val instructionsIterator = parsedInput.repeatedInstructions.iterator()

        val startingNodes = parsedInput.nodesMap.keys.filter { it.endsWith('A') }
        var nodeToPath = startingNodes.associateWithTo(mutableMapOf()) { ArrayList<Node>(50000).apply { add(it) } to false }

        // Build paths until all starting nodes end up somewhere on a loop with a Z
        var stepsSoFar = 0
        while (nodeToPath.any { !it.value.second }) {
            val nextStep = instructionsIterator.next()
            stepsSoFar += 1

            nodeToPath.keys.forEach {
                val newEntry = run {
                    val entry = nodeToPath.getValue(it)
                    val (currentPath, finished) = entry

                    if (finished) return@run entry

                    // Not on a loop yet or we don't have an **Z node
                    val nextNode = parsedInput.nodesMap.getValue(currentPath.last()).getDirection(nextStep)

                    val addingZNow = nextNode.endsWith('Z')
                    val nextNodeIndex = currentPath.indexOf(nextNode)

                    val willBeFinished = if (addingZNow) {
                        currentPath.any { it == nextNode }
                    } else if (nextNodeIndex >= 0) {
                        currentPath.any { it.endsWith('Z') }
                    } else {
                        false
                    }

                    currentPath.apply { add(nextNode) } to willBeFinished
                }

                nodeToPath[it] = newEntry
            }
        }

        return nodeToPath.values
            .map { it.first.indexOfFirst { it.endsWith('Z') }.toLong() }
            .let(::lcm)
    }

    readInput("Day08_test_2")
        .let { measureTimedValue { part1(it) } }
        .let {
            it.println("Part 1 test 2")
            check(it.value == 6)
        }

    testAll(
        day = 8,
        part1Fn = ::part1,
        part1TestSolution = 2,
        part2Fn = ::part2,
        part2TestSolution = 6L,
    )
}

private data class Day8Input1(
    val instructions: List<Direction>,
    val nodesMap: Map<Node, Pair<Node, Node>>,
) {

    val repeatedInstructions = sequence {
        while (true) yieldAll(instructions)
    }
}

private typealias Node = String

private enum class Direction {
    L,
    R,
    ;

    companion object {

        fun fromChar(char: Char) = Direction.entries.first { it.name.single() == char }
    }
}

private fun Pair<Node, Node>.getDirection(direction: Direction) = when (direction) {
    Direction.L -> first
    Direction.R -> second
}
