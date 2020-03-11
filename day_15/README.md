# Day 15: Oxygen System
From start to finish, this one took me several weeks to get to. While the solutions are relatively easy, it took me a while
to discover the correct algoritms to use.

For part 1, I used depth-first search to generate the maze. From the resulting one-way graph, I used breadth-first search to
calculate the shortest path. Generating the maze proved to be quite difficult for me. I was sure I had the DFS algorithm
correct, and I worked out a way to tell the robot how to backtrack positions, but my code would apparently find all of the
nodes, yet not the location of the oxygen sensor.

Thinking I had an error in my DFS, I hand-built a 2-dimensional array and modified my DFS function to process it. Once I
proved that my code was correct, I knew it had to something to do with the commands I was sending to the robot. Again,
everything looked correct and, in fact it was. The problem was that after sending to command to move back to a previous
position, I wasn't checking the output queue. This meant that output was queuing up and being processed incorrectly. As
soon as I cleared the output queue after moving back a position, everything worked.

Part 2 turned out to be slightly trickier than I wanted. I wanted to avoid creating objects for everything (even though I
should have!). As a result, working backwards from the location of the oxygen sensor proved impossible. Luckily, as part
of my debug process, I had code to take the map generated from the DFS and turn it into a 2-D array. For part 2, I merely
generated a clean maze, and then wrote code to perform a flood fill starting from the sensor location to every other point,
keeping track of the max depth of the nodes as I went (specialized object - sigh).

Part 1 wasn't terribly ugly, but I'm not very happy with part 2. This was a set of tricky puzzles and I'm happy to have gotten
through it. Definitely need to work on writing nicer code going forward.
