package core;

import models.Hero;
import models.HeroType;

import java.util.*;
import java.util.stream.Collectors;

public class HeroDatabaseImpl implements HeroDatabase {

    private static class HeroesOrderedByName {
        SortedMap<String, Hero> sortedHeroes;

        HeroesOrderedByName() {
            this.sortedHeroes = new TreeMap<>();
        }

        int size() {
            return this.sortedHeroes.size();
        }

        void add(Hero hero) {
            this.sortedHeroes.put(hero.getName(), hero);
        }

        void remove(String name) {
            this.sortedHeroes.remove(name);
        }

        void remove(Hero hero) {
            this.remove(hero.getName());
        }

        Collection<Hero> getAll() {
            return this.sortedHeroes.values();
        }
    }

    private static class HeroesOrderedByLevelThenName {
        TreeSet<Hero> heroesByLevelThenName;

        HeroesOrderedByLevelThenName(boolean levelAscending) {
            Comparator<Hero> heroComparator;

            if (levelAscending) {
                heroComparator = Comparator.comparingInt(Hero::getLevel)
                        .thenComparing(Hero::getName);
            } else {
                heroComparator = Comparator.comparingInt(Hero::getLevel).reversed()
                        .thenComparing(Hero::getName);
            }

            this.heroesByLevelThenName = new TreeSet<>(heroComparator);
        }

        int size() {
            return this.heroesByLevelThenName.size();
        }

        void add(Hero hero) {
            this.heroesByLevelThenName.add(hero);
        }

        void remove(Hero hero) {
            this.heroesByLevelThenName.remove(hero);
        }

        Collection<Hero> getAll() {
            return this.heroesByLevelThenName;
        }
    }

    private Map<String, Hero> heroesByName;

    // Indices
    private Map<HeroType, HeroesOrderedByName> heroesByType;
    private Map<Integer, HeroesOrderedByName> heroesByLevel;
    private TreeMap<Integer, HeroesOrderedByLevelThenName> heroesByPoints;
    HeroesOrderedByLevelThenName heroesByLevelDescThenByName;

    public HeroDatabaseImpl() {
        this.heroesByName = new HashMap<>();

        this.heroesByType = new HashMap<>();
        this.heroesByLevel = new HashMap<>();
        this.heroesByPoints = new TreeMap<>();
        this.heroesByLevelDescThenByName = new HeroesOrderedByLevelThenName(false);
    }

    @Override
    public void addHero(Hero hero) {
        if (this.contains(hero)) {
            throw new IllegalArgumentException();
        }

        this.heroesByName.put(hero.getName(), hero);

        this.addToIndices(hero);
    }

    private void addToIndices(Hero hero) {
        this.heroesByType.computeIfAbsent(hero.getHeroType(), k -> new HeroesOrderedByName()).add(hero);
        this.heroesByLevel.computeIfAbsent(hero.getLevel(), k -> new HeroesOrderedByName()).add(hero);
        this.heroesByPoints.computeIfAbsent(hero.getPoints(), k -> new HeroesOrderedByLevelThenName(true)).add(hero);
        this.heroesByLevelDescThenByName.add(hero);
    }

    @Override
    public boolean contains(Hero hero) {
        return this.contains(hero.getName());
    }

    private boolean contains(String name) {
        return this.heroesByName.containsKey(name);
    }

    @Override
    public int size() {
        return this.heroesByName.size();
    }

    @Override
    public Hero getHero(String name) {
        Hero hero = this.heroesByName.get(name);

        if (hero == null) {
            throw new IllegalArgumentException();
        }

        return hero;
    }

    @Override
    public Hero remove(String name) {
        Hero removedHero = this.heroesByName.remove(name);

        if (removedHero == null) {
            throw new IllegalArgumentException();
        }

        this.removeFromIndices(removedHero);

        return removedHero;
    }

    private void removeFromIndices(Hero hero) {
        HeroType heroType = hero.getHeroType();
        int level = hero.getLevel();
        int points = hero.getPoints();

        HeroesOrderedByName byType = this.heroesByType.get(heroType);
        if (byType.size() == 1) {
            this.heroesByType.remove(heroType);
        } else {
            byType.remove(hero);
        }

        HeroesOrderedByName byLevel = this.heroesByLevel.get(level);
        if (byLevel.size() == 1) {
            this.heroesByLevel.remove(level);
        } else {
            byLevel.remove(hero);
        }

        HeroesOrderedByLevelThenName byPoints = this.heroesByPoints.get(points);
        if (byPoints.size() == 1) {
            this.heroesByPoints.remove(points);
        } else {
            byPoints.remove(hero);
        }

        this.heroesByLevelDescThenByName.remove(hero);
    }

    @Override
    public Iterable<Hero> removeAllByType(HeroType type) {
        HeroesOrderedByName removedHeroes = this.heroesByType.remove(type);

        if (removedHeroes == null) {
            return Collections.emptyList();
        }

        for (Hero hero : removedHeroes.getAll()) {
            this.remove(hero.getName());
        }

        return removedHeroes.getAll();
    }

    @Override
    public void levelUp(String name) {
        Hero hero = this.getHero(name);

        int newLevel = hero.getLevel() + 1;
        hero.setLevel(newLevel);

        this.remove(name);
        this.addHero(hero);
    }

    @Override
    public void rename(String oldName, String newName) {
        if (this.contains(newName)) {
            throw new IllegalArgumentException();
        }

        Hero hero = this.remove(oldName);
        hero.setName(newName);
        this.addHero(hero);
    }

    @Override
    public Iterable<Hero> getAllByType(HeroType type) {
        HeroesOrderedByName heroesOrderedByName = this.heroesByType.get(type);

        if (heroesOrderedByName == null) {
            return Collections.emptyList();
        }

        return heroesOrderedByName.getAll();
    }

    @Override
    public Iterable<Hero> getAllByLevel(int level) {
        return this.heroesByLevel.getOrDefault(level, new HeroesOrderedByName()).getAll();
    }

    @Override
    public Iterable<Hero> getAllInPointsRange(int lowerBound, int upperBound) {
        return this.heroesByPoints.subMap(lowerBound, true, upperBound, true)
                .descendingMap()
                .values()
                .stream()
                .flatMap(l -> l
                        .getAll()
                        .stream())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Hero> getAllOrderedByLevelDescendingThenByName() {
        return this.heroesByLevelDescThenByName.getAll();
    }
}