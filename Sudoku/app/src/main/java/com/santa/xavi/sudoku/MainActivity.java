package com.santa.xavi.sudoku;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    int[][] S = new int[9][9];
    int[][] Solution = new int[9][9];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        S = initMatrix(S);
    }
    public int[][] initMatrix(int[][] S){
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                S[i][j] = 0;
            }
        }
        return S;
    }
    public void changeValue(Button b, EditText input){
        String s = input.getText().toString();
        String nameId = getResources().getResourceEntryName(b.getId()); //The id contains the row and col
        String sRow = nameId.substring(1,2);
        String sCol = nameId.substring(2);
        int num;
        int row;
        int col;
        try {
            num = Integer.parseInt(s);
        } catch(NumberFormatException nfe) {num = 0;}
        try {
            row = Integer.parseInt(sRow);
        } catch(NumberFormatException nfe) {row = 0;}
        try {
            col = Integer.parseInt(sCol);
        } catch(NumberFormatException nfe) {col = 0;}
        this.S[row][col] = num;
    }
    public void xavi(View view){
        Button b = (Button) view;
        EditText input = findViewById(R.id.editText);
        String s = input.getText().toString();   //Get the number in the editText
        changeValue(b,input);                   //change it in the matrix S
        b.setText(s);                          //put it in the visual cell
    }
    public void solveSudoku(View view){
        Sudoku s = new Sudoku();
        try{
            this.Solution = s.start(this.S);
            showSolution();
        }catch(Exception e){

        }

    }
    public void showSolution(){
        TextView v = findViewById(R.id.textView1);
        v.setText(" ");
        int num;
        Button b;
        String s;
        int Id ;
        for (int i = 0; i < 9; i ++){
            for(int j = 0; j < 9; j++){
                s = "_"+i+""+j;
                Id = getResources().getIdentifier(s,"id",getPackageName());
                b = findViewById(Id);
                num = this.Solution[i][j];
                s = String.valueOf(num);
                b.setText(s);
            }
        }
    }
    public void restart(View view){
        TextView v = findViewById(R.id.textView1);
        v.setText("Input your sudoku");
        this.S = initMatrix(this.S);
        this.Solution = initMatrix(this.Solution);
        Button b;
        String s;
        int Id ;
        for (int i = 0; i < 9; i ++){
            for(int j = 0; j < 9; j++){
                s = "_"+i+""+j;
                Id = getResources().getIdentifier(s,"id",getPackageName());
                b = findViewById(Id);
                b.setText("");
            }
        }
    }
}
