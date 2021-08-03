package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MazeDisplayer extends Canvas {


    private Maze maze;
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;
    private int finalPositionRow = 1;
    private int finalPositionColumn = 1;
    private ArrayList<AState>solutionPath;
    private boolean ifDisplaySolution = false;
    public javafx.scene.control.Button solutionButton;
/*
    String path1 = "src/View/music/background1.mp3";
    String path2 = "src/View/music/finishmusic.mp3";
    private Media media1 = new Media(new File(path1).toURI().toString());
    private Media media2 = new Media(new File(path2).toURI().toString());
    //Instantiating MediaPlayer class
    MediaPlayer mediaPlayer1 = new MediaPlayer(media1);
    MediaPlayer mediaPlayer2 = new MediaPlayer(media2);
    //by setting this property to true, the audio will be played


 */

    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();

    private StringProperty ImageFileNameFinish = new SimpleStringProperty();
    private StringProperty MediaBackgroundMusic = new SimpleStringProperty();
    private StringProperty MediaFinakMusic = new SimpleStringProperty();

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }


    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }


    public String getImageFileNameFinish() {
        return ImageFileNameFinish.get();
    }


    public void setImageFileNameFinish(String imageFileNameFinish) {
        this.ImageFileNameFinish.set(imageFileNameFinish);
    }



    public void setCharacterPosition(int row, int column) {

        int oldRow = characterPositionRow;
        int oldColumn = characterPositionColumn;


            characterPositionRow = row;
            characterPositionColumn = column;
            redrawCharacter(oldRow, oldColumn);


    }

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }


    public void setMaze(Maze maze) {

        this.maze = maze;
        redraw();
    }

    public void redraw() {

        if (maze != null) {

            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.getMaze().length;
            double cellWidth = canvasWidth / maze.getMaze()[0].length;


            //by setting this property to true, the audio will be played

            try {

                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));

                Image finishImage = new Image(new FileInputStream(ImageFileNameFinish.get()));


                //Instantiating Media class
                //Media media = new Media(new File(path).toURI().toString());

                //Instantiating MediaPlayer class
                // MediaPlayer mediaPlayer = new MediaPlayer(media);


                //Draw Maze
                for (int i = 0; i < maze.getMaze().length; i++) {
                    for (int j = 0; j < maze.getMaze()[i].length; j++) {
                        if (maze.getMaze()[i][j] == 1) {
                            //gc.fillRect(i * cellHeight, j * cellWidth, cellHeight, cellWidth);
                            gc.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                    }
                }
                gc.drawImage(finishImage, maze.getGoalPosition().getColumnIndex() * cellWidth, maze.getGoalPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);


                //Draw Character
                //gc.setFill(Color.RED);
                //gc.fillOval(characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);

                // gc.drawImage(characterImage, maze.getStartPosition().getRowIndex() * cellHeight, maze.getStartPosition().getColumnIndex() * cellWidth, cellHeight, cellWidth);


            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }


    public void redrawCharacter(int oldRow, int oldColumm) {

        try {



            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.getMaze().length; //row
            double cellWidth = canvasWidth / maze.getMaze()[0].length; //column


            GraphicsContext gc = getGraphicsContext2D();

            gc.clearRect(oldColumm * cellWidth, oldRow * cellHeight, cellWidth, cellHeight);


            Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));

            gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);

            MazeState mazeState = new MazeState(new Position(oldRow, oldColumm));
            if (ifDisplaySolution && solutionPath.contains(mazeState)){

                gc.fillRect(oldColumm * cellWidth, oldRow * cellHeight, cellWidth, cellHeight);
            }

        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }

    }

    /*
    public boolean ifGoToFinal() {
        if (characterPositionRow == maze.getGoalPosition().getRowIndex() && characterPositionColumn == maze.getGoalPosition().getColumnIndex()) {

            //mediaPlayer.setAutoPlay(false);
            mediaPlayer1.stop();
            mediaPlayer2.setAutoPlay(true);
            return true;
        }
        return false;

        //endregion

    }

     */
    public void displaySolution(ArrayList<AState> solutionPath) {

       ifDisplaySolution=true;

       this.solutionPath = solutionPath;
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        double cellHeight = canvasHeight / maze.getMaze().length; //row
        double cellWidth = canvasWidth / maze.getMaze()[0].length; //column

        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.RED);
        gc.setGlobalAlpha(0.6);

        for (int i = 0; i < solutionPath.size(); i++){

            gc.fillRect(((MazeState)solutionPath.get(i)).getCurrentPosition().getColumnIndex() * cellWidth, ((MazeState)solutionPath.get(i)).getCurrentPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);
        }

    }

    public void hideSolution(){

        ifDisplaySolution = false;

        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        double cellHeight = canvasHeight / maze.getMaze().length; //row
        double cellWidth = canvasWidth / maze.getMaze()[0].length; //column

        try {
            GraphicsContext gc = getGraphicsContext2D();
            gc.setGlobalAlpha(1);
            Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
            //gc.clearRect(oldColumm * cellHeight , oldRow * cellWidth, cellWidth , cellHeight);

            for (int i = 0; i < solutionPath.size(); i++){

                if(((MazeState)solutionPath.get(i)).getCurrentPosition().getColumnIndex() != finalPositionColumn || ((MazeState)solutionPath.get(i)).getCurrentPosition().getRowIndex() != finalPositionRow){

                    gc.clearRect(((MazeState)solutionPath.get(i)).getCurrentPosition().getColumnIndex() * cellWidth , ((MazeState)solutionPath.get(i)).getCurrentPosition().getRowIndex() * cellHeight, cellWidth , cellHeight);
                }

            }
            try {
                Image finishImage = new Image(new FileInputStream(ImageFileNameFinish.get()));
                gc.drawImage(finishImage,maze.getGoalPosition().getColumnIndex()*cellWidth,maze.getGoalPosition().getRowIndex()*cellHeight, cellWidth, cellHeight);
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));

                gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
            }
            catch (Exception e){

            }
        }
        catch (Exception e){

        }
    }






    public Maze getMaze(){

        return maze;
    }
    public void zoom() {

        setOnScroll(new EventHandler<ScrollEvent>() {

            @Override

            public void handle(ScrollEvent event) {

                double zoom = 1.05;
                double y = event.getDeltaY();
                double x = event.getDeltaX();


                if(y < 0) {

                    zoom = 0.95;
                }
                else {
                    zoom = 1.05;
                }
                setScaleX(getScaleX() * zoom );
                setScaleY(getScaleY() * zoom);
                event.consume();
            }
        });
    }

}


