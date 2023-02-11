import java.util.*;
import java.util.stream.Collectors;

public class PersonCollectionSlowImpl implements PersonCollection {
    private List<Person> people;

    public PersonCollectionSlowImpl() {
        this.people = new ArrayList<>();
    }

    @Override
    public boolean add(String email, String name, int age, String town) {
        if (this.find(email) == null) {
            Person person = new Person(email, name, age, town);
            this.people.add(person);
            return true;
        }

        return false;
    }

    @Override
    public int getCount() {
        return this.people.size();
    }

    @Override
    public boolean delete(String email) {
        return this.people.removeIf(p -> p.getEmail().equals(email));
    }

    @Override
    public Person find(String email) {
        for (Person person : this.people) {
            if (person.getEmail().equals(email)) {
                return person;
            }
        }

        return null;
    }

    @Override
    public Iterable<Person> findAll(String emailDomain) {
        return this.people
                .stream()
                .filter(p -> p.getEmail().substring(p.getEmail().lastIndexOf("@") + 1).equals(emailDomain))
                .sorted(Comparator.comparing(Person::getEmail))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Person> findAll(String name, String town) {
        return this.people
                .stream()
                .filter(p -> p.getName().equals(name)
                        && p.getTown().equals(town))
                .sorted(Comparator.comparing(Person::getEmail))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Person> findAll(int startAge, int endAge) {
        return this.people
                .stream()
                .filter(p -> startAge <= p.getAge()
                        && p.getAge() <= endAge)
                .sorted(Comparator.comparingInt(Person::getAge)
                        .thenComparing(Person::getEmail))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Person> findAll(int startAge, int endAge, String town) {
        return this.people
                .stream()
                .filter(p -> startAge <= p.getAge()
                        && p.getAge() <= endAge
                        && p.getTown().equals(town))
                .sorted(Comparator.comparingInt(Person::getAge)
                        .thenComparing(Person::getEmail))
                .collect(Collectors.toList());
    }
}