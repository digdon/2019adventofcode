# Day 14: Space Stoichiometry
Part 1 of this was absolutely killer.

I tried to tackle this from a variety of angles. Using the first couple of examples, I managed to come up with a solution that
involved treating the list of reactions like a tree, working in the appropriate multipliers when necessary. A few bugs here and
there, but they worked.

Then I tried the 3rd example and I was way off - my required ORE number was much larger.

I knew I needed to approach it differently. I tried drawing things out by hand and trying to calculate all of the intermediate
values. This worked for the first couple of examples (again), but multi-depth nature of the 3rd example was giving me grief.
I was pretty sure some breadth-first technique (using a queue) was going to be required, but I just could wrap my head around
it.

Eventually I had to look for a hint. I found this on Reddit - [Day 14: Space Stoichiometry](https://github.com/jeffjeffjeffrey/advent-of-code/blob/master/2019/day_14.ipynb) - and it gave me enough hints to proceed. I was in the right ballpark, but I wasn't accounting for the 'extras' that accumulate from various reactions.

As with day 12, I absolutely consider this a cheat....
