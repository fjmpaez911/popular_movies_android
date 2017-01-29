package com.example.android.popularmovies.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private List<Movie> movies;
    private Context context;
    private final MovieAdapterOnClickHandler clickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Integer movieId);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView movieImageView;
        public final TextView movieTextView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            movieImageView = (ImageView) view.findViewById(R.id.iv_grid_item);
            movieTextView = (TextView) view.findViewById(R.id.tv_grid_item);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = movies.get(adapterPosition);
            clickHandler.onClick(movie.getId());
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        context = viewGroup.getContext();

        int layoutIdForListItem = R.layout.grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        String posterPath = movies.get(position).getPosterPath();
        String posterUrl = context.getResources().getString(R.string.movie_db_base_url_poster) + posterPath;

        Picasso.with(context).load(posterUrl).into(movieAdapterViewHolder.movieImageView);
        movieAdapterViewHolder.movieTextView.setText(movies.get(position).getOriginalTitle());

        Log.v(TAG, "Getting items");
    }

    @Override
    public int getItemCount() {

        if (movies != null) {
            return movies.size();
        }
        return 0;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

}
