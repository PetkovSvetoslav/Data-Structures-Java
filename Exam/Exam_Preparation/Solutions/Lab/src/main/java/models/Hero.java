package models;

public class Hero {
    private HeroType heroType;
    private int level;
    private String name;
    private int points;

    public Hero(HeroType heroType, int level, String name, int points) {
        this.heroType = heroType;
        this.level = level;
        this.name = name;
        this.points = points;
    }

    public HeroType getHeroType() {
        return this.heroType;
    }

    public void setHeroType(HeroType heroType) {
        this.heroType = heroType;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}