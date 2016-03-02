package mckenna.colin.hw3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by cmckenna on 10/14/2015.
 */
public class JewelBoard {

    private Jewel [][] board;

    private float boardWidth;
    private   float gridPadding;


    private final int NUM_ROWS = 8;
    private final int NUM_COLS = 8;

    private Random random;
    private boolean initialized;
    private boolean hasMatches;
    private boolean hasHighlights;
    private float jewelWidth;
    private boolean firstRun;

    private int score;

    public JewelBoard()
    {
        board = new Jewel[NUM_ROWS][NUM_COLS];
        random = new Random();
        this.firstRun = false;
        fillBoard();
    }

    public JewelBoard(int boardWidth, int jewelPadding)
    {
        board = new Jewel[NUM_ROWS][NUM_COLS];
        random = new Random();
        this.boardWidth = boardWidth;
        this.jewelWidth = boardWidth / 9;
        this.gridPadding = jewelPadding;
        this.firstRun = false;
        fillBoard();
        initBoard();
    }

    private void fillBoard() {
        for (int i = 0; i< NUM_ROWS; i++)
            for(int j = 0; j < NUM_COLS; j++)
                board[i][j] = new Jewel(getRandJewelType(), i, j);
    }

    private Jewel.Type getRandJewelType() {

        int shape = random.nextInt(4);
        Jewel.Type temp = Jewel.Type.Circle;
        switch (shape) {
            case 0:
                temp = Jewel.Type.Circle;
                break;
            case 1:
                temp = Jewel.Type.Square;
                break;
            case 2:
                temp = Jewel.Type.Diamond;
                break;
            case 3:
                temp = Jewel.Type.Hourglass;
                break;
        }
        return temp;
    }

    public int getRows() { return NUM_ROWS; }
    public int getCols() { return NUM_COLS; }

    public boolean isFirstRun(){return firstRun;}

    public Jewel getJewel(int i, int j) {
        if(i< NUM_ROWS && j < NUM_COLS)
            return board[i][j];
        else
            return null;
    }

    public void setBoardWidth(float boardWidth) { this.boardWidth = boardWidth;
                                                 this.jewelWidth = boardWidth / 9;}
    public float getBoardWidth () { return boardWidth; }

    public float getGridPadding() {
        return gridPadding;
    }

