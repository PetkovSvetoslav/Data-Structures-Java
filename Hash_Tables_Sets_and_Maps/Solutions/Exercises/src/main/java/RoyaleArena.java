import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RoyaleArena implements IArena {
    private final Map<Integer, Battlecard> battlecardsById;

    public RoyaleArena() {
        this.battlecardsById = new LinkedHashMap<>();
    }

    @Override
    public void add(Battlecard card) {
        this.battlecardsById.put(card.getId(), card);
    }

    @Override
    public boolean contains(Battlecard card) {
        return this.battlecardsById.containsKey(card.getId());
    }

    @Override
    public int count() {
        return this.battlecardsById.size();
    }

    @Override
    public void changeCardType(int id, CardType type) {
        Battlecard battlecard = this.battlecardsById.get(id);

        if (battlecard == null) {
            throw new IllegalArgumentException();
        }

        battlecard.setType(type);
    }

    @Override
    public Battlecard getById(int id) {
        Battlecard battlecard = this.battlecardsById.get(id);

        if (battlecard == null) {
            throw new UnsupportedOperationException();
        }

        return battlecard;
    }

    @Override
    public void removeById(int id) {
        if (this.battlecardsById.get(id) == null) {
            throw new UnsupportedOperationException();
        }

        this.battlecardsById.remove(id);
    }

    @Override
    public Iterable<Battlecard> getByCardType(CardType type) {
        Predicate<Battlecard> withType = c -> c.getType().equals(type);

        return this.getCards(withType, compareByDamageDescThenById());
    }

    @Override
    public Iterable<Battlecard> getByTypeAndDamageRangeOrderedByDamageThenById(CardType type, int low, int high) {
        Predicate<Battlecard> withTypeInBounds = c -> c.getType().equals(type)
                && c.getDamage() > low
                && c.getDamage() < high;

        return this.getCards(withTypeInBounds, compareByDamageDescThenById());
    }

    @Override
    public Iterable<Battlecard> getByCardTypeAndMaximumDamage(CardType type, double damage) {
        Predicate<Battlecard> byTypeWithMaxDamage = c -> c.getType().equals(type) && c.getDamage() <= damage;

        return this.getCards(byTypeWithMaxDamage, compareByDamageDescThenById());
    }

    @Override
    public Iterable<Battlecard> getByNameOrderedBySwagDescending(String name) {
        Predicate<Battlecard> withName = c -> c.getName().equals(name);

        return this.getCards(withName, compareBySwagDescThenById());
    }

    @Override
    public Iterable<Battlecard> getByNameAndSwagRange(String name, double low, double high) {
        Predicate<Battlecard> withNameInBounds = c -> c.getName().equals(name)
                && c.getSwag() >= low
                && c.getSwag() < high;

        return this.getCards(withNameInBounds, compareBySwagDescThenById());
    }

    @Override
    public Iterable<Battlecard> getAllByNameAndSwag() {
        Map<String, Battlecard> cardsByName = new LinkedHashMap<>();

        for (Battlecard battlecard : this.battlecardsById.values()) {
            String name = battlecard.getName();
            if (cardsByName.get(name) == null || battlecard.getSwag() > cardsByName.get(name).getSwag()) {
                cardsByName.put(name, battlecard);
            }
        }

        return cardsByName.values();
    }

    @Override
    public Iterable<Battlecard> findFirstLeastSwag(int n) {
        List<Battlecard> battlecards = this.battlecardsById
                .values()
                .stream()
                .sorted(
                        Comparator.comparingDouble(Battlecard::getSwag)
                                .thenComparingInt(Battlecard::getId))
                .limit(n)
                .collect(Collectors.toList());

        if (battlecards.size() < n) {
            throw new UnsupportedOperationException();
        }

        return battlecards;
    }

    @Override
    public Iterable<Battlecard> getAllInSwagRange(double low, double high) {
        return this.battlecardsById
                .values()
                .stream()
                .filter(c -> c.getSwag() >= low && c.getSwag() <= high)
                .sorted(Comparator.comparingDouble(Battlecard::getSwag))
                .collect(Collectors.toList());
    }

    @Override
    public Iterator<Battlecard> iterator() {
        return this.battlecardsById.values().iterator();
    }

    private Comparator<Battlecard> compareBySwagDescThenById() {
        return (c1, c2) -> {
            int compare = Double.compare(c2.getSwag(), c1.getSwag());

            if (compare == 0) {
                compare = Integer.compare(c1.getId(), c2.getId());
            }

            return compare;
        };
    }

    private Comparator<Battlecard> compareByDamageDescThenById() {
        return (c1, c2) -> {
            int compare = Double.compare(c2.getDamage(), c1.getDamage());

            if (compare == 0) {
                compare = Integer.compare(c1.getId(), c2.getId());
            }

            return compare;
        };
    }

    private Iterable<Battlecard> getCards(Predicate<Battlecard> predicate, Comparator<Battlecard> comparator) {
        List<Battlecard> result = this.battlecardsById
                .values()
                .stream()
                .filter(predicate)
                .sorted(comparator)
                .collect(Collectors.toList());

        this.ensureNonEmpty(result);

        return result;
    }

    private void ensureNonEmpty(Collection collection) {
        if (collection.isEmpty()) {
            throw new UnsupportedOperationException();
        }
    }
}