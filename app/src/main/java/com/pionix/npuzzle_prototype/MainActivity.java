package com.pionix.npuzzle_prototype;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


public class MainActivity extends AppCompatActivity {

    AbstractBoard board = new BoardArray2D();
    Interpreter tflite;

    public void incrementRowClick(View view) {
        int rowSize = board.getRowSize();
        int colSize = board.getColSize();

        if(rowSize < 9) {
            rowSize++;
            TextView rowSizeText = findViewById(R.id.rowSizeText);
            rowSizeText.setText(String.valueOf(rowSize));
            board.setSize(rowSize, colSize);
            resizeGrid(rowSize, colSize);
        }

        resetToSolution();
    }

    public void decrementRowClick(View view) {
        int rowSize = board.getRowSize();
        int colSize = board.getColSize();

        if(rowSize > 3) {
            rowSize--;
            TextView rowSizeText = findViewById(R.id.rowSizeText);
            rowSizeText.setText(String.valueOf(rowSize));
            board.setSize(rowSize, colSize);
            resizeGrid(rowSize, colSize);
        }

        resetToSolution();
    }

    public void incrementColClick(View view) {
        int rowSize = board.getRowSize();
        int colSize = board.getColSize();

        if(colSize < 9) {
            colSize++;
            TextView colSizeText = findViewById(R.id.colSizeText);
            colSizeText.setText(String.valueOf(colSize));
            board.setSize(rowSize, colSize);
            resizeGrid(rowSize, colSize);
        }

        resetToSolution();
    }

    public void decrementColClick(View view) {
        int rowSize = board.getRowSize();
        int colSize = board.getColSize();

        if(colSize > 3) {
            colSize--;
            TextView colSizeText = findViewById(R.id.colSizeText);
            colSizeText.setText(String.valueOf(colSize));
            board.setSize(rowSize, colSize);
            resizeGrid(rowSize, colSize);
        }

        resetToSolution();
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
        board.shuffle(10);
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
                    titleText.setText("  ");
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

                titleText.setGravity(Gravity.CENTER);
                titleText.setBackgroundColor(Color.RED);
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

        try {
            tflite = new Interpreter(loadModelFile());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("npuzzlev1.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffSet = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffSet, declaredLength);
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

    public void hintBoard(View view) {
        int[] boardOneDimension = board.toOneDimension();

        float[][] nnInput = new float[1][boardOneDimension.length];
        for(int i = 0; i < boardOneDimension.length; i++) {
            double currValueScaled = (boardOneDimension[i] / 100.0);
            nnInput[0][i] = (float)currValueScaled;
        }

        float[][] nnOutput = new float[1][4];

        tflite.run(nnInput, nnOutput);

        int moveIndex = 0;
        float max = 0;
        for(int i = 0; i < 4; i++) {
            float curr = nnOutput[0][i];
            if(curr > max) {
                max = curr;
                moveIndex = i;
            }
        }

        switch (moveIndex) {
            case 0:
                board.move('U');
                break;
            case 1:
                board.move('D');
                break;
            case 2:
                board.move('L');
                break;
            case 3:
                board.move('R');
                break;
        }

        resetToSolution();
    }


}
