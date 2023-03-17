package com.labbserver.barnappkai;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {

    Paint paint;
    boolean run;  // if true = run game

    int blockSizeVertical;
    int blockSizeHorizontal;

    int currentBlockPos;
    int currentBlockSize;
    int currentBlockColor;

    ArrayList<Rect> displayedBlocks;  // contains displayed block excluding current block
    int displayedBlocksLimit;
    boolean initializeBlocks;  // if true = create starting blocks only once

    ArrayList<Integer> blockColorArray;
    boolean initializeColors;

    boolean touched;

    int blockVelocity;
    boolean moveBack;
    boolean moveForward;

    int score;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        run = true;

        blockSizeVertical = 100;
        blockSizeHorizontal = 400;

        currentBlockSize = blockSizeHorizontal;

        displayedBlocks = new ArrayList<Rect>();
        displayedBlocksLimit = 7;
        initializeBlocks = true;

        blockColorArray = new ArrayList<Integer>();
        initializeColors = true;

        currentBlockColor = randomColor();

        touched = false;

        currentBlockPos = 0;
        blockVelocity = 6;
        moveBack = true;
        moveForward = false;

        score = 0;

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(0);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);


    }

    @Override
    public void onDraw(Canvas canvas) {

        // Add starting blocks and colors to array once at beginning of game
        setColorArray();
        generateInitialBlocks();

        for(int i = 0; i < displayedBlocks.size(); i++) {
            paint.setColor(blockColorArray.get(i));
            canvas.drawRect(displayedBlocks.get(i), paint);
        }

        Rect currentBlock = generateBlock(currentBlockSize);
        paint.setColor(currentBlockColor);
        canvas.drawRect(currentBlock, paint);

        if(run) {
            moveBlock(currentBlock);
            if(touched) {
                boolean success = validateBlock(currentBlock);
                if(success) {
                    score++;

                    updateBlockArray(currentBlock);
                    updateColorArray(currentBlockColor);
                    updateSpeed();

                    currentBlockColor = randomColor();

                    ((MainActivity) getActivity()).setScoreText(score);

                    currentBlockPos = 0;
                    if(currentBlockSize <= 0) {
                        endGame(this);
                    }
                }
                else {
                    run = false;
                    endGame(this);
                }
                touched = false;
            }
        }

        invalidate();



    }

    public int randomColor() {
        Random rand = new Random();
        String backgroundColor = "#89b5fa";
        while(true) {
            int r = rand.nextInt(256);
            int g = rand.nextInt(256);
            int b = rand.nextInt(256);
            int color = Color.rgb(r, g, b);

            // Regenerate color if matches background for visibility issues
            if(color != Color.parseColor(backgroundColor)) {
                return color;
            }
        }
    }

    // Initialize random colors to array
    public void setColorArray() {
        if(initializeColors) {
            for(int i = 0; i <= displayedBlocksLimit; i++) {
                int color = randomColor();
                blockColorArray.add(color);
            }
            initializeColors = false;
        }
    }

    public void updateColorArray(int color) {
        blockColorArray.remove(0);

        blockColorArray.add(color);
    }

    // Initialize starting blocks to array once
    public void generateInitialBlocks() {
        if(initializeBlocks) {
            for(int i = 0; i <= displayedBlocksLimit; i++) {
                Rect block = new Rect();

                block.left = this.getWidth()/2 - blockSizeHorizontal/2;
                block.top = this.getHeight() - (blockSizeVertical * (i+1));
                block.right = block.left + blockSizeHorizontal;
                block.bottom = block.top + blockSizeVertical;

                displayedBlocks.add(block);
            }
            initializeBlocks = false;
        }
    }

    public Rect generateBlock(int newBlockSize) {
        Rect block = new Rect();
        block.left = this.getWidth() - currentBlockSize + currentBlockPos;
        block.top = this.getHeight() - (blockSizeVertical * (displayedBlocksLimit + 2));
        block.right = block.left + newBlockSize;
        block.bottom = block.top + blockSizeVertical;

        return block;
    }

    public void moveBlock(Rect block) {

        if(block.right >= this.getWidth()) {
            moveBack = true;
            moveForward = false;
        }
        if(block.left <= 0) {
            moveForward = true;
            moveBack = false;
        }

        if(moveBack) {
            currentBlockPos -= blockVelocity;
        }
        if(moveForward) {
            currentBlockPos += blockVelocity;
        }
    }

    public void updateSpeed() {
        if(score == 5) {
            blockVelocity++;
        }
        else {
            if(score % 10 == 0) {
                int speedChange = score / 10;
                blockVelocity += 2 * speedChange;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            touched = true;
        }
        return true;
    }

    public boolean validateBlock(Rect block) {

        Rect lastBlock = displayedBlocks.get(displayedBlocksLimit - 1);

        if(block.left > lastBlock.right || block.right < lastBlock.left) {
            return false;
        }

        return true;
    }


    public void updateBlockArray(Rect newBlock) {

        Rect lastBlock = displayedBlocks.get(displayedBlocksLimit);

        // Calculate size difference
        if(newBlock.left < lastBlock.left) {
            currentBlockSize = newBlock.right - lastBlock.left;
            newBlock.left = lastBlock.left;
            newBlock.right = newBlock.left + currentBlockSize;
        }
        else if(newBlock.left > lastBlock.left) {
            currentBlockSize = lastBlock.right - newBlock.left;
            newBlock.right = lastBlock.right;
            newBlock.left = newBlock.right - currentBlockSize;
        }
        else {
            currentBlockSize = lastBlock.right - lastBlock.left;
            newBlock.left = lastBlock.left;
            newBlock.right = lastBlock.right;
        }

        if(currentBlockSize < 0) {
            currentBlockSize = 0;
        }

        displayedBlocks.remove(0);
        displayedBlocks.add(newBlock);

        // Update positions
        for(int i = 0; i <= displayedBlocksLimit; i++) {
            displayedBlocks.get(i).top = this.getHeight() - (blockSizeVertical * (i+1));
            displayedBlocks.get(i).bottom = displayedBlocks.get(i).top + blockSizeVertical;
        }
    }

    public void endGame(View view) {
        Intent i = new Intent(this.getContext(), GameOverActivity.class);
        i.putExtra("score", score);
        this.getContext().startActivity(i);
    }

    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }
}
