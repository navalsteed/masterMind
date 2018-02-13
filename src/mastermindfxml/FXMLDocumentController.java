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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import derbyDB.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextInputDialog;

/**
 *
 * @author Elev
 */
public class FXMLDocumentController implements Initializable {

    private final Code code;
    private final Guess guess;

    public FXMLDocumentController() {
        code = new Code();
        code.setCodeList(code.generateCode());
        guess = new Guess();
    }

    @FXML
    private GridPane viewGridpane;// fx:id="viewGridpane"

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
    private CheckBox checkBoxRule;

    @FXML
    private Button checkButton;

    private int count = 0;
    private Circle circle = null;
    private Map<String, Color> map;
    private GridPane[] validateGridpanes;
    private boolean onCheck = false;
    private boolean isWinning = false;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (count == 1) {
            start = System.currentTimeMillis();  // start recording time
        }
        if (onCheck) {
            Button btn = (Button) event.getSource();
            String id = btn.getId();

            if (!guess.getGuess().contains(id)) {
                viewGridpane.add(addCircle(map.get(id)), count % 4, count / 4); //place the circle on gridpane
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

    private Circle addCircle(Color hexColor) {
        circle = new Circle();
        circle.setRadius(20.0f);
        circle.setFill(hexColor);
        return circle;
    }

    private List validateResults() {
        int blackNum = 0;
        List<Color> guessResults = new ArrayList();
        List<Color> guessResultsWhite = new ArrayList();
        List<Color> guessResultsBlack = new ArrayList();
        List<Color> guessResultsGray = new ArrayList();
        System.out.println("myGuess: " + guess.getGuess());
        System.out.println("codeList: " + code.getCodeList());
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
            end = LocalTime.now();     //finishing recording time;
            isWinning = true;
        }
        guessResults.addAll(guessResultsWhite);
        guessResults.addAll(guessResultsBlack);
        guessResults.addAll(guessResultsGray);
        return guessResults;
    }

    @FXML
    private void validate(ActionEvent event) {
        if (guess.getGuess().size() == 4) {
            onCheck = true;
            System.out.println(guess.getGuess());
            List<Color> guessList = validateResults();

            for (Node node : validateGridpanes[count / 4 - 1].getChildren()) {
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
                TextInputDialog dialog = new TextInputDialog("cai");
                dialog.setTitle("Winning Message");
                dialog.setHeaderText("      \\(^_^)/");
                dialog.setContentText("Good Job!\nYou took " + count / 4 + " times!\nPlease enter your name:");

// Traditional way to get the response value.
                Optional<String> result = dialog.showAndWait();
                LocalTime timeElapsed = end.minus(start + 3600000, ChronoUnit.MILLIS);


                if (result.isPresent()) {
                    System.out.println("Your name: " + result.get());
                    try {
                        dbConnectorSingleton.getInstance().insert(result.get(), timeElapsed.toString(), code.substringCodelist());
                    } catch (SQLException ex) {
                        Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                result.ifPresent(name -> System.out.println("Your name: " + name));
//                Alert alert = new Alert(AlertType.INFORMATION);
//                alert.setTitle("Winning Message");
//                alert.setHeaderText("       \\(^_^)/");
//                alert.setContentText("Good Job!\n You took " + count / 4 + " times!");
//                alert.showAndWait();
                reset();// reset code and play again
            }

            if (count == 40 && !isWinning) {

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Losing Message");
                alert.setHeaderText("       (>_<)");
                alert.setContentText("Game Over");
                alert.showAndWait();
                reset();// reset code and play again
            }
        }
    }

    @FXML
    Circle ansCircle0;
    @FXML
    Circle ansCircle1;
    @FXML
    Circle ansCircle2;
    @FXML
    Circle ansCircle3;

    /*
    reset gridpanes, erase all painted circles 
    remove previous results
    
     */
    private void reset() {
        checkButton.setDisable(true);
        isWinning = false;
        count = 0;
        viewGridpane.getChildren().clear();
        code.setCodeList(code.generateCode());
        clearVGP();
        ansCircle0.setFill(Color.GRAY);
        ansCircle1.setFill(Color.GRAY);
        ansCircle2.setFill(Color.GRAY);
        ansCircle3.setFill(Color.GRAY);

    }

    @FXML
    TextArea textArea;

    /**
     * show or hide game rule
     *
     */
    @FXML
    private void displayText() {

        if (checkBoxRule.isSelected() == true) {
            textArea.setVisible(true);
        } else {
            textArea.setVisible(false);
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

    long start;
    LocalTime end;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //your code
        onCheck = true;
        checkButton.setDisable(true);
        map = new HashMap<>();
        map.put("blueButton", Color.DODGERBLUE);
        map.put("yellowButton", Color.YELLOW);
        map.put("purpleButton", Color.MAGENTA);
        map.put("redButton", Color.RED);
        map.put("greenButton", Color.GREEN);
        map.put("orangeButton", Color.ORANGE);
        //setMyGuess((List<String>) new ArrayList());
        validateGridpanes = new GridPane[]{validateGridpane0, validateGridpane1, validateGridpane2, validateGridpane3, validateGridpane4, validateGridpane5,
            validateGridpane6, validateGridpane7, validateGridpane8, validateGridpane9};
        textArea.setVisible(false);
        textArea.setBackground(Background.EMPTY);
        textArea.setText("• Click peg to guess correct code order\n"
                + "• White indicates correct color wrong position\n"
                + "• Black indicates correct color & position\n"
                + "• Tap check button to verify result\n"
                + "• One peg only appear once\n"
                + "• Ten chances in total");

    }

}
