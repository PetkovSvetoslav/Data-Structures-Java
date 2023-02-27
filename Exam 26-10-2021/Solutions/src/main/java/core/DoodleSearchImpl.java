package core;

import models.Doodle;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DoodleSearchImpl implements DoodleSearch {
    //            Id    Doodle
    private Map<String, Doodle> doodlesById;
    //          Title   Doodle
    private Map<String, Doodle> doodlesByTitle;
    //            Id    Doodle
    private Map<String, Doodle> ads;

    public DoodleSearchImpl() {
        this.doodlesById = new LinkedHashMap<>();
        this.doodlesByTitle = new LinkedHashMap<>();
        this.ads = new LinkedHashMap<>();
    }

    @Override
    public void addDoodle(Doodle doodle) {
        this.doodlesById.put(doodle.getId(), doodle);
        this.doodlesByTitle.put(doodle.getTitle(), doodle);

        if (doodle.getIsAd()) {
            ads.put(doodle.getId(), doodle);
        }
    }

    @Override
    public void removeDoodle(String doodleId) {
        Doodle removedDoodle = this.doodlesById.remove(doodleId);

        if (removedDoodle == null) {
            throw new IllegalArgumentException();
        }

        this.doodlesByTitle.remove(removedDoodle.getTitle());

        if (removedDoodle.getIsAd()) {
            this.ads.remove(doodleId);
        }
    }

    @Override
    public int size() {
        return this.doodlesById.size();
    }

    @Override
    public boolean contains(Doodle doodle) {
        return this.doodlesById.containsKey(doodle.getId());
    }

    @Override
    public Doodle getDoodle(String id) {
        Doodle doodle = this.doodlesById.get(id);

        if (doodle == null) {
            throw new IllegalArgumentException();
        }

        return doodle;
    }

    @Override
    public double getTotalRevenueFromDoodleAds() {
        return this.ads
                .values()
                .stream()
                .mapToDouble(d -> d.getRevenue() * d.getVisits())
                .sum();
    }

    @Override
    public void visitDoodle(String title) {
        Doodle searchedDoodle = this.doodlesByTitle.get(title);

        if (searchedDoodle == null) {
            throw new IllegalArgumentException();
        }

        searchedDoodle.setVisits(searchedDoodle.getVisits() + 1);
    }

    @Override
    public Iterable<Doodle> searchDoodles(String searchQuery) {
        return this.doodlesByTitle
                .entrySet()
                .stream()
                .filter(e -> e.getKey().contains(searchQuery))
                .map(Map.Entry::getValue)
                .sorted((d1, d2) -> {
                    int result = Boolean.compare(d2.getIsAd(), d1.getIsAd());

                    if (result == 0) {
                        result = d1.getTitle().indexOf(searchQuery) - d2.getTitle().indexOf(searchQuery);
                    }

                    if (result == 0) {
                        result = d2.getVisits() - d1.getVisits();
                    }

                    return result;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Doodle> getDoodleAds() {
        return this.ads
                .values()
                .stream()
                .sorted(Comparator.comparingDouble(Doodle::getRevenue)
                        .thenComparingInt(Doodle::getVisits).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Doodle> getTop3DoodlesByRevenueThenByVisits() {
        return this.doodlesById
                .values()
                .stream()
                .sorted(Comparator.comparingDouble(Doodle::getRevenue)
                        .thenComparingInt(Doodle::getVisits).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }
}