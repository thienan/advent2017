package advent

/*
--- Day 12: Digital Plumber ---

Walking along the memory banks of the stream, you find a small village that is
experiencing a little confusion: some programs can't communicate with each
other.

Programs in this village communicate using a fixed system of pipes. Messages
are passed between programs using these pipes, but most programs aren't
connected to each other directly. Instead, programs pass messages between each
other until the message reaches the intended recipient.

For some reason, though, some of these messages aren't ever reaching their
intended recipient, and the programs suspect that some pipes are missing. They
would like you to investigate.

You walk through the village and record the ID of each program and the IDs with
which it can communicate directly (your puzzle input). Each program has one or
more programs with which it can communicate, and these pipes are bidirectional;
if 8 says it can communicate with 11, then 11 will say it can communicate with
8.

You need to figure out how many programs are in the group that contains program
ID 0.

For example, suppose you go door-to-door like a travelling salesman and record
the following list:

0 <-> 2
1 <-> 1
2 <-> 0, 3, 4
3 <-> 2, 4
4 <-> 2, 3, 6
5 <-> 6
6 <-> 4, 5

In this example, the following programs are in the group that contains program
ID 0:

    Program 0 by definition.
    Program 2, directly connected to program 0.
    Program 3 via program 2.
    Program 4 via program 2.
    Program 5 via programs 6, then 4, then 2.
    Program 6 via programs 4, then 2.

Therefore, a total of 6 programs are in this group; all but program 1, which
has a pipe that connects it to itself.

How many programs are in the group that contains program ID 0?

--- Part Two ---

There are more programs than just the ones in the group containing program ID
0. The rest of them have no way of reaching that group, and still might have no
way of reaching each other.

A group is a collection of programs that can all communicate via pipes either
directly or indirectly. The programs you identified just a moment ago are all
part of the same group. Now, they would like you to determine the total number
of groups.

In the example above, there were 2 groups: one consisting of programs
0,2,3,4,5,6, and the other consisting solely of program 1.

How many groups are there in total?
*/

import java.io.File
import java.util.*

private val parser = "(\\d+) <-> (.*)".toRegex()

data class PlumberGraph(val id: String, val children: List<String>)

fun buildPlumberGraph(lines: List<String>): HashMap<String, PlumberGraph> {
    val graph = HashMap<String, PlumberGraph>()
    for (line in lines) {
        val ml = parser.matchEntire(line)
                ?: throw RuntimeException("could not parse $line")
        val id = ml.groupValues[1]
        val children = ml.groupValues[2].split(", ")
        graph[id] = PlumberGraph(id, children)
    }
    return graph
}

fun programsConnected(graph: HashMap<String, PlumberGraph>, id: String): List<String> {
    val node = graph[id] ?: throw RuntimeException("could not find id $id")
    val q = ArrayDeque<String>()
    val seen = HashSet<String>()
    val group = ArrayList<String>()

    q.add(node.id)
    seen.add(node.id)

    while (q.isNotEmpty()) {
        val n = q.pop()
        group.add(n)
        graph[n]!!.children.filter { !seen.contains(it) }.let { unseenChildren ->
            seen += unseenChildren
            q += unseenChildren
        }
    }
    return group
}

fun findProgramGroups(graph: HashMap<String, PlumberGraph>): List<List<String>> {
    val groups = ArrayList<List<String>>()
    for (id in graph.keys) {
        if (!groups.flatten().contains(id))
            groups.add(programsConnected(graph, id))
    }
    return groups
}

fun day12() {
    val lines = File("day12.txt").readLines()
    val graph = buildPlumberGraph(lines)
    println("Day 12 Part 1: ${programsConnected(graph, "0").size}")
    println("Day 12 Part 2: ${findProgramGroups(graph).size}")
}
