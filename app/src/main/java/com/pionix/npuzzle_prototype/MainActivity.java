package com.pionix.npuzzle_prototype;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    AbstractBoard board = new BoardArray2D();

    public void incrementRowClick(View view) {
        int rowSize = board.getRowSize();
        int colSize = board.getColSize();

        if(rowSize < 9) {
            rowSize++;
            TextView rowSizeText = findViewById(R.id.rowSizeText);
            rowSizeText.setText(String.valueOf(rowSize));
            board.setSize(rowSize, colSize);
        }
    }

    public void decrementRowClick(View view) {
        int rowSize = board.getRowSize();
        int colSize = board.getColSize();

        if(rowSize > 2) {
            rowSize--;
            TextView rowSizeText = findViewById(R.id.rowSizeText);
            rowSizeText.setText(String.valueOf(rowSize));
            board.setSize(rowSize, colSize);
        }
    }

    public void incrementColClick(View view) {
        int rowSize = board.getRowSize();
        int colSize = board.getColSize();

        if(colSize < 9) {
            colSize++;
            TextView colSizeText = findViewById(R.id.colSizeText);
            colSizeText.setText(String.valueOf(colSize));
            board.setSize(rowSize, colSize);
        }
    }

    public void decrementColClick(View view) {
        int rowSize = board.getRowSize();
        int colSize = board.getColSize();

        if(colSize > 2) {
            colSize--;
            TextView colSizeText = findViewById(R.id.colSizeText);
            colSizeText.setText(String.valueOf(colSize));
            board.setSize(rowSize, colSize);
        }
    }

    public void updateDimensionsDisplay() {
        int rowSize = board.getRowSize();
        int colSize = board.getColSize();

        TextView currentBoardDimensionDisplay = findViewById(R.id.currentBoardDimensionDisplay);
        currentBoardDimensionDisplay.setText(rowSize + " x " + colSize);
    }

    public void resetBoard(View view) {
        int rowSize = board.getRowSize();
        int colSize = board.getColSize();

        resizeGrid(rowSize, colSize);
        board.reset();
        resetToSolution();
    }

    public void resizeGrid(int newRow, int newCol) {
        GridLayout boardLayout = findViewById(R.id.gameLayout);
        boardLayout.removeAllViews();
        boardLayout.setColumnCount(newCol);
        boardLayout.setRowCount(newRow);
    }

    public void shuffleBoard(View view) {
        board.setCell(0,0,69);
        resetToSolution();
    }

    public void resetToSolution() {
        int rowSize = board.getRowSize();
        int colSize = board.getColSize();

        updateDimensionsDisplay();

        Log.d("info", board.toString());
        float dimsHeightOfText =  (325 / rowSize) * this.getResources().getDisplayMetrics().density;
        float dimsWidthOfText =  (325 / colSize) * this.getResources().getDisplayMetrics().density;
        GridLayout boardLayout = findViewById(R.id.gameLayout);
        TextView titleText;
        boardLayout.removeAllViews();
        int index = 0;
        //int currentCorrect = 1;
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                titleText = new TextView(this);
                //String txt = i + "," + j;

                int currentValue = board.cell(i, j);

                if(currentValue == -1) {
                    titleText.setText("__");
                } else {
                    titleText.setText(String.valueOf(board.cell(i,j)));
                }



                /*
                if(currentCorrect == rowSize * colSize) {
                    titleText.setId(1000);
                    titleText.setText("__");
                } else {
                    titleText.setId(1000 + currentCorrect);
                    titleText.setText(String.valueOf(currentCorrect++));
                }*/

                boardLayout.addView(titleText, index);
                index++;

                GridLayout.LayoutParams param =new GridLayout.LayoutParams();
                param.height = (int) dimsHeightOfText;
                param.width = (int) dimsWidthOfText;
                param.leftMargin = 5;
                param.topMargin = 5;
                param.setGravity(Gravity.CENTER);
                param.columnSpec = GridLayout.spec(j);
                param.rowSpec = GridLayout.spec(i);

                titleText.setLayoutParams (param);
            }
        }
    }

    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View boardLayout = findViewById(R.id.gameLayout);

        DetectSwipeGestureListener swipeListener = new DetectSwipeGestureListener();
        swipeListener.setActivity(this);

        mDetector = new GestureDetector(this, swipeListener);

        boardLayout.setOnTouchListener(touchListener);

    }

    // This touch listener passes everything on to the gesture detector.
    // That saves us the trouble of interpreting the raw touch events
    // ourselves.
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // pass the events to the gesture detector
            // a return value of true means the detector is handling it
            // a return value of false means the detector didn't
            // recognize the event
            if(v.getId() == R.id.gameLayout) {
                return mDetector.onTouchEvent(event);
            }

            return false;
        }
    };


}
