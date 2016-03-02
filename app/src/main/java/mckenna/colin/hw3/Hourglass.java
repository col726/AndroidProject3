package mckenna.colin.hw3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.Shape;

/**
 * Created by cmckenna on 10/22/2015.
 */
public class Hourglass extends Shape {

    private Context context;
    private int strokeWidth;

    public Hourglass(Context context){
        this.context = context;
        strokeWidth = (int)context.getResources().getDimension(R.dimen.star_stroke);
    }

    @Override
    public void draw(Canvas canvas, Paint paint){
        float width = getWidth();
        float height = getHeight();
        Path path = new Path();
        //create path
        path.moveTo(width / 2, height / 2);
        path.lineTo(0 , height);
        path.lineTo(width, height);
        //path.lineTo(width / 2, height / 2);
        path.lineTo(0, 0);
        path.lineTo(width, 0);
        path.close();
        //fill
        paint.setColor(context.getResources().getColor(R.color.red));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
       //stroke
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawPath(path, paint);
    }

}
