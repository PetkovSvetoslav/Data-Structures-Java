public class Main {
    public static void main(String[] args) {
        HashTable<String, Integer> table = new HashTable<>();

        for (int i = 1; i <= 1000; i++) {
            table.put("S" + i, i);
        }

        for (KeyValue<String, Integer> element : table) {
            System.out.println(element.getKey() + " -> " + element.getValue());
        }
    }
}