package com.example.johnvdk.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class GameBoard extends AppCompatActivity implements View.OnClickListener {

    private Button[][] buttons = new Button[3][3];

    private boolean player1Turn = true;

    private int roundCount;

    private int player1Points;
    private int player2Points;

    private boolean computer = false;

    private TextView textViewPlayer1;
    private TextView textViewPlayer2;

    private ArrayList<String> winners = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameboard);

        roundCount = 0;
        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);
        if(getIntent().getIntExtra("numPlayer", 1) == 1){
            computer = true;
            textViewPlayer2.setText("Computer: " + 0);
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++){
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }

        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(!((Button) v).getText().toString().equals("")) {
            return;
        }

        String[][] field = new String[3][3];

        if (computer){
            ((Button) v).setText("X");
            updateGameBoard(field);
            roundCount++;
            if(roundCount < 9){
                getNextMove(field);
                updateGameBoard(field);
            }
        }
        else{
            if (roundCount%2 == 0){((Button) v).setText("X"); }
            else{((Button) v).setText("O");}
            updateGameBoard(field);
            roundCount++;
        }

        if (checkForWin(field, "X")) {
            player1Wins();
        } else if (checkForWin(field, "O")){
            player2Wins();
        } else if (roundCount == 9) {
            draw();
        }

    }

    private void updateGameBoard(String[][] gameBoard){
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++) {
                gameBoard[i][j] = buttons[i][j].getText().toString();
            }
        }
    }

    private boolean checkForWin(String[][] field, String letter) {

        if(letter == "X"){
            if(getScore(field, true) == -100){return true;}
        } else if (letter == "O"){
            if(getScore(field, true) == 100){return true;}
        }

        return false;
    }

    private boolean checkForWin(String[][] field) {

        if(abs(getScore(field, true)) == 100){
            return true;
        }
        return false;
    }

    private void player1Wins() {
        player1Points++;
        Toast.makeText( this, "Player 1 wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void player2Wins() {
        player2Points++;
        Toast.makeText( this, "Player 2 wins !", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void draw() {
        Toast.makeText( this, "Draw!", Toast.LENGTH_SHORT).show();
        resetBoard();
    }

    private void updatePointsText() {
        textViewPlayer1.setText("Player 1: " + player1Points);
        if (computer){
            textViewPlayer2.setText("Computer: " + player2Points);
        }
        else{
            textViewPlayer2.setText("Player 2: " + player2Points);
        }
    }

    private void resetBoard() {
        for (int i = 0; i <3; i++){
            for (int j = 0; j < 3; j++){
                buttons[i][j].setText("");
            }
        }
        roundCount = 0;
    }

    private void resetGame() {
        player1Points = 0;
        player2Points = 0;
        updatePointsText();
        resetBoard();
    }


    private void getNextMove(String[][] gameBoard){
        int[] bestChoice = miniMax(gameBoard, 4, "O", Integer.MIN_VALUE, Integer.MAX_VALUE);
        if(roundCount < 8){
            roundCount++;
        }
        buttons[bestChoice[1]][bestChoice[2]].setText("O");
    }

    private int[] miniMax(String[][] gameBoard, int roundCount, String player, int maximum, int minimum){
        int score;
        int bestRow = -1;
        int bestCol = -1;
        int tempCount = roundCount - 1;

        if (checkForWin(gameBoard) || isFull(gameBoard) || (roundCount == 0)){
            score = getScore(gameBoard, false);
            if(player == "O"){
                score += roundCount;
            }
            else if(player == "X"){
                score -= roundCount;
            }
            return new int[] {score, bestRow, bestCol};
        }
        else{
            for (int row = 0; row < 3; row++){
                for (int col = 0; col < 3; col++) {

                    if (gameBoard[row][col].equals("")) {
                        String[][] copy = copyGameBoard(gameBoard);
                        copy[row][col] = player;
                        if (player == "O"){
                            score = miniMax(copy, tempCount, "X", maximum, minimum)[0];
                            if (score > maximum) {
                                maximum = score;
                                bestRow = row;
                                bestCol = col;
                            }
                        } else {
                            score = miniMax(copy, tempCount, "O", maximum, minimum)[0];
                            if(score < minimum){
                                minimum = score;
                                bestRow = row;
                                bestCol = col;
                            }
                        }
                    }
                    if(maximum >= minimum){break;}
                }
                if(maximum >= minimum){break;}
            }
            if(player == "O"){return new int[] {maximum, bestRow, bestCol}; }
            else{
                return new int[] {minimum, bestRow, bestCol};
            }
        }
    }

//    private void Debug(String msg){
//        Log.d("TESTING", msg);
//    }


//    private void printGameBoard(String[][] gameBoard){
//        StringBuilder row = new StringBuilder();
//        for(int i = 0; i < 3; i++) {
//            for (int j = 0; j < 3; j++) {
//                if(gameBoard[i][j] == ""){
//                    row.append("_");
//                }else{
//
//                    row.append(gameBoard[i][j].toString());
//                }
//            }
//            row.append("\n");
//        }
//        row.append("done \n");
//        Log.d("TESTING", row.toString());
//    }

    private boolean isFull (String[][] gameBoard){
        for(int row = 0; row < 3; row++){
            for(int col = 0; col < 3; col++){

                if(gameBoard[col][row] == ""){
                    return false;
                }
            }
        }

        return true;
    }

    private int getScore (String[][] gameBoard, boolean checkForWin){
        int score = 0;
        String[] diag1 = new String[3];
        String[] diag2 = new String[3];
        for (int i = 0; i < 3; i++){
            String[] row = new String[3];
            String[] col = new String[3];
            for (int j = 0; j < 3; j++){
                row[j] = gameBoard[i][j];
                col[j] = gameBoard[j][i];
            }
            diag1[i] = gameBoard[i][i];
            diag2[i] = gameBoard[i][2-i];
            int rowScore = getLineScore(row);
            int colScore = getLineScore(col);
            if(checkForWin){
                if(rowScore == 100 || colScore == 100){return 100;}
                else if (rowScore == -100 || colScore == -100){return -100;}
            }
            else{
                score += rowScore;
                score += colScore;
            }
        }


        int diag1Score = getLineScore(diag1);
        int diag2Score = getLineScore(diag2);
        if(checkForWin){
            if(diag1Score == 100 || diag2Score == 100){return 100;}
            else if (diag1Score == -100 || diag2Score == -100){return -100;}
        } else{
            score+= diag1Score;
            score+= diag2Score;
        }

        return score;
    }

    private int getLineScore (String[] line){
        int score = 0;

        if(line[0] == "O"){
            score = 1;
        }
        else if (line[0] == "X"){
            score = -1;
        }

        if(line[1] == "O"){
            if(score == 1){
                score = 10;
            } else if (score == -1){
                return 0;
            }
            else{
                score = 1;
            }
        } else if (line[1] == "X"){
            if(score == -1){
                score = -10;
            } else if (score == 1){
                return 0;
            }
            else{
                score = -1;
            }
        }

        if(line[2] == "O"){
            if(score > 0){
                score *= 10;
            } else if (score < 0){
                return 0;
            }
            else{
                score = 1;
            }
        } else if (line[2] == "X"){
            if(score < 0){
                score *= 10;
            } else if (score > 0){
                return 0;
            }
            else{
                score = -1;
            }
        }



        return score;
    }

    private String[][] copyGameBoard(String[][] gameBoard){
        String[][] copy = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                copy[i][j] = gameBoard[i][j];
            }
        }

        return copy;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("roundCount", roundCount);
        outState.putInt("player1Points", player1Points);
        outState.putInt("player2Points", player2Points);
        outState.putBoolean("player1Turn", player1Turn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        roundCount = savedInstanceState.getInt("roundCount");
        player1Points = savedInstanceState.getInt("player1Points");
        player2Points = savedInstanceState.getInt("player2Points");
        player1Turn = savedInstanceState.getBoolean("player1Turn");
    }
}
