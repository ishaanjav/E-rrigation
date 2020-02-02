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
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

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

        //README INFO to get a Textview object in Canvas without any errors, you have to use
        //  relativelayout that it is in then .findViewById();
        length = background.findViewById(R.id.length);
        //handleSideLength();

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

        //radius.setVisibility(View.VISIBLE);
        //real.setVisibility(View.VISIBLE);
        radius.getProgressDrawable().setColorFilter(Color.parseColor("#70c48c"), PorterDuff.Mode.SRC_IN);
        radius.getThumb().setColorFilter(Color.parseColor("#3abd66"), PorterDuff.Mode.SRC_IN);
        //polygon.setVisibility(View.INVISIBLE);
        context = getApplicationContext();
        //askForLength(true, 2);

        angleAndRotateClicks();

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
                                    //dv.ratio = dv.radius / numVal;
                                    dv.ratio = dv.radius / (dv.radius * (dv.screenW / 200));
                                    Log.wtf("**Ratio, ", "Ratio is: " + dv.ratio);

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
        maxSideLength = 0;
        for (int i = 0; i < dv.xlist.size() - 1; i++) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                    ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = (dv.xlist.get(i) + dv.xlist.get(i + 1)) / 2;
            params.topMargin = (dv.ylist.get(i) + dv.ylist.get(i + 1)) / 2;

            TextView textView = new TextView(context);
            int val = (int) (Math.hypot(Math.abs(dv.xlist.get(i) - dv.xlist.get(i + 1)),
                    Math.abs(dv.ylist.get(i) - dv.ylist.get(i + 1))) * saveRadius);
            //if (maxSideLength < val) maxSideLength = val;
            //Log.wtf("**A", maxSideLength+"");
            textView.setText("" + val);
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
        //makeToast("show loading called");
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

    int messageCount = 0;

    private void setRadiusBar() {
        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //INFO this is when the user lets go of the slider
                //makeToast("Invalidating");
                //dv.sradius = seekBar.getProgress();
                int max = seekBar.getMax();
                int min = seekBar.getMin();
                double sprinklerRadius = seekBar.getProgress() * (dv.screenW / 200);
                float bigCircleRadius = dv.setCircleRadiusTo;
                float bigCircleFeet = dv.length;
                Log.wtf("*Seek bar info:", "max: " + max + "  min: " + min + "  current: " + seekBar.getProgress());
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

                /*double setTo = ((double) ((double) dv.screenW / 1000) * Math.pow(seekBar.getProgress() / 5.9f, 2));
                double scale = (double) bigCircleRadius / (bigCircleFeet - 2);
                double t = seekBar.getProgress() / 100 * scale;
                double newProgress = scale * dv.sradius;*/

                if (dv.currentMode != DrawingView.Mode.drawc) {

                    Log.wtf("**IMPORTANT INFO :", (double) pixelSideLength + " " + sprinklerRadius
                            + " " + dv.sradius /*+ " " + dv.radius*/ + " Max - " + maxSideLength + " Min- " + minSideLength + " Part 1- " + (((double) 100 - progress) * dif) + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tProgress: " + progress +
                            "   Set to I - " + ((int) Math.round(setToI * 100000) / 100000));
                    //makeToast("Radius: " + (String.format("%1$,.1f", (sprinklerRadius * bigCircleFeet /bigCircleRadius))) + " feet.");
                    //makeToast("Radius: " + (String.format("%1$,.1f", (newProgress * dv.ratio))) + " feet.");
                    makeToast("Radius: " + (String.format("%1$,.1f", (setToI * ((double) dv.ratio)))) + " feet.");
                }


                //makeToast("Radius: " + (String.format("%1$,.1f", (50f*((double)seekBar.getProgress()/(double)seekBar.getMax()) - 24))) + " feet.");
                // dv.sradius = (int) (40f*((double)seekBar.getProgress()/(double)seekBar.getMax()) - 18);
                /*Log.wtf("* Sprinkler Radius Info: ", "Pixel radius (setTo): " + setToI + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t"
                        + "Radius: " + (String.format("%1$,.1f", (setToI * dv.ratio))) + " feet");*/
                //makeToast("Updating sradius: " + dv.sradius);
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
                //dv.sradius = (int) (50f*((double)seekBar.getProgress()/(double)seekBar.getMax()) - 24);


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
        //README I commented out below because when user presses make into rectangle buttona and then plots sprimnklers
        // The radius is completely messed up.
        /*polygon.setOnClickListener(new View.OnClickListener() {
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
        });*/
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
                    //README Added below because even when undo button pressed, angle list only grew in size.
                    //  This may have potentially led to problem where randomly when plotting sprinklers
                    // sprinkler from 5 taps ago changes angles.
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
                    //dv.plotSprinklers2(dv.mCanvas);
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
                    plotSprinklers2(canvas);
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
                            Log.wtf("*Sprinkler Being Drawn INFO", ":" + (radius * (screenW / 200)));
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
                    //Log.wtf("*DOTPLOT", "DOT Plot being called");
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
                        setCircleRadiusTo = 300;
                        mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                setCircleRadiusTo, fillPaint);
                        mCanvas.drawCircle(screenW / 2, screenH / 2 - (screenH / 8) - 30,
                                setCircleRadiusTo + 8, linePaint);
                    } else {
                        setCircleRadiusTo = radius * (screenW / 200);
                        Log.wtf("*BIG CIRCLE RADIUS: ", setCircleRadiusTo + " radius:" + radius + " ScreenW:" + screenW);
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
                            String logger = "";
                            if (dv.xlist.size() > 0) {
                                for (int i = 0; i < dv.xlist.size(); i++) {
                                    logger += "\n*- X - " + dv.xlist.get(i) + "  Y - " + dv.ylist.get(i);
                                }
                            }
                            //Log.wtf("*- Polygon coordinates", "Information: " + logger);
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

                    int progress = MainActivity.radius.getProgress();
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
                    /*progress -= 50;
                    progress *= 2;*/
                    setToI = pixelSideLength - ((double) 100 - sradius) / 100 * dif;


                    //makeToast("Side Length : " + maxSideLength);
                    //Log.wtf("*- Set to I", (int) setToI + " " + (int) pixelSideLength + " " + sradius + " " + (int) maxSideLength + " " + (int) dv.ratio);
                    //Log.d("***--- Set to I", (int) setToI + " " + (int) pixelSideLength + " " + sradius + " " + (int) maxSideLength + " " + (int) dv.ratio+"\n..---...");
                    //sprinkr.add((int) ((double) ((double) screenW / 1000) * Math.pow(sradius / 5.9f, 2)) - 50);
                    sprinkr.add((int) setToI);
                    //Log.d("*-", "Radii: " + sprinkr.toString());
                    //sprinkr.add((int) ((double) (sradius/dv.ratio)));
                    //Log.wtf("*- IMPORTANT: ", " SRADIUS: " + sradius + " " + " RATIO: " + dv.ratio);
                    //Log.wtf("*- IMPORTANT: ", "Change Rotation Size: " + changeRotation.size() + "  rotation-" + rotate + "  angle-" + angle);
                    /*String logger = "";
                    if (dv.xlist.size() > 0) {
                        for (int i = 0; i < dv.xlist.size(); i++) {
                            logger += "\n*- X - " + dv.xlist.get(i) + "  Y - " + dv.ylist.get(i);
                        }
                    }*/
                    //Log.wtf("*- Polygon coordinates", "Information: " + logger);
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
                int val2 = (int) (Math.hypot(Math.abs(xlist.get(xlist.size() - 1) - xlist.get(0)),
                        Math.abs(ylist.get(ylist.size() - 1) - ylist.get(0))) * ratio);
                if (maxSideLength < val2) maxSideLength = val2;
                last.setText("" + val2);
                last.setId(specialCounter);
                last.setLayoutParams(params2);
                //makeToast("Making the last text");
                rlDvHolder.addView(last);

                //Log.wtf("**a-", "Side Length - " + maxSideLength);
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

        private void plotSprinklers2(Canvas canvas) {

            if (sprinkx.size() > 0) {
                //String logger = "";
                /*for (int i = 0; i < rotationList.size(); i++) {
                    logger += "Rotate: " + rotationList.get(i) + "  Angle: " + angleList.get(i) + " " + sprinkx.get(i)
                            + " " + sprinky.get(i) + " " + sprinkr.get(i) + " " + "\n";
                }*/
                //Log.wtf("*-Lists", logger);
                for (int i = 0; i < sprinkx.size() - 1; i++) {
                    //Log.wtf("*Sprinkler Location: ", sprinkx.get(i) + " " + sprinky.get(i) + " " + sprinkr.get(i));
                    // canvas.dra wCissdddddcrcle(sprinkx.get(i), sprinky.get(i), sprinkr.get(i), sprinklerC);
                    int m = i;
                    float radius = sprinkr.get(m);
                    canvas.drawArc(new RectF(sprinkx.get(m) - radius, sprinky.get(m) - radius, sprinkx.get(m) + radius,
                                    sprinky.get(m) + radius), (rotationList.get(i)) % 360 - 90,
                            /*(rotationList.get(i))%360 + */angleList.get(i), true, sprinklerC);
                }
                int m = sprinkx.size() - 1;
                float radius = sprinkr.get(m);

                canvas.drawArc(new RectF(sprinkx.get(m) - radius, sprinky.get(m) - radius, sprinkx.get(m) + radius,
                                sprinky.get(m) + radius), (rotationList.get(rotationList.size() - 1)) % 360 - 90,
                        /*(rotationList.get(i))%360 + */angleList.get(angleList.size() - 1), true, sprinklerC);
                //Log.wtf("*-Status:", "Plot Sprinkler2 ");

            }
        }

        private void plotSprinklers(Canvas canvas) {
            //Log.wtf("*Plotting Sprinklers", "Number of Sprinklers: " + sprinkx.size());
            if (sprinkx.size() > 0) {
                for (int i = 0; i < sprinkx.size(); i++) {
                    //Log.wtf("*Sprinkler Location: ", sprinkx.get(i) + " " + sprinky.get(i) + " " + sprinkr.get(i));
                    // canvas.drawCissdddddcrcle(sprinkx.get(i), sprinky.get(i), sprinkr.get(i), sprinklerC);
                    int m = i;
                    float radius = sprinkr.get(m);
                    canvas.drawArc(new RectF(sprinkx.get(m) - radius, sprinky.get(m) - radius, sprinkx.get(m) + radius,
                                    sprinky.get(m) + radius), (rotationList.get(i)) % 360 - 90,
                            /*(rotationList.get(i))%360 + */angleList.get(i), true, sprinklerC);
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
                //INFO The purpose of this is to display the loading Alert Dialog.
                //Bitmap tr = takeScreenShot(dv);
                showLoading();

                calculateSprinklerOverflow();
/*
                //INFO Wait a bit so that the Dialog is showing, then do the calculations.
                Handler h = new Handler();
                final Bitmap[] bmp = {tr};
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Bitmap bmp = takeScreenShot(dv);
               *//*try (FileOutputStream out = new FileOutputStream("test.png")) {
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
               startActivity(opener);*//*


                        //DONE Before counting pixel colors, try compressing PNG to 50% quality or less
                        //  that way there are fewer colors for sprinkler.
                        //TODO When calculating area of overlapping regions, actually calculate proper
                        //  areas of 1 sprinkler and 2 sprinklers with formula.
                        //  for the rest, then you can use pixels

                        //takeScreenShot2();
                        //makeToast("Bitmap Info: " + bmp.getWidth() + " " + bmp.getHeight());
                        for (int i = 0; i < dv.sprinkx.size(); i++) {
                            //Log.wtf("*  Sprinkler Location ", "X: " + dv.sprinkx.get(i) + "  Y: " + dv.sprinky.get(i) + "  R: " + dv.sprinkr.get(i));
                        }
                        //Log.wtf("*BITMAP DIMENSIONS --------------------", "Width: " + bmp.getWidth() + " Height: " + bmp.getHeight());
                        //getIndividualCircles();
                        //Log.wtf("*Done getting circles", " DONE GETTING CIRCLES");
                        //README Make Bitmap smaller.
                        bmp[0] = Bitmap.createScaledBitmap(bmp[0], (int) (bmp[0].getWidth() * 1), (int) (bmp[0].getHeight() * 1), true);
                        iterateThroughPixels(bmp[0]);
                        //askForLength(true, 3);

                        //File file = takeScreenShot2();
                        //iterateThroughPixels(file);
                    }
                }, 300);*/

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
        Log.wtf("*---------------*-", "*----*----*----*----*----*----*----*----*----*----*----*----*----");
        Log.wtf("*---------------*-", "*----*----*----*----*----*----*----*----*----*----*----*----*----");
        Log.wtf("*---------------*-", "*----*----*----*----*----*----*----*----*----*----*----*----*----");
        Log.wtf("*---------------*-", "*----*----*----*----*----*----*----*----*----*----*----*----*----");
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
                        //Log.wtf("*- complete outside info - ", "added: " + t + " SIze: " + completelyOutside.size());
                    } else {
                        //README Point had no intersections and was inside
                        insideCirclesH.put(t, 1);
                        if (!removedCompletelyInsideCircles)
                            completelyInsideH.put(t, 1);
                        if (!removedIndividual)
                            individualCircles.put(t, radius);
                    }
                    //FUTURE Files Try going through remaining sprinklers and calculate 2 intersections in pairs.
                    // go from i =0 and j= i and check if they intersect. If the 2 sprinklers intersect calculate wastage.
                    // we won't be able to account for 3 circles.
                    // At most, check if singleR is 3 or 4 less than total circles. then estimate duplicate wastage from 3 circles.
                    //FUTURE FILES
                    // You may have to check if non intersecting circle is inside. If inside, then calculate areas of only these.
                    // Right now you are calculating areas of individual ones here and area of individual ones in iterateThroughPixels
                    //overFlowWastage += radius * radius * Math.PI * dv.angleList.get(t) / 360d;
                    //totalOverflowArea += radius * radius * Math.PI * dv.angleList.get(t) / 360d;
                    //Log.wtf("*- Sprinkler Overflow (" + t + " " + b + ")", "No overflow??");
                    // return Collections.emptyList();
                } else {
                    // if disc == 0 ... dealt with later
                    double tmpSqrt = Math.sqrt(disc);
                    double abScalingFactor1 = -pBy2 + tmpSqrt;
                    double abScalingFactor2 = -pBy2 - tmpSqrt;

                    /*Point p1 = new Point(pointA.x - baX * abScalingFactor1, pointA.y
                            - baY * abScalingFactor1);*/
                    //README 1 intersection
                    if (disc == 0) { // abScalingFactor1 == abScalingFactor2
                        //DONE calculate 30% wastage
                        completelyInsideH.remove(t);
                        removedCompletelyInsideCircles = true;
                        overFlowWastage += radius * radius * Math.PI * dv.angleList.get(t) / 360d * .3;
                        totalOverflowArea += radius * radius * Math.PI * dv.angleList.get(t) / 360d;
                        Log.wtf("*- Sprinkler Overflow (" + t + " " + b + ")", "1 INTERSECTION - " + " x: " +
                                (startX - baX * abScalingFactor1) + " y: " + (startY - baY * abScalingFactor1));
                    } else {
                        double x1 = (startX - baX * abScalingFactor1);
                        double y1 = (startY - baY * abScalingFactor1);
                        double x2 = (startX - baX * abScalingFactor2);
                        double y2 = (startY - baY * abScalingFactor2);
                        //README False intersection (line not line segment)
                        if (!((x1 > Math.min(startX, endX) && x1 < Math.max(startX, endX) && y1 > Math.min(startY, endY) && y1 < Math.max(startY, endY))
                                || (x2 > Math.min(startX, endX) && x2 < Math.max(startX, endX) && y2 > Math.min(startY, endY) && y2 < Math.max(startY, endY)))) {
                        /*if ((radius < Math.sqrt(Math.pow(circleX - startX, 2) + Math.pow(circleY - startY, 2))
                                || radius < Math.sqrt(Math.pow(circleX - endX, 2) + Math.pow(circleY - endY, 2)))
                                && !((circleX > startX && circleX < endX) && (circleY > startY && circleY < endY))) {*/
                        /*if ((circleX < (Math.min(startX, endX) - radius)) || (circleX > (Math.max(startX, endX) + radius))
                                || (circleY < (Math.min(startY, endY) - radius)) || (circleY > (Math.max(startY, endY) + radius))) {*/
                            /*Log.wtf("*- Sprinkler Overflow (" + t + " " + b + ")", "\n\t\t\\t\tTOO FAR AWAY: -" +
                                    "Bool 1 - " + (x1 > Math.min(startX, endX) && x1 < Math.max(startX, endX) && y1 > Math.min(startY, endY) && y1 < Math.max(startY, endY)) +
                                    " Bool 2- " + (x2 > Math.min(startX, endX) && x2 < Math.max(startX, endX) && y2 > Math.min(startY, endY) && y2 < Math.max(startY, endY)) +
                                    *//*"Radius bool-" + (radius < Math.sqrt(Math.pow(circleX - startX, 2) + Math.pow(circleY - startY, 2))
                                            || radius < Math.sqrt(Math.pow(circleX - endX, 2) + Math.pow(circleY - endY, 2))) + " " +
                                            "Other 2:" + !((circleX > startX && circleX < endX) && (circleX > startX && circleX < endX))
                                            + (circleX > startX && circleX < endX) + (circleX > startX && circleX < endX) +*//* " \n\n1x: " +
                                    x1 + " 1y: " + y1
                                    + " 2x: " +
                                    x2 + " 2y: " + y2 + " StartX: " + startX + " StartY: " + startY + " EndX: " + endX +
                                    " EndY: " + endY);*/
                        } else {
                            //README actual intersection with 2 points
                            //  circle is not fully outside land.
                            /*fullOutside = false;
                            if(added == false)
                                removedFirst = true;
                            if(added == true)
                                removedFirst = false;*/
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

                            //Log.wtf("*- complete outside info - " , "removed: " + t + " SIze: " + completelyOutside.size());
                            overflowInfo.add(new OverflowInfo(t, b, (b + 1 > dv.xlist.size() - 1 ? 0 : b + 1), startX, startY,
                                    endX, endY, circleX, circleY, radius, x1, x2, y1, y2));
                            Log.wtf("*- Sprinkler Overflow (" + t + " " + b + ")", "2 INTERSECTIONs - \n\t\t\t\t\t\t" + " 1x: " +
                                    x1 + " 1y: " + y1
                                    + " 2x: " +
                                    x2 + " 2y: " + y2 + " StartX: " + startX + " StartY: " + startY + " EndX: " + endX +
                                    " EndY: " + endY);
                        }
                        /*Point p2 = new Point(pointA.x - baX * abScalingFactor2, pointA.y
                                - baY * abScalingFactor2);*/
                        //return Arrays.asList(p1, p2);
                    }
                    Log.wtf("*---------------*-", "*----*----*----*----*----*----*----*----*----*----*----*----*----");

                }
            }
            if (outsideIntersectingGood)
                outsideIntersectingH.put(t, new OverflowInfo(t, 0, 1, 0, 1,
                        0, 1, circleX, circleY, radius, ox1, ox2, oy1, oy2));
            // if(removedFirst)
            //   completelyOutside.remove(t);

            //README Circle not fully outside ladn so have to remove from list.
            //Log.wtf("*- \t#" + (t+1)+ " Bool vars - " , "Full Outside: " + fullOutside + " Added: " + added + " Outside: " + outside);
            //INFO Uncomment below if using ArrayList.
            /*if (!fullOutside) {
                //README Only if it was added to list, remove that sprinkler.
                if (added)
                    completelyOutside.remove(completelyOutside.size() - 1);
                //makeToast("FUll Outside: " + fullOutside);
            }*/
        }

        for (Map.Entry<Integer, Double> map : individualCircles.entrySet()) {
            //README Candidates contains the positions of possible individual sprinklers.
            candidates.add(map.getKey());
        }
        //README method uses positions in candidates list to actually calculate individual or not.
        //DONE Calculate areas of individual circles (including angles)
        getIndividualCircles2();
        //individualCircleArea();

        outsideIntersecting = new ArrayList<>(outsideIntersectingH.values());
        insideIntersecting = new ArrayList<>(insideIntersectingH.values());
        completelyInside = new ArrayList<>(completelyInsideH.keySet());
        insideCircles = new ArrayList<>(insideCirclesH.keySet());

        //TODO Calculate 4 things on sheet of paper.
        //  1. Start with calculating overlap of 2 circles from insideCircles list
        //NOTES Total area calculated -
        // At this point all outside, intersecting, and completely inside cirlces have been calculated.

        //DONE - ideally for insideCircleOverlap should only go for completelyinsideCircles with insideCircles
        //    Then, you have 3 other cases - insideIntersecting with insideIntersecting, insideIntersecting with outsideIntersecting,
        //    and outsideIntersecting with outsideIntersecting
        //  For the above 3 cases, when calculating overlap, you also have to account for angle between centers because
        //  the intersecting area can be inside and outside the region.

        //OLD Don't call below anymore.
        //individualCircleArea();

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


        //Log.wtf("*- Completely Outside", "Sprinkler # - " + completelyOutside.size() + "\n\t\t\t\t\t\t\t\t\t Wastage - " + overFlowWastage);

        //total += totalInsideArea;
        //total += totalOverflowArea;
        wasted += overFlowWastage;
        wasted += overlapWastage;
        //INFO Subtracts overlap of 3 circles
        wasted -= overCounted3;
        Log.wtf("*- End Results:", "- " + (int) wasted + " " + (int) total + " " + (int) (wasted * 100 / total) + "%\n---___---");
        Log.wtf("*- Individual Circles", "Size: " + singleR.size());
        Log.wtf("*- Outside Intersecting", "Size: " + outsideIntersecting.size());
        Log.wtf("*- Completely Inside Circle", "Size: " + completelyInside.size());
        Log.wtf("*- Inside Circles", "Size: " + insideCircles.size());
        Log.wtf("*- Inside Intersecting", "Size: " + insideIntersecting.size());
        hideLoading();
        displayResults();
    }

    private void displayResults() {
        final Dialog dialog = new Dialog(MainActivity.this);
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
/*
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
        final TextView perYear = dialog.findViewById(R.id.peryear);*/

        //TODO rephrase question for how much water sprinkler uses to
        // How much water system uses (System always outputs same amount of water)
        // Then, change calculations (it will be easier now)

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
                Log.wtf("*  Progress", fgood + " " + sgood);

                if (!(fgood && sgood))
                    makeToast("Please fill in the information.");
                else {
                    //DONE IT is good to contineu ahead.
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.real_result);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.getWindow().setLayout(dialog.getWindow().getAttributes().width,
                            (int)(dv.screenH*0.9d));


                    showResults(dialog);
                    makeToast("Information calculated. Scroll for more.");


                    dialog.show();
                    /*toHide.setVisibility(View.INVISIBLE);
                    results.setVisibility(View.VISIBLE);

                    Button goBack = dialog.findViewById(R.id.goBack);
                    Button done = dialog.findViewById(R.id.done);
                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    });

                    goBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            toHide.setVisibility(View.VISIBLE);
                            results.setVisibility(View.INVISIBLE);
                        }
                    });*/
/*

                    //DONE Just do the calculations to display the actual stuff.

                    //INFO README
                    // I think a better method of calculating coverage is simply totalWaterOutput *(1-percentWastage)
                    double percentWastage = ((double) (area)) / ((double) (non + counter + overlappingButOnly1SprinklerRegion));
                    double coverage = ((double) (non + overlappingButOnly1SprinklerRegion + counter - area) / (v));
                    if (coverage > 1) {
                        coverage = 0;
                        for (int r : dv.sprinkr) {
                            coverage += Math.PI * Math.pow(r, 2);
                        }
                        coverage -= coverage * percentWastage;
                        coverage = coverage / v;
                        if (coverage > 1)
                            coverage = 0.7678;
                    }
                    //double percentWastage = area / (non + counter + overlappingButOnly1SprinklerRegion);

                    //README Accounting for soil type
                    String choice = soilType.getSelectedItem().toString();
                    if (choice.equals("Sandy")) {
                        percentWastage *= 0.95f;
                    } else if (choice.equals("Loam")) {
                        percentWastage *= 0.9f;
                    } else if (choice.equals("Clay")) {
                        percentWastage *= 1.15f;
                    }

                    numSprink.setText(dv.sprinkx.size() + "");
                    Log.wtf("* Stats Nums: ", waterUsed + " " + duration + " " + coverage + " " + percentWastage);
                    numIntersect.setText("" + (dv.sprinkx.size() - singleX.size()));
                    totalLandA.setText(String.format("%1$,.2f", (v * Math.pow(dv.ratio, 2))) + " sq. ft");
                    Log.wtf("* INFO", coverage + " " + v + " " + dv.ratio);
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
*/

                    //TODO Shift left and shift right

                    //wasted.setText(String.format("%1$,.1f", duration * pixToGallon(area, waterUsed)) + " gal/wk");
                    //totalWaterOutput.setText(String.format("%1$,.2f", duration * (pixToGallon(non + counter + overlappingButOnly1SprinklerRegion, waterUsed))) + " gal/wk");

                }

            }
        });

        dialog.show();
    }

    private void showResults(Dialog dialog) {

    }


    double overCounted3 = 0;

    private void calculate3CircleOverlap() {
        Log.wtf("*- 3 Overlapping Circles", "_______________________________________");
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
                //README The circles overlap
                if (distance <= r1 + r2) {
                    for (int k = j + 1; k < insideCircles.size(); k++) {
                        int circle3 = insideCircles.get(k);
                        double x3 = dv.sprinkx.get(circle3);
                        double y3 = dv.sprinky.get(circle3);
                        double r3 = dv.sprinkr.get(circle3);
                        double angle3 = dv.angleList.get(circle3);
                        double dist1 = Math.sqrt(Math.pow(x1 - x3, 2) + Math.pow(y1 - y3, 2));
                        double dist2 = Math.sqrt(Math.pow(x2 - x3, 2) + Math.pow(y2 - y3, 2));
                        if ((dist1 <= r1 + r3) && (dist2 <= r2 + r3)) {
                            Log.wtf("*- 3 intersects: ", circle1 + " " + circle2 + " " + circle3);
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
                                Log.wtf("*- Chain Circles", "Overlap: " + intersectionArea + " Total Area: " + area);
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
                                Log.wtf("*- Areas", "Overlap: " + intersectionArea + " Total Area: " + area + " Triangle: "
                                        + triangleArea + " Arc1:" + arc1 + " Arc2:" + arc2 + " Arc3:" + arc3 + " Circles: " + (Math.PI * r1 * r1 * 3));
                            }

                        }
                    }
                }
            }
        }
        overCounted3 += area;
        Log.wtf("*- 3 Circle Overlap Results", overCounted3 + "");
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
        Log.wtf("*- Inside and Outside Intersecting", "_______________________________________");
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

                /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = (int) x3;
                params.topMargin = (int) y3;
                TextView textView = new TextView(context);
                textView.setText("(" + (int) x3 + "," + (int) y3 + ")");
                textView.setId(dv.idCounter);
                textView.setTextSize(7);
                textView.setLayoutParams(params);
                //makeToast("Making the text");
                rlDvHolder.addView(textView);
                dv.idCounter++;
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.leftMargin = (int) x4;
                params2.topMargin = (int) y4;
                TextView textView2 = new TextView(context);
                textView2.setText("(" + (int) x4 + "," + (int) y4 + ")");
                textView2.setId(dv.idCounter);
                textView2.setLayoutParams(params2);
                textView2.setTextSize(7);
                //makeToast("Making the text");
                rlDvHolder.addView(textView2);
                dv.idCounter++;*/

                boolean out1 = outside(x3, y3);
                boolean out2 = outside(x4, y4);

                if (distance < r1 + r2) {
                    //README Both intersection points are outside meaning that area is already accounted for in overflowWastage
                    if (out1 && out2) {

                    } else if (!out1 && !out2) {
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

                        /*double l = ix1 * (iy2 - circleY);
                        double m = ix2 * (circleY - iy1);
                        double n = circleX * (iy1 - iy2);

                        double triangleArea = Math.abs((l + m + n) - 50) % (Math.PI * Math.max(r1, r2) * Math.max(r1, r2));*/
                        //INFO Area of the triangle formed by the three points
                        double triangleArea = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));
                        //INFO Area of each of the two arcs.
                        double arc1 = 0, arc2 = 0;
                        arc1 = arcArea(r1, ix1, iy1, circleX, circleY, x1, y1);
                        arc2 = arcArea(r1, ix2, iy2, circleX, circleY, x2, y2);

                        overlap2 += triangleArea + arc1 + arc2;
                        Log.wtf("*- Information: ", "Wastage - " + (int) overlap2 + "  Full - " +
                                overlap(r1, r2, x1, x2, y1, y2, circle1, circle2) + " ix1-" + (int) ix1 + " ix2-" + (int) ix2
                                + " iy1-" + (int) iy1 + " iy2-" + (int) iy2 + " c-cX:" + (int) circleX + " c-cY:" + (int) circleY
                                + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t1 circle- " + (int) (r1 * r1 * Math.PI) + "  X1: " + (int) x1 + " Y1: " + (int) y1 + " X2: " +
                                (int) x2 + " Y2: " + (int) y2 + " Arc1-" + (int) arc1 + " Arc2- " + (int) arc2 + " Triangle-" + (int) triangleArea);

                        /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
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
                        dv.idCounter++;*/
                    }

                    /* Log.wtf("*- Information: ", "X1: " + (int) x1 + " Y1: " + (int) y1 + " X2: " + (int) x2 + " Y2: " + (int) y2
                            + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t Radius - " + (int) r1 + " X3: " + (int) x3 + " Y3: " + (int) y3 + " X4: " + (int) x4 + " Y4: " + (int) y4
                            + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tWastage - " + overlap2 +"  Total - " +
                            overlap(r1, r2, x1, x2, y1, y2, circle1, circle2));*/

                }
            }
        }
        overlapWastage += overlap2;
        Log.wtf("*- Results insideIntersecting-outsideIntersecting:", (int) overlap2 + ".\n-");
    }

    private void outsideIntersectingOutsideIntersecting() {
        Log.wtf("*- Outside and Outside Intersecting", "_______________________________________");
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

                /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = (int) x3;
                params.topMargin = (int) y3;
                TextView textView = new TextView(context);
                textView.setText("(" + (int) x3 + "," + (int) y3 + ")");
                textView.setId(dv.idCounter);
                textView.setTextSize(7);
                textView.setLayoutParams(params);
                //makeToast("Making the text");
                rlDvHolder.addView(textView);
                dv.idCounter++;
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.leftMargin = (int) x4;
                params2.topMargin = (int) y4;
                TextView textView2 = new TextView(context);
                textView2.setText("(" + (int) x4 + "," + (int) y4 + ")");
                textView2.setId(dv.idCounter);
                textView2.setLayoutParams(params2);
                textView2.setTextSize(7);
                //makeToast("Making the text");
                rlDvHolder.addView(textView2);
                dv.idCounter++;*/

                boolean out1 = outside(x3, y3);
                boolean out2 = outside(x4, y4);

                if (distance < r1 + r2) {
                    //README Both intersection points are outside meaning that area is already accounted for in overflowWastage
                    if (out1 && out2) {

                    } else if (!out1 && !out2) {
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

                        /*double l = ix1 * (iy2 - circleY);
                        double m = ix2 * (circleY - iy1);
                        double n = circleX * (iy1 - iy2);

                        double triangleArea = Math.abs((l + m + n) - 50) % (Math.PI * Math.max(r1, r2) * Math.max(r1, r2));*/
                        //INFO Area of the triangle formed by the three points
                        double triangleArea = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));
                        //INFO Area of each of the two arcs.
                        double arc1 = 0, arc2 = 0;
                        arc1 = arcArea(r1, ix1, iy1, circleX, circleY, x1, y1);
                        arc2 = arcArea(r1, ix2, iy2, circleX, circleY, x2, y2);

                        overlap2 += triangleArea + arc1 + arc2;
                        Log.wtf("*- Information: ", "Wastage - " + (int) overlap2 + "  Full - " +
                                overlap(r1, r2, x1, x2, y1, y2, circle1, circle2) + " ix1-" + (int) ix1 + " ix2-" + (int) ix2
                                + " iy1-" + (int) iy1 + " iy2-" + (int) iy2 + " c-cX:" + (int) circleX + " c-cY:" + (int) circleY
                                + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t1 circle- " + (int) (r1 * r1 * Math.PI) + "  X1: " + (int) x1 + " Y1: " + (int) y1 + " X2: " +
                                (int) x2 + " Y2: " + (int) y2 + " Arc1-" + (int) arc1 + " Arc2- " + (int) arc2 + " Triangle-" + (int) triangleArea);

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

                    /* Log.wtf("*- Information: ", "X1: " + (int) x1 + " Y1: " + (int) y1 + " X2: " + (int) x2 + " Y2: " + (int) y2
                            + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t Radius - " + (int) r1 + " X3: " + (int) x3 + " Y3: " + (int) y3 + " X4: " + (int) x4 + " Y4: " + (int) y4
                            + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tWastage - " + overlap2 +"  Total - " +
                            overlap(r1, r2, x1, x2, y1, y2, circle1, circle2));*/

                }
            }
        }
        overlapWastage += overlap2;
        Log.wtf("*- Results outsideIntersecting-outsideIntersecting:", (int) overlap2 + ".\n-");
    }

    private void insideIntersectingInsideIntersecting() {
        Log.wtf("*- Inside and Inside Intersecting", "_______________________________________");
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

                /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = (int) x3;
                params.topMargin = (int) y3;
                TextView textView = new TextView(context);
                textView.setText("(" + (int) x3 + "," + (int) y3 + ")");
                textView.setId(dv.idCounter);
                textView.setTextSize(7);
                textView.setLayoutParams(params);
                //makeToast("Making the text");
                rlDvHolder.addView(textView);
                dv.idCounter++;
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams
                        ((int) ViewGroup.LayoutParams.WRAP_CONTENT, (int) ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.leftMargin = (int) x4;
                params2.topMargin = (int) y4;
                TextView textView2 = new TextView(context);
                textView2.setText("(" + (int) x4 + "," + (int) y4 + ")");
                textView2.setId(dv.idCounter);
                textView2.setLayoutParams(params2);
                textView2.setTextSize(7);
                //makeToast("Making the text");
                rlDvHolder.addView(textView2);
                dv.idCounter++;*/

                boolean out1 = outside(x3, y3);
                boolean out2 = outside(x4, y4);

                if (distance < r1 + r2) {
                    //README Both intersection points are outside meaning that area is already accounted for in overflowWastage
                    if (out1 && out2) {

                    } else if (!out1 && !out2) {
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

                        /*double l = ix1 * (iy2 - circleY);
                        double m = ix2 * (circleY - iy1);
                        double n = circleX * (iy1 - iy2);

                        double triangleArea = Math.abs((l + m + n) - 50) % (Math.PI * Math.max(r1, r2) * Math.max(r1, r2));*/
                        //INFO Area of the triangle formed by the three points
                        double triangleArea = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));
                        //INFO Area of each of the two arcs.
                        double arc1 = 0, arc2 = 0;
                        arc1 = arcArea(r1, ix1, iy1, circleX, circleY, x1, y1);
                        arc2 = arcArea(r1, ix2, iy2, circleX, circleY, x2, y2);

                        overlap2 += triangleArea + arc1 + arc2;
                        Log.wtf("*- Information: ", "Wastage - " + (int) overlap2 + "  Full - " +
                                overlap(r1, r2, x1, x2, y1, y2, circle1, circle2) + " ix1-" + (int) ix1 + " ix2-" + (int) ix2
                                + " iy1-" + (int) iy1 + " iy2-" + (int) iy2 + " c-cX:" + (int) circleX + " c-cY:" + (int) circleY
                                + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t1 circle- " + (int) (r1 * r1 * Math.PI) + "  X1: " + (int) x1 + " Y1: " + (int) y1 + " X2: " +
                                (int) x2 + " Y2: " + (int) y2 + " Arc1-" + (int) arc1 + " Arc2- " + (int) arc2 + " Triangle-" + (int) triangleArea);

                        /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
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
                        dv.idCounter++;*/
                    }

                    /* Log.wtf("*- Information: ", "X1: " + (int) x1 + " Y1: " + (int) y1 + " X2: " + (int) x2 + " Y2: " + (int) y2
                            + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t Radius - " + (int) r1 + " X3: " + (int) x3 + " Y3: " + (int) y3 + " X4: " + (int) x4 + " Y4: " + (int) y4
                            + "\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tWastage - " + overlap2 +"  Total - " +
                            overlap(r1, r2, x1, x2, y1, y2, circle1, circle2));*/

                }
            }
        }
        overlapWastage += overlap2;
        Log.wtf("*- Results insideIntersecting-insideIntersecting:", (int) overlap2 + ".\n-");
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

        //README Maybe intersectionArea is 0 because circle is inside other circle.
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
        Log.wtf("*- Completely Inside - Outside Intersect", " ");
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
                    //TODO Do calculations for overlap of completely inside circle and outside-intersecting circle
                    if (distance + Math.min(r1, r2) <= Math.max(r1, r2) * 0.98) {
                        double intersectionArea = Math.PI * Math.pow(Math.min(r1, r2), 2) * (360 - (360 - dv.angleList.get((Math.min(r1, r2) == r1 ? circle1 : circle2)) / 2)) / 360;
                        double totalArea = 0;
                        if (completelyInside.contains(circle1))
                            totalArea += (Math.PI * Math.pow(r1, 2)) * ((double) (angle1) / 360);
                        if (completelyInside.contains(circle2))
                            totalArea += (Math.PI * Math.pow(r2, 2)) * ((double) (angle2) / 360);
                        overlap2 += intersectionArea;
                        Log.wtf("*- INFORMATION (" + circle1 + "," + circle2 + ")", "Circle In Circle: " + intersectionArea + "  Total Area: " + totalArea);
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

                        overlap2 += intersectionArea;
                        //intersectionArea = Math.PI * Math.pow(r, 2) * 0.015f;
                        Log.wtf("*- INFORMATION (" + circle1 + "," + circle2 + ")", "Intersection Area: " + intersectionArea);
                    }
                }
            }
        }
        overlapWastage += overlap2;
        Log.wtf("*- Results completelyInside-outsideIntersect: ", (int) overlap2 + "\n-");
    }

    //DONE Test below function
    private void insideCircleOverlap() {
        Log.wtf("*- INSIDE CIRCLE WASTAGE", " ");
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
                        if (distance + Math.min(r1, r2) <= Math.max(r1, r2) * 1.08) {
                            double intersectionArea = Math.PI * Math.pow(Math.min(r1, r2), 2) * dv.angleList.get((Math.min(r1, r2) == r1 ? circle1 : circle2)) / 360;
                            double totalArea = 0;
                            if (completelyInside.contains(circle1))
                                totalArea += (Math.PI * Math.pow(r1, 2)) * ((double) (angle1) / 360);
                            if (completelyInside.contains(circle2))
                                totalArea += (Math.PI * Math.pow(r2, 2)) * ((double) (angle2) / 360);
                            Log.wtf("*- INFORMATION (" + circle1 + "," + circle2 + ")", "Circle In Circle: " + intersectionArea + "  Total Area: " + totalArea);
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
                                intersectionArea = Math.PI * Math.pow(r, 2) * dv.angleList.get(smallC) / 360;
                            }

                            //IMPORTANT Total Area is already calculated for overflow circles.
                            //DONE When iterating through inside circle, only if it not insideIntersecting, calculate its area.
                            double totalArea = 0;
                            if (completelyInside.contains(circle1))
                                totalArea += (Math.PI * Math.pow(r1, 2)) * ((double) (angle1) / 360);
                            if (completelyInside.contains(circle2))
                                totalArea += (Math.PI * Math.pow(r2, 2)) * ((double) (angle2) / 360);

                            overlap2 += intersectionArea;
                            //intersectionArea = Math.PI * Math.pow(r, 2) * 0.015f;
                            Log.wtf("*- INFORMATION (" + circle1 + "," + circle2 + ")", "Intersection Area: " + intersectionArea + "  Total Area: " + totalArea);
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
        Log.wtf("*- Results insideCircleOverlap: ", (int) overlap2 + " " + (int) totalArea + "\n-");
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

    //README Calculate areas of individual circles.
    private void individualCircleArea() {
        for (int i = 0; i < singleR.size(); i++)
            totalInsideArea += singleR.get(i) * singleR.get(i) * Math.PI * dv.angleList.get(singleP.get(i)) / 360d;
    }

    //INFO IMPORTANT overflowWastage SHOULD NOT be added for land area covered
    //NOTES
    // Things that I am calculating:
    // 1. 0 intersections:: Completely outside sprinklers - total area  (keep in mind that for list (have to remove element if it intersects with line)
    // 2. 1 intersection::: 30% wastage
    // 3. 2 intersecitons:: If overflowing sprinkler is outside
    //    Different overflowWastage if circle is outside.

    //NOTES
    //
    //TODO Check if set works with completely outside sprinklers. Have to make sure to add it only once and remove only if added and intersecting.
    //TODO Check if overFlowWastage for Completely Outside sprinklers works.
    //DONE Check if outside function is able to determine outside sprinklers.
    //TODO If outside, do additional calculation to subtract sector area (without triangle) from total circle
    //TODO Check if angles and everything is working.

    public void calculateOverflowWastage() {
        Log.wtf("*- - - - - --- --- --- - - --  -- ", "----------_-------------_------------_");
        int counteR = 0;
        ArrayList<Integer> used = new ArrayList<>();
        ArrayList<Integer> fullCircleUsed = new ArrayList<>();
        double previousWastage = 0;
        double initialWastage = overFlowWastage;
        for (OverflowInfo overflowInfo : overflowInfo) {
            if (!used.contains(overflowInfo.getCirclePos())) {
                double x1 = overflowInfo.getX1();
                double x2 = overflowInfo.getX2();
                double y1 = overflowInfo.getY1();
                double y2 = overflowInfo.getY2();

                double centerX = overflowInfo.getCircleX();
                double centerY = overflowInfo.getCircleY();

                double a = x1 * (y2 - centerY);
                double b = x2 * (centerY - y1);
                double c = centerX * (y1 - y2);
                double totalArea = Math.PI * overflowInfo.getRadius() * overflowInfo.getRadius();

                //INFO Added the mod totalArea because sometimes, triangleArea gets greater than totalArea.
                //double triangleArea = Math.abs((a + b + c) - 50) % totalArea;

                double length1 = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                double length2 = Math.sqrt(Math.pow(x2 - centerX, 2) + Math.pow(y2 - centerY, 2));
                double length3 = Math.sqrt(Math.pow(x1 - centerX, 2) + Math.pow(y1 - centerY, 2));
                double s = (length1 + length2 + length3) / 2;
                double triangleArea = Math.sqrt(s * (s - length1) * (s - length2) * (s - length3));

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

                /*Log.wtf("*- - Overflow Info", "Sprinkler " + (overflowInfo.getCirclePos()) + ((outside) ? " is outside" : " is inside")
                        + "\n\t\t\t\t\t\t\t\t\t\t\t\t\tThe angle is - " + (int) degrees + "\n\t\t\t\t\t\t\t\t\t\t\t\t\tTriangle Area - "
                        + triangleArea + "\n\t\t\t\t\t\t\t\t\t\t\t\t\tSector Area - " + sectorArea +
                        "\n\t\t\t\t\t\t\t\t\t\t\t\tTotal Area: " + totalArea + "\n\t\t\t\t\t\t\t\t\t\t\t    Final Area - " + wastedArea);*/

                previousWastage = overFlowWastage - initialWastage;
                overFlowWastage += wastedArea;

                if (fullCircleUsed.contains(overflowInfo.getCirclePos())) {
                    //double latestGain = overFlowWastage - initialWastage - previousWastage;
                    double latestGain = wastedArea;
                    //README If the latest addition was larger, then subtract 80% of previous smaller one.
                    double toAdd = previousWastage * .24 + wastedArea * .21;
                    overFlowWastage += toAdd;
                    if (overFlowWastage > totalOverflowArea)
                        overFlowWastage -= toAdd * .9;
                    //TODO Fix problem that overflowwastage can go above totalOverflowArea
                    //if(overFlowWastage )
                    if (latestGain >= previousWastage) {
                        //overFlowWastage -= previousWastage * .59;
                    } else { //README The previous area was larger.
                        //overFlowWastage -= wastedArea * .55;
                    }
                }

                if (!fullCircleUsed.contains(overflowInfo.getCirclePos())) {
                    //totalOverflowArea += totalArea;
                    Log.wtf("*- Total Overflow Area: ", "ADDING: " + totalArea + " = " + totalOverflowArea);
                }

                Log.wtf("*- - Overflow Info", "Sprinkler " + (overflowInfo.getCirclePos()) + ((outside) ? " is outside" : " is inside")
                        + "\n\t\t\t\t\t\t\t\t\t\t\t\t\tThe angle is - " + (int) degrees + "\n\t\t\t\t\t\t\t\t\t\t\t\t\tTriangle Area - "
                        + triangleArea + "\n\t\t\t\t\t\t\t\t\t\t\t\t\tSector Area - " + sectorArea +
                        "\n\t\t\t\t\t\t\t\t\t\t\t\tTotal Area: " + totalArea + "\n\t\t\t\t\t\t\t\t\t\t\t    Final Area - " + wastedArea
                        + "\n\t\t\t\t\t\t\t Previous Wastage: " + previousWastage + "\n\t\t\t\t\t\t\t Overflow Wastage: "
                        + overFlowWastage + "\n\t\t\t\t\t\t\t Total Overflow Area: " + totalOverflowArea + "\n-----");
                //makeToast("Angle is: "  + angle + " Triangle Area: " + triangleArea);
                //outsideResults(counteR, centerX, centerY);
                if (dv.angleList.get(overflowInfo.getCirclePos()) > 316) {
                    fullCircleUsed.add(overflowInfo.getCirclePos());
                    //Log.wtf("*- Full circle used", "It was added");
                }
                counteR++;
                //Log.wtf("*-", ",\n");
            }
        }
        Log.wtf("*- Overflow Results: ", "Overflow Wastage: " + (int) overFlowWastage + " Total Area: " + (int) totalOverflowArea + "\n-");
    }

    private boolean outsideResults(int counteR, double centerX, double centerY) {
        int counter = 0;
        for (int b = 0; b < dv.xlist.size(); b++) {
            double startX = dv.xlist.get(b);
            double startY = dv.ylist.get(b);
            double endX = dv.xlist.get((b + 1 > dv.xlist.size() - 1 ? 0 : b + 1));
            double endY = dv.ylist.get((b + 1 > dv.ylist.size() - 1 ? 0 : b + 1));
            if (centerY >= Math.min(startY, endY) && centerY <= Math.max(startY, endY)) {
                double inverseSlope = (startX - endX) / (startY - endY);
                double expectedX = (-1 * inverseSlope * (startY - centerY)) + startX;
                Log.wtf("*- Sprink#- " + counteR + " Line#- " + b + " Outside Results - ", ",\n\t\t\t\t\t\t\t\t\t\tStart X: " + startX + " Start Y: " + startY
                        + "\n\t\t\t\t\t\t\t\t\t\tEnd X: " + endX + " End Y: " + endY + "\n\t\t\t\t\t\t\t\t\t\tCenter X: " + centerX + " Center Y: " + centerY
                        + "\n\t\t\t\t\t\t\t\t\t\tInverse Slope - " + (double) ((int) inverseSlope * 100) / 100d + " Expected X - " + (int) expectedX + " Start-Center: " + (startY - centerY));
                if (centerX < expectedX) {
                    counter++;
                }
            }
        }
        return counter % 2 == 0;

    }

    //INFO Possible problem could be with formula below. (inverseSlope * otherstuff). Or could be (startY -centerY) is opposite.
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

    //TODO Check if below function works
    private void getIndividualCircles2() {
        singleX.clear();
        singleY.clear();
        singleR.clear();
        singleP.clear();
        //Log.wtf("*Still getting circles", " Still GETTING CIRCLES");

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
            singleAngle.add(dv.angleList.get(0));
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
                    singleAngle.add(dv.angleList.get(i));
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

    //FUTURE FILES - Land Area is Circle: Water Wastage
    //INFO Work on this so that it also calculates the water wastage for when land area is circle.
    //  What you do is before iterateThroughPixels() is called, check pastMode to see if it was a
    //  circle or a polygon. If circle, then make a new function to call to display stats
    //  otherwise just call iteratethroughpixels.
    //INFO actually you may have to make the circle background white that way it works in iteratethroughpixels

    //Not important - Improve the sprinkler radius
    //INFO Sprinkler radius is very small compared to the side.
    // Make it so that it is Math.max of something that way it is larger. Maximum should always be at least 20 feet.
    // README
    //  min = 2, max >= 15-20 (Based on largest side length)

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

    //OLD - AlertDialog for getting side length
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
        Log.wtf("* Sprinkler INFO: ", "SINGLE SIZE: " + singleR.size() + "\tSprinkler Size: " + dv.sprinkx.size());

        //makeToast("SINGLE SIZE: " + singleR.size() + "\tSprinkler Size: " + dv.sprinkx.size());
        Log.wtf("*  Calculations 2", "WASTED: " + area + "  TOTAL WATER AREA: " + counter);
        if (singleR.size() == dv.sprinkx.size()) {
            //Do nothing.
        } else if (singleR.size() + 2 == dv.sprinkx.size()) {
//Calculate the intersection area of 2 circles.
            int[] temp = calculateWastage(true);
            area += temp[0];
            //INFO Increase the area of non-individual sprinklers.
            //counter += temp[1] - area;
            counter = temp[1];

            //makeToast("SINGLE SIZE: " + singleR.size() + "\tSprinkler Size: " + dv.sprinkx.size());
            Log.wtf("*  Calculations 2", "WASTED: " + area + "  TOTAL WATER AREA: " + counter);
        } else if (singleR.size() + 3 == dv.sprinkx.size()) {
            //Just double the intersection area of any 2 circles.
            int temp[] = calculateWastage(false);
            area += temp[0] * 2;
            //INFO Increase the area of non-individual sprinklers.
            counter += temp[1];
            //counter += temp[1] - area;
            // makeToast("SINGLE SIZE: " + singleR.size() + "\tSprinkler Size: " + dv.sprinkx.size());
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

                        //NOTE Keep this commented out below.
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

                                if ("4b".compareTo(pix) < 0 && "48".compareTo(pix) > 0)
                                    overlappingButOnly1SprinklerRegion++;
                                else if ("49a".compareTo(pix) < 0 && "51d".compareTo(pix) > 0)
                                    area += 4;
                                if ("4b".compareTo(pix) < 0 && "51d".compareTo(pix) > 0)
                                    area += 4;
                                else if ("54c5ff".compareTo(pix) < 0 && "54c8".compareTo(pix) > 0)
                                    area += 5;
                                else if ("6".compareTo(pix) < 0)
                                    area += 6;
                                else
                                    area += 0;

                                //OLD code to try to count whether 6 sprinklers are overlapping.
                                *//**//**//*if (tracker % 4 == 1) area += 1;
                            else if (tracker % 4 == 2) area += 2;
                                else area += 6;*//**//**//*
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
        //TODO CHeck if below is impacting the exact formula.
        if (dv.sprinkx.size() < 4) {
        } else if (dv.sprinkx.size() < 8)
            counter *= 1.85;
        else if (dv.sprinkx.size() < 12)
            counter *= 3.2;
        else if (dv.sprinkx.size() < 17)
            counter *= 4.15;
        else if (dv.sprinkx.size() < 25)
            counter *= 7.5;
        else
            counter *= 9.6;

        for (Map.Entry<String, Integer> entry : hm.entrySet()) {
            logger += "\n" + entry.toString();
            if (!entry.getKey().equals("000000") && !entry.getKey().equals("369646")) {
                //README This statement is making sure that counter is a sprinkler.
                if ("42".compareTo(entry.getKey()) < 0 && "65".compareTo(entry.getKey()) > 0)
                    counter += entry.getValue();
            }
        }
        //Log.wtf("*ITERATION STATUS:", "Done tallying");

        //INFO Calculate area of nonoverlapping individual circles
        int posCounter = 0;
        for (double m : singleR) {
            //README The last multiplication part was added to account for fact that the circles may not be full 360
            non += ((double) (Math.pow(m, 2) * Math.PI)) * (double) (dv.angleList.get(posCounter)) / 360f;
            //makeToast(non + " " + dv.angleList.get(posCounter));
            posCounter++;
        }


        hideLoading();
        //  makeToast("Got everything");


        Log.wtf("*                    ", "_-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-_");
//        Log.wtf("*   Pixels -  METHOD ----", logger);
//        Log.wtf("*  Not accepted ----", "O: " + output);

        //INFO Area of individual sprinklers calculated with formula.           NOT WASTED
        Log.wtf("*  Non overlap Sprinkler Area: ----", "" + non);
        //INFO Counter is the area of each sprinkler that overlaps. FULL AREA - multicounts overlapping
        Log.wtf("*  Everything Besides Individual (not non): --------", "" + counter);
        //INFO Area of intersecting sprinkler, nonoverlap part.                 NOT WASTED
        Log.wtf("*  Intersecting Sprinkler, But Only 1 Region: --------", "" + overlappingButOnly1SprinklerRegion);
        //INFO Amount of intersecting sprinkler, overlap part.
        Log.wtf("*    Water Being Wasted", "This much water being wasted: " + area);
        Log.wtf("*      TOTAL AMOUNT WASTED: -------", (((double) (area)) / ((double) (non + counter + overlappingButOnly1SprinklerRegion)) + "\n"));
        //DONE Calculate polygon area.
        Log.wtf("*      TOTAL AREA COVERED: --------", "" + ((double) (non + overlappingButOnly1SprinklerRegion + counter - area) / (dv.polygonArea())) + "\n");
        Log.wtf("*--------------------", "_-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-__-_-_-_");

        handleResults(non, counter, overlappingButOnly1SprinklerRegion, area, dv.polygonArea());
    }

    private void handleResults(final int non, final int counter, final int overlappingButOnly1SprinklerRegion, final int area, final double v) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.result);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button continueBtn = (Button) dialog.findViewById(R.id.continueBtn);
        Button backBtn = (Button) dialog.findViewById(R.id.backBtn);
        final RelativeLayout results = (RelativeLayout) dialog.findViewById(R.id.realresults);
        final RelativeLayout toHide = dialog.findViewById(R.id.questions);
        final EditText waterUsedE = dialog.findViewById(R.id.waterused);
        final EditText durationE = dialog.findViewById(R.id.duration);
        Button next = (Button) dialog.findViewById(R.id.continueBtn);
        final Spinner soilType = dialog.findViewById(R.id.soilType);
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

        //TODO rephrase question for how much water sprinkler uses to
        // How much water system uses (System always outputs same amount of water)
        // Then, change calculations (it will be easier now)

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
                Log.wtf("*  Progress", fgood + " " + sgood);

                if (!(fgood && sgood))
                    makeToast("Please fill in the information.");
                else {
                    //DONE IT is good to contineu ahead.
                    toHide.setVisibility(View.INVISIBLE);
                    results.setVisibility(View.VISIBLE);

                    Button goBack = dialog.findViewById(R.id.goBack);
                    Button done = dialog.findViewById(R.id.done);
                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    });

                    goBack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            toHide.setVisibility(View.VISIBLE);
                            results.setVisibility(View.INVISIBLE);
                        }
                    });

                    //DONE Just do the calculations to display the actual stuff.

                    //INFO README
                    // I think a better method of calculating coverage is simply totalWaterOutput *(1-percentWastage)
                    double percentWastage = ((double) (area)) / ((double) (non + counter + overlappingButOnly1SprinklerRegion));
                    double coverage = ((double) (non + overlappingButOnly1SprinklerRegion + counter - area) / (v));
                    if (coverage > 1) {
                        coverage = 0;
                        for (int r : dv.sprinkr) {
                            coverage += Math.PI * Math.pow(r, 2);
                        }
                        coverage -= coverage * percentWastage;
                        coverage = coverage / v;
                        if (coverage > 1)
                            coverage = 0.7678;
                    }
                    //double percentWastage = area / (non + counter + overlappingButOnly1SprinklerRegion);

                    //README Accounting for soil type
                    String choice = soilType.getSelectedItem().toString();
                    if (choice.equals("Sandy")) {
                        percentWastage *= 0.95f;
                    } else if (choice.equals("Loam")) {
                        percentWastage *= 0.9f;
                    } else if (choice.equals("Clay")) {
                        percentWastage *= 1.15f;
                    }

                    numSprink.setText(dv.sprinkx.size() + "");
                    Log.wtf("* Stats Nums: ", waterUsed + " " + duration + " " + coverage + " " + percentWastage);
                    numIntersect.setText("" + (dv.sprinkx.size() - singleX.size()));
                    totalLandA.setText(String.format("%1$,.2f", (v * Math.pow(dv.ratio, 2))) + " sq. ft");
                    Log.wtf("* INFO", coverage + " " + v + " " + dv.ratio);
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
        int firstAngle = 360, secondAngle = 360;
        boolean firstUsed = false;
        double totalArea = 0;
        for (int i = 0; i < dv.sprinkx.size(); i++) {
            if (!singleX.contains(dv.sprinkx.get(i))) {
                totalArea = Math.PI * Math.pow(dv.sprinkr.get(i), 2);
                if (!firstUsed) {
                    firstX = dv.sprinkx.get(i);
                    firstR = dv.sprinkr.get(i);
                    firstY = dv.sprinky.get(i);
                    firstAngle = dv.angleList.get(i);
                    firstUsed = true;
                    totalArea += Math.PI * Math.pow(dv.sprinkr.get(i), 2);
                } else {
                    secondX = dv.sprinkx.get(i);
                    secondR = dv.sprinkr.get(i);
                    secondY = dv.sprinky.get(i);
                    secondAngle = dv.angleList.get(i);
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
            //README Just added something to account for different angle sprinklers
            totalArea = (Math.PI * Math.pow(secondR, 2)) * ((double) (secondAngle) / 360) + (Math.PI * Math.pow(firstR, 2)) * ((double) (firstAngle) / 360);
        }

        if (firstAngle != 360 || secondAngle != 360) {

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
