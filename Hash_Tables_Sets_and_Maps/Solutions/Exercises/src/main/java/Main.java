public class Main {
    public static void main(String[] args) {
        RoyaleArena arena = new RoyaleArena();

        arena.add(new Battlecard(
                1,
                CardType.MELEE,
                "Hans",
                8.1,
                2));

        arena.add(new Battlecard(
                2,
                CardType.MELEE,
                "Hans",
                8.1,
                5));

        arena.add(new Battlecard(
                3,
                CardType.MELEE,
                "Ivan",
                8.1,
                1));

        arena.add(new Battlecard(
                4,
                CardType.MELEE,
                "Hans",
                8.1,
                3));


        for (Battlecard battlecard : arena.getAllByNameAndSwag()) {
            System.out.println(battlecard);
        }
    }
}