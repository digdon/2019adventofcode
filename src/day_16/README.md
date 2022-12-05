# Day 16: Flawed Frequency Transmission

As with so many of these, part 1 was pretty easy and part 2 was absolutely brutal.

Part 1 was very easy to brute force and didn't need much code - about a dozen lines.

Part 2 was an absolute nightmare. Yes, my code could have brute forced the answer, but it would have taken a considerable
amount of time. As with the other complicated problems (looking at you, [Day 14](https://github.com/digdon/2019adventofcode/tree/master/day_14)),
I knew that there had to be an easier/faster solution. Again, I went to Reddit for assistance ([2019 day 16 solutions](https://www.reddit.com/r/adventofcode/comments/ebai4g/2019_day_16_solutions/)).
However, I had a very hard time wrapping my head around the solutions being discussed. I spent a lot of time writing out stuff
on a pad of paper, looking for patterns I could use. The best hint that really allowed me to make progress was "I used an input
input string of '1111111' to help location patterns." That made it obvious that, starting from right to left, the first value
never changes and that each subsequent value is a sum of the ones before it in the previous signal value.

It took quite a bit of time to recognize the purpose of the next hint, which was to chop off everything before the offset before
running the input string through the multiple phases. Eventually I found a hint that said if the offset is bigger than halfway,
all of the co-efficients will be 1 (allowing for the easy summing mentioned above). I had seen this on my own when I was
drawing everything out, but the significance was lost on me at the time.

Normally I feel I'm pretty good at finding patterns and working out how to replicate them, but this one absolutely stumped me.
I feel like I gave up far too quickly.
