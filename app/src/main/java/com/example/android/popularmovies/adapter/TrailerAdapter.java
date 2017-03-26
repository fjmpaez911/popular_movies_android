package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Trailer;

import java.util.List;

public class TrailerAdapter extends BaseAdapter {

    List<Trailer> trailers;
    Context context;

    private static LayoutInflater layoutInflater;

    public TrailerAdapter(Context context, List<Trailer> trailers) {
        this.trailers = trailers;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (trailers != null) {
            return trailers.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return trailers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = layoutInflater.inflate(R.layout.trailer, null);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.name = (TextView) row.findViewById(R.id.trailer_name);
        viewHolder.image = (ImageView) row.findViewById(R.id.trailer_image);

        viewHolder.name.setText(trailers.get(position).getName());
        viewHolder.image.setImageResource(R.drawable.trailer_icon);

        return row;

    }

    public static class ViewHolder {
        public TextView name;
        public ImageView image;
    }

}
