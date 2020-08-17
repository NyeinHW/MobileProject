package com.nus.iss.workshop.the_memory_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.GridLayout;
import android.widget.ImageButton;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;

public class MemoryButton extends AppCompatImageButton {
    private int row;
    private int column;
    private Bitmap frontImageId;

    private boolean isFlipped = false;
    private boolean isMatched = false;

    private Bitmap frontImage;
    private Bitmap backImage;

    public MemoryButton(Context context, int r, int c, Bitmap bitmap) {
        super(context);

        this.row = r;
        this.column = c;
        this.frontImageId = bitmap;


        frontImage = bitmap;
        backImage = BitmapFactory.decodeResource(getResources(), R.drawable.question);

        //setting default question mark
        setImageBitmap(backImage);

        //manipulating child elements of the grid layout which is parent
        GridLayout.LayoutParams tempParams = new GridLayout.LayoutParams(GridLayout.spec(r), GridLayout.spec(c));
        //controlling the dimensions
        tempParams.width = (int) getResources().getDisplayMetrics().density * 150;
        tempParams.height = (int) getResources().getDisplayMetrics().density * 150;
        setLayoutParams(tempParams);
    }

    public Bitmap getFrontImageId() {
        return frontImageId;
    }

    public void setFrontImageId(Bitmap frontImageId) {
        this.frontImageId = frontImageId;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    public void setFlipped(boolean flipped) {
        isFlipped = flipped;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    public Bitmap getFrontImage() {
        return frontImage;
    }

    public void setFrontImage(Bitmap frontImage) {
        this.frontImage = frontImage;
    }

    //flipping function
    public void flip(){
        if(isMatched){
            return;
        }
        if(isFlipped){
            setImageBitmap(backImage);
            isFlipped = false;
        }
        else {
            setImageBitmap(frontImage);
            isFlipped = true;
        }
    }
}
