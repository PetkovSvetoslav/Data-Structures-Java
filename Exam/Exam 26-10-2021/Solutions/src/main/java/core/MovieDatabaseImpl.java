package core;

import models.Movie;

import java.util.*;
import java.util.stream.Collectors;

public class MovieDatabaseImpl implements MovieDatabase {
    //            Id    Movie
    private Map<String, Movie> moviesById;
    //          Actor   Movies By rating desc then release year desc
    private Map<String, TreeSet<Movie>> moviesByActor;
    //              Rating  Movies          Id    Movie
    private TreeMap<Double, LinkedHashMap<String, Movie>> moviesByRating;
    //          Year             Rating                  Id    Movie
    private Map<Integer, TreeMap<Double, LinkedHashMap<String, Movie>>> moviesByYear;

    public MovieDatabaseImpl() {
        this.moviesById = new LinkedHashMap<>();
        this.moviesByActor = new LinkedHashMap<>();
        this.moviesByRating = new TreeMap<>();
        this.moviesByYear = new HashMap<>();
    }

    @Override
    public void addMovie(Movie movie) {
        this.moviesById.put(movie.getId(), movie);

        this.addToIndices(movie);
    }

    private void addToIndices(Movie movie) {
        for (String actor : movie.getActors()) {
            this.moviesByActor.computeIfAbsent(actor, k ->
                    new TreeSet<>(Comparator.comparingDouble(Movie::getRating)
                            .thenComparingInt(Movie::getReleaseYear).reversed()
                            .thenComparing(Movie::getId))).add(movie);
        }

        this.moviesByRating.computeIfAbsent(movie.getRating(), k -> new LinkedHashMap<>()).put(movie.getId(), movie);

        this.moviesByYear.computeIfAbsent(movie.getReleaseYear(), k -> new TreeMap<>())
                .computeIfAbsent(movie.getRating(), l -> new LinkedHashMap<>())
                .put(movie.getId(), movie);
    }

    @Override
    public void removeMovie(String movieId) {
        if (!this.contains(movieId)) {
            throw new IllegalArgumentException();
        }

        Movie removedMovie = this.moviesById.remove(movieId);

        this.removeFromIndices(removedMovie);
    }

    private void removeFromIndices(Movie movieToRemove) {
        String id = movieToRemove.getId();

        for (String actor : movieToRemove.getActors()) {
            TreeSet<Movie> movies = this.moviesByActor.get(actor);

            if (movies.size() == 1) {
                this.moviesByActor.remove(actor);
            } else {
                movies.remove(movieToRemove);
            }
        }

        double rating = movieToRemove.getRating();
        LinkedHashMap<String, Movie> moviesByEntrance = this.moviesByRating.get(rating);

        if (moviesByEntrance.size() == 1) {
            this.moviesByRating.remove(rating);
        } else {
            moviesByEntrance.remove(id);
        }

        int releaseYear = movieToRemove.getReleaseYear();
        this.moviesByYear.get(releaseYear).get(rating).remove(id);
    }

    @Override
    public int size() {
        return this.moviesById.size();
    }

    @Override
    public boolean contains(Movie movie) {
        return this.contains(movie.getId());
    }

    private boolean contains(String id) {
        return this.moviesById.containsKey(id);
    }

    @Override
    public Iterable<Movie> getMoviesByActor(String actorName) {
        TreeSet<Movie> movies = this.moviesByActor.get(actorName);

        if (movies == null || movies.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return movies;
    }

    @Override
    public Iterable<Movie> getMoviesByActors(List<String> actors) {
        if (actors.isEmpty()) {
            throw new IllegalArgumentException();
        }

        Iterator<String> iterator = actors.iterator();
        TreeSet<Movie> movies = this.moviesByActor.get(iterator.next());

        if (movies == null) {
            throw new IllegalArgumentException();
        }

        List<Movie> result = new LinkedList<>(movies);

        while (!result.isEmpty() && iterator.hasNext()) {
            TreeSet<Movie> currentMovies = this.moviesByActor.get(iterator.next());
            result.removeIf(m -> !currentMovies.contains(m));
        }

        if (result.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return result;
    }

    @Override
    public Iterable<Movie> getMoviesByYear(Integer releaseYear) {
        if (this.moviesByYear.isEmpty() || this.moviesByYear.get(releaseYear).isEmpty()) {
            return Collections.emptyList();
        }

        return this.moviesByYear
                .get(releaseYear)
                .descendingMap()
                .values()
                .stream()
                .flatMap(k -> k
                        .values()
                        .stream())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Movie> getMoviesInRatingRange(double lowerBound, double upperBound) {
        return this.moviesByRating
                .subMap(lowerBound, true, upperBound, true)
                .descendingMap()
                .values()
                .stream()
                .flatMap(l -> l.values().stream())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Movie> getAllMoviesOrderedByActorPopularityThenByRatingThenByYear() {
        return this.moviesById
                .values()
                .stream()
                .sorted((m1, m2) -> {
                    int firstAmount = this.getTotalAmountOfMoviesWitchActorsPlayed(m1.getActors());
                    int secondAmount = this.getTotalAmountOfMoviesWitchActorsPlayed(m2.getActors());

                    int result = secondAmount - firstAmount;

                    if (result == 0) {
                        result = Double.compare(m2.getRating(), m1.getRating());
                    }

                    if (result == 0) {
                        result = Integer.compare(m2.getReleaseYear(), m1.getReleaseYear());
                    }

                    return result;
                })
                .collect(Collectors.toList());
    }

    private int getTotalAmountOfMoviesWitchActorsPlayed(List<String> actors) {
        int totalAmount = 0;

        for (String actor : actors) {
            TreeSet<Movie> movies = this.moviesByActor.get(actor);
            if (movies != null) {
                totalAmount += movies.size();
            }
        }

        return totalAmount;
    }
}