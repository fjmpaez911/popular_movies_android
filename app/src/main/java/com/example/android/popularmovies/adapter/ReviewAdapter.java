package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Review;

import java.util.List;

public class ReviewAdapter extends BaseAdapter {

    List<Review> reviews;
    Context context;

    private static LayoutInflater layoutInflater;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.reviews = reviews;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (reviews != null) {
            return reviews.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = layoutInflater.inflate(R.layout.review, null);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.author = (TextView) row.findViewById(R.id.review_author);
        viewHolder.content = (TextView) row.findViewById(R.id.review_content);

        viewHolder.author.setText(reviews.get(position).getAuthor());
        viewHolder.content.setText(reviews.get(position).getContent());

        return row;

    }

    public static class ViewHolder {
        public TextView author;
        public TextView content;
    }

}
