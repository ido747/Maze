package Model;

import Client.Client;
import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import Server.Configurations;
import Server.Server;
import algorithms.mazeGenerators.*;
import algorithms.search.*;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import Client.IClientStrategy;


import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;

import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.search.AState;
import algorithms.search.Solution;
import test.RunCommunicateWithServers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyModel extends Observable implements IModel {

    //private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Maze maze;
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;
    private Solution mazeSolution;
    private boolean finish;
    private static HashSet<String> tableOfMaze;
    private static final Logger LOG = LogManager.getLogger();

    public MyModel() {
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        finish = false;
        this.startServers();
        tableOfMaze = new HashSet<>();
    }

    public void startServers() {
        LOG.info("ths servers are started");
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
    }

    public void stopServers() {
        LOG.info("the servers are stopped");
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }

    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;

    // i add this
    private int goalPositionRow = 1;
    private int goalPositionColumn =1;



    @Override
    public void generateMaze(int width, int height) {

        try {

            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {

                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{height, width};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) ((byte[]) fromServer.readObject());
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[height * width + 12];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);

                        //addWalls(maze);
                        characterPositionRow = maze.getStartPosition().getRowIndex();
                        characterPositionColumn = maze.getStartPosition().getColumnIndex();
                        //i add this
                        goalPositionRow = maze.getGoalPosition().getRowIndex();
                        goalPositionColumn = maze.getGoalPosition().getColumnIndex();

                        maze.print();
                        System.out.println();
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }

        setChanged();
        notifyObservers();

    }

    public void showSolution() {

        try {

            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {

                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze);
                        toServer.flush();
                        mazeSolution = (Solution) fromServer.readObject();
//
                    } catch (Exception var10) {
                        var10.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }

        setChanged();
        notifyObservers(mazeSolution);
    }


    @Override
    public int[][] getMaze() {
        return maze.getMaze();
    }

    public Maze getObjectMaze(){

        return this.maze;
    }

    @Override
    public void moveCharacter(KeyCode movement) {

if(finish ==false) {
    switch (movement) {
        //UP
        case UP:
            LOG.info("ths user press on up button ");
            if (characterPositionRow - 1 < 0 || characterPositionRow - 1 >= getMaze().length || getMaze()[characterPositionRow - 1][characterPositionColumn] == 1)
                break;
            else {
                characterPositionRow--;
            }
            break;

        case DIGIT8:
            LOG.info("ths user press on  button 8 ");
            if (characterPositionRow - 1 < 0 || characterPositionRow - 1 >= getMaze().length || getMaze()[characterPositionRow - 1][characterPositionColumn] == 1)
                break;
            else {
                characterPositionRow--;
            }
            break;

        //DOWN
        case DOWN:
            LOG.info("ths user press on down button ");
            if (characterPositionRow + 1 >= getMaze().length || getMaze()[characterPositionRow + 1][characterPositionColumn] == 1)
                break;
            else
                characterPositionRow++;
            break;
        case DIGIT2:
            LOG.info("ths user press on button 2 ");
            if (characterPositionRow + 1 >= getMaze().length || getMaze()[characterPositionRow + 1][characterPositionColumn] == 1)
                break;
            else
                characterPositionRow++;
            break;

        //RIGHT
        case RIGHT:
            LOG.info("ths user press on right button ");
            if (characterPositionColumn + 1 >= getMaze()[characterPositionRow].length || getMaze()[characterPositionRow][characterPositionColumn + 1] == 1)
                break;
            else
                characterPositionColumn++;
            break;
        case DIGIT6:
            LOG.info("ths user press on button 6 ");
            if (characterPositionColumn + 1 >= getMaze()[characterPositionRow].length || getMaze()[characterPositionRow][characterPositionColumn + 1] == 1)
                break;
            else
                characterPositionColumn++;
            break;

        //LEFT
        case LEFT:
            LOG.info("ths user press on button left ");
            if (characterPositionColumn - 1 < 0 || getMaze()[characterPositionRow][characterPositionColumn - 1] == 1)
                break;
            else
                characterPositionColumn--;
            break;
        case DIGIT4:
            LOG.info("ths user press on button 4 ");
            if (characterPositionColumn - 1 < 0 || getMaze()[characterPositionRow][characterPositionColumn - 1] == 1)
                break;
            else
                characterPositionColumn--;
            break;

        //RIGHTUP
        case DIGIT9:
            LOG.info("ths user press on button 9 ");
            if (characterPositionRow - 1 < 0 || characterPositionColumn + 1 >= getMaze()[characterPositionRow].length || getMaze()[characterPositionRow - 1][characterPositionColumn + 1] == 1)
                break;
            else {
                characterPositionRow--;
                characterPositionColumn++;

            }

            break;
        //LEFTUP
        case DIGIT7:
            LOG.info("ths user press on button 7 ");
            if (characterPositionRow - 1 < 0 || characterPositionColumn - 1 < 0 || getMaze()[characterPositionRow - 1][characterPositionColumn - 1] == 1)
                break;
            else {
                characterPositionRow--;
                characterPositionColumn--;

            }

            break;
        //RIGHTDOWN
        case DIGIT3:
            LOG.info("ths user press on button 3 ");
            if (characterPositionRow + 1 >= getMaze().length || characterPositionColumn + 1 >= getMaze()[characterPositionRow].length || getMaze()[characterPositionRow + 1][characterPositionColumn + 1] == 1)
                break;
            else {
                characterPositionRow++;
                characterPositionColumn++;

            }

            break;
        //LEFTDOWN
        case DIGIT1:
            LOG.info("ths user press on button 1 ");
            if (characterPositionRow + 1 >= getMaze().length || characterPositionColumn - 1 < 0 || getMaze()[characterPositionRow + 1][characterPositionColumn - 1] == 1)
                break;
            else {
                characterPositionRow++;
                characterPositionColumn--;
            }

            break;
    }
}

        if(characterPositionColumn==maze.getGoalPosition().getColumnIndex()&& characterPositionRow==maze.getGoalPosition().getRowIndex())
        {
            finish = true;


        }



        setChanged();
        notifyObservers(finish);

        }


    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }




    public void exitProgram() {
        LOG.info("ths user exit from the program ");
        this.stopServers();
    }

    public void saveMaze(String string) {

        if (maze != null) {
            LOG.info("ths user saves the maze ");
            try {

                maze.setStartposition(new Position(characterPositionRow,characterPositionColumn));
                String tempDirectoryPath = System.getProperty("java.io.tmpdir");
                String path =  tempDirectoryPath+"/" + string + ".txt";
                //String path = tempDirectoryPath + "/" + key;
                File file = new File(path);
                file.createNewFile();

                if (!tableOfMaze.contains(path)) {

                    OutputStream out = new MyCompressorOutputStream(new FileOutputStream(path));
                    out.write(maze.toByteArray());
                    out.flush();
                    out.close();
                }
            } catch (IOException var16) {
                var16.printStackTrace();
            }
        }
    }
    public void loadMaze(String string){
        String tempDirectoryPath = System.getProperty("java.io.tmpdir");
        //File folder = new File("./mazes");
        File folder = new File( tempDirectoryPath );
        File[] listOfFiles = folder.listFiles();
        String path =  string + ".txt";

        boolean flag = false;


        try{
            for (File file : listOfFiles) {
                if(file.getName().equals(path)){

                    flag = true;

                }
            }

            if(flag){

                /*
                 InputStream in;
                BufferedReader input = new BufferedReader(new InputStreamReader(this.in));

                 InputStream in = new MyDecompressorInputStream(new FileInputStream(mazeFileName));
                 */

                InputStream in = new MyDecompressorInputStream(new FileInputStream(  tempDirectoryPath+"/" + string + ".txt"));
                BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(  tempDirectoryPath+"/" + string + ".txt")));

                input.readLine();
                int rows = Integer.parseInt(input.readLine());
                input.readLine();
                int columns = Integer.parseInt(input.readLine());
                System.out.println(rows + "" + columns);
                byte[] savedMazeBytes = new byte[rows * columns + 12];

                in.read(savedMazeBytes);

                maze = new Maze(savedMazeBytes);
                maze.print();

                characterPositionRow = maze.getStartPosition().getRowIndex();
                characterPositionColumn = maze.getStartPosition().getColumnIndex();
                //i add this
                goalPositionRow = maze.getGoalPosition().getRowIndex();
                goalPositionColumn = maze.getGoalPosition().getColumnIndex();
                in.close();

            }
         else {



                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText(" the file is not exist! "); //edit
                    alert.show();
                }

            }


        catch (Exception e){

        }

        setChanged();
        notifyObservers();

    }


}