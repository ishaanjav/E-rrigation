package com.example.anany.drawingrectangles;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CleanMainActivity extends AppCompatActivity {

    Button button, btnUndo;
    static RelativeLayout rlDvHolder;
    static DrawingView dv;
    TextView display;
    TextView real;
    Button polygon;
    static SeekBar radius;
    static RelativeLayout background;
    public static Context context;
    public EditText length;
    static RelativeLayout container;
    public TextView textview, ft;
    TextView angleText, rotateText;
    ImageView left1, left2, right1, right2;

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
        textview = background.findViewById(R.id.textView);
        ft = background.findViewById(R.id.ft);

        length = background.findViewById(R.id.length);

        angleText = background.findViewById(R.id.adjustAngleText);
        rotateText = background.findViewById(R.id.rotateSprinklerText);
        left1 = background.findViewById(R.id.leftAngle1);
        left2 = background.findViewById(R.id.leftAngle2);
        right1 = background.findViewById(R.id.rightAngle1);
        right2 = background.findViewById(R.id.rightAngle2);

        btnUndo = findViewById(R.id.btnUndo);
        setButtonClick();
        setRadiusBar();
        dv.sradius = radius.getProgress();
        container = background.findViewById(R.id.container);
        lengthUpdater();

        radius.getProgressDrawable().setColorFilter(Color.parseColor("#70c48c"), PorterDuff.Mode.SRC_IN);
        radius.getThumb().setColorFilter(Color.parseColor("#3abd66"), PorterDuff.Mode.SRC_IN);
        context = getApplicationContext();

        angleAndRotateClicks();
    }

    public boolean leaveAlone = false;
    boolean done = false;
    int timesR = 0;

    public void lengthUpdater() {
        length.addTextChangedListener(new TextWatcher() {
            String previous = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (dv.xlist.size() > 2)
                    length.setEnabled(false);
                else
                    length.setEnabled(true);
                previous = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String r = length.getText().toString();
                if (dv.currentMode != DrawingView.Mode.drawc && dv.currentMode != DrawingView.Mode.resetc) {
                    if (r == null) {
                        timesR = 0;
                    } else if (previous.length() > 0 && dv.xlist.size() > 2 && !leaveAlone) {
                        makeToast("To change the length, please reset the plot");
                    } else if (dv.xlist.size() > 1) {
                        timesR = 0;
                        String temp = length.getText().toString();
                        if (temp == null) {
                            makeToast("Please enter the side length.");
                        } else if (temp.isEmpty() || temp.length() == 0) {
                            makeToast("You must enter the side length in feet");
                        } else {
                            int numVal = Integer.parseInt(temp);
                            if (numVal < 2) {
                                makeToast("Please make your side length larger");
                            } else {
                                //TODO Have to also set the value of dv.ratio to get
                                // the ratio between their length and pixel length;
                                dv.length = numVal;
                                if (dv.pastMode == DrawingView.PastMode.CIRCLE) {
                                    //dv.ratio = dv.radius / numVal;
                                    dv.ratio = dv.radius / (dv.radius * (dv.screenW / 200));
                                } else {
                                    dv.ratio = (double) numVal / (double) (Math.hypot(Math.abs(dv.xlist.get(0) - dv.xlist.get(1)),
                                            Math.abs(dv.ylist.get(0) - dv.ylist.get(1))));
                                }
                            }
                        }
                    } else if (length.getText().toString().equals("") || length.getText().toString().equals(previous)) {

                    } else {
                        length.setText("");
                        //leaveAlone = true;
                        makeToast("First, you must plot the first side.");
                    }
                } else {
                    timesR = 0;
                    String temp = length.getText().toString();
                    if (temp == null) {
                        makeToast("Please enter the radius in feet.");
                    } else if (temp.isEmpty() || temp.length() == 0) {
                        makeToast("You must enter the radius in feet");
                    } else {
                        int numVal = Integer.parseInt(temp);
                        if (numVal < 2) {
                            makeToast("Please make your radius larger");
                        } else {
                            dv.length = numVal;
                            dv.ratio = dv.radius / numVal;
                        }
                    }
                }
                leaveAlone = false;
            }
        });
    }

    private void createAllTextViews() {
        dv.idCounter++;
        maxSideLength = 0;
        for (int i = 0; i < dv.xlist.size() - 1; i++) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                    ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = (dv.xlist.get(i) + dv.xlist.get(i + 1)) / 2;
            params.topMargin = (dv.ylist.get(i) + dv.ylist.get(i + 1)) / 2;

            TextView textView = new TextView(context);
            int val = (int) (Math.hypot(Math.abs(dv.xlist.get(i) - dv.xlist.get(i + 1)),
                    Math.abs(dv.ylist.get(i) - dv.ylist.get(i + 1))) * saveRadius);
            textView.setText("" + val);
            textView.setId(dv.idCounter);
            textView.setLayoutParams(params);
            rlDvHolder.addView(textView);
            dv.idCounter++;
        }

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams
                ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
        int xlast = dv.xlist.get(dv.xlist.size() - 1) + dv.xlist.get(0);
        int ylast = dv.ylist.get(dv.ylist.size() - 1) + dv.ylist.get(0);
        params2.leftMargin = (xlast) / 2;
        params2.topMargin = (ylast) / 2;

        TextView last = new TextView(context);
        last.setText("" + (int) (Math.hypot(Math.abs(dv.xlist.get(dv.xlist.size() - 1) - dv.xlist.get(0)),
                Math.abs(dv.ylist.get(dv.ylist.size() - 1) - dv.ylist.get(0))) * saveRadius));
        last.setId(dv.specialCounter);
        last.setLayoutParams(params2);
        //makeToast("Making the last text");
        rlDvHolder.addView(last);

        //dv.posCount++;
        dv.specialCounter--;
    }

    public boolean handleSideLength() {
        String temp = length.getText().toString();
        if (temp == null) {
            makeToast("Please enter the side length/radius.");
            return false;
        } else if (temp.isEmpty() || temp.length() == 0) {
            makeToast("You must enter the side length/radius in feet");
            return false;
        } else {
            int numVal = Integer.parseInt(temp);
            if (numVal < 2) {
                makeToast("Please make your side length larger");
                return false;
            } else {
                //TODO Have to also set the value of dv.ratio to get
                // the ratio between their length and pixel length;
                dv.length = numVal;
                if (dv.currentMode == DrawingView.Mode.drawc || dv.currentMode == DrawingView.Mode.resetc) {
                    dv.ratio = dv.radius / numVal;
                } else {
                    dv.ratio = (double) numVal / (double) (Math.hypot(Math.abs(dv.xlist.get(0) - dv.xlist.get(1)),
                            Math.abs(dv.ylist.get(0) - dv.ylist.get(1))));
                }
                return true;
            }
        }

    }

    AlertDialog.Builder loading;
    AlertDialog created;

    public void showLoading() {
        loading = new AlertDialog.Builder(CleanMainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogCoordinate = inflater.inflate(R.layout.loading, null);
        loading.setCancelable(false);
        loading.setView(dialogCoordinate);

        created = loading.create();
        created.show();
    }

    public void hideLoading() {
        created.hide();
        created.cancel();
    }

    //README this function asks user for length of side after user has plotted 1 side.
    private void askForLength(boolean plot, int a) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CleanMainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogCoordinate = inflater.inflate(R.layout.specify_length, null);
        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    int messageCount = 0;

    private void setRadiusBar() {
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //INFO this is when the user lets go of the slider
                int max = seekBar.getMax();
                int min = seekBar.getMin();
                double sprinklerRadius = seekBar.getProgress() * (dv.screenW / 200);
                float bigCircleRadius = dv.setCircleRadiusTo;
                float bigCircleFeet = dv.length;
                int progress = seekBar.getProgress();
                progress -= 50;
                progress *= 2;
                dv.sradius = progress;

                double pixelSideLength = (double) maxSideLength / dv.ratio * 0.7d;
                double setToI = ((double) ((double) dv.screenW / 1000) * Math.pow(seekBar.getProgress() / 5.9f, 2)) - 50;
                setToI = ((double) progress / 100d * pixelSideLength) / 2;
                double minSideLength = 4d / dv.ratio;

                if (maxSideLength > 35) {
                    pixelSideLength = (double) 25d / dv.ratio;
                    minSideLength = 4d / dv.ratio;
                }
                if (maxSideLength > 50) {
                    pixelSideLength = (double) 35d / dv.ratio;
                    minSideLength = 4d / dv.ratio;
                }
                if (maxSideLength > 74) {
                    pixelSideLength = (double) 40d / dv.ratio;
                    minSideLength = 5d / dv.ratio;
                }
                if (maxSideLength > 90) {
                    pixelSideLength = (double) 50d / dv.ratio;
                    minSideLength = 6d / dv.ratio;
                }
                if (maxSideLength > 150) {
                    pixelSideLength = (double) 80d / dv.ratio;
                    minSideLength = 8d / dv.ratio;
                }
                if (maxSideLength > 325) {
                    pixelSideLength = (double) 250d / dv.ratio;
                    minSideLength = 10d / dv.ratio;
                }

                double dif = pixelSideLength - minSideLength;
                setToI = pixelSideLength - ((double) 100 - progress) / 100 * dif;

                if (dv.currentMode != DrawingView.Mode.drawc) {
                    makeToast("Radius: " + (String.format("%1$,.1f", (setToI * ((double) dv.ratio)))) + " feet.");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                //dv.sradius = progress;
                if (dv.sprinkr.size() > 0) {
                    //dv.sprinkr.set(dv.sprinkr.size() - 1, (int) ((double) ((double) dv.screenW / 1000) * Math.pow(progress / 5.9f, 2)) - 50);
                    double pixelSideLength = (double) maxSideLength / dv.ratio * 0.7d;
                    progress -= 50;
                    progress *= 2;
                    dv.sradius = progress;
                    double setToI = ((double) ((double) dv.screenW / 1000) * Math.pow(seekBar.getProgress() / 5.9f, 2)) - 50;
                    setToI = ((double) progress / 100d * pixelSideLength) / 2;
                    double minSideLength = 4d / dv.ratio;
                    if (maxSideLength > 35) {
                        pixelSideLength = (double) 25d / dv.ratio;
                        minSideLength = 4d / dv.ratio;
                    }
                    if (maxSideLength > 50) {
                        pixelSideLength = (double) 35d / dv.ratio;
                        minSideLength = 4d / dv.ratio;
                    }
                    if (maxSideLength > 74) {
                        pixelSideLength = (double) 40d / dv.ratio;
                        minSideLength = 5d / dv.ratio;
                    }
                    if (maxSideLength > 90) {
                        pixelSideLength = (double) 50d / dv.ratio;
                        minSideLength = 6d / dv.ratio;
                    }
                    if (maxSideLength > 150) {
                        pixelSideLength = (double) 80d / dv.ratio;
                        minSideLength = 8d / dv.ratio;
                    }
                    if (maxSideLength > 325) {
                        pixelSideLength = (double) 250d / dv.ratio;
                        minSideLength = 10d / dv.ratio;
                    }
                    double dif = pixelSideLength - minSideLength;
                    setToI = pixelSideLength - ((double) 100 - progress) / 100 * dif;
                    dv.sprinkr.set(dv.sprinkr.size() - 1, (int) setToI);
                    //dv.sprinkr.set(dv.sprinkr.size() - 1, (int) (((double) progress / 100d * pixelSideLength) / 2));
                }


                if (dv.currentMode == DrawingView.Mode.drawc) {
                    messageCount++;
                    if (messageCount % 15 == 0)
                        makeToast("ATTENTION!\nEnter the length of the circle's radius in feet.");
                    dv.radius = progress;
                    //dv.ratio =
                }

                dv.invalidate();
            }
        });
    }

    double saveRadius = 0;
    static int maxSideLength = 0;

    private void setButtonClick() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //makeToast("MODE: " + dv.currentMode);
                switch (dv.currentMode) {
                    case splot:
                        //TODO Reset the sprinkler points.
                        dv.currentMode = DrawingView.Mode.sreset;
                        dv.invalidate();
                        break;
                    case drawc:
                        dv.currentMode = DrawingView.Mode.resetc;
                        removeAllLengths();
                        dv.pastMode = DrawingView.PastMode.CIRCLE;
                        dv.invalidate();
                        break;
                    case DOTPLOT:
                        dv.currentMode = DrawingView.Mode.RESET;
                        dv.pastMode = DrawingView.PastMode.DRAW;
                        removeAllLengths();
                        dv.invalidate();
                        break;
                    case PLOT:
                        //Current mode is drawing.
                        dv.pastMode = DrawingView.PastMode.DRAW;
                        dv.currentMode = DrawingView.Mode.RESET;
                        removeAllLengths();
                        dv.resetTouchPoints();
                        //dv.invalidate();
                        break;
                    case DRAWING:
                        dv.resetTouchPoints();
                        //dv.currentMode = DrawingView.Mode.RECORDING;
                        //dv.currentMode = DrawingView.Mode.DOTPLOT;
                        dv.pastMode = DrawingView.PastMode.DRAW;
                        removeAllLengths();
                        dv.invalidate();
                        button.setText("Draw Line");
                        break;
                    case RECORDING:
                        dv.currentMode = DrawingView.Mode.DRAWING;
                        removeAllLengths();
                        dv.pastMode = DrawingView.PastMode.DRAW;
                        dv.invalidate();
                        button.setText("Reset");
                        break;
                }
            }
        });

        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dv.currentMode != DrawingView.Mode.splot) {
                    dv.undo();

                    if (dv.xlist.size() == 0) {
                        removeAllLengths();
                    }
                    if (dv.xlist.size() < 3)
                        length.setEnabled(true);

                    removeAllLengths(true);
                } else {
                    if (dv.angleList.size() > 0) {
                        dv.angleList.remove(dv.angleList.size() - 1);
                        dv.rotationList.remove(dv.rotationList.size() - 1);
                    }
                    dv.removeLastSprinkler();
                }

            }
        });
    }

    public void removeAllLengths() {
        for (int i = 0; i < dv.idCounter; i++)
            rlDvHolder.findViewById(i).setVisibility(View.GONE);
        for (int i = Integer.MAX_VALUE; i > dv.specialCounter; i--)
            rlDvHolder.findViewById(i).setVisibility(View.GONE);
        saveRadius = dv.ratio;
        dv.ratio = 1;

        dv.length = 0;
        leaveAlone = true;
        length.setText("");
        length.setEnabled(true);
    }

    public void removeAllLengths(boolean t) {
        for (int i = 0; i < dv.idCounter; i++)
            rlDvHolder.findViewById(i).setVisibility(View.GONE);
        for (int i = Integer.MAX_VALUE; i > dv.specialCounter; i--)
            rlDvHolder.findViewById(i).setVisibility(View.GONE);
    }


    public void angleAndRotateClicks() {
        left1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dv.angle = Math.max((dv.angle - 30) % 360, 0);
                if (dv.angle == 0)
                    dv.angle = 360;


                double rotateDisplay = dv.rotate;
                if (rotateDisplay > 180)
                    rotateDisplay -= 360;
                //makeToast("Angle: " + dv.angle + "  Rotate: " + rotateDisplay);

                if (dv.sprinkx.size() > 0) {
                    dv.rotationList.set(dv.sprinkx.size() - 1, dv.rotate);
                    dv.angleList.set(dv.sprinkx.size() - 1, dv.angle);
                    dv.invalidate();
                    //dv.plotSprinklers2(dv.mCanvas);
                }
                return false;
            }
        });
        right1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dv.angle = Math.max((dv.angle + 30) % 360, 0);
                if (dv.angle == 0)
                    dv.angle = 360;

                double rotateDisplay = dv.rotate;
                if (rotateDisplay > 180)
                    rotateDisplay -= 360;
                makeToast("Angle: " + dv.angle + "  Rotate: " + rotateDisplay);

                if (dv.sprinkx.size() > 0) {
                    dv.rotationList.set(dv.sprinkx.size() - 1, dv.rotate);
                    dv.angleList.set(dv.sprinkx.size() - 1, dv.angle);
                    dv.invalidate();
                    //dv.plotSprinklers2(dv.mCanvas);
                }
                return false;
            }
        });
        left2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (dv.angle != 360)
                    dv.rotate = (dv.rotate - 15) % 360;
                else
                    makeToast("Change the sprinkler angle before rotating it.");

                if (dv.rotate < 0)
                    dv.rotate += 360;

                double rotateDisplay = dv.rotate;
                if (rotateDisplay > 180)
                    rotateDisplay -= 360;
                makeToast("Angle: " + dv.angle + "  Rotate: " + rotateDisplay);

                if (dv.sprinkx.size() > 0) {
                    dv.rotationList.set(dv.sprinkx.size() - 1, dv.rotate);
                    dv.angleList.set(dv.sprinkx.size() - 1, dv.angle);
                    dv.invalidate();
                    //dv.plotSprinklers2(dv.mCanvas);
                }
                return false;
            }
        });
        right2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (dv.angle != 360)
                    dv.rotate = (dv.rotate + 15) % 360;
                else
                    makeToast("Change the sprinkler angle before rotating it.");

                double rotateDisplay = dv.rotate;
                if (rotateDisplay > 180)
                    rotateDisplay -= 360;
                makeToast("Angle: " + dv.angle + "  Rotate: " + rotateDisplay);

                if (dv.sprinkx.size() > 0) {
                    dv.rotationList.set(dv.sprinkx.size() - 1, dv.rotate);
                    dv.angleList.set(dv.sprinkx.size() - 1, dv.angle);
                    dv.invalidate();
                }
                return false;
            }
        });
    }

    public static class DrawingView extends View {
        private static final float TOUCH_TOLERANCE = 4;
        public int width;
        public int height;
        public int radius = -5;
        public static int sradius = -5;
        List<Integer> xlist = new ArrayList<>();
        List<Integer> ylist = new ArrayList<>();
        List<Integer> sprinkx = new ArrayList<>();
        List<Integer> sprinky = new ArrayList<>();
        List<Integer> sprinkr = new ArrayList<>();
        List<Boolean> changeAngle = new ArrayList<>();
        List<Boolean> changeRotation = new ArrayList<>();
        List<Integer> angleList = new ArrayList<>();
        List<Integer> rotationList = new ArrayList<>();
        List<Integer> xs = new ArrayList<>();
        List<Integer> ys = new ArrayList<>();
        //TODO Comment out below if you want to use a dotplot
        //  Mode currentMode = Mode.PLOT;
        Mode currentMode = Mode.DOTPLOT;
        PastMode pastMode = PastMode.DRAW;
        Context context;
        Paint linePaint;
        Canvas cd;
        public int attentionCounter = -1;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        private Paint circlePaint;
        private Paint sprinklerC;
        private Paint sprinklerSurround;
        private Paint sprinklerBorder;
        private Paint fillPaint;
        TextView display;
        private Path circlePath;
        private float mX, mY;
        public boolean wasCircle = false;
        int screenW, screenH;
        public int length = 0;
        public double ratio = 1;
        public float setCircleRadiusTo = 1;
        public int angle = 360;
        public int rotate = 0;

        public DrawingView(Context c, TextView display, int height, int width) {
            super(c);
            context = c;
            mPath = new Path();
            cd = new Canvas();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLACK);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(8f);

            screenW = width;
            screenH = height;

            this.display = display;
            //makeToast("HI");

            fillPaint = new Paint();
            fillPaint.setAntiAlias(true);
            fillPaint.setColor(0X1A187507);
            fillPaint.setStyle(Paint.Style.FILL);
            fillPaint.setStrokeJoin(Paint.Join.MITER);

            sprinklerSurround = new Paint();
            sprinklerSurround.setAntiAlias(true);
            sprinklerSurround.setColor(0XAA76f760);
            sprinklerSurround.setStyle(Paint.Style.FILL);
            sprinklerSurround.setStrokeJoin(Paint.Join.MITER);

            sprinklerC = new Paint();
            sprinklerC.setAntiAlias(true);
            sprinklerC.setColor(0X7557c4ff);
            sprinklerC.setStyle(Paint.Style.FILL);
            sprinklerC.setStrokeJoin(Paint.Join.MITER);

            linePaint = new Paint();
            linePaint.setStrokeWidth(8f);
            linePaint.setColor(Color.parseColor("#369646"));
            linePaint.setStrokeWidth(8f);
            linePaint.setStyle(Paint.Style.STROKE);
        }

        public void resetSprinklers() {
            sprinkx = new ArrayList<>();
            sprinky = new ArrayList<>();
            sprinkr = new ArrayList<>();
            rotationList = new ArrayList<>();
            angleList = new ArrayList<>();
            invalidate();
        }

        public void resetTouchPoints() {
            xlist = new ArrayList<>();
            ylist = new ArrayList<>();
            xs = new ArrayList<>();
            ys = new ArrayList<>();
            invalidate();
        }

        public void removeLastSprinkler() {
            if (sprinkx.size() > 0) {
                sprinkx.remove(sprinkx.size() - 1);
                sprinkr.remove(sprinkr.size() - 1);
                sprinky.remove(sprinky.size() - 1);
            }
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

            //INFO I think when you call invalidate() it calls onDraw again. Check it out by putting
            // your custom draw function for the line here. Test it out.
            //drawCustomLine(mCanvas, downx, downy, upx, upy);
            switch (currentMode) {
                case splot:
                    plotSprinklers2(canvas);
                    if (!wasCircle)
                        drawLine(canvas);
                    else {
                        if (radius == -5) {
                            mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                    300, fillPaint);
                            mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                    308, linePaint);
                        } else {
                            mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                    radius * (screenW / 200), fillPaint);
                            mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                    radius * (screenW / 200) + 8, linePaint);
                        }
                    }
                    /*angle = 360;
                    rotate = 0;*/

                    break;
                case sreset:
                    currentMode = Mode.splot;
                    resetSprinklers();
                    //makeToast("Resetting sprinklers.");
                    break;
                case DOTPLOT:
                    showDots(canvas);
                    drawLine(canvas);
                    if (xlist.size() == 2) {
                        //TODO Make an ALert Dialog asking for the length of the side that has been drawn.
                        //NOTES Make it non-dimissible, but provide an Undo and a done button. for undo,
                        //  call dv.undo();
                        attentionCounter++;
                        if (attentionCounter % 5 == 0)
                            makeToast("ATTENTION!\nEnter the length of this first side in feet.");
                        //MainActivity.askForLength(true);
                    }
                    break;
                case RESET:
                    //Need to reset the canvas
                    currentMode = Mode.DOTPLOT;
                    resetTouchPoints();
                    resetSprinklers();
                    //makeToast("Resetting");
                    //TODO Try uncommenting and commenting out below line to see if it works after reset button
                    //linePaint.setColor(Color.WHITE);
                    break;
                case PLOT:
                    drawCustomLine(mCanvas, downx, downy, upx, upy);
                    break;
                case resetc:
                    currentMode = Mode.drawc;
                    //makeToast("Reset c. Mode is draw c");
                    break;
                case drawc:
                    //makeToast("In draw c");
                    //README This is for drawing the circle region.
                    //MainActivity.askForLength(false);
                    wasCircle = true;
                    if (radius == -5) {
                        setCircleRadiusTo = 300;
                        mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                setCircleRadiusTo, fillPaint);
                        mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                setCircleRadiusTo + 8, linePaint);
                    } else {
                        setCircleRadiusTo = radius * (screenW / 200);
                        mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                setCircleRadiusTo, fillPaint);
                        mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                setCircleRadiusTo + 8, linePaint);
                    }
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

        int exceededCount = 0;

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();
            //makeToast("TAPPED");
            //README Uncomment below if you are trying to do drawCustomLine()
           /*switch (event.getAction()) {
               case MotionEvent.ACTION_DOWN:
                   makeToast("Finger Down: " + x + " " + y);
                   if (down) {
                       downx = x;
                       downy = y;
                       xlist.add(x);
                       ylist.add(y);
                       down = false;
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
                       down = true;
                       invalidate();
                   }
           }*/

            if (currentMode == Mode.DOTPLOT) {
               /*float x = event.getX();
               float y = event.getY();
               Log.d("Touch Point", "Co x:" + x + ", y:" + y);*/
                //TODO Write function to iterate through both lists and check a point's distance. Dont' just do it for the lastest one.
                boolean duplicate = checkForDuplicate(x, y, false);
                if (/*xlist.size() > 0 && xlist.get((int) xlist.size() - 1) < (int) x + 30 && xlist.get((int) xlist.size() - 1) > (int) x - 30
                       && ylist.get((int) ylist.size() - 1) < (int) y + 30 && ylist.get((int) ylist.size() - 1) > (int) y - 30
               */duplicate) {
                    //duplicate touch recording, skip it
                    Log.d("Duplicate", "Avoiding it");
                } else {
                    if (xlist.size() <= 12) {
                        if (xlist.size() > 1 && length == 0) {
                            makeToast("Please specify the length of the first side you drew.");
                        } else {
                            xlist.add((int) x);
                            ylist.add((int) y);
                            String logger = "";
                            if (dv.xlist.size() > 0) {
                                for (int i = 0; i < dv.xlist.size(); i++) {
                                    logger += "\n*- X - " + dv.xlist.get(i) + "  Y - " + dv.ylist.get(i);
                                }
                            }
                            //INFO If you comment out below, then it does not draw the dots. Only when you press a button it draws.
                            // I think this is because when button pressed, it calls invalidate(). invalidate() leads to onDraw()
                            // That's why try writing your customDrawLine() code in onDraw().
                            invalidate();
                        }
                    } else {
                        if (exceededCount % 5 == 0)
                            makeToast("You have exceeded the limit on the number of sides.");
                        exceededCount++;
                    }
                }
            } else if (currentMode == Mode.splot) {
                //TODO Deal with plotting sprinklers.

                boolean duplicate = checkForDuplicate(x, y, true);
                if (!duplicate) {
                    sprinkx.add((int) x);
                    sprinky.add((int) y);
                    rotationList.add(rotate);
                    angleList.add(angle);
                    changeRotation.add(rotate != 0);
                    changeAngle.add(angle != 360);
                    //OLD code below just does it based on number of pixels.
                    //  On higher ppi phones, sprinkle  r appears very small.
                    //INFO For 3 lines, changed sradius/9 to /4.

                    int progress = CleanMainActivity.radius.getProgress();
                    double pixelSideLength = (double) maxSideLength / dv.ratio * 0.7d;
                    double setToI = ((double) progress / 100d * pixelSideLength) / 2;
                    double minSideLength = 4d / dv.ratio;
                    if (maxSideLength > 35) {
                        pixelSideLength = (double) 25d / dv.ratio;
                        minSideLength = 4d / dv.ratio;
                    }
                    if (maxSideLength > 50) {
                        pixelSideLength = (double) 35d / dv.ratio;
                        minSideLength = 4d / dv.ratio;
                    }
                    if (maxSideLength > 74) {
                        pixelSideLength = (double) 40d / dv.ratio;
                        minSideLength = 5d / dv.ratio;
                    }
                    if (maxSideLength > 90) {
                        pixelSideLength = (double) 50d / dv.ratio;
                        minSideLength = 6d / dv.ratio;
                    }
                    if (maxSideLength > 150) {
                        pixelSideLength = (double) 80d / dv.ratio;
                        minSideLength = 8d / dv.ratio;
                    }
                    if (maxSideLength > 325) {
                        pixelSideLength = (double) 250d / dv.ratio;
                        minSideLength = 10d / dv.ratio;
                    }
                    double dif = pixelSideLength - minSideLength;
                    setToI = pixelSideLength - ((double) 100 - sradius) / 100 * dif;



                    sprinkr.add((int) setToI);
                    invalidate();
                }
            }
            return true;
        }

        protected boolean checkForDuplicate(float x, float y, boolean sprinkler) {
            double distance = 0;
            if (!sprinkler) {
                for (int i = 0; i < xlist.size(); i++) {
                    int xTemp = xlist.get(i);
                    int yTemp = ylist.get(i);
                    distance = Math.sqrt(Math.pow(xTemp - x, 2) + Math.pow(yTemp - y, 2));
                    if (distance < 120)
                        return true;
                }
                return false;
            } else {
                for (int i = 0; i < sprinkx.size(); i++) {
                    int xTemp = sprinkx.get(i);
                    int yTemp = sprinky.get(i);
                    distance = Math.sqrt(Math.pow(xTemp - x, 2) + Math.pow(yTemp - y, 2));
                    if (distance < 30)
                        return true;
                }
                return false;
            }
        }

        int idCounter = 0;
        int posCount = 0;

        int specialCounter = Integer.MAX_VALUE;

        private void drawLine(Canvas canvas) {
            maxSideLength = 0;
            if (xlist.size() > 2) {
                posCount = xlist.size() - 2;
                if (xlist.size() == 3) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                            ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = (xlist.get(0) + xlist.get(1)) / 2;
                    params.topMargin = (ylist.get(0) + ylist.get(1)) / 2;

                    TextView textView = new TextView(context);
                    int val = (int) (Math.hypot(Math.abs(xlist.get(0) - xlist.get(1)),
                            Math.abs(ylist.get(0) - ylist.get(1))) * ratio);
                    if (maxSideLength < val) maxSideLength = val;
                    textView.setText("" + val);
                    textView.setId(idCounter);
                    textView.setLayoutParams(params);
                    //makeToast("Making the text");
                    rlDvHolder.addView(textView);
                    idCounter++;
                }

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = (xlist.get(posCount) + xlist.get(posCount + 1)) / 2;
                params.topMargin = (ylist.get(posCount) + ylist.get(posCount + 1)) / 2;

                TextView textView = new TextView(context);
                int val = (int) (Math.hypot(Math.abs(xlist.get(posCount) - xlist.get(posCount + 1)),
                        Math.abs(ylist.get(posCount) - ylist.get(posCount + 1))) * ratio);
                if (maxSideLength < val) maxSideLength = val;
                textView.setText("" + val);
                textView.setId(idCounter);
                textView.setLayoutParams(params);
                rlDvHolder.addView(textView);

                //DONE HAVE to deal with adding textview from last to first. and removing from previous touch.

                if (specialCounter != Integer.MAX_VALUE) {
                    rlDvHolder.findViewById(dv.specialCounter + 1).setVisibility(View.GONE);
                }


                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                int xlast = xlist.get(xlist.size() - 1) + xlist.get(0);
                int ylast = ylist.get(ylist.size() - 1) + ylist.get(0);
                params2.leftMargin = (xlast) / 2;
                params2.topMargin = (ylast) / 2;

                TextView last = new TextView(context);
                int val2 = (int) (Math.hypot(Math.abs(xlist.get(xlist.size() - 1) - xlist.get(0)),
                        Math.abs(ylist.get(ylist.size() - 1) - ylist.get(0))) * ratio);
                if (maxSideLength < val2) maxSideLength = val2;
                last.setText("" + val2);
                last.setId(specialCounter);
                last.setLayoutParams(params2);
                //makeToast("Making the last text");
                rlDvHolder.addView(last);

                idCounter++;
                posCount++;
                specialCounter--;

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
            } else if (xlist.size() == 2) {
                canvas.drawLine(xlist.get(0), ylist.get(0), xlist.get(1), ylist.get(1), linePaint);
            }
        }

        private void plotSprinklers2(Canvas canvas) {
            if (sprinkx.size() > 0) {
                for (int i = 0; i < sprinkx.size() - 1; i++) {
                    int m = i;
                    float radius = sprinkr.get(m);
                    canvas.drawArc(new RectF(sprinkx.get(m) - radius, sprinky.get(m) - radius, sprinkx.get(m) + radius,
                                    sprinky.get(m) + radius), (rotationList.get(i)) % 360 - 90,
                            angleList.get(i), true, sprinklerC);
                }
                int m = sprinkx.size() - 1;
                float radius = sprinkr.get(m);

                canvas.drawArc(new RectF(sprinkx.get(m) - radius, sprinky.get(m) - radius, sprinkx.get(m) + radius,
                                sprinky.get(m) + radius), (rotationList.get(rotationList.size() - 1)) % 360 - 90,
                        angleList.get(angleList.size() - 1), true, sprinklerC);
            }
        }

        private void drawCustomLine(Canvas canvas, int x1, int y1, int x2, int y2) {
            linePaint.setColor(Color.BLUE);
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
            if (xlist.size() > 0) {
                for (int i = 0; i < xlist.size(); i++) {
                    canvas.drawCircle(xlist.get(i), ylist.get(i), 9.0f, circlePaint);
                }
            }

        }

        private void makeToast(String s) {
            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
        }


        enum Mode {RECORDING, DRAWING, PLOT, SPRINKLER, RESET, DOTPLOT, drawc, resetc, splot, sreset, POLYGON}

        enum PastMode {DRAW, CIRCLE}

        ;

        public double polygonArea() {
            // Initialze area
            //if (currentMode == Mode.splot || currentMode == Mode.RESET) {
            if (pastMode == PastMode.DRAW) {
                int n = xlist.size();
                double area = 0.0;

                // Calculate value using shoelace formula
                int j = n - 1;
                for (int i = 0; i < n; i++) {
                    area += (xlist.get(j) + xlist.get(i)) * (ylist.get(j) - ylist.get(i));
                    // j is previous vertex to i
                    j = i;
                }
                // Return absolute value
                if (area != 0)
                    return Math.abs(area / 2.0);
                else {
                    double curRadius = radius * (screenW / 200);
                    return (int) Math.pow(curRadius, 2) * Math.PI;
                }
            } else {
                if (radius == -5)
                    return (int) 90000 * Math.PI;
                else {
                    double curRadius = radius * (screenW / 200);
                    return (int) Math.pow(curRadius, 2) * Math.PI;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    boolean rectangle = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //makeToast("Polygon area: " + dv.polygonArea());
        switch (item.getItemId()) {
            case R.id.circle:
                //TODO Have to draw circle.
                if (dv.currentMode == DrawingView.Mode.drawc || dv.currentMode == DrawingView.Mode.resetc) {
                    //INFO Need to switch from circle to polygon
                    radius.setVisibility(View.INVISIBLE);
                    polygon.setVisibility(View.VISIBLE);
                    item.setTitle("Draw Circle");
                    dv.currentMode = DrawingView.Mode.DOTPLOT;
                    dv.wasCircle = false;
                    dv.resetSprinklers();
                    dv.resetTouchPoints();
                    ft.setVisibility(View.VISIBLE);
                    textview.setVisibility(View.VISIBLE);
                    length.setVisibility(View.VISIBLE);
                    angleText.setVisibility(View.INVISIBLE);
                    rotateText.setVisibility(View.INVISIBLE);
                    left1.setVisibility(View.INVISIBLE);
                    left2.setVisibility(View.INVISIBLE);
                    right2.setVisibility(View.INVISIBLE);
                    right1.setVisibility(View.INVISIBLE);
                    removeAllLengths();
                    dv.invalidate();
                } else {
                    //INFO Currently on polygon. Switch to circle.
                    dv.currentMode = DrawingView.Mode.drawc;
                    dv.wasCircle = true;
                    item.setTitle("Draw Shapes");
                    dv.resetTouchPoints();
                    ft.setVisibility(View.VISIBLE);
                    textview.setVisibility(View.VISIBLE);
                    length.setVisibility(View.VISIBLE);
                    dv.resetSprinklers();
                    removeAllLengths();
                    polygon.setVisibility(View.INVISIBLE);
                    radius.setVisibility(View.VISIBLE);
                    angleText.setVisibility(View.INVISIBLE);
                    rotateText.setVisibility(View.INVISIBLE);
                    left1.setVisibility(View.INVISIBLE);
                    left2.setVisibility(View.INVISIBLE);
                    right2.setVisibility(View.INVISIBLE);
                    right1.setVisibility(View.INVISIBLE);
                    dv.invalidate();
                }
                // dv.resetSprinklers();
                return true;

            case R.id.sprinklers:
                //polygon.setText("");
                if (dv.xlist.size() < 3 && dv.currentMode != DrawingView.Mode.drawc && dv.currentMode != DrawingView.Mode.resetc) {
                    makeToast("You must first plot the area of land.");
                } else if (!handleSideLength()) {
                    //INFO They have not entered a side length/radius
                } else {
                    ft.setVisibility(View.INVISIBLE);
                    textview.setVisibility(View.INVISIBLE);
                    length.setVisibility(View.INVISIBLE);
                    polygon.setVisibility(View.INVISIBLE);
                    radius.setVisibility(View.VISIBLE);
                    angleText.setVisibility(View.VISIBLE);
                    rotateText.setVisibility(View.VISIBLE);
                    left1.setVisibility(View.VISIBLE);
                    left2.setVisibility(View.VISIBLE);
                    right2.setVisibility(View.VISIBLE);
                    right1.setVisibility(View.VISIBLE);
                    dv.currentMode = DrawingView.Mode.splot;
                }
                return true;


            case R.id.calculate:
                showLoading();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    double overFlowWastage = 0;      //INFO This is numerator for wastage
    double totalOverflowArea = 0;    //INFO This is denominator for wastage
    double overlapWastage = 0;       //INFO This is numbrator for wastage
    double totalInsideArea = 0;      //INFO This is denominator for wastage
    ArrayList<OverflowInfo> overflowInfo = new ArrayList<>();
    //Set<OverflowInfo> completelyOutside = new HashSet<>();
    HashMap<Integer, OverflowInfo> completelyOutside = new HashMap<>();
    HashMap<Integer, Double> individualCircles;
    ArrayList<Integer> candidates = new ArrayList<>();
    HashMap<Integer, Integer> insideCirclesH = new HashMap<>();
    HashMap<Integer, OverflowInfo> outsideIntersectingH = new HashMap<>();
    HashMap<Integer, Integer> completelyInsideH = new HashMap<>();
    HashMap<Integer, OverflowInfo> insideIntersectingH = new HashMap<>();
    ArrayList<Integer> insideCircles = new ArrayList<>();
    ArrayList<OverflowInfo> outsideIntersecting = new ArrayList<>();
    ArrayList<OverflowInfo> insideIntersecting = new ArrayList<>();
    ArrayList<Integer> completelyInside = new ArrayList<>();
    double wasted, total = 0;

    private void calculateSprinklerOverflow() {
        overFlowWastage = 0;
        overlapWastage = 0;
        totalInsideArea = 0;
        wasted = 0;
        total = 0;
        totalOverflowArea = 0;
        wasted = 0;
        total = 0;
        overlaps = new HashSet<>();
        completelyOutside = new HashMap<>();
        overflowInfo = new ArrayList<>();

        candidates = new ArrayList<>();
        individualCircles = new HashMap<>();

        insideCirclesH = new HashMap<>();
        outsideIntersectingH = new HashMap<>();
        completelyInsideH = new HashMap<>();
        insideIntersectingH = new HashMap<>();

        insideCircles = new ArrayList<>();
        outsideIntersecting = new ArrayList<>();
        completelyInside = new ArrayList<>();
        insideIntersecting = new ArrayList<>();
        for (int t = 0; t < dv.sprinkx.size(); t++) {
            double circleX = dv.sprinkx.get(t);
            double circleY = dv.sprinky.get(t);
            double radius = dv.sprinkr.get(t);
            boolean outside = outside(circleX, circleY);
            boolean removedCompletelyInsideCircles = false;
            boolean removed = false;
            boolean removedIndividual = false;
            boolean outsideIntersectingGood = false;
            double ox1 = 0, ox2 = 0, oy1 = 0, oy2 = 0;
            for (int b = 0; b < dv.xlist.size(); b++) {
                double startX = dv.xlist.get(b);
                double startY = dv.ylist.get(b);
                double endX = dv.xlist.get((b + 1 > dv.xlist.size() - 1 ? 0 : b + 1));
                double endY = dv.ylist.get((b + 1 > dv.ylist.size() - 1 ? 0 : b + 1));


                double baX = endX - startX;
                double baY = endY - startY;
                double caX = circleX - startX;
                double caY = circleY - startY;

                double a = baX * baX + baY * baY;
                double bBy2 = baX * caX + baY * caY;
                double c = caX * caX + caY * caY - radius * radius;

                double pBy2 = bBy2 / a;
                double q = c / a;

                double disc = pBy2 * pBy2 - q;
                //README No intersections, but still need to check if it was outside
                if (disc < 0) {
                    //README Circle center is outside and no intersections
                    if (outside) {
                        //if (!added)
                        individualCircles.remove(t);
                        removedIndividual = true;
                        if (!removed)
                            completelyOutside.put(t, new OverflowInfo(t, 0, 0, 0, 0, 0, 0, 0, 0, radius
                                    , 0, 0, 0, 0));
                    } else {
                        //README Point had no intersections and was inside
                        insideCirclesH.put(t, 1);
                        if (!removedCompletelyInsideCircles)
                            completelyInsideH.put(t, 1);
                        if (!removedIndividual)
                            individualCircles.put(t, radius);
                    }
                } else {
                    double tmpSqrt = Math.sqrt(disc);
                    double abScalingFactor1 = -pBy2 + tmpSqrt;
                    double abScalingFactor2 = -pBy2 - tmpSqrt;

                    //README 1 intersection
                    if (disc == 0) { // abScalingFactor1 == abScalingFactor2
                        //DONE calculate 30% wastage
                        completelyInsideH.remove(t);
                        removedCompletelyInsideCircles = true;
                        overFlowWastage += radius * radius * Math.PI * dv.angleList.get(t) / 360d * .3;
                        totalOverflowArea += radius * radius * Math.PI * dv.angleList.get(t) / 360d;
                    } else {
                        double x1 = (startX - baX * abScalingFactor1);
                        double y1 = (startY - baY * abScalingFactor1);
                        double x2 = (startX - baX * abScalingFactor2);
                        double y2 = (startY - baY * abScalingFactor2);
                        //README False intersection (line not line segment)
                        if (!((x1 > Math.min(startX, endX) && x1 < Math.max(startX, endX) && y1 > Math.min(startY, endY) && y1 < Math.max(startY, endY))
                                || (x2 > Math.min(startX, endX) && x2 < Math.max(startX, endX) && y2 > Math.min(startY, endY) && y2 < Math.max(startY, endY)))) {
                        } else {
                            //README actual intersection with 2 points
                            //  circle is not fully outside land.
                            completelyOutside.remove(t);
                            completelyInsideH.remove(t);
                            removedCompletelyInsideCircles = true;
                            individualCircles.remove(t);
                            removedIndividual = true;
                            if (outside) {
                                outsideIntersectingGood = true;
                                ox1 = x1;
                                ox2 = x2;
                                oy1 = y1;
                                oy2 = y2;
                            }
                            removed = true;
                            if (!outside)
                                insideIntersectingH.put(t, new OverflowInfo(t, b, (b + 1 > dv.xlist.size() - 1 ? 0 : b + 1), startX, startY,
                                        endX, endY, circleX, circleY, radius, x1, x2, y1, y2));

                            overflowInfo.add(new OverflowInfo(t, b, (b + 1 > dv.xlist.size() - 1 ? 0 : b + 1), startX, startY,
                                    endX, endY, circleX, circleY, radius, x1, x2, y1, y2));
                        }
                    }
                }
            }
            if (outsideIntersectingGood)
                outsideIntersectingH.put(t, new OverflowInfo(t, 0, 1, 0, 1,
                        0, 1, circleX, circleY, radius, ox1, ox2, oy1, oy2));
        }

        for (Map.Entry<Integer, Double> map : individualCircles.entrySet()) {
            //README Candidates contains the positions of possible individual sprinklers.
            candidates.add(map.getKey());
        }
        //README method uses positions in candidates list to actually calculate individual or not.
        //DONE Calculate areas of individual circles (including angles)
        getIndividualCircles2();

        outsideIntersecting = new ArrayList<>(outsideIntersectingH.values());
        insideIntersecting = new ArrayList<>(insideIntersectingH.values());
        completelyInside = new ArrayList<>(completelyInsideH.keySet());
        insideCircles = new ArrayList<>(insideCirclesH.keySet());

        calculateOverflowWastage();
        insideCircleOverlap();
        completelyInsideOverlapOutside();
        insideIntersectingOutsideIntersecting();
        outsideIntersectingOutsideIntersecting();
        insideIntersectingInsideIntersecting();
        calculate3CircleOverlap();

        total = 0;
        for (Map.Entry<Integer, OverflowInfo> completelyOutside : completelyOutside.entrySet()) {
            total += completelyOutside.getValue().getRadius() * completelyOutside.getValue().getRadius() *
                    Math.PI * dv.angleList.get(completelyOutside.getValue().getCirclePos()) / 360d;
            totalOverflowArea += completelyOutside.getValue().getRadius() * completelyOutside.getValue().getRadius() *
                    Math.PI * dv.angleList.get(completelyOutside.getValue().getCirclePos()) / 360d;
            overFlowWastage += completelyOutside.getValue().getRadius() * completelyOutside.getValue().getRadius() *
                    Math.PI * dv.angleList.get(completelyOutside.getValue().getCirclePos()) / 360d;
        }
        for (OverflowInfo outside : outsideIntersecting) {
            total += Math.PI * outside.getRadius() * outside.getRadius() *
                    dv.angleList.get(outside.getCirclePos()) / 360;
            totalInsideArea += Math.PI * outside.getRadius() * outside.getRadius() *
                    dv.angleList.get(outside.getCirclePos()) / 360;
        }
        for (Integer o : insideCircles) {
            total += Math.PI * dv.sprinkr.get(o) * dv.sprinkr.get(o) * dv.angleList.get(o) / 360;
        }

        wasted += overFlowWastage;
        wasted += overlapWastage;
        //INFO Subtracts overlap of 3 circles
        wasted -= overCounted3;

        hideLoading();
        displayResults();
    }

    private void displayResults() {
        final Dialog dialog = new Dialog(CleanMainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.result);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button next = (Button) dialog.findViewById(R.id.done);
        Button backBtn = (Button) dialog.findViewById(R.id.goBack);
        final RelativeLayout results = (RelativeLayout) dialog.findViewById(R.id.realresults);
        final RelativeLayout toHide = dialog.findViewById(R.id.questions);
        final EditText waterUsedE = dialog.findViewById(R.id.waterused);
        final EditText durationE = dialog.findViewById(R.id.duration);
        final Spinner soilType = dialog.findViewById(R.id.soilType);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                dialog.dismiss();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String wU = waterUsedE.getText().toString();
                boolean fgood = false;
                boolean sgood = false;
                double waterUsed = 0;
                double duration = 0;
                if (wU != null) {
                    if (wU.length() > 0) {
                        waterUsed = Double.parseDouble(wU);
                        fgood = true;
                    }
                }

                String d = durationE.getText().toString();
                if (d != null) {
                    if (d.length() > 0) {
                        duration = Double.parseDouble(d);
                        sgood = true;
                    }
                }

                if (!(fgood && sgood))
                    makeToast("Please fill in the information.");
                else {
                    //DONE IT is good to contineu ahead.
                    dialog.cancel();
                    dialog.dismiss();

                    String choice = soilType.getSelectedItem().toString();
                    showResults(waterUsed, duration, choice);

                }

            }
        });

        dialog.show();
    }

    int overlappingCount = 0;
    Set<Integer> overlaps;

    //FUTURE FILES see how app looks with plotting sprinkler center as a black dot.
    //FUTURE FILES Give actual feedback as to how they can improve the design plan.
    //  Have if statements. Maybe say you have 3 sprinklers outside land plot, reposition them inside to
    //  save x amount of water. You have x overlapping sprinklers
    //  and only 60% coverage, reposition them to cover more land.
    private void showResults(double waterUsed, double duration, String choice) {
        final Dialog dialog = new Dialog(CleanMainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.real_result);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(dialog.getWindow().getAttributes().width,
                (int) (dv.screenH * 0.9d));
        dialog.show();
        final ImageView excessHelp = dialog.findViewById(R.id.excessHelp);
        ImageView overflowHelp = dialog.findViewById(R.id.overflowHelp);
        final ImageView insideHelp = dialog.findViewById(R.id.insideHelp);
        final ImageView outsideHelp = dialog.findViewById(R.id.outsideHelp);
        View.OnTouchListener toucher = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view.getId() == excessHelp.getId())
                    makeToast("Amount of water wasted by overwatering" +
                            " the same region repeatedly.");
                if (view.getId() == insideHelp.getId())
                    makeToast("# of sprinklers inside the land plot.");
                if (view.getId() == outsideHelp.getId())
                    makeToast("You have " + (outsideIntersecting.size() + completelyOutside.size()) + " sprinklers placed outside your land plot.");
                else
                    makeToast("Amount of water wasted by overflowing out of the specified land plot.");
                return false;
            }
        };
        excessHelp.setOnTouchListener(toucher);
        overflowHelp.setOnTouchListener(toucher);
        insideHelp.setOnTouchListener(toucher);
        outsideHelp.setOnTouchListener(toucher);

        final Button back = dialog.findViewById(R.id.goBack);
        Button done = dialog.findViewById(R.id.done);
        View.OnClickListener clicker = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                if (view.getId() == back.getId()) displayResults();
            }
        };
        back.setOnClickListener(clicker);
        done.setOnClickListener(clicker);

        double soilFactor = 1;
        if (choice.equals("Sandy")) {
            soilFactor *= 0.95f;
        } else if (choice.equals("Loam")) {
            soilFactor *= 0.9f;
        } else if (choice.equals("Clay")) {
            soilFactor *= 1.1f;
        }

        wasted *= soilFactor;
        if (wasted < 0 || wasted > total)
            wasted = total * .95;
        TextView numSprink = (TextView) dialog.findViewById(R.id.sprinks);
        numSprink.setText(Integer.toString(dv.sprinkx.size()));
        TextView outsideSprink = dialog.findViewById(R.id.outsideNum);
        TextView insideSprink = dialog.findViewById(R.id.insideNum);
        outsideSprink.setText(Integer.toString(outsideIntersecting.size() + completelyOutside.size()));
        insideSprink.setText(Integer.toString(insideCircles.size()));
        TextView overlappingSprink = dialog.findViewById(R.id.overlapNum);
        TextView overflowSprink = dialog.findViewById(R.id.overflowNum);
        overflowSprink.setText(Integer.toString(outsideIntersecting.size() + completelyOutside.size()
                + insideIntersecting.size()));
        overlappingSprink.setText(Integer.toString(overlaps.size()));

        final TextView totalA = dialog.findViewById(R.id.tA);
        final TextView landCovered = dialog.findViewById(R.id.landCovered);
        final TextView totalLandA = dialog.findViewById(R.id.totalLand);
        final TextView percentCoveredA = dialog.findViewById(R.id.pcA);
        final TextView wastedT = dialog.findViewById(R.id.ww);
        final TextView totalWaterOutput = dialog.findViewById(R.id.two);
        final TextView percentWasted = dialog.findViewById(R.id.pww);
        final TextView perMonth = dialog.findViewById(R.id.permonth);
        final TextView perYear = dialog.findViewById(R.id.peryear);
        TextView overflowWaterOutput = dialog.findViewById(R.id.overflow);
        TextView excessiveWaterOutput = dialog.findViewById(R.id.excess);

        double landArea = dv.polygonArea() * dv.ratio * dv.ratio;
        int num = dv.sprinkx.size();
        double totalWater = num * duration * waterUsed;
        totalLandA.setText(format(landArea) + " sq. ft");
        totalWaterOutput.setText(format(totalWater) + " gal/wk");
        totalA.setText(Math.round(totalWater * 10) / 10 + " gal/wk");
        double overflowWater = totalWater * overFlowWastage / total;
        overflowWaterOutput.setText(format(overflowWater) + " gal/wk");
        double excessive = totalWater * (overlapWastage - overCounted3) / total;
        excessiveWaterOutput.setText(format(excessive) + " gal/wk");

        double waterWasted = totalWater * wasted / total;
        wastedT.setText(format(waterWasted) + " gal/wk");

        double percentageWasted = wasted / total;
        percentWasted.setText(format(percentageWasted * 100) + "%");

        perMonth.setText((format2(waterWasted * 4)) + " gal");
        perYear.setText((format2(waterWasted * 52)) + " gal");

        double notWastedWater = 1 - percentageWasted;
        double landCoveredArea = notWastedWater * total / dv.polygonArea() * landArea;
        double percentLandCovered = landCoveredArea / landArea;
        landCovered.setText(format(landCoveredArea) + " sq. ft");
        percentCoveredA.setText(format(percentLandCovered * 100) + "%");

    }

    private String format2(double v) {
        return String.format("%1$,.0f", (int) Math.round(v * 10) / 10d);
    }

    private String format(double overflowWater) {
        return String.format("%1$,.1f", Math.round(overflowWater * 10) / 10d);
    }


    double overCounted3 = 0;

    private void calculate3CircleOverlap() {
        double area = 0;
        for (int i = 0; i < insideCircles.size() - 2; i++) {
            for (int j = i + 1; j < insideCircles.size() - 1; j++) {
                int circle1 = insideCircles.get(i);
                int circle2 = insideCircles.get(j);

                double x1 = dv.sprinkx.get(circle1);
                double x2 = dv.sprinkx.get(circle2);
                double y1 = dv.sprinky.get(circle1);
                double y2 = dv.sprinky.get(circle2);

                double r1 = dv.sprinkr.get(circle1);
                double r2 = dv.sprinkr.get(circle2);
                double angle1 = dv.angleList.get(circle1);
                double angle2 = dv.angleList.get(circle2);

                double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                //README The 2 circles overlap
                if (distance <= r1 + r2) {
                    for (int k = j + 1; k < insideCircles.size(); k++) {
                        int circle3 = insideCircles.get(k);
                        double x3 = dv.sprinkx.get(circle3);
                        double y3 = dv.sprinky.get(circle3);
                        double r3 = dv.sprinkr.get(circle3);
                        double angle3 = dv.angleList.get(circle3);
                        overlaps.add(circle1);
                        overlaps.add(circle2);
                        double dist1 = Math.sqrt(Math.pow(x1 - x3, 2) + Math.pow(y1 - y3, 2));
                        double dist2 = Math.sqrt(Math.pow(x2 - x3, 2) + Math.pow(y2 - y3, 2));
                        if ((dist1 <= r1 + r3) && (dist2 <= r2 + r3)) {
                            overlaps.add(circle3);
                            //README 3rd circle overlaps with other 2.
                            //TODO Have to determine the intersection points of c1-c2, c1-c3, c2-c3 that are inside the other circle.
                            double d = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                            double a = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
                            double h = Math.sqrt(r1 * r1 - a * a);

                            double x4 = x1 + a * (x2 - x1) / d;
                            double y4 = y1 + a * (y2 - y1) / d;
                            double x5 = x4 + h * (y2 - y1) / d;     // also x3=x2-h*(y1-y0)/d
                            double y5 = y4 - h * (x2 - x1) / d;
                            x4 = x4 - h * (y2 - y1) / d;
                            y4 = y4 + h * (x2 - x1) / d;

                            double d2 = Math.sqrt(Math.pow(x1 - x3, 2) + Math.pow(y1 - y3, 2));
                            double a2 = (r1 * r1 - r3 * r3 + d2 * d2) / (2 * d2);
                            double h2 = Math.sqrt(r1 * r1 - a2 * a2);

                            double x6 = x1 + a2 * (x3 - x1) / d2;
                            double y6 = y1 + a2 * (y3 - y1) / d2;
                            double x7 = x6 + h2 * (y3 - y1) / d2;     // also x3=x2-h*(y1-y0)/d
                            double y7 = y6 - h2 * (x3 - x1) / d2;
                            x6 = x6 - h2 * (y3 - y1) / d2;
                            y6 = y6 + h2 * (x3 - x1) / d2;

                            double d3 = Math.sqrt(Math.pow(x2 - x3, 2) + Math.pow(y2 - y3, 2));
                            double a3 = (r2 * r2 - r3 * r3 + d3 * d3) / (2 * d3);
                            double h3 = Math.sqrt(r2 * r2 - a3 * a3);

                            double x8 = x2 + a3 * (x3 - x2) / d3;
                            double y8 = y2 + a3 * (y3 - y2) / d3;
                            double x9 = x8 + h3 * (y3 - y2) / d3;     // also x3=x2-h*(y1-y0)/d
                            double y9 = y8 - h3 * (x3 - x2) / d3;
                            x8 = x8 - h3 * (y3 - y2) / d3;
                            y8 = y8 + h3 * (x3 - x2) / d3;

                            double i1x, i1y, i2x, i2y, i3x, i3y;


                            boolean chain1 = false, chain2 = false, chain3 = false;
                            double[] intersection1 = circleCircleIntersectionisInOtherCircle(x4, y4, x5, y5, x3, y3, r3);
                            double[] intersection2 = circleCircleIntersectionisInOtherCircle(x6, y6, x7, y7, x2, y2, r2);
                            double[] intersection3 = circleCircleIntersectionisInOtherCircle(x8, y8, x9, y9, x1, y1, r1);
                            chain3 = intersection1[2] == 1;
                            chain2 = intersection2[2] == 1;
                            chain1 = intersection3[2] == 1;
                            //README There is a chain
                            if (chain1 || chain2 || chain3) {
                                //TODO IF chain need to subtract area of overlap of 2 circles.
                                //INFO Below is the info for the circles that we need to calculate the overlap of.
                                double xc1 = x1, xc2 = x2, yc1 = y1, yc2 = y2, rc1 = r1, rc2 = r2;
                                if (chain3) {
                                    //Overlap of 1 and 2
                                } else if (chain2) {
                                    //Overlap of 1 and 3
                                    xc2 = x3;
                                    yc2 = y3;
                                    rc2 = r3;
                                } else {
                                    //Overlap of 2 and 3
                                    xc1 = x2;
                                    yc1 = y2;
                                    rc1 = r2;
                                    xc2 = x3;
                                    yc2 = y3;
                                    rc2 = r3;
                                }
                                //INFO calculate the overlap
                                Double r = rc1;
                                Double R = rc2;
                                double firstX = xc1;
                                double secondX = xc2;
                                double firstY = yc1;
                                double secondY = yc2;
                                Double dm = Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(secondY - firstY, 2));
                                if (R < r) {
                                    // swap
                                    r = rc2;
                                    R = rc1;
                                }
                                Double part1 = r * r * Math.acos((dm * dm + r * r - R * R) / (2 * dm * r));
                                Double part2 = R * R * Math.acos((dm * dm + R * R - r * r) / (2 * dm * R));
                                Double part3 = 0.5f * Math.sqrt((-dm + r + R) * (dm + r - R) * (dm - r + R) * (dm + r + R));

                                double intersectionArea = part1 + part2 - part3;

                                //README Maybe intersectionArea is 0 because circle is inside other circle.
                                if (!(intersectionArea > 0)) {
                                    intersectionArea = Math.PI * Math.pow(r, 2) * 345 / 360;
                                }
                                area += intersectionArea;
                            } else {
                                i1x = intersection3[0];
                                i1y = intersection3[1];
                                i2x = intersection2[0];
                                i2y = intersection2[1];
                                i3x = intersection1[0];
                                i3y = intersection1[1];

                                double length1 = Math.sqrt(Math.pow(i1x - i2x, 2) + Math.pow(i1y - i2y, 2));
                                double length2 = Math.sqrt(Math.pow(i2x - i3x, 2) + Math.pow(i2y - i3y, 2));
                                double length3 = Math.sqrt(Math.pow(i1x - i3x, 2) + Math.pow(i1y - i3y, 2));
                                double s = (length1 + length2 + length3) / 2;

                                //INFO Area of the triangle formed by the three points
                                double triangleArea = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));
                                double arc1, arc2, arc3;
                                arc1 = arcArea(r1, i2x, i2y, i3x, i3y, x1, y1);
                                arc2 = arcArea(r2, i1x, i1y, i3x, i3y, x2, y2);
                                arc3 = arcArea(r3, i1x, i1y, i2x, i2y, x3, y3);
                                double intersectionArea = arc1 + arc2 + arc3 + triangleArea;
                                area += intersectionArea;
                            }

                        }
                    }
                }
            }
        }
        overCounted3 = area;
    }

    //README Give it the intersections of 2 circles (x1, y1) and (x2, y2) then the center/radius of the other circle.
    private double[] circleCircleIntersectionisInOtherCircle(double x1, double y1, double x2, double y2, double centerX, double centerY, double radius) {
        double distance1 = Math.sqrt(Math.pow(x1 - centerX, 2) + Math.pow(y1 - centerY, 2));
        double distance2 = Math.sqrt(Math.pow(x2 - centerX, 2) + Math.pow(y2 - centerY, 2));
        boolean b1 = false;
        boolean b2 = false;
        //README Chainis important becasue we can have a chain of 3 circles, side by side
        //  and 1 circle intersects with other 2.
        boolean chain = false;
        b1 = distance1 <= radius;
        b2 = distance2 <= radius;
        chain = b1 && b2;
        return new double[]{b1 ? x1 : x2, b1 ? y1 : y2, chain ? 1 : 0};
    }

    private void insideIntersectingOutsideIntersecting() {
        double overlap2 = 0;
        for (int i = 0; i < insideIntersecting.size(); i++) {
            for (int j = 0; j < outsideIntersecting.size(); j++) {
                int circle1 = insideIntersecting.get(i).getCirclePos();
                int circle2 = outsideIntersecting.get(j).getCirclePos();

                double x1 = dv.sprinkx.get(circle1);
                double x2 = dv.sprinkx.get(circle2);
                double y1 = dv.sprinky.get(circle1);
                double y2 = dv.sprinky.get(circle2);

                double r1 = dv.sprinkr.get(circle1);
                double r2 = dv.sprinkr.get(circle2);
                double angle1 = dv.angleList.get(circle1);
                double angle2 = dv.angleList.get(circle2);

                double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

                double d = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                double a = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
                double h = Math.sqrt(r1 * r1 - a * a);
                double x3 = x1 + a * (x2 - x1) / d;
                double y3 = y1 + a * (y2 - y1) / d;
                double x4 = x3 + h * (y2 - y1) / d;     // also x3=x2-h*(y1-y0)/d
                double y4 = y3 - h * (x2 - x1) / d;
                x3 = x3 - h * (y2 - y1) / d;
                y3 = y3 + h * (x2 - x1) / d;

                boolean out1 = outside(x3, y3);
                boolean out2 = outside(x4, y4);

                if (distance < r1 + r2) {
                    //README Both intersection points are outside meaning that area is already accounted for in overflowWastage
                    if (out1 && out2) {

                    } else if (!out1 && !out2) {
                        //README Both intersection points are inside the land meaning that it is simply overlap wastage
                        overlaps.add(circle2);
                        overlaps.add(circle1);
                        overlappingCount++;
                        double intersectionArea = 0;
                        Double r = r1;
                        Double R = r2;
                        double firstX = x1;
                        double secondX = x2;
                        double firstY = y1;
                        double secondY = y2;
                        int smallC = circle1;
                        int bigc = circle2;
                        if (R < r) {
                            // swap
                            r = r2;
                            R = r1;
                            smallC = circle2;
                            bigc = circle1;
                        }
                        Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
                        Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
                        Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

                        intersectionArea = part1 + part2 - part3;

                        //README Maybe intersectionArea is 0 because circle is inside other circle.
                        if (!(intersectionArea > 0)) {
                            intersectionArea = Math.PI * Math.pow(r, 2) * (360 - (360 - dv.angleList.get(smallC) / 2)) / 360;
                        }

                        overlap2 += intersectionArea;
                    } else {
                        overlaps.add(circle2);
                        overlaps.add(circle1);
                        overlappingCount++;
//README Intersection area is inside and outside
                        //INFO You need the circle-circle intersection that is inside.
                        //README Below is for the circle-circle intersection
                        double circleX = 0, circleY = 0;
                        if (out1) {
                            circleX = x4;
                            circleY = y4;
                        } else if (out2) {
                            circleX = x3;
                            circleY = y3;
                        }
                        //TODO Calculate circle-line intersection for both circles.
                        //INFO Below is for the circle-line intersections.
                        double ix1, ix2, iy1, iy2;
                        ix1 = insideIntersecting.get(i).getX1();
                        iy1 = insideIntersecting.get(i).getY1();
                        double dist = Math.sqrt(Math.pow(ix1 - outsideIntersecting.get(j).circleX, 2) +
                                Math.pow(iy1 - outsideIntersecting.get(j).circleY, 2));
                        //INFO This circle-intersection point is not inside the other circle so switch them
                        if (dist > outsideIntersecting.get(j).getRadius()) {
                            ix1 = insideIntersecting.get(i).getX2();
                            iy1 = insideIntersecting.get(i).getY2();
                        }

                        ix2 = outsideIntersecting.get(j).getX1();
                        iy2 = outsideIntersecting.get(j).getY1();
                        dist = Math.sqrt(Math.pow(ix2 - insideIntersecting.get(i).circleX, 2) +
                                Math.pow(iy2 - insideIntersecting.get(i).circleY, 2));
                        //INFO This circle-intersection point is not inside the other circle so switch them
                        if (dist > insideIntersecting.get(i).getRadius()) {
                            ix2 = outsideIntersecting.get(j).getX2();
                            iy2 = outsideIntersecting.get(j).getY2();
                        }

                        double length1 = Math.sqrt(Math.pow(ix1 - ix2, 2) + Math.pow(iy1 - iy2, 2));
                        double length2 = Math.sqrt(Math.pow(ix2 - circleX, 2) + Math.pow(iy2 - circleY, 2));
                        double length3 = Math.sqrt(Math.pow(ix1 - circleX, 2) + Math.pow(iy1 - circleY, 2));
                        double s = (length1 + length2 + length3) / 2;

                        //INFO Area of the triangle formed by the three points
                        double triangleArea = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));
                        //INFO Area of each of the two arcs.
                        double arc1 = 0, arc2 = 0;
                        arc1 = arcArea(r1, ix1, iy1, circleX, circleY, x1, y1);
                        arc2 = arcArea(r1, ix2, iy2, circleX, circleY, x2, y2);

                        overlap2 += triangleArea + arc1 + arc2;
                    }
                }
            }
        }
        overlapWastage += overlap2;
    }

    private void outsideIntersectingOutsideIntersecting() {
        double overlap2 = 0;
        for (int i = 0; i < outsideIntersecting.size() - 1; i++) {
            for (int j = i + 1; j < outsideIntersecting.size(); j++) {
                int circle1 = outsideIntersecting.get(i).getCirclePos();
                int circle2 = outsideIntersecting.get(j).getCirclePos();

                double x1 = dv.sprinkx.get(circle1);
                double x2 = dv.sprinkx.get(circle2);
                double y1 = dv.sprinky.get(circle1);
                double y2 = dv.sprinky.get(circle2);

                double r1 = dv.sprinkr.get(circle1);
                double r2 = dv.sprinkr.get(circle2);
                double angle1 = dv.angleList.get(circle1);
                double angle2 = dv.angleList.get(circle2);

                double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

                double d = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                double a = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
                double h = Math.sqrt(r1 * r1 - a * a);
                double x3 = x1 + a * (x2 - x1) / d;
                double y3 = y1 + a * (y2 - y1) / d;
                double x4 = x3 + h * (y2 - y1) / d;     // also x3=x2-h*(y1-y0)/d
                double y4 = y3 - h * (x2 - x1) / d;
                x3 = x3 - h * (y2 - y1) / d;
                y3 = y3 + h * (x2 - x1) / d;

                boolean out1 = outside(x3, y3);
                boolean out2 = outside(x4, y4);

                if (distance < r1 + r2) {
                    //README Both intersection points are outside meaning that area is already accounted for in overflowWastage
                    if (out1 && out2) {

                    } else if (!out1 && !out2) {
                        overlaps.add(circle2);
                        overlaps.add(circle1);
                        overlappingCount++;
                        //README Both intersection points are inside the land meaning that it is simply overlap wastage
                        double intersectionArea = 0;
                        Double r = r1;
                        Double R = r2;
                        double firstX = x1;
                        double secondX = x2;
                        double firstY = y1;
                        double secondY = y2;
                        int smallC = circle1;
                        int bigc = circle2;
                        if (R < r) {
                            // swap
                            r = r2;
                            R = r1;
                            smallC = circle2;
                            bigc = circle1;
                        }
                        Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
                        Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
                        Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

                        intersectionArea = part1 + part2 - part3;

                        //README Maybe intersectionArea is 0 because circle is inside other circle.
                        if (!(intersectionArea > 0)) {
                            intersectionArea = Math.PI * Math.pow(r, 2) * (360 - (360 - dv.angleList.get(smallC) / 2)) / 360;
                        }

                        overlap2 += intersectionArea;
                    } else {
                        overlaps.add(circle2);
                        overlaps.add(circle1);
                        overlappingCount++;
//README Intersection area is inside and outside
                        //INFO You need the circle-circle intersection that is inside.
                        //README Below is for the circle-circle intersection
                        double circleX = 0, circleY = 0;
                        if (out1) {
                            circleX = x4;
                            circleY = y4;
                        } else if (out2) {
                            circleX = x3;
                            circleY = y3;
                        }
                        //TODO Calculate circle-line intersection for both circles.
                        //INFO Below is for the circle-line intersections.
                        double ix1, ix2, iy1, iy2;
                        ix1 = outsideIntersecting.get(i).getX1();
                        iy1 = outsideIntersecting.get(i).getY1();
                        double dist = Math.sqrt(Math.pow(ix1 - outsideIntersecting.get(j).circleX, 2) +
                                Math.pow(iy1 - outsideIntersecting.get(j).circleY, 2));
                        //INFO This circle-intersection point is not inside the other circle so switch them
                        if (dist > outsideIntersecting.get(j).getRadius()) {
                            ix1 = outsideIntersecting.get(i).getX2();
                            iy1 = outsideIntersecting.get(i).getY2();
                        }

                        ix2 = outsideIntersecting.get(j).getX1();
                        iy2 = outsideIntersecting.get(j).getY1();
                        dist = Math.sqrt(Math.pow(ix2 - outsideIntersecting.get(i).circleX, 2) +
                                Math.pow(iy2 - outsideIntersecting.get(i).circleY, 2));
                        //INFO This circle-intersection point is not inside the other circle so switch them
                        if (dist > outsideIntersecting.get(i).getRadius()) {
                            ix2 = outsideIntersecting.get(j).getX2();
                            iy2 = outsideIntersecting.get(j).getY2();
                        }

                        double length1 = Math.sqrt(Math.pow(ix1 - ix2, 2) + Math.pow(iy1 - iy2, 2));
                        double length2 = Math.sqrt(Math.pow(ix2 - circleX, 2) + Math.pow(iy2 - circleY, 2));
                        double length3 = Math.sqrt(Math.pow(ix1 - circleX, 2) + Math.pow(iy1 - circleY, 2));
                        double s = (length1 + length2 + length3) / 2;

                        //INFO Area of the triangle formed by the three points
                        double triangleArea = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));
                        //INFO Area of each of the two arcs.
                        double arc1 = 0, arc2 = 0;
                        arc1 = arcArea(r1, ix1, iy1, circleX, circleY, x1, y1);
                        arc2 = arcArea(r1, ix2, iy2, circleX, circleY, x2, y2);

                        overlap2 += triangleArea + arc1 + arc2;

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                                ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = (int) ix1 - 3;
                        params.topMargin = (int) iy1;
                        TextView textView = new TextView(context);
                        textView.setText("o");
                        textView.setId(dv.idCounter);
                        textView.setTextSize(7);
                        textView.setLayoutParams(params);
                        //makeToast("Making the text");
                        rlDvHolder.addView(textView);
                        dv.idCounter++;
                        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams
                                ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                        params2.leftMargin = (int) ix2 - 5;
                        params2.topMargin = (int) iy2;
                        TextView textView2 = new TextView(context);
                        textView2.setText("o");
                        textView2.setId(dv.idCounter);
                        textView2.setLayoutParams(params2);
                        textView2.setTextSize(7);
                        //makeToast("Making the text");
                        rlDvHolder.addView(textView2);
                        dv.idCounter++;
                        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams
                                ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                        params3.leftMargin = (int) circleX - 6;
                        params3.topMargin = (int) circleY;
                        TextView textView3 = new TextView(context);
                        textView3.setText("o");
                        textView3.setId(dv.idCounter);
                        textView3.setLayoutParams(params3);
                        textView3.setTextSize(7);
                        //makeToast("Making the text");
                        rlDvHolder.addView(textView3);
                        dv.idCounter++;
                    }
                }
            }
        }
        overlapWastage += overlap2;
    }

    private void insideIntersectingInsideIntersecting() {
        double overlap2 = 0;
        for (int i = 0; i < insideIntersecting.size() - 1; i++) {
            for (int j = i + 1; j < insideIntersecting.size(); j++) {
                int circle1 = insideIntersecting.get(i).getCirclePos();
                int circle2 = insideIntersecting.get(j).getCirclePos();

                double x1 = dv.sprinkx.get(circle1);
                double x2 = dv.sprinkx.get(circle2);
                double y1 = dv.sprinky.get(circle1);
                double y2 = dv.sprinky.get(circle2);

                double r1 = dv.sprinkr.get(circle1);
                double r2 = dv.sprinkr.get(circle2);
                double angle1 = dv.angleList.get(circle1);
                double angle2 = dv.angleList.get(circle2);

                double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

                double d = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                double a = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
                double h = Math.sqrt(r1 * r1 - a * a);
                double x3 = x1 + a * (x2 - x1) / d;
                double y3 = y1 + a * (y2 - y1) / d;
                double x4 = x3 + h * (y2 - y1) / d;     // also x3=x2-h*(y1-y0)/d
                double y4 = y3 - h * (x2 - x1) / d;
                x3 = x3 - h * (y2 - y1) / d;
                y3 = y3 + h * (x2 - x1) / d;


                boolean out1 = outside(x3, y3);
                boolean out2 = outside(x4, y4);

                if (distance < r1 + r2) {
                    //README Both intersection points are outside meaning that area is already accounted for in overflowWastage
                    if (out1 && out2) {

                    } else if (!out1 && !out2) {
                        overlaps.add(circle2);
                        overlaps.add(circle1);
                        overlappingCount++;
                        //README Both intersection points are inside the land meaning that it is simply overlap wastage
                        double intersectionArea = 0;
                        Double r = r1;
                        Double R = r2;
                        double firstX = x1;
                        double secondX = x2;
                        double firstY = y1;
                        double secondY = y2;
                        int smallC = circle1;
                        int bigc = circle2;
                        if (R < r) {
                            // swap
                            r = r2;
                            R = r1;
                            smallC = circle2;
                            bigc = circle1;
                        }
                        Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
                        Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
                        Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

                        intersectionArea = part1 + part2 - part3;

                        if (!(intersectionArea > 0)) {
                            intersectionArea = Math.PI * Math.pow(r, 2) * (360 - (360 - dv.angleList.get(smallC) / 2)) / 360;
                        }

                        overlap2 += intersectionArea;
                    } else {
                        overlaps.add(circle2);
                        overlaps.add(circle1);
                        overlappingCount++;
                        //README Intersection area is inside and outside
                        //INFO You need the circle-circle intersection that is inside.
                        //README Below is for the circle-circle intersection
                        double circleX = 0, circleY = 0;
                        if (out1) {
                            circleX = x4;
                            circleY = y4;
                        } else if (out2) {
                            circleX = x3;
                            circleY = y3;
                        }
                        //TODO Calculate circle-line intersection for both circles.
                        //INFO Below is for the circle-line intersections.
                        double ix1, ix2, iy1, iy2;
                        ix1 = insideIntersecting.get(i).getX1();
                        iy1 = insideIntersecting.get(i).getY1();
                        double dist = Math.sqrt(Math.pow(ix1 - insideIntersecting.get(j).circleX, 2) +
                                Math.pow(iy1 - insideIntersecting.get(j).circleY, 2));
                        //INFO This circle-intersection point is not inside the other circle so switch them
                        if (dist > insideIntersecting.get(j).getRadius()) {
                            ix1 = insideIntersecting.get(i).getX2();
                            iy1 = insideIntersecting.get(i).getY2();
                        }

                        ix2 = insideIntersecting.get(j).getX1();
                        iy2 = insideIntersecting.get(j).getY1();
                        dist = Math.sqrt(Math.pow(ix2 - insideIntersecting.get(i).circleX, 2) +
                                Math.pow(iy2 - insideIntersecting.get(i).circleY, 2));
                        //INFO This circle-intersection point is not inside the other circle so switch them
                        if (dist > insideIntersecting.get(i).getRadius()) {
                            ix2 = insideIntersecting.get(j).getX2();
                            iy2 = insideIntersecting.get(j).getY2();
                        }

                        double length1 = Math.sqrt(Math.pow(ix1 - ix2, 2) + Math.pow(iy1 - iy2, 2));
                        double length2 = Math.sqrt(Math.pow(ix2 - circleX, 2) + Math.pow(iy2 - circleY, 2));
                        double length3 = Math.sqrt(Math.pow(ix1 - circleX, 2) + Math.pow(iy1 - circleY, 2));
                        double s = (length1 + length2 + length3) / 2;

                        //INFO Area of the triangle formed by the three points
                        double triangleArea = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));
                        //INFO Area of each of the two arcs.
                        double arc1 = 0, arc2 = 0;
                        arc1 = arcArea(r1, ix1, iy1, circleX, circleY, x1, y1);
                        arc2 = arcArea(r1, ix2, iy2, circleX, circleY, x2, y2);

                        overlap2 += triangleArea + arc1 + arc2;
                    }
                }
            }
        }
        overlapWastage += overlap2;
    }

    public int overlap(double r1, double r2, double x1, double x2, double y1, double y2, int circle1, int circle2) {
        double intersectionArea = 0;
        Double r = r1;
        Double R = r2;
        double firstX = x1;
        double secondX = x2;
        double firstY = y1;
        double secondY = y2;
        int smallC = circle1;
        int bigc = circle2;
        Double d = Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(secondY - firstY, 2));
        if (R < r) {
            // swap
            r = r2;
            R = r1;
            smallC = circle2;
            bigc = circle1;
        }
        Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
        Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
        Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

        intersectionArea = part1 + part2 - part3;

        if (!(intersectionArea > 0)) {
            intersectionArea = Math.PI * Math.pow(r, 2) * (360 - (360 - dv.angleList.get(smallC) / 2)) / 360;
        }
        return (int) intersectionArea;
    }

    private double arcArea(double r, double x1, double y1, double x2, double y2, double centerX, double centerY) {
        double a = x1 * (y2 - centerY);
        double b = x2 * (centerY - y1);
        double c = centerX * (y1 - y2);
        double totalArea = Math.PI * r * r;

        double length1 = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        double length2 = Math.sqrt(Math.pow(x2 - centerX, 2) + Math.pow(y2 - centerY, 2));
        double length3 = Math.sqrt(Math.pow(x1 - centerX, 2) + Math.pow(y1 - centerY, 2));
        double s = (length1 + length2 + length3) / 2;
        double area = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));

        //INFO Added the mod totalArea because sometimes, triangleArea gets greater than totalArea.
        //double triangleArea = Math.abs((a + b + c) - 50) % totalArea;

        double triangleArea = area;

        double angle = Math.abs(Math.toDegrees(Math.atan2(x1 - centerX, y1 - centerY) -
                Math.atan2(x2 - centerX, y2 - centerY)));
        double v1x = x1 - centerX;
        double v1y = y1 - centerY;

        //need to normalize:
        double l1 = Math.sqrt(v1x * v1x + v1y * v1y);
        v1x /= l1;
        v1y /= l1;

        double v2x = x2 - centerX;
        double v2y = y2 - centerY;

        //need to normalize:
        double l2 = Math.sqrt(v2x * v2x + v2y * v2y);
        v2x /= l2;
        v2y /= l2;
        double rad = Math.acos(v1x * v2x + v1y * v2y);
        //INFO Changed method of calculating angle.
        double degrees = Math.toDegrees(rad);

        double sectorArea = Math.PI * Math.pow(r, 2) * (degrees / 360d);

        double arcArea = Math.abs(sectorArea - triangleArea);
        return arcArea;
    }

    private void completelyInsideOverlapOutside() {
        double overlap2 = 0;
        double land2 = 0;
        for (int i = 0; i < completelyInside.size(); i++) {
            for (int j = 0; j < outsideIntersecting.size(); j++) {
                int circle1 = completelyInside.get(i);
                int circle2 = outsideIntersecting.get(j).getCirclePos();

                double x1 = dv.sprinkx.get(circle1);
                double x2 = dv.sprinkx.get(circle2);
                double y1 = dv.sprinky.get(circle1);
                double y2 = dv.sprinky.get(circle2);

                double r1 = dv.sprinkr.get(circle1);
                double r2 = dv.sprinkr.get(circle2);
                double angle1 = dv.angleList.get(circle1);
                double angle2 = dv.angleList.get(circle2);

                double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                //README The circles overlap
                if (distance <= r1 + r2) {
                    overlaps.add(circle2);
                    overlaps.add(circle1);
                    //TODO Do calculations for overlap of completely inside circle and outside-intersecting circle
                    if (distance + Math.min(r1, r2) <= Math.max(r1, r2) * 0.98) {
                        double intersectionArea = Math.PI * Math.pow(Math.min(r1, r2), 2) * (360 - (360 - dv.angleList.get((Math.min(r1, r2) == r1 ? circle1 : circle2)) / 2)) / 360;
                        double totalArea = 0;
                        if (completelyInside.contains(circle1))
                            totalArea += (Math.PI * Math.pow(r1, 2)) * ((double) (angle1) / 360);
                        if (completelyInside.contains(circle2))
                            totalArea += (Math.PI * Math.pow(r2, 2)) * ((double) (angle2) / 360);
                        overlap2 += intersectionArea;
                    } else {//README Not circle in circle
                        double intersectionArea = 0;
                        Double r = r1;
                        Double R = r2;
                        double firstX = x1;
                        double secondX = x2;
                        double firstY = y1;
                        double secondY = y2;
                        int smallC = circle1;
                        int bigc = circle2;
                        Double d = Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(secondY - firstY, 2));
                        if (R < r) {
                            // swap
                            r = r2;
                            R = r1;
                            smallC = circle2;
                            bigc = circle1;
                        }
                        Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
                        Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
                        Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

                        intersectionArea = part1 + part2 - part3;

                        //README Maybe intersectionArea is 0 because circle is inside other circle.
                        if (!(intersectionArea > 0)) {
                            intersectionArea = Math.PI * Math.pow(r, 2) * (360 - (360 - dv.angleList.get(smallC) / 2)) / 360;
                        }
                        overlappingCount++;
                        overlap2 += intersectionArea;
                    }
                }
            }
        }
        overlapWastage += overlap2;
    }

    //DONE Test below function
    private void insideCircleOverlap() {
        double overlap2 = 0;
        double land2 = 0;
        //IMPORTANT Ideally below should loop for only completelyInsideCircles and inisideCircles
        // Check calling function for more info
        for (int i = 0; i < insideCircles.size() - 1; i++) {
            for (int j = i + 1; j < insideCircles.size(); j++) {
                //README insideCirles contains the positions
                int circle1 = insideCircles.get(i);
                int circle2 = insideCircles.get(j);

                //INFO if both circles are insideIntersecting, don't calculate because we have another function for that.
                if (insideIntersectingContains(circle1, circle2)) {
                } else {
                    double x1 = dv.sprinkx.get(circle1);
                    double x2 = dv.sprinkx.get(circle2);
                    double y1 = dv.sprinky.get(circle1);
                    double y2 = dv.sprinky.get(circle2);

                    double r1 = dv.sprinkr.get(circle1);
                    double r2 = dv.sprinkr.get(circle2);
                    double angle1 = dv.angleList.get(circle1);
                    double angle2 = dv.angleList.get(circle2);

                    double distance = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                    //README The circles overlap
                    if (distance <= r1 + r2) {
                        //README Circle in circle
                        overlaps.add(circle2);
                        overlaps.add(circle1);
                        if (distance + Math.min(r1, r2) <= Math.max(r1, r2) * 1.08) {
                            double intersectionArea = Math.PI * Math.pow(Math.min(r1, r2), 2) * dv.angleList.get((Math.min(r1, r2) == r1 ? circle1 : circle2)) / 360;
                            double totalArea = 0;
                            if (completelyInside.contains(circle1))
                                totalArea += (Math.PI * Math.pow(r1, 2)) * ((double) (angle1) / 360);
                            if (completelyInside.contains(circle2))
                                totalArea += (Math.PI * Math.pow(r2, 2)) * ((double) (angle2) / 360);
                        } else {//README Not circle in circle
                            double intersectionArea = 0;
                            Double r = r1;
                            Double R = r2;
                            double firstX = x1;
                            double secondX = x2;
                            double firstY = y1;
                            double secondY = y2;
                            int smallC = circle1;
                            Double d = Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(secondY - firstY, 2));
                            if (R < r) {
                                r = r2;
                                R = r1;
                                smallC = circle2;
                            }
                            Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
                            Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
                            Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

                            intersectionArea = part1 + part2 - part3;

                            if (!(intersectionArea > 0)) {
                                intersectionArea = Math.PI * Math.pow(r, 2) * dv.angleList.get(smallC) / 360;
                            }

                            overlap2 += intersectionArea;
                            overlappingCount++;
                        }
                    }
                }
            }
        }

        double totalArea = 0;
        //README Calculating completely inside circle area outside the loop so no duplicates.
        for (int i : completelyInside)
            totalArea += Math.PI * Math.pow(dv.sprinkr.get(i), 2) * dv.angleList.get(i) / 360;

        overlapWastage += overlap2;
        totalInsideArea += totalArea;
    }

    private boolean insideIntersectingContains(int circle1, int circle2) {
        boolean c1 = false;
        boolean c2 = false;
        for (OverflowInfo info : insideIntersecting) {
            if (info.getCirclePos() == circle1)
                c1 = true;
            if (info.getCirclePos() == circle2)
                c2 = true;
        }
        if (c1 && c2)
            return true;
        return false;
    }

    public void calculateOverflowWastage() {
        ArrayList<Integer> used = new ArrayList<>();
        ArrayList<Integer> fullCircleUsed = new ArrayList<>();
        double previousWastage = 0;
        double initialWastage = overFlowWastage;
        int counteR =0;
        for (OverflowInfo overflowInfo : overflowInfo) {
            if (!used.contains(overflowInfo.getCirclePos())) {
                double x1 = overflowInfo.getX1();
                double x2 = overflowInfo.getX2();
                double y1 = overflowInfo.getY1();
                double y2 = overflowInfo.getY2();

                double centerX = overflowInfo.getCircleX();
                double centerY = overflowInfo.getCircleY();

                double totalArea = Math.PI * overflowInfo.getRadius() * overflowInfo.getRadius();

                //INFO Added the mod totalArea because sometimes, triangleArea gets greater than totalArea.

                double length1 = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                double length2 = Math.sqrt(Math.pow(x2 - centerX, 2) + Math.pow(y2 - centerY, 2));
                double length3 = Math.sqrt(Math.pow(x1 - centerX, 2) + Math.pow(y1 - centerY, 2));
                double s = (length1 + length2 + length3) / 2;
                double triangleArea = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));

                double v1x = x1 - centerX;
                double v1y = y1 - centerY;

                double l1 = Math.sqrt(v1x * v1x + v1y * v1y);
                v1x /= l1;
                v1y /= l1;

                double v2x = x2 - centerX;
                double v2y = y2 - centerY;

                double l2 = Math.sqrt(v2x * v2x + v2y * v2y);
                v2x /= l2;
                v2y /= l2;
                double rad = Math.acos(v1x * v2x + v1y * v2y);
                //INFO Changed method of calculating angle.
                double degrees = Math.toDegrees(rad);

                double sectorArea = Math.PI * Math.pow(overflowInfo.getRadius(), 2) * (degrees / 360d);

                boolean outside = outside(centerX, centerY);

                double wastedArea = sectorArea - triangleArea;
                //DONE Check if below works
                if (wastedArea < 0) wastedArea = Math.abs(wastedArea) / 1.95;

                if (outside)
                    wastedArea = (totalArea) - wastedArea;
                if (dv.angleList.get(overflowInfo.getCirclePos()) < 316) {
                    double sprinklerAngle = dv.angleList.get(overflowInfo.getCirclePos());
                    totalArea *= sprinklerAngle / 360;
                    wastedArea = totalArea * 0.05;
                    used.add(overflowInfo.getCirclePos());
                }
                previousWastage = overFlowWastage - initialWastage;
                overFlowWastage += wastedArea;

                if (fullCircleUsed.contains(overflowInfo.getCirclePos())) {
                    double toAdd = previousWastage * .24 + wastedArea * .21;
                    overFlowWastage += toAdd;
                    if (overFlowWastage > totalOverflowArea)
                        overFlowWastage -= toAdd * .9;

                }
                if (dv.angleList.get(overflowInfo.getCirclePos()) > 316)
                    fullCircleUsed.add(overflowInfo.getCirclePos());
                counteR++;
            }
        }
    }

    //README Function determines if sprinkler center is outside, not full sprinkler.
    private boolean outside(double centerX, double centerY) {
        int counter = 0;
        for (int b = 0; b < dv.xlist.size(); b++) {
            double startX = dv.xlist.get(b);
            double startY = dv.ylist.get(b);
            double endX = dv.xlist.get((b + 1 > dv.xlist.size() - 1 ? 0 : b + 1));
            double endY = dv.ylist.get((b + 1 > dv.ylist.size() - 1 ? 0 : b + 1));
            if (centerY >= Math.min(startY, endY) && centerY <= Math.max(startY, endY)) {
                double inverseSlope = (startX - endX) / (startY - endY);
                double expectedX = (-1 * inverseSlope * (startY - centerY)) + startX;
                if (centerX < expectedX) {
                    counter++;
                }
            }
        }
        return counter % 2 == 0;
    }


    HashMap<String, Integer> hm;

    ArrayList<Double> singleX = new ArrayList<>();
    ArrayList<Double> singleY = new ArrayList<>();
    ArrayList<Double> singleR = new ArrayList<>();
    ArrayList<Integer> singleP = new ArrayList<>();
    ArrayList<Integer> singleAngle = new ArrayList<>();

    private void getIndividualCircles2() {
        singleX.clear();
        singleY.clear();
        singleR.clear();
        singleP.clear();
        if (dv.sprinkx.size() == 1) {
            singleX.add((double) dv.sprinkx.get(0));
            singleY.add((double) dv.sprinky.get(0));
            singleR.add((double) dv.sprinkr.get(0));
            singleAngle.add(dv.angleList.get(0));
            singleP.add(0);
        } else {
            for (int a = 0; a < candidates.size(); a++) {
                int i = candidates.get(a);
                double x = dv.sprinkx.get(i);
                double y = dv.sprinky.get(i);
                double r = dv.sprinkr.get(i);

                boolean good = true;
                for (int j = 0; j < candidates.size(); j++) {
                    double tempX = dv.sprinkx.get(j);
                    double tempY = dv.sprinky.get(j);
                    double tempR = dv.sprinkr.get(j);

                    if (j != i) {
                        double distance = Math.sqrt(Math.pow(tempX - x, 2) + Math.pow(tempY - y, 2));
                        if (distance <= tempR + r) {
                            good = false;
                            break;
                        }
                    }
                }

                if (good) {
                    singleX.add(x);
                    singleY.add(y);
                    singleR.add(r);
                    singleP.add(i);
                    singleAngle.add(dv.angleList.get(i));
                }

            }
        }
    }

    private static void makeToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

}
