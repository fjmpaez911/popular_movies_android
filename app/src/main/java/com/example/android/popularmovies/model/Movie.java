package com.example.android.popularmovies.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Movie implements Parcelable {

    private Integer id;
    private String originalTitle;
    private String posterPath;
    private String overview;
    private String voteAverage;
    private String realeseDate;
    private List<Review> reviews;
    private List<Trailer> trailers;

    public Movie() {
    }

    public Movie(Integer id, String originalTitle, String posterPath) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
    }

    public Movie(Integer id, String originalTitle, String posterPath, String overview, String voteAverage, String realeseDate) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.realeseDate = realeseDate;
    }

    public Movie(Integer id, String originalTitle, String posterPath, String overview, String voteAverage, String realeseDate, List<Review> reviews, List<Trailer> trailers) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.realeseDate = realeseDate;
        this.reviews = reviews;
        this.trailers = trailers;
    }

    public Movie(Parcel in) {
        this.id = in.readInt();
        this.originalTitle = in.readString();
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.voteAverage = in.readString();
        this.realeseDate = in.readString();
        in.readList(this.reviews, null);
        in.readList(this.trailers, null);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getRealeseDate() {
        return realeseDate;
    }

    public void setRealeseDate(String realeseDate) {
        this.realeseDate = realeseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        return id.equals(movie.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", originalTitle='" + originalTitle + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", overview='" + overview + '\'' +
                ", voteAverage='" + voteAverage + '\'' +
                ", realeseDate='" + realeseDate + '\'' +
                ", reviews=" + reviews +
                ", trailers=" + trailers +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(voteAverage);
        dest.writeString(realeseDate);
        dest.writeList(reviews);
        dest.writeList(trailers);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int index) {
            return new Movie[index];
        }
    };
}
