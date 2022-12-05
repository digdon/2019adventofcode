# Day 17: Set and Forget

Part 1 proved to be quite easy, although I was pretty lazy about it. I fetched the grid from the computer as
a list of strings (terminated by each newline). I then took the strings, converted into a 2-D array, and then
brute-forced my way through the grid. Any time there was a piece of scaffolding, I checked to see if that piece
was in the middle of an intersection (I allowed an intersection to have 3 or 4 exits). For each intersection,
calculate the 'distance', add to the running total.

Part 2, again, was a nightmare. Generating my way through the scaffolding was relatively easy. There are multiple
ways through, but I took a simple and greedy method - go straight until you can go straight no more. Most people
seem to have used this method. My code is ugly, but functional.

My knowledge of compression algorithms, apart from RLE, is pretty much non-existent. As with many folks, I wrote
the path down on a piece of paper and worked out the answer manually. I did see someone on Reddit with a glorious
regex snippet to work out functional groupings. I incorporated that into my code, just so that the whole thing
wasn't completely hard-coded.

Once I generated the movement routine and movement functions, the hard part was getting that data into the int code
computer. I was pushing movement numbers, such as 6, 8, 10, as actual numerical values. Turns out the computer
actually wanted ASCII values for those two. '6' and '8' were easy, but what was important was making sure that 10
went in as '1' and '0'.

Then there was wrestling with all of the output the computer generated as a result. I eventually opted for printing out
every output value as a character, unless the value was greater than 255, at which point I printed out the raw
numerical value.
  