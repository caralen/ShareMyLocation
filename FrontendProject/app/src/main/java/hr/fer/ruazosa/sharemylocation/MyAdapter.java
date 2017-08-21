package hr.fer.ruazosa.sharemylocation;

/**
 * Created on 6/26/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends ArrayAdapter<Model> {

    private final Context context;
    private final ArrayList<Model> modelsArrayList;

    public MyAdapter(Context context, ArrayList<Model> modelsArrayList) {

        super(context, R.layout.listelement, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView = null;
        rowView = inflater.inflate(R.layout.listelement, parent, false);

        // 3. Get icon,title & counter views from the rowView
        CircleImageView imgView = (CircleImageView) rowView.findViewById(R.id.circleImg);
        TextView titleView = (TextView) rowView.findViewById(R.id.grName);


        // 4. Set the text for textView
        imgView.setImageBitmap(decodeBase64(modelsArrayList.get(position).getIcon()));
        titleView.setText(modelsArrayList.get(position).getName());


        // 5. return rowView
        return rowView;

    }

    private Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}