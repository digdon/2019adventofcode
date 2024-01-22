# Day 18: Many-Worlds Interpretation

January, 2024. I have been thinking about this puzzle for a very long time now. Over the years I'd re-read it, give it some thought, get started on something, and then abandon because it's just far too difficult for my current algorithmic knowledge.

I stopped working on the 2023 advent for a while (the days I have remaining will take me days or weeks of thought and effort) and thought I'd try finishing up the 2019 event. I got through a couple days pretty easily and figured now was the time to really bear down on day 18. Oi.

I read the Reddit solutions thread (https://www.reddit.com/r/adventofcode/comments/ec8090/2019_day_18_solutions/) endlessly, looking for suggestions that I could understand. I suspected that part 1 was going to involve some sort of BFS, but I couldn't figure out how. Eventually I saw something that mentioned adding the list of keys currently picked up to the standard 'visited' set. I used bit-twiddling to manage the current key set, added it to my tuple, and presto, got an answer. It was pretty fast too (less than 300 ms), which surprised me because so many folks talked about using Dijkstra, A*, memoization, etc. My code runs through a lot of nodes, but it was more than fast enough.

As I should have surmised, part 2 was even more difficult. Rather than keeping track of the position of a single bot and the active key set, I tried keeping track of all 4 bots. That worked for the first couple of sample inputs, but I got wildly different answers for a couple more. My puzzle input ran for a very long time before I eventually stopped it. If it wasn't working on all of the test data, it certainly wasn't going to work for the real stuff. Having said that, many people just worked it as 4 separate grids, gave each bot the keys not in its grid, and added everything up. Pretty much everyone said, "it works for almost all of the test input, and yet I still managed to get a correct answer for the puzzle input." I was tempted by this, but every time someone mentioned it, comments filled up with "no, that's really not the correct way." I wanted to do this properly, so I abandoned that path.

I kept digging. Djikstra came up a lot. So did memoization. Eventually I saw this - https://www.reddit.com/r/adventofcode/comments/ec8090/comment/fbb0sqh/. That led to this - https://old.reddit.com/r/adventofcode/comments/ec8090/2019_day_18_solutions/fb9wg35/. I understood some of what was required, but not all. I went here for some hints - https://github.com/firetech/advent-of-code/blob/%F0%9F%8E%84/2019/18/keymaze.rb

Here's a basic breakdown of what the code is doing:
1. find the row/col of each key (start [@] positions too)
1. for each key and bot (start), calculate the distance to every other key
   - for part 1, this means from each key to every other key in the entire grid. For part 2, it's limited to each quadrant
   - we find the distance to each key using a modified "flood fill"
   - as we move along, we keep track of all doors we pass through on the way to a particular key
