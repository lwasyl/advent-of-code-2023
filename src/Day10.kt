import MazeDirection.E
import MazeDirection.N
import MazeDirection.S
import MazeDirection.W
import kotlin.math.abs

fun main() {

    fun parseInput(input: List<String>): Day10Input {
        val cols = input[0].length
        return Day10Input(
            maze = buildList {
                add(List(cols) { '.' })
                addAll(input.map { line ->
                    buildList {
                        add('.')
                        addAll(line.map { it })
                        add('.')
                    }
                })
                add(List(cols) { '.' })
            }
        )
    }

    fun findMainLoop(maze: Maze): List<Coords> {
        val startCoords = maze.startCoords()
        return maze[startCoords].possibleDirections().asSequence()
            .mapNotNull {
                findLoop(
                    maze = maze,
                    startCoords = startCoords,
                    firstStep = it
                )
            }
            .first()
    }

    fun part1(input: List<String>): Int {
        val parsed = parseInput(input)

        return findMainLoop(parsed.maze).size / 2
    }

    fun part2(input: List<String>): Int {
        val parsed = parseInput(input)

        val mainLoop = findMainLoop(parsed.maze).toSet()
        val flipPattern = "(L-*7)|(F-*J)".toRegex()
        parsed.maze
            .asSequence()
            .mapIndexed { row, line ->
                line
                    .mapIndexed { col, char -> if (mainLoop.contains(Coords(row, col))) char else '.' }
                    .joinToString(separator = "")
            }
            .map { line -> line.replace(flipPattern, "|") }
            .map { line ->
                line.fold(0 to false) { (count, inside), char ->
                    when {
                        char == '|' -> count to !inside
                        char == '.' && inside -> (count + 1) to inside
                        else -> count to inside
                    }
                }
                    .first
            }
            .sum()
            .let { return it }
    }

    fun part2Analytical(input: List<String>): Int {
        val parsed = parseInput(input)

        val mainLoop = findMainLoop(parsed.maze)
        return mainLoop
            .let { it.plus(it.first()) }
            .windowed(2)
            // https://en.wikipedia.org/wiki/Shoelace_formula
            .fold(0) { acc, (left, right) ->
                acc + ((left.row * right.col) - (left.col * right.row))
            }
            .let { abs(it / 2) }
            // https://en.wikipedia.org/wiki/Pick%27s_theorem
            .let { area -> area - (mainLoop.size / 2) + 1 }
    }

    testAll(
        day = 10,
        part1Fn = ::part1,
        part1TestSolutions = listOf(4, 8),
        part2Fn = ::part2Analytical,
        part2TestSolutions = listOf(4, 8, 10),
    )
}

private fun findLoop(maze: Maze, startCoords: Coords, firstStep: MazeDirection): List<Coords>? {
    check(firstStep in maze[startCoords].possibleDirections())

    val path = mutableListOf(startCoords, startCoords.go(firstStep))
    var cameFrom = firstStep.opposite()
    while (true) {
        val stepDirection = maze[path.last()].possibleDirections().minus(cameFrom).singleOrNull() ?: return null
        val nextCoords = path.last().go(stepDirection)
        val nextTile = maze[nextCoords]
        cameFrom = stepDirection.opposite()

        if (nextTile == 'S') return path.toList()
        path += nextCoords
    }
}

private fun Maze.startCoords(): Coords {
    forEachIndexed { row, line ->
        line.forEachIndexed { col, char ->
            if (char == 'S') return Coords(row, col)
        }
    }
    error("No start in maze")
}

private fun Char.possibleDirections() = when (this) {
    'J' -> setOf(N, W)
    'L' -> setOf(N, E)
    'F' -> setOf(S, E)
    '7' -> setOf(S, W)
    'S' -> setOf(N, E, S, W)
    '|' -> setOf(N, S)
    '-' -> setOf(E, W)
    '.' -> emptySet()
    else -> error("unknown tile")
}

private operator fun Maze.get(coords: Coords) = this[coords.row][coords.col]

private data class Day10Input(
    val maze: Maze,
)

data class Coords(val row: Int, val col: Int)

private fun Coords.go(direction: MazeDirection) = when (direction) {
    N -> copy(row = row - 1)
    S -> copy(row = row + 1)
    E -> copy(col = col + 1)
    W -> copy(col = col - 1)
}

private typealias Maze = List<List<Char>>

private enum class MazeDirection {
    N,
    S,
    E,
    W,
    ;

    fun opposite() = when (this) {
        N -> S
        S -> N
        E -> W
        W -> E
    }
}
