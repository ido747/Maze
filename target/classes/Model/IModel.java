package Model;

import algorithms.mazeGenerators.Maze;
import javafx.scene.input.KeyCode;

public interface IModel {

    void generateMaze(int width, int height);
    void moveCharacter(KeyCode movement);
    int[][] getMaze();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    Maze getObjectMaze();

void setFinish(boolean finish);
    void showSolution();
    void stopServers();
    void saveMaze(String string);
    void loadMaze(String string);
    void exitProgram();
}
