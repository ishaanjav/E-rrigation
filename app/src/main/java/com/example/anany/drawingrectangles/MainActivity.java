package com.example.anany.drawingrectangles;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button button, btnUndo;
    static RelativeLayout rlDvHolder;
    static DrawingView dv;
    TextView display;
    TextView real;
    Button polygon;
    SeekBar radius;
    static RelativeLayout background;
    public static Context context;
    public EditText length;
    static RelativeLayout container;
    public TextView textview, ft;

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

        //README INFO to get a Textview object in Canvas without any errors, you have to use
        //  relativelayout that it is in then .findViewById();
        length = background.findViewById(R.id.length);
        //handleSideLength();

        btnUndo = findViewById(R.id.btnUndo);
        setButtonClick();
        setRadiusBar();
        dv.sradius = radius.getProgress();
        container = background.findViewById(R.id.container);
        lengthUpdater();

        //radius.setVisibility(View.VISIBLE);
        //real.setVisibility(View.VISIBLE);
        radius.getProgressDrawable().setColorFilter(Color.parseColor("#70c48c"), PorterDuff.Mode.SRC_IN);
        radius.getThumb().setColorFilter(Color.parseColor("#3abd66"), PorterDuff.Mode.SRC_IN);
        //polygon.setVisibility(View.INVISIBLE);
        context = getApplicationContext();
        //askForLength(true, 2);

        //handleResults(3, 3, 3, 3, 100);
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
                //Log.wtf("* Inside: ", "inside here " + dv.xlist.size());
                String r = length.getText().toString();
                if (dv.currentMode != DrawingView.Mode.drawc && dv.currentMode != DrawingView.Mode.resetc) {
                    if (r == null) {
                        timesR = 0;
                    } else if (previous.length() > 0 && dv.xlist.size() > 2 && !leaveAlone) {
                        makeToast("To change the length, please reset the plot");
                        /*if (done) {
                            length.setText(previous);
                            done = false;
                        }
                        if (timesR == 0)
                            done = true;*/
                        //FIXME, leavealone needs to be true. It is stalling here.
                    /*removeAllLengths();
                    //TODO Redo all stuff textview lengths
                    int numVal = Integer.parseInt(r);
                    if (numVal < 2) {
                        makeToast("Please make your side length larger");
                    } else {
                        dv.length = numVal;
                        if (dv.pastMode == DrawingView.PastMode.CIRCLE) {
                            dv.ratio = dv.radius / numVal;
                        } else {
                            dv.ratio = (double) numVal / (double) (Math.hypot(Math.abs(dv.xlist.get(0) - dv.xlist.get(1)),
                                    Math.abs(dv.ylist.get(0) - dv.ylist.get(1))));
                            Log.wtf("Side Length Calculations", "Hypotenuse - " + Math.hypot(Math.abs(dv.xlist.get(0) - dv.xlist.get(1)),
                                    Math.abs(dv.ylist.get(0) - dv.ylist.get(1))));
                        }
                        //createAllTextViews();
                        Log.wtf("*  INFORMATION ON RATIO: ", "Ratio: " + dv.ratio + "  Length: " + dv.length);
                    }*/
                    } else if (dv.xlist.size() > 1) {
                        timesR = 0;
                        Log.wtf("* Inside: ", "inside here deeper");
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
                                    dv.ratio = dv.radius / numVal;
                                } else {
                                    dv.ratio = (double) numVal / (double) (Math.hypot(Math.abs(dv.xlist.get(0) - dv.xlist.get(1)),
                                            Math.abs(dv.ylist.get(0) - dv.ylist.get(1))));
                                    Log.wtf("Side Length Calculations", "Hypotenuse - " + Math.hypot(Math.abs(dv.xlist.get(0) - dv.xlist.get(1)),
                                            Math.abs(dv.ylist.get(0) - dv.ylist.get(1))));
                                }
                                Log.wtf("*  INFORMATION ON RATIO: ", "Ratio: " + dv.ratio + "  Length: " + dv.length);
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
                            //TODO Have to also set the value of dv.ratio to get
                            // the ratio between their length and pixel length;
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
        for (int i = 0; i < dv.xlist.size() - 1; i++) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                    ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = (dv.xlist.get(i) + dv.xlist.get(i + 1)) / 2;
            params.topMargin = (dv.ylist.get(i) + dv.ylist.get(i + 1)) / 2;

            TextView textView = new TextView(context);
            textView.setText("" + (int) (Math.hypot(Math.abs(dv.xlist.get(i) - dv.xlist.get(i + 1)),
                    Math.abs(dv.ylist.get(i) - dv.ylist.get(i + 1))) * saveRadius));
            textView.setId(dv.idCounter);
            textView.setLayoutParams(params);
            //makeToast("Making the text");
            rlDvHolder.addView(textView);
            dv.idCounter++;

            //DONE HAVE to deal with adding textview from last to first. and removing from previous touch.

            //Log.wtf("* Location: ", dv.xlist.get(posCount) + " " + xlist.get(posCount + 1)
            //       + " " + dv.ylist.get(posCount) + " " + dv.ylist.get(posCount + 1));

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
                    Log.wtf("Side Length Calculations", "Hypotenuse - " + Math.hypot(Math.abs(dv.xlist.get(0) - dv.xlist.get(1)),
                            Math.abs(dv.ylist.get(0) - dv.ylist.get(1))));
                }
                return true;
            }
        }

    }

    AlertDialog.Builder loading;
    AlertDialog created;

    public void showLoading() {
        loading = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogCoordinate = inflater.inflate(R.layout.loading, null);
        loading.setCancelable(false);
        loading.setView(dialogCoordinate);

        created = loading.create();
        created.show();
        makeToast("show loading called");
        Log.wtf("* Alert Dialog", "Showing Loading. showLoading() called");
    }

    public void hideLoading() {
        created.hide();
        created.cancel();
    }

    //README this function asks user for length of side after user has plotted 1 side.
    private void askForLength(boolean plot, int a) {
        /*new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();*/
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
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

        /*AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Write your message here.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();*/
    }

    private static void askForLength(boolean plot) {
        /*final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.specify_length);

        TextView text = (TextView) dialog.findViewById(R.id.message);

        if (plot)
            text.setText("You just drew one side of the land area. Please specify the approximate " +
                    "length of that first side in feet.");
        else
            text.setText("You just chose to draw a circular area of land. " +
                    "Please specify its radius in feet.");

        final EditText input = dialog.findViewById(R.id.length);
        *//*TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                input.setText(editable.toString() + "ft");
            }
        };
        input.addTextChangedListener(textWatcher);*//*

        Button undo = (Button) dialog.findViewById(R.id.undo);
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog.cancel();
                //README INFO: In order to call dv.undo() below, dv was made static.
                // If any issues arise, try making dv not static.
                dv.undo();
            }
        });

        Button done = (Button) dialog.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sInput = input.getText().toString();
                if (sInput != null) {
                    int num = Integer.parseInt(sInput);
                    if(num > 0){
                        dv.length = num;
                    }else{
                       // makeToast("Please specify a length greater than 0 feet.");
                    }
                }else{
                    //makeToast("Please specify the side length in feet.");
                }
            }
        });

        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();*/
    }

    public Bitmap takeScreenShot(View view) {
        // configuramos para que la view almacene la cache en una imagen
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        view.buildDrawingCache();

        if (view.getDrawingCache() == null) return null; // Verificamos antes de que no sea null

        // utilizamos esa cache, para crear el bitmap que tendra la imagen de la view actual
        Bitmap snapshot = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();

        return snapshot;
    }

    public File takeScreenShot2() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        View view = dv;
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            view.setDrawingCacheEnabled(true);
            view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
            view.buildDrawingCache();

            if (view.getDrawingCache() == null) return null; // Verificamos antes de que no sea null

            // utilizamos esa cache, para crear el bitmap que tendra la imagen de la view actual
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            view.destroyDrawingCache();

            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            //openScreenshot(imageFile);
            return imageFile;
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
        return null;
    }

    private void setRadiusBar() {
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //INFO this is when the user lets go of the slider
                //makeToast("Invalidating");
                dv.sradius = seekBar.getProgress();
                int max = seekBar.getMax();
                int min = seekBar.getMin();
                int setToI = (int) ((double) ((double) dv.screenW / 1000) * Math.pow(seekBar.getProgress() / 9, 2));
                //double setTo = ((double) ((double) dv.screenW / 1000) * Math.pow(seekBar.getProgress() / 9, 2));
                makeToast("Radius: " + (String.format("%1$,.1f", (setToI * dv.ratio))) + " feet.");
                Log.wtf("* Sprinkler Radius Info: ", "Pixel radius (setTo): " + setToI + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"
                        + "Radius: " + (String.format("%1$,.1f", (setToI * dv.ratio))) + " feet");
                //makeToast("Updating sradius: " + dv.sradius);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                dv.sradius = progress;
                if (dv.currentMode == DrawingView.Mode.drawc) {
                    makeToast("ATTENTION! Enter the length of the circle's radius in feet.");
                    dv.radius = progress;
                }

                dv.invalidate();
            }
        });
    }

    double saveRadius = 0;

    private void setButtonClick() {
        polygon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Deal with making a polygon.
                if (dv.xlist.size() > 2 && !rectangle) {
                    rectangle = true;
                    //dv.currentMode = DrawingView.Mode.POLYGON;
                    //ArrayList<Integer> newX = new ArrayList<>();
                    //ArrayList<Integer> newY = new ArrayList<>();
                    int tL = 1000, bR = 0;
                    int tLy = 1000, bRy = 0;
                    for (int i = 0; i < dv.xlist.size(); i++) {
                        if (dv.xlist.get(i) < tL) tL = dv.xlist.get(i);
                        if (dv.ylist.get(i) < tLy) tLy = dv.ylist.get(i);

                        if (dv.xlist.get(i) > bR) bR = dv.xlist.get(i);
                        if (dv.ylist.get(i) > bRy) bRy = dv.ylist.get(i);
                    }
                    dv.resetTouchPoints();

                    dv.xlist.add(tL);
                    dv.xlist.add(bR);
                    dv.xlist.add(bR);
                    dv.xlist.add(tL);
                    dv.ylist.add(tLy);
                    dv.ylist.add(tLy);
                    dv.ylist.add(bRy);
                    dv.ylist.add(bRy);

                    dv.xs.add(tL);
                    dv.xs.add(bR);
                    dv.xs.add(bR);
                    dv.xs.add(tL);
                    dv.ys.add(tLy);
                    dv.ys.add(tLy);
                    dv.ys.add(bRy);
                    dv.ys.add(bRy);

                    removeAllLengths(false);
                    dv.invalidate();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeToast("MODE: " + dv.currentMode);
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

                    //INFO REMOVE THE LATEST TEXTVIEW
                    /*if (dv.idCounter != 0) {
                        dv.idCounter--;
                        TextView t = (TextView) rlDvHolder.findViewById(dv.idCounter);

                        t.setVisibility(View.GONE);
                    }
                    //INFO Remove the connection between last point and first point
                    if (dv.specialCounter != Integer.MAX_VALUE) {
                        dv.specialCounter++;
                        rlDvHolder.findViewById(++dv.specialCounter).setVisibility(View.GONE);
                    }*/
                    removeAllLengths(true);

                    Log.wtf("* Text ID Info:", dv.idCounter + " " + dv.specialCounter);
                } else {
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

        //makeToast("HERE");
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

    public static class DrawingView extends View {
        private static final float TOUCH_TOLERANCE = 4;
        public int width;
        public int height;
        public int radius = -5;
        public int sradius = -5;
        List<Integer> xlist = new ArrayList<>();
        List<Integer> ylist = new ArrayList<>();
        List<Integer> sprinkx = new ArrayList<>();
        List<Integer> sprinky = new ArrayList<>();
        List<Integer> sprinkr = new ArrayList<>();
        List<Integer> xs = new ArrayList<>();
        List<Integer> ys = new ArrayList<>();
        //TODO Comment out below if you want to use a dotplot
        //  Mode currentMode = Mode.PLOT;
        Mode currentMode = Mode.DOTPLOT;
        PastMode pastMode = PastMode.DRAW;
        Context context;
        Paint linePaint;
        Canvas cd;
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
            invalidate();
        }

        public void resetTouchPoints() {
            xlist = new ArrayList<>();
            ylist = new ArrayList<>();
            xs = new ArrayList<>();
            ys = new ArrayList<>();
            Log.wtf("*RESET RESET", "Touch points were reset");
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
                    plotSprinklers(canvas);
                    //makeToast("Plotting sprinklers");
                    //mmakeToast("Was Circle? : " + wasCircle);
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

                    break;
                case sreset:
                    currentMode = Mode.splot;
                    resetSprinklers();
                    //makeToast("Resetting sprinklers.");
                    break;
                case DOTPLOT:
                    showDots(canvas);
                    //Log.wtf("*DOTPLOT", "DOT Plot being called");
                    drawLine(canvas);
                    if (xlist.size() == 2) {
                        //TODO Make an ALert Dialog asking for the length of the side that has been drawn.
                        //NOTES Make it non-dimissible, but provide an Undo and a done button. for undo,
                        //  call dv.undo();
                        makeToast("ATTENTION! Enter the length of this first side in feet.");
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
                    Log.wtf("*RESET RESET RESET RESET RESET", "Reset was called. Reset screen.");
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
                       //Log.wtf("*Coordinates---------------------------", "Finger Down: " + x + " " + y);
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
                      // Log.wtf("*Coordinates---------------------------", "Finger Up: " + x + " " + y);
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
                            //INFO If you comment out below, then it does not draw the dots. Only when you press a button it draws.
                            // I think this is because when button pressed, it calls invalidate(). invalidate() leads to onDraw()
                            // That's why try writing your customDrawLine() code in onDraw().
                            invalidate();
                        }
                    } else {
                        makeToast("You have exceeded the limit on the number of sides.");
                    }
                }
            } else if (currentMode == Mode.splot) {
                //TODO Deal with plotting sprinklers.

                boolean duplicate = checkForDuplicate(x, y, true);
                if (!duplicate) {
                    sprinkx.add((int) x);
                    sprinky.add((int) y);
                    //OLD code below just does it based on number of pixels.
                    //  On higher ppi phones, sprinkler appears very small.
                    sprinkr.add((int) ((double) ((double) screenW / 1000) * Math.pow(sradius / 9, 2)));

                    //sprinkr.add((int) Math.pow(sradius / 9, 2));
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
            if (xlist.size() > 2) {
                posCount = xlist.size() - 2;
                if (xlist.size() == 3) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                            ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = (xlist.get(0) + xlist.get(1)) / 2;
                    params.topMargin = (ylist.get(0) + ylist.get(1)) / 2;

                    TextView textView = new TextView(context);
                    textView.setText("" + (int) (Math.hypot(Math.abs(xlist.get(0) - xlist.get(1)),
                            Math.abs(ylist.get(0) - ylist.get(1))) * ratio));
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
                textView.setText("" + (int) (Math.hypot(Math.abs(xlist.get(posCount) - xlist.get(posCount + 1)),
                        Math.abs(ylist.get(posCount) - ylist.get(posCount + 1))) * ratio));
                textView.setId(idCounter);
                textView.setLayoutParams(params);
                //makeToast("Making the text");
                rlDvHolder.addView(textView);

                //DONE HAVE to deal with adding textview from last to first. and removing from previous touch.

                //Log.wtf("* Location: ", xlist.get(posCount) + " " + xlist.get(posCount + 1)
                //       + " " + ylist.get(posCount) + " " + ylist.get(posCount + 1));

                Log.wtf("*  Length INFO", "Ratio: " + ratio + "  Length: " + length);
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
                last.setText("" + (int) (Math.hypot(Math.abs(xlist.get(xlist.size() - 1) - xlist.get(0)),
                        Math.abs(ylist.get(ylist.size() - 1) - ylist.get(0))) * ratio));
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
                //makeToast("Display is good: " + (display == null));
                //display.setText(s);
            } else if (xlist.size() == 2) {
                canvas.drawLine(xlist.get(0), ylist.get(0), xlist.get(1), ylist.get(1), linePaint);
            }
        }


        private void plotSprinklers(Canvas canvas) {
            //Log.wtf("*Plotting Sprinklers", "Number of Sprinklers: " + sprinkx.size());
            if (sprinkx.size() > 0) {
                for (int i = 0; i < sprinkx.size(); i++) {
                    //Log.wtf("*Sprinkler Location: ", sprinkx.get(i) + " " + sprinky.get(i) + " " + sprinkr.get(i));
                    canvas.drawCircle(sprinkx.get(i), sprinky.get(i), sprinkr.get(i), sprinklerC);
                }
            }
        }

        private void drawCustomLine(Canvas canvas, int x1, int y1, int x2, int y2) {
            //try calling invalidate()
            //makeToast("Being called");
            Log.wtf("*Draw Custom Line being called", "Draw Custom Line being called");
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
            //Log.wtf("*Showing dots", "X List size: " + xlist.size());
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
                return Math.abs(area / 2.0);
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
                    dv.currentMode = DrawingView.Mode.splot;
                }
                return true;


            case R.id.calculate:
                //INFO The purpose of this is to display the loading Alert Dialog.
                Bitmap tr = takeScreenShot(dv);
                showLoading();

                //INFO Wait a bit so that the Dialog is showing, then do the calculations.
                Handler h = new Handler();
                final Bitmap[] bmp = {tr};
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Bitmap bmp = takeScreenShot(dv);
               /*try (FileOutputStream out = new FileOutputStream("test.png")) {
                   bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
               } catch (Exception e) {
                   makeToast(e.toString());
                   Log.wtf("*ERROR WITH SAVING IMAGE: ", e.toString());
               }
               File root = Environment.getExternalStorageDirectory();
               Bitmap open = BitmapFactory.decodeFile(root + "/images/test.png");
               Intent opener = new Intent();
               opener.setAction(Intent.ACTION_VIEW);
               File temp = new File("sdcard/Images/test.png");
               opener.setDataAndType(Uri.parse("/test.png"), "image/*");
               startActivity(opener);*/


                        //DONE Before counting pixel colors, try compressing PNG to 50% quality or less
                        //  that way there are fewer colors for sprinkler.
                        //TODO When calculating area of overlapping regions, actually calculate proper
                        //  areas of 1 sprinkler and 2 sprinklers with formula.
                        //  for the rest, then you can use pixels

                        //takeScreenShot2();
                        //makeToast("Bitmap Info: " + bmp.getWidth() + " " + bmp.getHeight());
                        for (int i = 0; i < dv.sprinkx.size(); i++) {
                            Log.wtf("*  Sprinkler Location ", "X: " + dv.sprinkx.get(i) + "  Y: " + dv.sprinky.get(i) + "  R: " + dv.sprinkr.get(i));
                        }
                        //Log.wtf("*BITMAP DIMENSIONS --------------------", "Width: " + bmp.getWidth() + " Height: " + bmp.getHeight());
                        getIndividualCircles();
                        //Log.wtf("*Done getting circles", " DONE GETTING CIRCLES");
                        //README Make Bitmap smaller.
                        bmp[0] = Bitmap.createScaledBitmap(bmp[0], (int) (bmp[0].getWidth() * 1), (int) (bmp[0].getHeight() * 1), true);
                        iterateThroughPixels(bmp[0]);
                        //askForLength(true, 3);

                        //File file = takeScreenShot2();
                        //iterateThroughPixels(file);
                    }
                }, 300);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    HashMap<String, Integer> hm;

    ArrayList<Double> singleX = new ArrayList<>();
    ArrayList<Double> singleY = new ArrayList<>();
    ArrayList<Double> singleR = new ArrayList<>();
    ArrayList<Integer> singleP = new ArrayList<>();

    private void getIndividualCircles() {
        singleX.clear();
        singleY.clear();
        singleR.clear();
        singleP.clear();
        //Log.wtf("*Still getting circles", " Still GETTING CIRCLES");

        if (dv.sprinkx.size() == 1) {
            singleX.add((double) dv.sprinkx.get(0));
            singleY.add((double) dv.sprinky.get(0));
            singleR.add((double) dv.sprinkr.get(0));
            singleP.add(0);
        } else {
            for (int i = 0; i < dv.sprinkx.size(); i++) {
                double x = dv.sprinkx.get(i);
                double y = dv.sprinky.get(i);
                double r = dv.sprinkr.get(i);

                boolean good = true;
                for (int j = 0; j < dv.sprinkx.size(); j++) {
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
                }

            }
        }
    }


    private String convertTo16(int a) {
        String t = Integer.toString(
                Integer.parseInt(a + "", 10),
                16);
        if (t.length() == 1)
            return "0" + t;
        return t;
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    ArrayList<ArrayList<String>> colors = new ArrayList<>();
    ArrayList<String> pos2 = new ArrayList<String>() {{
        add("4dc4ff");
        add("54c4ff");
        add("55caff");
    }};

    ArrayList<String> pos3 = new ArrayList<String>() {{
        add("58c2ff");
        add("54c3ff");
        add("54c2ff");
    }};

    ArrayList<String> pos4 = new ArrayList<String>() {{
        add("49c8ff");
        add("51c0ff");
        add("51b9ff");
        add("4cc6ff");
        add("51c1ff");
    }};

    ArrayList<String> pos5 = new ArrayList<String>() {{
        add("54c5ff");
        add("54c6ff");
        add("54c7ff");
    }};

    //FUTURE FILES - Calculating area outside of polygon
    //INFO 11/29/19 to calculate the amount of water outside the polgyon, use a technique called ray-casting
    // Basically, before iterating through pixels, create an ArrayList for the equation of each polygon side,
    // storing m and b as well as both pairs of coordinates.
    //  Then, when iterating through pixel, if is not white, then get the y coordinate. Then, for each line side,
    // use the equation to calculate the x given the y. If the x is in between the xs of the line's coordinates
    // add 1 to a counter variable.
    // In the end, if counter % 2 == 0, then that means it is not in the polygon because it is even,
    // If odd, that means it is located inside the polygon.
    // Use that to add to the area variable to count that as also being wasted water.

    //FUTURE FILES - AlertDialog for getting side length
    //INFO As of 11/29/19 for some reason the AlertDialog is finally working now. So what you can do is make an AlertDialog
    //  when they place down 2 sprinklers and then ask them to enter in the length of that side.
    //  This approach is much nicer than having an always-present EditText.
    //  Use the Alert Dialog to ask for the side length.


    //FUTURE FIles
    //INFO Maybe have a feature where for the results it displays the bad sprinkler placements by making them pop out.
    //  For example, when iterating through bitmap pixels, if the sprinkler is fine, then you edit the color and make it
    //  greyed out, otherwise you leave it the same, that way they can see that those placements were bad.


    //FUTURE FILES Idea for accurately calculating area:
    //  Have an ArrayList of ArrayList<OBJECT> where OBJECT contains a position (int), x (int), y (int)
    //  Using math formula, for each circle, go through all circles and see whether it intersects, storing
    //  the x,y coordinate and the position of the intersecting circle from original list with all sprinklers.
    //  ------------------------------
    //  Now in this ArrayList of ArrayList, go through each position and check if that ArrayList length is 2.
    //  (If 2 that means that it was intersected by 1 circle.) Then, go and calculate intersection area using
    //  the x, y, and then position to get the radius.
    // ----
    // Do this for all and add themp up. Then divide by 2 for duplicates ---> Result
    //README NOTES INFO Consideration for this is that it does not account for fact that 1 of the 2 circles
    // may have more than 2 intersection points and may have a 3-way intersection with 2 other circles
    // in addition to that 1 circle that was used to calculate.
    //NOTES I have no idea how to calculate the rest of the intersections (3-way+).
    //  Even with pixel counting, how will I avoid counting double-intersections
    //README INFO Answer to above question is avoid counting the intersecting pairs by having an ArrayList of every circle
    //  (x, y, r) that had ONLY 2 intersections. Then, check if point is inside intersection. If it is, don't count.
    //  NOW YOU would have to check in case of circle that has such a pair, but also intersects other circles, how to
    // not count regions of only that circle where it is not sharing any other regions.

    private void iterateThroughPixels(Bitmap bmp) {
        //TODO Use singleX... arraylists and in for for, check if point is inside circle.
        //  IF inside circle, then
        hm = new HashMap<>();
        ArrayList<String> notGood = new ArrayList<>();

        //README area is the amount of wasted water.
        int area = 0;
        //README Below variable is for sprinkler that intersects with others, but pixel is region of only 1 sprinkler.
        int overlappingButOnly1SprinklerRegion = 0;
        int tracker = 0;
        int non = 0, counter = 0;

        makeToast("SINGLE SIZE: " + singleR.size() + "\tSprinkler Size: " + dv.sprinkx.size());
        Log.wtf("* Sprinkler INFO: ", "SINGLE SIZE: " + singleR.size() + "\tSprinkler Size: " + dv.sprinkx.size());
        if (singleR.size() == dv.sprinkx.size()) {
            //Do nothing.
        } else if (singleR.size() + 2 == dv.sprinkx.size()) {
//Calculate the intersection area of 2 circles.
            int[] temp = calculateWastage(true);
            area += temp[0];
            //INFO Increase the area of non-individual sprinklers.
            //counter += temp[1] - area;
            counter = temp[1];

            makeToast("SINGLE SIZE: " + singleR.size() + "\tSprinkler Size: " + dv.sprinkx.size());
            Log.wtf("*  Calculations 2", "WASTED: " + area + "  TOTAL WATER AREA: " + counter);
        } else if (singleR.size() + 3 == dv.sprinkx.size()) {
            //Just double the intersection area of any 2 circles.
            int temp[] = calculateWastage(false);
            area += temp[0] * 2;
            //INFO Increase the area of non-individual sprinklers.
            counter += temp[1];
            //counter += temp[1] - area;
            makeToast("SINGLE SIZE: " + singleR.size() + "\tSprinkler Size: " + dv.sprinkx.size());
            Log.wtf("*  Calculations 3", "WASTED: " + area + "  TOTAL WATER AREA: " + counter);
        } else {
            makeToast("SINGLE SIZE: " + singleR.size() + "\tSprinkler Size: " + dv.sprinkx.size());
            for (int y = 0; y < bmp.getHeight(); y++) {
                for (int x = 0; x < bmp.getWidth(); x++) {
                    int pixel = bmp.getPixel(x, y);

                    int redValue = Color.red(pixel);
                    int blueValue = Color.blue(pixel);
                    int greenValue = Color.green(pixel);

                    String red = "", blue = "", green = "";
                    //red += (redValue < 10) ? ("0" + redValue) : (redValue);
                    //blue += (blueValue < 10) ? ("0" + redValue) : (redValue);
                    //green += (greenValue < 10) ? ("0" + redValue) : (redValue);


                    String pix = red + "," + blue + "," + green;
                    pix = redValue + "," + blueValue + "," + greenValue;
                    pix = convertTo16(redValue) + convertTo16(greenValue) + convertTo16(blueValue);

                    boolean good = true;

                    for (int i = 0; i < singleR.size(); i++) {
                        double distance = Math.sqrt(Math.pow(x - singleX.get(i), 2) + Math.pow(y - singleY.get(i), 2));
                        if ((int) distance <= singleR.get(i)) {
                            good = false;
                            break;
                        }
                    }

                    //INFO if good: pixel is not in sprinkler which means it is in an overlapping region or no sprinkler.
                    if (good) {
                        //TODO Since pixel is not in the sprinkler, you have to do the pixel count stuff.
                        //TRY Checking (for pos 2 and maybe pos 3) if it is within a range of colors using compareTo
                        //  Do not just use discrete values. Check in a range to make sure.

                        if (("4b".compareTo(pix) < 0 && "51".compareTo(pix) > 0))
                            overlappingButOnly1SprinklerRegion++;
                        else if ("51".compareTo(pix) < 0 && "52".compareTo(pix) > 0) {
                            area += 4;
                            counter += 4;
                        } else if ("54".compareTo(pix) < 0 && "57".compareTo(pix) > 0) {
                            overlappingButOnly1SprinklerRegion++;
                        } else if ("52".compareTo(pix) < 0 && "54".compareTo(pix) > 0) {
                            area += 3;
                            counter += 3;
                        } else if ("57".compareTo(pix) < 0 && "61".compareTo(pix) > 0) {
                            area += 2;
                            counter += 2;
                        } else if ("61".compareTo(pix) < 0 && "65".compareTo(pix) > 0) {
                            area += 5;
                            counter += 5;
                        }

                    /*if (pos2.contains(pix))
                        area += 1;
                    else if (pos3.contains(pix))
                        area += 2;
                    else if (pos4.contains(pix))
                        area += 3;
                    else if (pos5.contains(pix))
                        area += 4;
                    else {
                        //INFO It was none of the above. This means that 25% 2 sprinklers, 25% 3 sprinklers, 50% 6+.
                        //FIXME Change above weights since it overstimates.
                        //TRY Checking if pix is in a certain range of colors by using compareTo (that way it doesn't get whites)
                        // , then add water wastage amount using new weights.
                        //NOTES
                        //  maybe ignore things starting with 4d-53 and treat it like it was in only one sprinkler.
                        //  Maybe just go up to 60 as max or something.
                        //  Then come up with 2/3 other intervals. Give the smallest ones weights of 2 and 3, with 2 being bigger.
                        //  Then in else case, just have it be 6;
                        if (!pix.equals("000000") && !pix.equals("369646")) {
                            tracker++;

                           *//* if ("4b".compareTo(pix) < 0 && "48".compareTo(pix) > 0)
                                overlappingButOnly1SprinklerRegion++;
                            else if ("49a".compareTo(pix) < 0 && "51d".compareTo(pix) > 0)
                                area += 4;*//*
                           if ("4b".compareTo(pix) < 0 && "51d".compareTo(pix) > 0)
                                area += 4;
                            else if ("54c5ff".compareTo(pix) < 0 && "54c8".compareTo(pix) > 0)
                                area += 5;
                            else if ("6".compareTo(pix) < 0)
                                area += 6;
                            else
                                area+=0;

                            //OLD code to try to count whether 6 sprinklers are overlapping.
                            *//*if (tracker % 4 == 1) area += 1;
                            else if (tracker % 4 == 2) area += 2;
                            else area += 6;*//*
                        }


                }*/
                        hm.put(pix, hm.getOrDefault(pix, 0) + 1);
                    } else {
                        //INFO If !good: pixel is inside sprinkler.
                        //  Area already calculated in non.
                        //notGood.add(pix + ":---  " + x + " " + y);
                    }
                    //hm.put(pix, hm.getOrDefault(pix, 0) + 1);

                }
            }
        }
        if (dv.sprinkx.size() == singleX.size())
            area = 0;

        //Log.wtf("*ITERATION STATUS:", "Done iterating through pixels");
        // makeToast(hm.toString());
        //makeToast("SIZE: " + singleX.size());
        String logger = "Result: ";
        int sum = 0;
        //OLD Code for calculating non-wasted water. INACCURATE
        if (dv.sprinkx.size() < 4) {
        } else if (dv.sprinkx.size() < 8)
            counter *= 1.85;
        else if (dv.sprinkx.size() < 12)
            counter *= 3.2;
        else if (dv.sprinkx.size() < 17)
            counter *= 3.9;
        else if (dv.sprinkx.size() < 25)
            counter *= 7.5;
        else
            counter *= 9.5;

        for (Map.Entry<String, Integer> entry : hm.entrySet()) {
            logger += "\n" + entry.toString();
            if (!entry.getKey().equals("000000") && !entry.getKey().equals("369646")) {
                //README This statement is making sure that counter is a sprinkler.
                if ("42".compareTo(entry.getKey()) < 0 && "65".compareTo(entry.getKey()) > 0)
                    counter += entry.getValue();
            }
        }
        //Log.wtf("*ITERATION STATUS:", "Done tallying");

        for (double m : singleR)
            non += Math.pow(m, 2) * Math.PI;

        //Log.wtf("*ITERATION STATUS:", "Done calculating non");

    /*String output = "";
    int counter2 = 0;
    for (String a : notGood) {
        output += a + "  ";
        counter2++;
        if (counter2 % 5 == 0)
            output += "\n";
    }*/

        hideLoading();
        makeToast("Got everything");

        String output = "hi";
        Log.wtf("*   Iterating Through Pixels ----", logger);
        //Log.wtf("*  Not accepted ----", "O: " + output);
        //INFO Area of individual sprinklers calculated with formula.           NOT WASTED
        Log.wtf("*  Non overlap Sprinkler Area: ----", "" + non);
        //INFO Counter is the area of each sprinkler that overlaps. FULL AREA - multicounts overlapping
        Log.wtf("*  Everything Besides Individual (not non): --------", "" + counter);
        //INFO Area of intersecting sprinkler, nonoverlap part.                 NOT WASTED
        Log.wtf("*  Intersecting Sprinkler, But Only 1 Region: --------", "" + overlappingButOnly1SprinklerRegion);
        //INFO Amount of intersecting sprinkler, overlap part.
        Log.wtf("*    Water Being Wasted", "This much water being wasted: " + area);
        Log.wtf("*      TOTAL AMOUNT WASTED: -------", (((double) (area)) / ((double) (non + counter + overlappingButOnly1SprinklerRegion)) + "\n"));
        //TODO Calculate polygon area.
        Log.wtf("*      TOTAL AREA COVERED: --------", "" + ((double) (non + overlappingButOnly1SprinklerRegion + counter - area) / (dv.polygonArea())) + "\n");
        Log.wtf("*--------------------", "_-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-_");

        //TODO Display the results in an AlertDialog.

        handleResults(non, counter, overlappingButOnly1SprinklerRegion, area, dv.polygonArea());
    }

    private void handleResults(final int non, final int counter, final int overlappingButOnly1SprinklerRegion, final int area, final double v) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.result);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button continueBtn = (Button) dialog.findViewById(R.id.continueBtn);
        final RelativeLayout results = (RelativeLayout) dialog.findViewById(R.id.realresults);
        final RelativeLayout toHide = dialog.findViewById(R.id.questions);
        final EditText waterUsedE = dialog.findViewById(R.id.waterused);
        final EditText durationE = dialog.findViewById(R.id.duration);
        Button next = (Button) dialog.findViewById(R.id.continueBtn);
        //results.setVisibility(View.VISIBLE);

        final TextView numSprink = results.findViewById(R.id.sNum);
        final TextView nonOverlapT = dialog.findViewById(R.id.noA);
        final TextView overlapA = dialog.findViewById(R.id.oA);
        final TextView totalA = dialog.findViewById(R.id.tA);
        final TextView landCovered = dialog.findViewById(R.id.lcA);
        final TextView totalLandA = dialog.findViewById(R.id.tlA);
        final TextView percentCoveredA = dialog.findViewById(R.id.pcA);
        final TextView numIntersect = dialog.findViewById(R.id.plc);
        final TextView wasted = dialog.findViewById(R.id.ww);
        final TextView totalWaterOutput = dialog.findViewById(R.id.two);
        final TextView percentWasted = dialog.findViewById(R.id.pww);
        final TextView perMonth = dialog.findViewById(R.id.permonth);
        final TextView perYear = dialog.findViewById(R.id.peryear);

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
                Log.wtf("*  Progress", fgood + " " + sgood);

                if (!(fgood && sgood))
                    makeToast("Please fill in the information.");
                else {
                    //DONE IT is good to contineu ahead.
                    toHide.setVisibility(View.INVISIBLE);
                    results.setVisibility(View.VISIBLE);

                    Button done = dialog.findViewById(R.id.done);
                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    });


                    //DONE Just do the calculations to display the actual stuff.

                    double coverage = ((double) (non + overlappingButOnly1SprinklerRegion + counter - area) / (v));
                    //double percentWastage = area / (non + counter + overlappingButOnly1SprinklerRegion);
                    double percentWastage = ((double) (area)) / ((double) (non + counter + overlappingButOnly1SprinklerRegion));

                    numSprink.setText(dv.sprinkx.size() + "");
                    Log.wtf("* Stats Nums: ", waterUsed + " " + duration + " " + coverage + " " + percentWastage);
                    numIntersect.setText("" + (dv.sprinkx.size() - singleX.size()));
                    totalLandA.setText(String.format("%1$,.2f", (v * Math.pow(dv.ratio, 2))) + " sq. ft");
                    landCovered.setText(String.format("%1$,.2f", (coverage * v * Math.pow(dv.ratio, 2))) + " sq. ft");
                    percentCoveredA.setText(String.format("%1$,.1f", coverage * 100) + "%");
                    percentWasted.setText(String.format("%1$,.1f", percentWastage * 100) + "%");
                    perMonth.setText(String.format("%1$,.0f", 4 * duration * waterUsed * dv.sprinky.size() * percentWastage) + " gal");
                    perYear.setText(String.format("%1$,.0f", 52 * duration * waterUsed * dv.sprinky.size() * percentWastage) + " gal");
                    totalWaterOutput.setText(String.format("%1$,.1f", duration * waterUsed * dv.sprinky.size()) + " gal/wk");
                    totalA.setText(String.format("%1$,.1f", duration * waterUsed * dv.sprinky.size()) + " gal/wk");
                    wasted.setText(String.format("%1$,.2f",
                            duration * waterUsed * dv.sprinky.size() * percentWastage) + " gal/wk");


                    nonOverlapT.setText(String.format("%1$,.2f", duration * waterUsed * singleX.size()) + " gal/wk");
                    //nonOverlapT.setText(String.format("%1$,.2f", (pixToGallon(non, waterUsed) * duration)) + " gal/wk");
                    overlapA.setText(String.format("%1$,.2f", duration * waterUsed * (dv.sprinkx.size() - singleX.size())) + " gal/wk");
                    //overlapA.setText(String.format("%1$,.2f", duration * (pixToGallon(non + counter + overlappingButOnly1SprinklerRegion, waterUsed) - pixToGallon(non, waterUsed))) + " gal/wk");

        //TODO Shift left and shift right

                    //wasted.setText(String.format("%1$,.1f", duration * pixToGallon(area, waterUsed)) + " gal/wk");
                    //totalWaterOutput.setText(String.format("%1$,.2f", duration * (pixToGallon(non + counter + overlappingButOnly1SprinklerRegion, waterUsed))) + " gal/wk");

                }

            }
        });

        dialog.show();
    }

    public double pixToGallon(double pixels, double gpm) {
        double r = dv.ratio;
        double squarefeet = pixels * (Math.pow(r, 2));
        double waterPerFootPerMinute = gpm / (Math.pow(2 * r, 2) * Math.PI);
        //double waterPerFootPerMinute = gpm / (Math.pow(dv.sprinkr.get(0) * r, 2) * Math.PI);

        return squarefeet * waterPerFootPerMinute;
    }

    private int[] calculateWastage(boolean two) {
        double firstX = 0, firstY = 0, firstR = 0, secondX = 0, secondY = 0, secondR = 0;
        boolean firstUsed = false;
        double totalArea = 0;
        for (int i = 0; i < dv.sprinkx.size(); i++) {
            if (!singleX.contains(dv.sprinkx.get(i))) {
                totalArea = Math.PI * Math.pow(dv.sprinkr.get(i), 2);
                if (!firstUsed) {
                    firstX = dv.sprinkx.get(i);
                    firstR = dv.sprinkr.get(i);
                    firstY = dv.sprinky.get(i);
                    firstUsed = true;
                    totalArea += Math.PI * Math.pow(dv.sprinkr.get(i), 2);
                } else {
                    secondX = dv.sprinkx.get(i);
                    secondR = dv.sprinkr.get(i);
                    secondY = dv.sprinky.get(i);
                    totalArea += Math.PI * Math.pow(dv.sprinkr.get(i), 2);
                    if (two) {
                        firstUsed = false;
                        break;
                    } else {
                        if (!firstUsed) {
                            firstUsed = true;
                        } else {
                            totalArea += Math.PI * Math.pow(dv.sprinkr.get(i), 2);
                            break;
                        }
                    }
                }
            }
        }
        //NOTES return {intersection, area of all circles combined}
        double intersectionArea = 0;
        //DONE Calculate total sprinkler coverage area.
        //TODO DO calculation to calculate intersection area.

        Double r = firstR;
        Double R = secondR;
        Double d = Math.sqrt(Math.pow(firstX - secondX, 2) + Math.pow(secondY - firstY, 2));
        if (R < r) {
            // swap
            r = secondR;
            R = firstR;
        }
        Double part1 = r * r * Math.acos((d * d + r * r - R * R) / (2 * d * r));
        Double part2 = R * R * Math.acos((d * d + R * R - r * r) / (2 * d * R));
        Double part3 = 0.5f * Math.sqrt((-d + r + R) * (d + r - R) * (d - r + R) * (d + r + R));

        intersectionArea = part1 + part2 - part3;
        Log.wtf("***SPRINKLER LIST: ", r + " " + R + " " + d + " " + part1 + " " + part2 + " " + part3);
        if (!(intersectionArea > 0)) {
            intersectionArea = Math.PI * Math.pow(Math.min(secondR, firstR), 2);
        }

        if (two) {
            totalArea = Math.PI * Math.pow(secondR, 2) + Math.PI * Math.pow(firstR, 2);
        }
        //intersectionArea = Math.PI * Math.pow(r, 2) * 0.015f;
        Log.wtf("** INFORMATION: ", "Intersection Area: " + intersectionArea + "  Total Area: " + totalArea);

        return new int[]{(int) intersectionArea, (int) totalArea};

    }

    private static void makeToast(String s) {
        //README I am providing a static context since makeToast is static in order to call it from
        // ask for length which is static because it has to be called in onDraw();
        // FIXME-- If you get any errors with making Toast, try making it unstatic.
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

}
