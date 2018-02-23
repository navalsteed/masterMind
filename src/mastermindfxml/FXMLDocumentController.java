/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mastermindfxml;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import derbyDB.*;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Text;

/**
 *
 * @author Elev
 */
public class FXMLDocumentController implements Initializable {

    private final Code code;
    private final Guess guess;

    public FXMLDocumentController() {
        this.code = new Code();
        code.setCodeList(code.generateCode());
        this.guess = new Guess();
    }

    @FXML
    private GridPane viewGridpane;
    @FXML
    private GridPane validateGridpane0;
    @FXML
    private GridPane validateGridpane1;
    @FXML
    private GridPane validateGridpane2;
    @FXML
    private GridPane validateGridpane3;
    @FXML
    private GridPane validateGridpane4;
    @FXML
    private GridPane validateGridpane5;
    @FXML
    private GridPane validateGridpane6;
    @FXML
    private GridPane validateGridpane7;
    @FXML
    private GridPane validateGridpane8;
    @FXML
    private GridPane validateGridpane9;

    @FXML
    private Button checkButton;

    @FXML
    Circle ansCircle0;
    @FXML
    Circle ansCircle1;
    @FXML
    Circle ansCircle2;
    @FXML
    Circle ansCircle3;

    private int count = -1;  //keep track of how many valid click user has made
    private Circle circle = null;
    private Map<String, Color> map;
    private GridPane[] validateGridpanes;
    private boolean onCheck = false;
    private boolean isWinning = false;

    long start;
    long end;

