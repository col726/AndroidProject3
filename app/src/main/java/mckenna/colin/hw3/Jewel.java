package mckenna.colin.hw3;

import android.graphics.Rect;

/**
 * Created by cmckenna on 10/14/2015.
 */
public class Jewel {




    public enum Type {
        Square, Circle, Diamond, Hourglass
    }

    private Rect bounds = new Rect();

    private Type type;

    private int row;
    private int column;

    private boolean isMatch;
    private boolean isHighlighted;

    public Jewel(){
        this.type = Type.Square;
        this.row = 0;
        this.column = 0;
        this.isMatch = false;
        this.isHighlighted = false;
    }

    public Jewel(Type type, int row, int column){
        this.type = type;
        this.row = row;
        this.column = column;
        this.isMatch = false;
        this.isHighlighted = false;
    }

    public Rect getBounds() { return bounds; }
    public Type getType() { return type; }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column){
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public boolean isMatch() {
        return isMatch;
    }

    public void setIsMatch(boolean isMatch) {
        this.isMatch = isMatch;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setIsHighlighted(boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
    }



}
