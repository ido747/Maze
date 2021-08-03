package View;
import Server.Configurations;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.ASearchingAlgorithm;
import algorithms.search.AState;
import algorithms.search.SearchableMaze;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.nio.ch.ThreadPool;

public class MyViewController implements Observer, IView {

    @FXML
    private MyViewModel viewModel;
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.Label save;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Label lbl_HeadlineStartGame;
    public javafx.scene.control.Label lbl_CurColNum;
    public javafx.scene.control.Label lbl_CurRowsNum;
    public javafx.scene.control.Button saveGameMaze;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button solutionButton;
    public javafx.scene.control.Button hideButton;
    public javafx.scene.control.Button loadButton;
    public javafx.scene.control.TextField columnsNumber;
    public javafx.scene.control.TextField rowsNumber;
    public javafx.scene.control.TextField saveGame;
    public javafx.scene.control.MenuItem saveMenu;
    public BorderPane borderPane;



    private boolean ifDisplayMaze;
    private Media media1;
    private Media media2;
    private MediaPlayer mediaPlayer1;
    private MediaPlayer mediaPlayer2;

    String path1 = "resources/music/background1.mp3";
    String path2 = "resources/music/finishmusic.mp3";

    private static final Logger LOG = LogManager.getLogger();


    //--------Methods----------




    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
    }

    private void bindProperties(MyViewModel viewModel) {
        lbl_CurRowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_CurColNum.textProperty().bind(viewModel.characterPositionColumn);
    }
    @Override

    public void update(Observable o, Object arg) {

        if(arg!=null && arg instanceof Boolean)
        {
            //changes by ido
            mediaPlayer1.stop();
            mediaPlayer2.setAutoPlay(true);
            hideButton.setDisable(true);
            solutionButton.setDisable(true);
            mazeDisplayer.hideSolution();

            //
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("congratulations you are finished the maze! ");
            alert.show();

        }

        else if(arg != null && arg instanceof Solution){

            Solution mazeSolution = (Solution)arg;

            System.out.println(String.format("Solution steps: %s", mazeSolution));
            ArrayList<AState> mazeSolutionSteps = mazeSolution.getSolutionPath();

            for(int i = 0; i < mazeSolutionSteps.size(); ++i) {
                System.out.println(String.format("%s. %s", i, ((AState)mazeSolutionSteps.get(i)).toString()));
            }

            mazeDisplayer.displaySolution(mazeSolution.getSolutionPath());
        }

        else {


            if(ifDisplayMaze){ //start game draw both board maze game and character

                displayMaze(viewModel.getObjectMaze());
                //btn_generateMaze.setDisable(false);

                ifDisplayMaze = false;
            }

            else { //draw only character after change

                mazeDisplayer.setCharacterPosition(viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn());
            }
        }
    }





    @Override
    public void displayMaze(Maze maze) {

        mazeDisplayer.setMaze(maze);
        int characterPositionRow = maze.getStartPosition().getRowIndex();
        int characterPositionColumn = maze.getStartPosition().getColumnIndex();
        mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
    }
