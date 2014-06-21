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
        
    	
    }
}