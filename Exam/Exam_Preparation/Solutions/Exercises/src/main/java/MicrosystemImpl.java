import java.util.*;
import java.util.stream.Collectors;

public class MicrosystemImpl implements Microsystem {
    private static class ComputersByPriceDesc {
        TreeSet<Computer> computersByPriceByNumber;

        ComputersByPriceDesc() {
            this.computersByPriceByNumber = new TreeSet<>(
                    Comparator.comparing(Computer::getPrice).reversed()
                            .thenComparing(Computer::getNumber)
            );
        }

        void add(Computer computer) {
            this.computersByPriceByNumber.add(computer);
        }

        void remove(Computer computer) {
            this.computersByPriceByNumber.remove(computer);
        }

        Collection<Computer> getAll() {
            return this.computersByPriceByNumber;
        }
    }

    private Map<Integer, Computer> computersByNumber;

    // Indices
    private Map<Brand, HashMap<Integer, Computer>> computersByBrand;
    private Map<Brand, ComputersByPriceDesc> computersByBrandByPriceDesc;
    private Map<Double, SortedMap<Integer, Computer>> computersByScreenSize;
    private Map<String, ComputersByPriceDesc> computersByColorByPriceDesc;
    private TreeMap<Double, TreeMap<Integer, Computer>> computersByPriceDesc;

    public MicrosystemImpl() {
        this.computersByNumber = new HashMap<>();

        this.computersByBrand = new HashMap<>();
        this.computersByBrandByPriceDesc = new HashMap<>();
        this.computersByScreenSize = new HashMap<>();
        this.computersByColorByPriceDesc = new HashMap<>();
        this.computersByPriceDesc = new TreeMap<>((a, b) -> Double.compare(b, a));
    }

    @Override
    public void createComputer(Computer computer) {
        if (computer == null || this.contains(computer.getNumber())) {
            throw new IllegalArgumentException();
        }

        this.computersByNumber.put(computer.getNumber(), computer);
        this.addToIndices(computer);
    }

    private void addToIndices(Computer computer) {
        this.computersByBrand.computeIfAbsent(computer.getBrand(), k -> new HashMap<>()).put(computer.getNumber(), computer);
        this.computersByBrandByPriceDesc.computeIfAbsent(computer.getBrand(), k -> new ComputersByPriceDesc()).add(computer);
        this.computersByScreenSize.computeIfAbsent(computer.getScreenSize(), K -> new TreeMap<>((s1, s2) -> Double.compare(s2, s1))).put(computer.getNumber(), computer);
        this.computersByColorByPriceDesc.computeIfAbsent(computer.getColor().toLowerCase(), k -> new ComputersByPriceDesc()).add(computer);
        this.computersByPriceDesc.computeIfAbsent(computer.getPrice(), k -> new TreeMap<>()).put(computer.getNumber(), computer);
    }

    private void removeFromIndices(Computer computer) {
        HashMap<Integer, Computer> computerByBrand = this.computersByBrand.get(computer.getBrand());
        if (computerByBrand != null) {
            computerByBrand.remove(computer.getNumber());
        }

        ComputersByPriceDesc computersByPriceDesc = this.computersByBrandByPriceDesc.get(computer.getBrand());
        if (computersByPriceDesc != null) {
            computersByPriceDesc.remove(computer);
        }

        SortedMap<Integer, Computer> computersByScreen = this.computersByScreenSize.get(computer.getScreenSize());
        if (computersByScreen != null) {
            computersByScreen.remove(computer.getNumber());
        }

        ComputersByPriceDesc computersByColor = this.computersByColorByPriceDesc.get(computer.getColor());
        if (computersByColor != null) {
            computersByColor.remove(computer);
        }
        TreeMap<Integer, Computer> byPriceDesc = this.computersByPriceDesc.get(computer.getPrice());
        if (byPriceDesc != null) {
            byPriceDesc.remove(computer.getNumber());
        }
    }

    @Override
    public boolean contains(int number) {
        return this.computersByNumber.containsKey(number);
    }

    @Override
    public int count() {
        return this.computersByNumber.size();
    }

    @Override
    public Computer getComputer(int number) {
        Computer computer = this.computersByNumber.get(number);

        if (computer == null) {
            throw new IllegalArgumentException();
        }

        return computer;
    }

    @Override
    public void remove(int number) {
        Computer removedComputer = this.computersByNumber.remove(number);

        if (removedComputer == null) {
            throw new IllegalArgumentException();
        }

        this.removeFromIndices(removedComputer);
    }

    @Override
    public void removeWithBrand(Brand brand) {
        HashMap<Integer, Computer> removedComputers = this.computersByBrand.remove(brand);

        if (removedComputers == null) {
            throw new IllegalArgumentException();
        }

        for (Integer number : removedComputers.keySet()) {
            this.remove(number);
        }
    }

    @Override
    public void upgradeRam(int ram, int number) {
        Computer computer = this.getComputer(number);

        if (ram > computer.getRAM()) {
            computer.setRAM(ram);
        }
    }

    @Override
    public Iterable<Computer> getAllFromBrand(Brand brand) {
        return this.computersByBrandByPriceDesc.computeIfAbsent(brand, k -> new ComputersByPriceDesc()).getAll();
    }

    @Override
    public Iterable<Computer> getAllWithScreenSize(double screenSize) {
        return this.computersByScreenSize.computeIfAbsent(screenSize, k -> new TreeMap<>()).values();
    }

    @Override
    public Iterable<Computer> getAllWithColor(String color) {
        return this.computersByColorByPriceDesc.computeIfAbsent(color.toLowerCase(), k -> new ComputersByPriceDesc()).getAll();
    }

    @Override
    public Iterable<Computer> getInRangePrice(double minPrice, double maxPrice) {
        NavigableMap<Double, TreeMap<Integer, Computer>> map = this.computersByPriceDesc.subMap(minPrice, true, maxPrice, true);

        List<Computer> result = new ArrayList<>();
        for (TreeMap<Integer, Computer> value : map.values()) {
            result.addAll(value.values());

        }

        return result;
    }
}