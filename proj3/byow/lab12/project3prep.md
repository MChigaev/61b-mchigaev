# Project 3 Prep

**For tessellating hexagons, one of the hardest parts is figuring out where to place each hexagon/how to easily place hexagons on screen in an algorithmic way.
After looking at your own implementation, consider the implementation provided near the end of the lab.
How did your implementation differ from the given one? What lessons can be learned from it?**

Answer:

My implementation differed in that my code for one worked with relative coordinates (eg a small TETile array for each hexagon object), which I was then mapping into the overall world. With this setup, I was hard coding the positions of each hexagon to tesselate them (but with relative coordinates). My setup was far more difficult than the given one, in that the given one used many helper functions to achieve what I essentially did manually.

**Can you think of an analogy between the process of tessellating hexagons and randomly generating a world using rooms and hallways?
What is the hexagon and what is the tesselation on the Project 3 side?**

Answer:

It is similar in the idea that tesselating hexagons means connecting together rooms/hallways. So the hexagon is a room/hallway, and tesselation is the process of connecting them together.
**If you were to start working on world generation, what kind of method would you think of writing first? 
Think back to the lab and the process used to eventually get to tessellating hexagons.**

Answer:

I would probably first work on an initial world generation function, from where the rest of the rooms and hallways branch from.
**What distinguishes a hallway from a room? How are they similar?**

Answer:
In my idea, the difference is a room is a large rectangular space, with multiple openings. A hallway, is a 3xn room, which is only open on the long ends of the room.