    /*
    *Interacte with six buttons 
    *peform certain commands depends on each state
    *ex. set verify state to true when four pegs are selected  && enable check button
     */
    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (count == -1) {
            start = System.currentTimeMillis();  // start recording time
            count++;
        }
        if (onCheck) {
            Button btn = (Button) event.getSource();
            String id = btn.getId();
            if (!guess.getGuess().contains(id)) {
                viewGridpane.add(addCircle(map.get(id)), count%40%4, count%40/4); //place the circle on gridpane
                guess.setGuess(id);
                count++;
            }
        }
        if (count % 4 == 0) {
            onCheck = false;
            checkButton.setDisable(false);
        } else {
            checkButton.setDisable(true);
        }

    }

    /*
    Create circle
    Paint color of selected peg on the circle 
     */
    private Circle addCircle(Color hexColor) {
        circle = new Circle();
        circle.setRadius(20.0f);
        circle.setFill(hexColor);
        return circle;
    }

    /*
     Compare color and position between user's input and answer
     Return result as number of White and/or black pattern
     */
    private List verifyResults() {
        int blackNum = 0;
        List<Color> guessResults = new ArrayList();
        List<Color> guessResultsWhite = new ArrayList();
        List<Color> guessResultsBlack = new ArrayList();
        List<Color> guessResultsGray = new ArrayList();
        for (int i = 0; i < code.getCodeList().size(); i++) {

            if (code.getCodeList().get(i).equals(guess.getGuess().get(i))) {
                blackNum++;
                guessResultsBlack.add(Color.BLACK);
            } else if (code.getCodeList().contains(guess.getGuess().get(i))) {
                guessResultsWhite.add(Color.WHITE);
            } else {
                guessResultsGray.add(Color.GRAY);
            }
        }
        if (blackNum == 4) {
            end = System.currentTimeMillis();     //finishing recording time;
            isWinning = true;
        }
        guessResults.addAll(guessResultsWhite);
        guessResults.addAll(guessResultsBlack);
        guessResults.addAll(guessResultsGray);
        return guessResults;
    }

    
    /**
     * display the rsults on th screen 
     */   
    @FXML
    private void validate(ActionEvent event) {
        if (guess.getGuess().size() == 4) {
            checkButton.setDisable(true);
            onCheck = true;
            List<Color> guessList = verifyResults();
            for (Node node : validateGridpanes[(count/4 - 1)%10].getChildren()) {
                if (node instanceof Circle) {
                    if (GridPane.getRowIndex(node) == 0 && GridPane.getColumnIndex(node) == 0) {
                        ((Circle) node).setFill(guessList.get(0));
                    }
                    if (GridPane.getRowIndex(node) == 0 && GridPane.getColumnIndex(node) == 1) {
                        ((Circle) node).setFill(guessList.get(1));
                    }
                    if (GridPane.getRowIndex(node) == 1 && GridPane.getColumnIndex(node) == 0) {
                        ((Circle) node).setFill(guessList.get(2));
                    }
                    if (GridPane.getRowIndex(node) == 1 && GridPane.getColumnIndex(node) == 1) {
                        ((Circle) node).setFill(guessList.get(3));
                    }
                }
            }
            guess.getGuess().clear();
            if (isWinning) {
                ansCircle0.setFill(map.get(code.getCodeList().get(0)));
                ansCircle1.setFill(map.get(code.getCodeList().get(1)));
                ansCircle2.setFill(map.get(code.getCodeList().get(2)));
                ansCircle3.setFill(map.get(code.getCodeList().get(3)));
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setTitle("Winning Message");
                dialog.setHeaderText("      \\(^_^)/");
                dialog.setContentText("Good Job!\nYou took " + count / 4 + " times!\nPlease enter your name:");
                Optional<String> result = dialog.showAndWait();
                long timeInterval = end - start;
                long second = (timeInterval / 1000) % 60;
                long minute = (timeInterval / (1000 * 60)) % 60;
                long hour = (timeInterval / (1000 * 60 * 60)) % 24;
                String timeSpent = String.format("%02d:%02d:%02d", hour, minute, second);
                String name = "default";
                if (result.isPresent() && (timeInterval < 86400000)) {  //only show result that can be represented as HH:mm:ss
                    if (result.get().length() <= 25) {//replace namer longer than 25 characters with "default"
                        name = result.get();
                    }
                    try {
                        dbConnectorSingleton.getInstance().insert(name, timeSpent, code.substringCodelist());
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                reset();// reset code and play again
                code.setCodeList(code.generateCode());
            }

            if (count%40==0 && !isWinning) {   //another cycle starts here
                reset();// reset code and play again
            }
        }
    }

    /*
    reset gridpanes, erase all painted circles 
    remove previous results and state 
     */
    private void reset() {
        radioBoxRule.setSelected(false);
        radioBoxDB.setSelected(false);
        checkButton.setDisable(true);
        isWinning = false;
        viewGridpane.getChildren().clear();
        clearVGP();
        ansCircle0.setFill(Color.GRAY);
        ansCircle1.setFill(Color.GRAY);
        ansCircle2.setFill(Color.GRAY);
        ansCircle3.setFill(Color.GRAY);
        textArea.setVisible(false);
        textArea.setText("");
    }

    @FXML
    TextArea textArea;
    @FXML
    RadioButton radioBoxRule;
    @FXML
    RadioButton radioBoxDB;

    /**
     * show or hide game rules with TextArea depeding on user's input
     */
    @FXML
    private void displayText() {
        radioBoxDB.setSelected(false);
        if (radioBoxRule.isSelected()) {
            textArea.setDisable(false);
            textArea.setVisible(true);
            setRuleText();

            gridpaneDB.setDisable(true);
            gridpaneDB.setVisible(false);
        } else {
            textArea.setVisible(false);
            textArea.setText("");
        }
    }

    @FXML
    GridPane gridpaneDB;

    /*
    *feth top 10 best players' names and their results
    *display them on gridpane
     */
    @FXML
    private void showDB() {
        if (!radioBoxDB.isSelected()) {
            textArea.setVisible(false);
            textArea.setText("");
            gridpaneDB.setDisable(true);
            gridpaneDB.setVisible(false);
        } else {
            gridpaneDB.setDisable(false);
            gridpaneDB.setVisible(true);
            radioBoxRule.setSelected(false);
            textArea.setDisable(true);
            textArea.setVisible(false);
            try {
                String[][] s = dbConnectorSingleton.getInstance().selectData();
                for (int i = 0; i < s.length; i++) {
                    gridpaneDB.add(new Text(s[i][0]), 0, i);
                    gridpaneDB.add(new Text(s[i][1]), 1, i);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Rest validate GridPanes
     */
    private void clearVGP() {
        for (GridPane gp : validateGridpanes) {
            for (Node node : gp.getChildren()) {
                if (node instanceof Circle) {
                    ((Circle) node).setFill(Color.GRAY);
                }
            }
        }
    }

    /*
    *Intialize components setting
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO your code
        onCheck = true;
        checkButton.setDisable(true);
        map = new HashMap<>();
        map.put("blueButton", Color.DODGERBLUE);
        map.put("yellowButton", Color.YELLOW);
        map.put("purpleButton", Color.MAGENTA);
        map.put("redButton", Color.RED);
        map.put("greenButton", Color.GREEN);
        map.put("orangeButton", Color.ORANGE);
        validateGridpanes = new GridPane[]{validateGridpane0, validateGridpane1, validateGridpane2, validateGridpane3, validateGridpane4, validateGridpane5,
            validateGridpane6, validateGridpane7, validateGridpane8, validateGridpane9};
        textArea.setVisible(false);
        textArea.setBackground(Background.EMPTY);
        textArea.setEditable(false);
        gridpaneDB.setDisable(true);
        gridpaneDB.setVisible(false);
    }

    /*
    *display MasterMind Game rule on textArea
     */
    private void setRuleText() {
        textArea.setText("• Click peg to guess correct code order\n"
                + "• White indicates correct color wrong position\n"
                + "• Black indicates correct color & position\n"
                + "• Tap check button to verify result\n"
                + "• Each peg should only appear once\n"
                + "• Guess as many times as you want ");
    }

}
