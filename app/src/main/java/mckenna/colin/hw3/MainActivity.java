package mckenna.colin.hw3;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private static final int CHECK_MATCH = 0;
    private static final int SHOW_MATCH = 1;
    private static final int REMOVE_MATCH = 2;
    private static final int MOVE_DOWN = 3;
    private static final int FILL_PIECES = 4;

    private static final int MATCH_GROWTH = 2;

    private ShapeDrawable diamond;
    private ShapeDrawable hourglass;
    private Drawable circle;
    private Drawable square;

    private Drawable highlightSquare;

    private TextView debugText;

    private JewelBoard board;
    private Jewel selectedJewel;
    private Rect originalLoc;
    private Rect swapLoc;

    private GameBoardArea gameBoardArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debugText = (TextView) findViewById(R.id.debugText);

        circle = getResources().getDrawable(R.drawable.circle);
        square = getResources().getDrawable(R.drawable.square);
        highlightSquare = getResources().getDrawable(R.drawable.highlight_square);

        diamond = createDiamond((int) getResources().getDimension(R.dimen.jewel_width));
        hourglass = createHourglass((int)getResources().getDimension(R.dimen.jewel_width));

        board = new JewelBoard();

        LinearLayout mainView = (LinearLayout) findViewById(R.id.main);
        gameBoardArea = new GameBoardArea(this);
        mainView.addView(gameBoardArea);

        new StateTask().execute("this");

    }

    private ShapeDrawable createDiamond(int px){
        ShapeDrawable diamond = new ShapeDrawable(new Diamond(this));
        diamond.setIntrinsicWidth(px);
        diamond.setIntrinsicHeight(px);
        diamond.setBounds(0, 0, px, px);
        return diamond;
    }

    private ShapeDrawable createHourglass(int px){
        ShapeDrawable hourglass = new ShapeDrawable(new Hourglass(this));
        hourglass.setIntrinsicWidth(px);
        hourglass.setIntrinsicHeight(px);
        hourglass.setBounds(0,0,px,px);
        return hourglass;
    }
    private class StateTask extends AsyncTask<String, Integer, String> {
        private Object lock = new Object();

        // Invoked on the user-interface thread BEFORE doInBackground is called
        @Override protected void onPreExecute() {
            debugText.setText("Starting task");
            //gameBoardArea.invalidate();
        }

        // Invoked on the user-interface thread AFTER doInBackground completes
        @Override protected void onPostExecute(String result) {
            debugText.setText(result);
            //gameBoardArea.invalidate();
        }

        // Invoked on the user-interface thread whenever publishProgress() is called
        @Override protected void onProgressUpdate(Integer... values) {
            synchronized (lock) {
                try {
                    switch(values[0]) {
                        case CHECK_MATCH:
                            debugText.setText("Checking Matches");
                            gameBoardArea.invalidate();
                            break;

                        case SHOW_MATCH:
                            debugText.setText("Displaying Matches");
                            gameBoardArea.invalidate();
                            break;

                        case REMOVE_MATCH:
                            debugText.setText("Removing Matches");
                            gameBoardArea.invalidate();
                            break;
                        case MOVE_DOWN:
                            debugText.setText("Moving Existing Pieces down");
                            gameBoardArea.invalidate();
                            break;

                        case FILL_PIECES:
                            debugText.setText("Filling Empty Pieces");
                            gameBoardArea.invalidate();
                            break;
                    }
                }
                finally {
                    //gameBoardArea.invalidate();
                    lock.notifyAll();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            synchronized (lock) {

                do {
                    try {
                        //Thread.sleep(1000);
                        // make model changes
                        board.checkMatches();
                        publishProgress(CHECK_MATCH);
                        lock.wait();
                        //Thread.sleep(1000);
                        // make model changes
                        //board.showMatches();
                        publishProgress(SHOW_MATCH);
                        //lock.wait();
                        //Thread.sleep(1000);
                        board.removeMatches();
                        publishProgress(REMOVE_MATCH);
                        lock.wait();
                        //Thread.sleep(1000);
                        board.moveDown();
                        publishProgress(MOVE_DOWN);
                        lock.wait();
                        //Thread.sleep(1000);
                        board.replaceJewels();
                        publishProgress(FILL_PIECES);
                        lock.wait();
                        board.checkMatches();
                        //Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                while(board.hasMatches());
            }
            // based on some criteria you may want to return different value
            // if (error)
            //		return getResources().getString(R.string.error);
            return params[0] + ": " + "SUCCESS";
        }
    }

    public class GameBoardArea extends View {

        private Paint paint;


        int viewWidth;
        int viewHeight;



        public GameBoardArea(Context context) {
            super(context);
            setBackgroundColor(Color.WHITE);
            // IS IT OK TO CREATE & SET PAINT HERE???

            board.setGridPadding(getResources().getDimension(R.dimen.jewel_margin));

            paint = new Paint();
            paint.setColor(Color.GRAY);

        }

        //from stackOverflow, found here (http://stackoverflow.com/questions/4074937/android-how-to-get-a-custom-views-height-and-width)
        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            viewWidth = r;
            //board.setBoardWidth(this.getWidth());
            //board.initBoard();
            //new StateTask().execute("this");
            super.onLayout(changed, l, t, r, b);

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            switch (event.getAction()) {


                case MotionEvent.ACTION_DOWN:
                    //TODO: HIGHLIGHT ALL SAME SHAPE
                    Jewel jewel = null;
                    Drawable drawable = null;
                    // based on the mode, choose the drawable to display and
                    //   create a Thing to represent it
                    // we're only looking at the drawable here so we can
                    //   determine its size
                    for (int i = 0; i < board.getRows(); i++) {
                        for (int j = 0; j < board.getCols(); j++) {
                            jewel = board.getJewel(i,j);
                            if (jewel != null){
                                if (jewel.getBounds().contains(x, y)) {
                                    selectedJewel = jewel;
                                    originalLoc = jewel.getBounds();
                                }
                            }
                        }
                    }
                       board.highlightJewels(selectedJewel.getType());

                    invalidate();
                    break;



                case MotionEvent.ACTION_MOVE:
                    //TODO: Move piece under user finger
                    board.clearHighlights();
                    if (selectedJewel != null) {
                        Drawable d = null;
                        // get the drawable so we can appropriately size
                        switch(selectedJewel.getType()) {
                            case Circle:
                                d = circle;
                                break;
                            case Square:
                                d = square;
                                break;
                            case Diamond:
                                d = diamond;
                                break;
                            case Hourglass:
                                d = hourglass;
                                break;
                        }
                        Rect bounds = selectedJewel.getBounds();
                        bounds.left = x - d.getIntrinsicWidth()/2;
                        bounds.top = y - d.getIntrinsicHeight()/2;
                        bounds.right = x + d.getIntrinsicWidth()/2;
                        bounds.bottom = y + d.getIntrinsicHeight()/2;
                        invalidate();
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    //TODO: Drop piece if valid and swap
                    Jewel swapJewel = null;
                    //Drawable drawable = null;
                    // based on the mode, choose the drawable to display and
                    //   create a Thing to represent it
                    // we're only looking at the drawable here so we can
                    //   determine its size
                    for (int i = 0; i < board.getRows(); i++) {
                        for (int j = 0; j < board.getCols(); j++) {
                            jewel = board.getJewel(i,j);
                            if (jewel != null){
                                if (jewel.getBounds().contains(x, y) && jewel != selectedJewel) {
                                    swapJewel = jewel;
                                    swapLoc = jewel.getBounds();
                                }
                            }
                        }
                    }

                    //if(selectedJewel != null && swapJewel == null)
                    //    selectedJewel.getBounds().set(originalLoc);
                    if(swapJewel != null) {
                        board.swapJewels(selectedJewel, swapJewel);
                        new StateTask().execute("this");
                    }

                    board.clearHighlights();
                    invalidate();
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                    board = new JewelBoard();
                    invalidate();
                    break;


            }
            return true;
        }



        protected void onDraw(Canvas canvas){
            Jewel jewel;

            board.setBoardWidth(this.getWidth());
            if(!board.isInitialized())
                board.initBoard();
            //invalidate();
            for(int i=0; i < board.getRows(); i++)
                for(int j=0; j < board.getCols(); j++) {
                    jewel = board.getJewel(i, j);
                    if(jewel != null) {
                        switch (jewel.getType()) {
                            case Circle:
                                circle.setBounds(jewel.getBounds());
                                circle.draw(canvas);
                                break;
                            case Square:
                                square.setBounds(jewel.getBounds());
                                square.draw(canvas);
                                break;
                            case Diamond:
                                diamond.setBounds(jewel.getBounds());
                                diamond.draw(canvas);
                                break;
                            case Hourglass:
                                hourglass.setBounds(jewel.getBounds());
                                hourglass.draw(canvas);
                                break;
                        }
                    }

                }

            if(board.hasMatches() || board.hasHighlights()) {

                for (int i = 0; i < board.getRows(); i++)
                    for (int j = 0; j < board.getCols(); j++) {
                        jewel = board.getJewel(i, j);
                        if(jewel != null) {
                            if (jewel.isMatch() || jewel.isHighlighted()) {
                                highlightSquare.setBounds(jewel.getBounds());
                                highlightSquare.draw(canvas);
                            }
                        }
                    }
            }
            paint.setTextSize(100);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Score: " + Integer.toString(board.getScore()),board.getBoardWidth()/2, board.getJewelWidth() *11, paint);

        }




    }






}
