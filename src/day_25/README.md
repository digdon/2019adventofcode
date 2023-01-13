# Day 25: Cryostasis

This was a fun one. I was originally thinking I'd have to build some algorithm to work through the whole thing, but that seemed ridiculous. Instead, I just wrote a wrapper that would display
contents of the computer output queue and take input (commands) that I typed, making it behave like an old-skool text adventure game.

Wandering around and building the map on a piece of paper was easy. The hard part was taking the full list of inventory items and working out which ones are needed
to correctly trigger the pressure sensor. Nothing but trial and error here. I quickly worked out that the pointer, manifold, and loom were all too heavy on their own.
From there, it was just a matter of working out which of the remaining items were required. I got lucky with the following:
* hypercube
* mutex
* klein bottle
* mug

Once I had the right items, the program displayed the code and ended the game. Fun.
