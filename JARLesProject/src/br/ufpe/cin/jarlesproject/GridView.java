package br.ufpe.cin.jarlesproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class GridView extends View {
    
    private static final int CELL_SIZE = 8;
    private static final int WIDTH = 320 / CELL_SIZE;
    private static final int HEIGHT = 480 / CELL_SIZE;
    
    public GridView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        
    	/*
    	Paint background = new Paint();
        background.setColor(111);
        
        Paint cell = new Paint();
        cell.setColor(0);
        
        
        // draw background
        canvas.drawRect(0, 0, getWidth(), getHeight(), background);
        
        // draw cells
        for (int h = 0; h < HEIGHT; h++) {
            for (int w = 0; w < WIDTH; w++) {
                if (GridActivity.getGridArray()[h][w] != 0) {
                    canvas.drawRect(
                        w * CELL_SIZE, 
                        h * CELL_SIZE, 
                        (w * CELL_SIZE) +CELL_SIZE,
                        (h * CELL_SIZE) +CELL_SIZE,
                        cell);
                }
            }
        }
        */
    }
}