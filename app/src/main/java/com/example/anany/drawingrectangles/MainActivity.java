package com.example.anany.drawingrectangles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button button, btnUndo;
    RelativeLayout rlDvHolder;
    DrawingView dv;
    TextView display;
    TextView real;
    Button polygon;
    SeekBar radius;
    RelativeLayout background;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        display = findViewById(R.id.textView);
        real = (TextView) findViewById(R.id.real);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        dv = new DrawingView(getApplicationContext(), display, height, width);

        setContentView(R.layout.activity_main);
        rlDvHolder = findViewById(R.id.dvHolder);
        rlDvHolder.addView(dv);
        polygon = findViewById(R.id.regular);

        button = findViewById(R.id.button);
        background = findViewById(R.id.layout);
        radius = findViewById(R.id.seekBar);

        btnUndo = findViewById(R.id.btnUndo);
        setButtonClick();
        setRadiusBar();

        //radius.setVisibility(View.VISIBLE);
        //real.setVisibility(View.VISIBLE);
        radius.getProgressDrawable().setColorFilter(Color.parseColor("#70c48c"), PorterDuff.Mode.SRC_IN);
        radius.getThumb().setColorFilter(Color.parseColor("#3abd66"), PorterDuff.Mode.SRC_IN);
        //polygon.setVisibility(View.INVISIBLE);
    }

    private void setRadiusBar() {
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //INFO this is when the user lets go of the slider
                //makeToast("Invalidating");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                dv.radius = progress;
                dv.invalidate();
            }
        });
    }

    private void setButtonClick() {
        polygon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Deal with making a polygon.
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeToast("MODE: " + dv.currentMode);
                switch (dv.currentMode) {
                    case drawc:
                        dv.currentMode = DrawingView.Mode.resetc;
                        dv.invalidate();
                        break;
                    case DOTPLOT:
                        dv.currentMode = DrawingView.Mode.RESET;
                        dv.invalidate();
                        break;
                    case PLOT:
                        //Current mode is drawing.
                        dv.currentMode = DrawingView.Mode.RESET;
                        dv.resetTouchPoints();
                        //dv.invalidate();
                        break;
                    case DRAWING:
                        dv.resetTouchPoints();
                        //dv.currentMode = DrawingView.Mode.RECORDING;
                        //dv.currentMode = DrawingView.Mode.DOTPLOT;
                        dv.invalidate();
                        button.setText("Draw Line");
                        break;
                    case RECORDING:
                        dv.currentMode = DrawingView.Mode.DRAWING;
                        dv.invalidate();
                        button.setText("Reset");
                        break;
                }

            }
        });

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dv.undo();
            }
        });
    }

    public static class DrawingView extends View {
        private static final float TOUCH_TOLERANCE = 4;
        public int width;
        public int height;
        public int radius = -5;
        List<Integer> xlist = new ArrayList<>();
        List<Integer> ylist = new ArrayList<>();
        List<Integer> xs = new ArrayList<>();
        List<Integer> ys = new ArrayList<>();
        //TODO Comment out below if you want to use a dotplot
        //  Mode currentMode = Mode.PLOT;
        Mode currentMode = Mode.DOTPLOT;
        Context context;
        Paint linePaint;
        Canvas cd;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        private Paint circlePaint;
        TextView display;
        private Path circlePath;
        private float mX, mY;
        int screenW, screenH;

        public DrawingView(Context c, TextView display, int height, int width) {
            super(c);
            context = c;
            mPath = new Path();
            cd = new Canvas();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.RED);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(8f);

            screenW = width;
            screenH = height;

            this.display = display;
            makeToast("HI");

            linePaint = new Paint();
            linePaint.setAntiAlias(true);
            linePaint.setColor(Color.RED);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setStrokeJoin(Paint.Join.MITER);
            linePaint.setStrokeWidth(8f);

        }

        public void resetTouchPoints() {
            xlist = new ArrayList<>();
            ylist = new ArrayList<>();
            xs = new ArrayList<>();
            ys = new ArrayList<>();
            Log.wtf("RESET RESET", "Touch points were reset");
            invalidate();
        }

        public void undo() {
            if (xlist.size() > 0) {
                Log.d("Removal,", "before size is" + xlist.size());
                xlist.remove((int) xlist.size() - 1);
                ylist.remove((int) ylist.size() - 1);
                Log.d("Removal,", "Now size is" + xlist.size());
                invalidate();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            /*Path path = new Path();
            boolean first = true;
            for(Point point : points){
                if(first){
                    first = false;
                    path.moveTo(point.x, point.y);
                }
                else{
                    path.lineTo(point.x, point.y);
                }
            }
            canvas.drawPath(path, paint);*/
            mCanvas = canvas;

            //README I think when you call invalidate() it calls onDraw again. Check it out by putting
            // your custom draw function for the line here. Test it out.
            //drawCustomLine(mCanvas, downx, downy, upx, upy);

            switch (currentMode) {
                case DOTPLOT:
                    showDots(canvas);
                    Log.wtf("DOTPLOT", "DOT Plot being called");
                    drawLine(canvas);
                    break;
                case RESET:
                    //Need to reset the canvas
                    currentMode = Mode.DOTPLOT;
                    resetTouchPoints();
                    makeToast("Resetting");
                    //TODO Try uncommenting and commenting out below line to see if it works after reset button
                    //linePaint.setColor(Color.WHITE);
                    Log.wtf("RESET RESET RESET RESET RESET", "Reset was called. Reset screen.");
                    break;
                case PLOT:
                    drawCustomLine(mCanvas, downx, downy, upx, upy);
                    break;
                case resetc:
                    currentMode = Mode.drawc;
                    makeToast("Reset c. Mode is draw c");
                    break;
                case drawc:
                    //makeToast("In draw c");
                    if (radius == -5)
                        mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30, 300, linePaint);
                    else
                        mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30, radius * (screenW / 200), linePaint);

                    break;
                case DRAWING:
                    //showDots(canvas);
                    //drawLine(canvas);
                    break;
                case RECORDING:
                    //showDots(canvas);
                    break;

            }
            super.onDraw(canvas);
        }

        boolean down = true;
        boolean up = true;
        int downx, downy;
        int upx, upy;

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();
            //makeToast("TAPPED");
            //INFO Uncomment below if you are trying to do drawCustomLine()
            /*switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    makeToast("Finger Down: " + x + " " + y);
                    if (down) {
                        downx = x;
                        downy = y;
                        xlist.add(x);
                        ylist.add(y);
                        down = false;
                        //Log.wtf("Coordinates---------------------------", "Finger Down: " + x + " " + y);
                    }
                case MotionEvent.ACTION_MOVE:
                    up = true;
                case MotionEvent.ACTION_UP:
                    makeToast("Finger Up: " + x + " " + y);
                    if (up) {
                        upx = x;
                        upy = y;
                        xlist.add(upx);
                        ylist.add(upy);
                        up = false;
                       // Log.wtf("Coordinates---------------------------", "Finger Up: " + x + " " + y);
                        down = true;
                        invalidate();
                    }
            }*/

            if (currentMode == Mode.DOTPLOT) {
                /*float x = event.getX();
                float y = event.getY();
                Log.d("Touch Point", "Co x:" + x + ", y:" + y);*/
                //TODO Write function to iterate through both lists and check a point's distance. Dont' just do it for the lastest one.
                boolean duplicate = checkForDuplicate(x, y);
                if (/*xlist.size() > 0 && xlist.get((int) xlist.size() - 1) < (int) x + 30 && xlist.get((int) xlist.size() - 1) > (int) x - 30
                        && ylist.get((int) ylist.size() - 1) < (int) y + 30 && ylist.get((int) ylist.size() - 1) > (int) y - 30
                */duplicate) {
                    //duplicate touch recording, skip it
                    Log.d("Duplicate", "Avoiding it");
                } else {
                    if (xlist.size() <= 12) {
                        xlist.add((int) x);
                        ylist.add((int) y);
                        //INFO If you comment out below, then it does not draw the dots. Only when you press a button it draws.
                        // I think this is because when button pressed, it calls invalidate(). invalidate() leads to onDraw()
                        // That's why try writing your customDrawLine() code in onDraw().
                        invalidate();
                    } else {
                        makeToast("You have exceeded the limit on the number of sides.");
                    }
                }
            } else if (currentMode == Mode.drawc) {
                //TODO Deal with drawing circle.

            }
            return true;
        }

        protected boolean checkForDuplicate(float x, float y) {
            double distance = 0;
            for (int i = 0; i < xlist.size(); i++) {
                int xTemp = xlist.get(i);
                int yTemp = ylist.get(i);
                distance = Math.sqrt(Math.pow(xTemp - x, 2) + Math.pow(yTemp - y, 2));
                if (distance < 120)
                    return true;
            }
            return false;
        }

        private void drawLine(Canvas canvas) {
            if (xlist.size() > 1) {
                for (int i = 0; i < xlist.size() - 1; i++) {
                    canvas.drawLine(xlist.get(i), ylist.get(i), xlist.get(i + 1), ylist.get(i + 1), linePaint);
                }

                //display = findViewById(R.id.textView);
                canvas.drawLine(xlist.get(xlist.size() - 1), ylist.get(ylist.size() - 1), xlist.get(0), ylist.get(0), linePaint);
                int a = xlist.get(0);
                int b = ylist.get(0);
                int x = xlist.get(1);
                int y = ylist.get(1);
                int c = a - x;
                int d = b - y;
                c = c * c;
                d = d * d;
                int z = c - d;
                double df = Math.sqrt(z);
                String s = Double.toString(df);
                //makeToast("Display is good: " + (display == null));
                //display.setText(s);
            }
        }

        private void drawCustomLine(Canvas canvas, int x1, int y1, int x2, int y2) {
            //try calling invalidate()
            //makeToast("Being called");
            Log.wtf("Draw Custom Line being called", "Draw Custom Line being called");
            linePaint.setColor(Color.BLUE);
            //canvas.drawLine(x1, y1, x2, y2, linePaint);
            xs.add(x1);
            xs.add(x2);
            ys.add(y1);
            ys.add(y2);
            for (int i = 0; i < xs.size(); i += 2)
                canvas.drawLine(xs.get(i), ys.get(i), xs.get(i + 1), ys.get(i + 1), linePaint);
            linePaint.setColor(Color.GREEN);

            //invalidate();
        }

        private void showDots(Canvas canvas) {
            Log.wtf("Showing dots", "X List size: " + xlist.size());
            if (xlist.size() > 0) {
                for (int i = 0; i < xlist.size(); i++) {
                    canvas.drawCircle(xlist.get(i), ylist.get(i), 9.0f, circlePaint);
                }
            }

        }

        private void makeToast(String s) {
            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        }


        enum Mode {RECORDING, DRAWING, PLOT, SPRINKLER, RESET, DOTPLOT, drawc, resetc}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.circle:
                //TODO Have to draw circle.
                if (dv.currentMode == DrawingView.Mode.drawc || dv.currentMode == DrawingView.Mode.resetc) {
                    //INFO Need to switch from circle to polygon
                    radius.setVisibility(View.INVISIBLE);
                    polygon.setVisibility(View.VISIBLE);
                    item.setTitle("Draw Shapes");
                    dv.currentMode = DrawingView.Mode.DOTPLOT;
                    dv.resetTouchPoints();
                    dv.invalidate();
                } else {
                    //INFO Currently on polygon. Switch to circle.
                    dv.currentMode = DrawingView.Mode.drawc;
                    item.setTitle("Draw again");
                    polygon.setVisibility(View.INVISIBLE);
                    radius.setVisibility(View.VISIBLE);
                    dv.invalidate();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}