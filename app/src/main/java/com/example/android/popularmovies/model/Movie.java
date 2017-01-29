package com.example.android.popularmovies.model;


import java.io.Serializable;

public class Movie implements Serializable {

    Integer id;
    String originalTitle;
    String posterPath;
    String overview;
    String voteAverage;
    String realeseDate;

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
                '}';
    }
}