/* consider use it
    public void solveMaze(ActionEvent actionEvent) {
        showAlert("Solving maze..");
    }


 */
    public void newAction(ActionEvent actionEvent){


        LOG.info("play new action");

        if(mediaPlayer1 != null){

            mediaPlayer1.stop();
        }

        solutionButton.setDisable(true);
        hideButton.setDisable(true);
        viewModel.setFinish(false);
        columnsNumber.setVisible(true);
        rowsNumber.setVisible(true);
        lbl_rowsNum.setVisible(true);
        lbl_columnsNum.setVisible(true);
        lbl_HeadlineStartGame.setVisible(true);
        btn_generateMaze.setVisible(true);
        saveGameMaze.setVisible(false);
        saveGame.setVisible(false);
        save.setVisible(false);
        loadButton.setVisible(false);

        if(columnsNumber.isDisable()){

            columnsNumber.setDisable(false);
            rowsNumber.setDisable(false);
        }


    }

    public void showSolutionAction(ActionEvent actionEvent){
        LOG.info("user press on show solution button");
        viewModel.showSolution();
        solutionButton.setDisable(true);
        hideButton.setDisable(false);
    }
    public void hideSolutionAction(ActionEvent actionEvent){
        LOG.info("user press on hide solution button");
        mazeDisplayer.hideSolution();
        solutionButton.setDisable(false);
        hideButton.setDisable(true);

    }


    public void saveAction(ActionEvent actionEvent) {
        LOG.info("user save maze");


        save.setVisible(true);
        saveGame.setVisible(true);
        saveGameMaze.setVisible(true);
    }
    public void saveGameAction(ActionEvent actionEvent) {
       // LOG.info("user save maze");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to save?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){

            viewModel.saveMaze(saveGame.getText());

        }

        saveGame.setText("");
        save.setVisible(false);
        saveGame.setVisible(false);
        saveGameMaze.setVisible(false);


    }




    public void loadAction(ActionEvent actionEvent)
    {
        saveGameMaze.setVisible(false);
        ifDisplayMaze = true;
        save.setVisible(true);
        saveGame.setVisible(true);
        loadButton.setVisible(true);
    }


    public void loadGameAction(ActionEvent actionEvent){

        LOG.info("user load maze");
        viewModel.loadMaze(saveGame.getText());
        saveGame.setText("");
        save.setVisible(false);
        saveGame.setVisible(false);
        loadButton.setVisible(false);

        if(mediaPlayer1 != null){

            mediaPlayer1.stop();
        }

        if(mediaPlayer2 != null){

            mediaPlayer2.stop();
        }

        if(hideButton.isDisable() == false){

            mazeDisplayer.hideSolution();
            hideButton.setDisable(true);
        }

        media1 = new Media(new File(path1).toURI().toString());
        media2 = new Media(new File(path2).toURI().toString());

        mediaPlayer1 = new MediaPlayer(media1);
        mediaPlayer2 = new MediaPlayer(media2);
        saveMenu.setDisable(false);
        viewModel.setFinish(false);
        columnsNumber.setDisable(true);
        rowsNumber.setDisable(true);

        ifDisplayMaze = true;


        mediaPlayer1.setAutoPlay(true);
        solutionButton.setDisable(false);

    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void KeyPressed(KeyEvent keyEvent) {

        if(keyEvent.getCode().getName().equals("Ctrl")){
            mazeDisplayer.zoom();

        }

        else if(mazeDisplayer.getMaze() != null){

            viewModel.moveCharacter(keyEvent.getCode());
            keyEvent.consume();
        }


    }

    //region String Property for Binding
    public StringProperty characterPositionRow = new SimpleStringProperty();

    public StringProperty characterPositionColumn = new SimpleStringProperty();

    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }

    public StringProperty characterPositionRowProperty() {
        return characterPositionRow;
    }

    public String getCharacterPositionColumn() {
        return characterPositionColumn.get();
    }

    public StringProperty characterPositionColumnProperty() {
        return characterPositionColumn;
    }

    public void setResizeEvent(Scene scene) {
        long width = 0;
        long height = 0;
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
            }
        });
    }

    public void startPlay(ActionEvent actionEvent) {
        LOG.info("user start to play");

        if(mediaPlayer1 != null){

            mediaPlayer1.stop();
        }

        if(mediaPlayer2 != null){

            mediaPlayer2.stop();
        }

        if(hideButton.isDisable() == false){

            mazeDisplayer.hideSolution();
            hideButton.setDisable(true);
        }

        media1 = new Media(new File(path1).toURI().toString());
        media2 = new Media(new File(path2).toURI().toString());


        mediaPlayer1 = new MediaPlayer(media1);
        mediaPlayer2 = new MediaPlayer(media2);
        viewModel.setFinish(false);
        columnsNumber.setDisable(true);
        rowsNumber.setDisable(true);
        saveGameMaze.setVisible(false);
        saveGame.setVisible(false);
        save.setVisible(false);
        loadButton.setVisible(false);


        ifDisplayMaze = true;

        try {
            Integer.parseInt(rowsNumber.getText());
            Integer.parseInt(columnsNumber.getText());

            int heigth = Integer.parseInt(rowsNumber.getText());
            int width = Integer.parseInt(columnsNumber.getText());

            if(heigth >= 2 && width >= 3){

                mediaPlayer1.setAutoPlay(true);
                solutionButton.setDisable(false);
                viewModel.generateMaze(heigth,width);
                saveMenu.setDisable(false);
            }

            else { //wrong size of lines and columns

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(" wrong parameters please insert again! "); //edit
                alert.show();
            }

        }
        catch (Exception e){
           LOG.error(e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(" wrong parameters please insert again! "); //edit
            alert.show();
        }
    }


    public void About(ActionEvent actionEvent) {
        try {
            LOG.info("user open About menu item");
            Stage stage = new Stage();
            stage.setTitle("AboutController");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 400, 350);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
            LOG.error("IOException", e);
        }
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        this.mazeDisplayer.requestFocus();
    }

    public void helpAction(ActionEvent actionEvent) {
        LOG.info("user open help menu item");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../View/helpMe.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stage = new Stage();
            stage.setTitle("New Window");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            LOG.error("IOException", e);
        }

    }

    public void openProperties(ActionEvent actionEvent) {
        try {
            LOG.info("user open Properties");
             InputStream input = new FileInputStream("resources/config.properties");
              Properties prop = new Properties();
              prop.load(input);
              String value1 = prop.getProperty("SearchingAlgorithm");
            String value2 = prop.getProperty("MazeGenerator");
            String value3 = prop.getProperty("ThreadPool");


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Properties");
        String str = "SearchingAlgorithm=" +""+ value1+"\n" ;
        str = str + "MazeGenerator="+"" + value2+"\n";
        str = str  +"ThreadPool="+"" + value3;
        alert.setContentText(str); //edit
        alert.show();
        input.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    public void aboutAction(ActionEvent actionEvent) {

        try {
            LOG.info("user open About menu item");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../View/AboutUs.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 600,400);
            Stage stage = new Stage();
            stage.setTitle("New Window");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            LOG.error("IOException", e);
        }


    }


    private void exitProgram(){
        LOG.info("user exit game");
        Stage stage = (Stage) borderPane.getScene().getWindow();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){

            if(mediaPlayer1.isAutoPlay()){
                mediaPlayer1.stop();
            }

            if(mediaPlayer2.isAutoPlay()){
                mediaPlayer2.stop();
            }

            viewModel.exit();

            stage.close();
            Platform.exit();
        }
    }

    public void exitAction(ActionEvent actionEvent) {
        LOG.info("user exit game");
        this.exitProgram();
    }


    //endregion


}