    public void setGridPadding(float gridPadding) {
        this.gridPadding = gridPadding;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized(boolean i){
        this.initialized = i;
    }

    public void initBoard()
    {
        Jewel jewel;

        if(firstRun == false)
            firstRun = true;
        for(int i=0; i < this.getRows(); i++)
            for(int j=0; j < this.getCols(); j++) {
                jewel = this.getJewel(i, j);
                int left = j * (int)(jewelWidth + gridPadding);
                int top = i * (int)(jewelWidth + gridPadding);
                if(jewel != null)
                    jewel.getBounds().set(left, top, left + (int) jewelWidth, top + (int) (jewelWidth));
            }

        this.setInitialized(true);
    }

    private void checkBoard(){

    }

    public float getJewelWidth() {
        return jewelWidth;
    }

    public void setJewelWidth(float jewelWidth) {
        this.jewelWidth = jewelWidth;
    }

    public void checkMatches() {
        Jewel curr ;
        Jewel oneBack;
        Jewel twoBack;

        boolean check1 = false;
        boolean check2 = false;
        boolean check3 = false;
        boolean check4 = false;
        for(int i=0; i < this.getRows(); i++)
            for(int j=0; j < this.getCols(); j++) {
                curr=this.getJewel(i, j);
                if(i-2 > -1) {
                    oneBack = this.getJewel(i - 1, j);
                    twoBack = this.getJewel(i - 2, j);
                    check1 = checkMatch(curr, oneBack, twoBack);
                }
                if(i+2 < NUM_ROWS -1) {
                    oneBack = this.getJewel(i + 1, j);
                    twoBack = this.getJewel(i + 2, j);
                    check2 = checkMatch(curr, oneBack, twoBack);
                }

                if(j-2 > -1) {
                    oneBack = this.getJewel(i, j - 1);
                    twoBack = this.getJewel(i, j - 2);
                    check3 = checkMatch(curr, oneBack, twoBack);
                }
                if(j+2 < NUM_COLS -1) {
                    oneBack = this.getJewel(i, j + 1);
                    twoBack = this.getJewel(i, j + 2);
                    check4 = checkMatch(curr, oneBack, twoBack);
                }
            }
        if(!(check1 || check2 || check3 || check4 ))
            this.hasMatches = false;
    }

    private boolean checkMatch(Jewel curr, Jewel oneBack, Jewel twoBack) {
        if(curr != null && oneBack != null && twoBack != null) {
            if (curr.getType() == oneBack.getType() && curr.getType() == twoBack.getType()) {
                curr.setIsMatch(true);
                oneBack.setIsMatch(true);
                twoBack.setIsMatch(true);
                this.hasMatches = true;
                score++;
                return true;
            }
        }
        return false;
    }

    public void showMatches() {
        Jewel temp;
        for(int i=0; i < this.getRows(); i++)
            for(int j=0; j < this.getCols(); j++) {
                if (i%2==0) {
                    temp = this.getJewel(i, j);
                    temp.getBounds().set(temp.getBounds());
                }
            }
    }

    public boolean hasMatches() {
        return hasMatches;
    }

    public void setHasMatches(boolean hasMatches) {
        this.hasMatches = hasMatches;
    }

    public void removeMatches() {
        for(int i=0; i < this.getRows(); i++)
            for(int j=0; j < this.getCols(); j++) {
                if(getJewel(i,j) != null) {
                    if (getJewel(i, j).isMatch()) {
                        setJewel(i, j, null);
                    }
                }
            }
    }

    public void moveDown() {
        this.setInitialized(false);

        List<Jewel> active = new ArrayList<Jewel>();
        Iterator it = active.iterator();
        Jewel temp;
        int tmpCol;
        int tmpRow;

        do {
            active.clear();
            for (int i = 0; i < this.getRows() - 1; i++) {//skip bottom row, nothing can ever fall on bottom row
                for (int j = 0; j < this.getCols(); j++) {
                    temp = this.getJewel(i, j);
                    if (this.getJewel(i,j) != null && this.getJewel(i + 1, j) == null)
                        active.add(temp);
                }
            }

            for (Jewel jwl : active) {
                if(jwl != null) {
                    temp = jwl;
                    tmpRow = temp.getRow();
                    tmpCol = temp.getColumn();
                    setJewel(tmpRow + 1, tmpCol, temp);
                    setJewel(tmpRow, tmpCol, null);
                }
            }


        }
        while(!active.isEmpty());

        //initBoard();

    }

    public void replaceJewels() {
        this.setInitialized(false);
        Jewel temp;
        for (int i = 0; i < this.getRows(); i++) {
            for (int j = 0; j < this.getCols(); j++) {
                temp = getJewel(i, j);
                if(temp ==  null)
                    setJewel(i,j,new Jewel(getRandJewelType(),i,j));
            }

        }
    }

    private void setJewel(int i, int j, Jewel set) {
        if(set != null) {
            set.setRow(i);
            set.setColumn(j);
        }
        board[i][j] = set;
    }

    public void highlightJewels(Jewel.Type type) {
        Jewel temp;
        for (int i = 0; i < this.getRows(); i++) {
            for (int j = 0; j < this.getCols(); j++) {
                temp = getJewel(i, j);
                if(temp !=  null) {
                    if (temp.getType() == type) {
                        temp.setIsHighlighted(true);
                        this.hasHighlights = true;
                    }
                }
            }

        }
    }

    public void clearHighlights() {
        Jewel temp;
        for (int i = 0; i < this.getRows(); i++) {
            for (int j = 0; j < this.getCols(); j++) {
                temp = getJewel(i, j);
                if(temp !=  null) {
                    temp.setIsHighlighted(false);
                    this.hasHighlights = false;
                }
            }

        }
    }

    public boolean hasHighlights() {
        return this.hasHighlights;
    }

    public void setHasHighlights(boolean hasHighlights) {
        this.hasHighlights = hasHighlights;
    }

    public void swapJewels(Jewel selectedJewel, Jewel swapJewel) {
        Jewel tmp = selectedJewel;
        this.setJewel(swapJewel.getRow(), swapJewel.getColumn(), selectedJewel);
        this.setJewel(tmp.getRow(), tmp.getColumn(), swapJewel);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
