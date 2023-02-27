package core;

import models.Planet;
import models.Star;

import java.util.*;

public class MilkyWay implements Galaxy {

    private static class PlanetsByMassThenDistance extends TreeSet<Planet> {

        PlanetsByMassThenDistance() {
            super(new TreeSet<>(Comparator.comparingInt(Planet::getMass)
                    .thenComparingInt(Planet::getDistanceFromStar)));
        }
    }

    private LinkedHashMap<Integer, Star> starsById;
    private Map<Star, Set<Planet>> planetsByStar;
    private TreeSet<Star> starsByLuminosity;

    private Map<Integer, Planet> planetsById;
    private Map<Planet, Star> starByPlanet;
    private TreeSet<Planet> planetsByMass;

    public MilkyWay() {
        this.starsById = new LinkedHashMap<>();
        this.planetsByStar = new LinkedHashMap<>();
        this.starsByLuminosity = new TreeSet<>(Comparator.comparingInt(Star::getLuminosity));

        this.planetsById = new HashMap<>();
        this.starByPlanet = new HashMap<>();
        this.planetsByMass = new TreeSet<>(Comparator.comparingInt(Planet::getMass));
    }

    @Override
    public void add(Star star) {
        if (this.contains(star)) {
            throw new IllegalArgumentException();
        }

        this.starsById.put(star.getId(), star);
        this.planetsByStar.put(star, new PlanetsByMassThenDistance());
        this.starsByLuminosity.add(star);
    }

    @Override
    public void add(Planet planet, Star star) {
        star = this.getStar(star.getId());

        if (this.contains(planet)) {
            throw new IllegalArgumentException();
        }

        this.planetsById.put(planet.getId(), planet);
        this.starByPlanet.put(planet, star);
        this.planetsByStar.get(star).add(planet);
        this.planetsByMass.add(planet);
    }

    @Override
    public boolean contains(Planet planet) {
        return this.planetsById.containsKey(planet.getId());
    }

    @Override
    public boolean contains(Star star) {
        return this.starsById.containsKey(star.getId());
    }

    @Override
    public Star getStar(int id) {
        Star star = this.starsById.get(id);

        if (star == null) {
            throw new IllegalArgumentException();
        }

        return star;
    }

    @Override
    public Planet getPlanet(int id) {
        Planet planet = this.planetsById.get(id);

        if (planet == null) {
            throw new IllegalArgumentException();
        }

        return planet;
    }

    @Override
    public Star removeStar(int id) {
        Star starToRemove = this.getStar(id);

        Iterable<Planet> planetsByStar = new ArrayList<>(this.getPlanetsByStarCollection(starToRemove));

        for (Planet planet : planetsByStar) {
            this.removePlanet(planet.getId());
        }

        Star removedStar = this.starsById.remove(id);

        if (removedStar == null) {
            throw new IllegalArgumentException();
        }

        this.starsByLuminosity.remove(removedStar);

        return removedStar;
    }

    @Override
    public Planet removePlanet(int id) {
        Planet removedPlanet = this.planetsById.remove(id);

        if (removedPlanet == null) {
            throw new IllegalArgumentException();
        }

        Star star = this.starByPlanet.remove(removedPlanet);
        this.planetsByStar.get(star).remove(removedPlanet);
        this.planetsByMass.remove(removedPlanet);

        return removedPlanet;
    }

    @Override
    public int countObjects() {
        return this.starsById.size() + this.planetsById.size();
    }

    @Override
    public Iterable<Planet> getPlanetsByStar(Star star) {
        return this.getPlanetsByStarCollection(star);
    }

    private Collection<Planet> getPlanetsByStarCollection(Star star) {
        star = getStar(star.getId());

        if (star == null) {
            return Collections.emptyList();
        }

        Set<Planet> planetsByMassThenDistance = this.planetsByStar.get(star);

        return Objects.requireNonNullElse(planetsByMassThenDistance, Collections.emptyList());
    }

    @Override
    public Iterable<Star> getStars() {
        return this.starsByLuminosity.descendingSet();
    }

    @Override
    public Iterable<Star> gerStarsInOrderOfDiscovery() {
        return this.starsById.values();
    }

    @Override
    public Iterable<Planet> getAllPlanetsByMass() {
        return this.planetsByMass.descendingSet();
    }

    @Override
    public Iterable<Planet> getAllPlanetsByDistanceFromStar(Star star) {
        return this.getPlanetsByStar(star);
    }

    @Override
    public Map<Star, Set<Planet>> getStarsAndPlanetsByOrderOfStarDiscoveryAndPlanetDistanceFromStarTneByPlanetMass() {
        return this.planetsByStar;
    }
}