## Getting Started

This project is an implementation of an Othello AI bot that you can play against.

This was programmed in Java, and implements the Min-Max search algorithm, with A/B pruning and a custom heuristic evaluation function.

The evaluation function takes into account corners, edges, how many moves are available, the overall score, and more.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).


## COMP 670 AI Game Project Notes

Your OthelloAI class' name must begin with `OthelloAI_`, followed by your `full_name`, e.g. `OthelloAI_Johnn_Smith.java`. It must be saved in the `src` folder.
To run your program, you may choose from the menu: Run->Run without Debugging.
Or you can build your project: go to the Explorer, unfold Java Projects, right click the project name and choose Rebuild Project.
You can choose to play with your AI class or let it play against itself. You may also implement two or more different versions and let them play against one another.
