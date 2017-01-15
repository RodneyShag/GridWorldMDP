# GridWorldMDP
Uses Markov decision processes (MDPs) and Temporal Difference (TD) Q-learning to maximize reward in a "grid world".

Our agent can move horizontally or vertically in the map pictured below.

![][map]

**Rewards:** Rewards are shown on the map above. The rewards for the white squares are -0.04. 

**Transition model:** the intended outcome occurs with probability 0.8, and with probability 0.1 the agent moves at either right angle to the intended direction (see the figure above). If the move would make the agent walk into a wall, the agent stays in the same place as before. 

By modeling our game world as a MDP, we can solve for the optimal "policy" (pictured below) which tells us what the best action to take at each square.

![][policy]

We further attempt to solve this problem in an unknown environment in which the rewards and transition model are not known to the agent. In this scenario, we use TD Q-learning as reinforcement learning to balance exploration vs. exploitation and arrive at a similar policy as above.

[map]: https://github.com/rshaghoulian/GridWorldMDP/blob/master/documentation/documentation_files/part1_1_maze.jpg
[policy]: https://github.com/rshaghoulian/GridWorldMDP/blob/master/screenshots/policy.png
