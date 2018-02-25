package com.alexandruro.recyclinghelper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ShoppingListAdapter extends ArrayAdapter<ShoppingItem> {
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.shopping_item, null, false);
        }

        ((ImageView)convertView.findViewById(R.id.item_image)).setImageBitmap(getItem(position).getImage());
        ((TextView)convertView.findViewById(R.id.item_name)).setText(getItem(position).getName());
        ((TextView)convertView.findViewById(R.id.item_index)).setText(String.valueOf(position+1));

        return convertView;

    }

    public ShoppingListAdapter(@NonNull Context context, ArrayList<ShoppingItem> objects) {
        super(context, 0, objects);
    }
}
