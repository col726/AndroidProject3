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
public class Diamond extends Shape {

    private Context context;
    private int strokeWidth;

    public Diamond(Context context){
        this.context = context;
        strokeWidth = (int)context.getResources().getDimension(R.dimen.diamond_stroke);
    }

    @Override
    public void draw(Canvas canvas, Paint paint){
        float width = getWidth();
        float height = getHeight();
        Path path = new Path();
        //create path
        path.moveTo(0,height/2);
        path.lineTo(width / 2, height);
        path.lineTo(width ,height/2);
        path.lineTo(width / 2 , 0);
        path.close();
        //fill
        paint.setColor(context.getResources().getColor(R.color.green));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
       //stroke
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawPath(path, paint);
    }

}
