# SnakeEvolution

Snake: Evolution is a twist on the classic snake game. There is no player-controlled snake, instead the snakes are controlled by a primitive neural net based initially on random individual values. 

Upon reaching length 10 snakes are allowed to sexually reproduce allowing their genes to pass to future generations of snakes, resulting in improved fitness over time.

Below is a rough summary of the current and planned features.

Currently in build:
- Snake base logic & rendering
- Beautiful sprites  
- Moveable camera (wasd)
- Snake-emitted pheromones values with color data (based on parent snake)
- Food-emitted pheromones (not final implementation)
- Snake decision based on nerual net (not final implementation)
- Population histograms of various snake traits

Future features:
- Extensive commenting and explanations of logic
- Some kind of lighting field to help snakes detect dark places (read: dead ends)
- Better reproductive logic (Breeding chance weighted by snake size? better coloration?)
- Weighing the snake pheromone field based on color (may allow for complex 'family' behaviour)
