package app.ij.errigation;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SprinklerAdapter extends ArrayAdapter<SprinklerInfo>{
    ArrayList<SprinklerInfo> arrayList;
    MainActivity context;

    public SprinklerAdapter(MainActivity context, ArrayList<SprinklerInfo> arrayList) {
        super(context, 0, arrayList);
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        SprinklerInfo sprinklerInfo = arrayList.get(i);

        view = LayoutInflater.from(context).inflate(R.layout.sprinkler_row, parent, false);
        RelativeLayout background = view.findViewById(R.id.background);
        if(i % 2 == 0)
            background.setBackgroundColor(Color.parseColor("#E5E5E7"));
        else
            background.setBackgroundColor(Color.parseColor("#D6EBFA"));

        TextView radius = (TextView) view.findViewById(R.id.radius);
        TextView quant = (TextView) view.findViewById(R.id.quantity);
        double r = sprinklerInfo.getRadius();
        r = Math.round(r * 2)/2d;
        String str = Double.toString(r);
        if(str.charAt(str.length()-1) == '0')
            str = str.substring(0, str.length()-2);

        radius.setText("" + str +"  ft");
        quant.setText("" + sprinklerInfo.getFrequency());
        return view;
    }
}
