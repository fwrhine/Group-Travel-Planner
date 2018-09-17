package com.example.aghniaprawira.ppl;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aghniaprawira.ppl.Group;
import com.example.aghniaprawira.ppl.R;

public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final Group[] groups;

    public CustomList(Activity context, Group[] groups) {
        super(context, R.layout.row);
        this.context = context;
        this.groups = groups;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.row, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        //set text
        txtTitle.setText(groups[position].getGroup_name());

        //set image
        byte[] byteArray = groups[position].getGroup_image();
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView.setImageBitmap(bmp);

        return rowView;
    }
}
