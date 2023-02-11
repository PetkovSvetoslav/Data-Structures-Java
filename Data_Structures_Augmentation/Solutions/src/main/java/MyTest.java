public class MyTest {
    public static void main(String[] args) {
        PersonCollectionImpl people = new PersonCollectionImpl();

        people.add("angel@gmail.com", "Ange", 20, "Sofia");
        people.add("ivan@outlook.com", "Ivan", 20, "Sofia");
        people.add("georgi@gmail.com", "Georgi", 20, "Sofia");
        people.add("martin@abv.bg", "Martin", 20, "Sofia");

        System.out.println(people.delete("angel@gmail.com"));

        for (Person person : people.findAll("gmail.com")) {
            System.out.println(person);
        }
    }
}