import java.util.*;
import java.util.stream.Collectors;

public class PersonCollectionImpl implements PersonCollection {
    private static class NameTownKey {
        String name;
        String town;

        NameTownKey(String name, String town) {
            this.setName(name);
            this.setTown(town);
        }

        void setName(String name) {
            this.name = Objects.requireNonNullElse(name, "");
        }

        void setTown(String town) {
            this.town = Objects.requireNonNullElse(town, "");
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            NameTownKey other = (NameTownKey) o;
            return this.name.equals(other.name) && this.town.equals(other.town);
        }

        @Override
        public int hashCode() {
            return this.name.hashCode() + 31 * this.town.hashCode();
        }
    }

    private final Map<String, Person> peopleByEmail;

    // Indices
    private final Map<String, SortedMap<String, Person>> peopleByEmailDomain;
    private final Map<NameTownKey, SortedMap<String, Person>> peopleByNameTownCombination;
    private final TreeMap<Integer, SortedMap<String, Person>> peopleByAge;
    private final Map<String, SortedMap<Integer, SortedMap<String, Person>>> peopleByTownByAge;

    public PersonCollectionImpl() {
        this.peopleByEmail = new HashMap<>();

        this.peopleByEmailDomain = new HashMap<>();
        this.peopleByNameTownCombination = new HashMap<>();
        this.peopleByAge = new TreeMap<>();
        this.peopleByTownByAge = new HashMap<>();
    }

    @Override
    public boolean add(String email, String name, int age, String town) {
        if (this.peopleByEmail.containsKey(email)) {
            return false;
        }

        Person person = new Person(email, name, age, town);
        this.peopleByEmail.put(email, person);
        this.addToIndices(person);

        return true;
    }

    private void addToIndices(Person person) {
        String domain = getEmailDomain(person.getEmail());
        this.peopleByEmailDomain.putIfAbsent(domain, new TreeMap<>());
        this.peopleByEmailDomain.get(domain).put(person.getEmail(), person);

        NameTownKey nameTownKey = new NameTownKey(person.getName(), person.getTown());
        this.peopleByNameTownCombination.putIfAbsent(nameTownKey, new TreeMap<>());
        this.peopleByNameTownCombination.get(nameTownKey).put(person.getEmail(), person);

        int age = person.getAge();
        this.peopleByAge.putIfAbsent(age, new TreeMap<>());
        this.peopleByAge.get(age).put(person.getEmail(), person);

        String town = person.getTown();
        this.peopleByTownByAge.putIfAbsent(town, new TreeMap<>());
        this.peopleByTownByAge.get(town).putIfAbsent(age, new TreeMap<>());
        this.peopleByTownByAge.get(town).get(age).put(person.getEmail(), person);
    }

    @Override
    public boolean delete(String email) {
        Person removedPerson = this.peopleByEmail.remove(email);

        if (removedPerson != null) {
            removeFromIndices(removedPerson);
            return true;
        }

        return false;
    }

    private void removeFromIndices(Person person) {
        String email = person.getEmail();

        String domain = getEmailDomain(email);
        SortedMap<String, Person> domainSortedPeopleByEmail = this.peopleByEmailDomain.get(domain);
        if (domainSortedPeopleByEmail != null) {
            domainSortedPeopleByEmail.remove(email);
        }

        SortedMap<String, Person> nameTownSortedPeopleByEmail
                = this.peopleByNameTownCombination.get(new NameTownKey(person.getName(), person.getTown()));
        if (nameTownSortedPeopleByEmail != null) {
            nameTownSortedPeopleByEmail.remove(email);
        }

        int age = person.getAge();
        SortedMap<String, Person> ageSortedPeopleByEmail = this.peopleByAge.get(age);
        if (ageSortedPeopleByEmail != null) {
            ageSortedPeopleByEmail.remove(email);
        }

        String town = person.getTown();
        SortedMap<Integer, SortedMap<String, Person>> townAgeSortedPeopleByEmail = this.peopleByTownByAge.get(town);
        if (townAgeSortedPeopleByEmail != null) {
            SortedMap<String, Person> ageSortedPeople = townAgeSortedPeopleByEmail.get(age);
            if (ageSortedPeople != null) {
                ageSortedPeople.remove(email);
            }
        }
    }

    @Override
    public int getCount() {
        return this.peopleByEmail.size();
    }

    @Override
    public Person find(String email) {
        return this.peopleByEmail.get(email);
    }

    @Override
    public Iterable<Person> findAll(String emailDomain) {
        SortedMap<String, Person> people = this.peopleByEmailDomain.get(emailDomain);

        if (people == null) {
            return new ArrayList<>();
        }

        return people.values();
    }

    @Override
    public Iterable<Person> findAll(String name, String town) {
        SortedMap<String, Person> people = this.peopleByNameTownCombination.get(new NameTownKey(name, town));

        if (people == null) {
            return new ArrayList<>();
        }

        return people.values();
    }

    @Override
    public Iterable<Person> findAll(int startAge, int endAge) {
        return this.peopleByAge
                .entrySet()
                .stream()
                .filter(p -> p.getKey() >= startAge && p.getKey() <= endAge)
                .map(Map.Entry::getValue)
                .flatMap(ppl -> ppl
                        .values()
                        .stream()
                ).collect(Collectors.toList());
    }

    @Override
    public Iterable<Person> findAll(int startAge, int endAge, String town) {
        SortedMap<Integer, SortedMap<String, Person>> peopleByAgeByEmail = this.peopleByTownByAge.get(town);

        if (peopleByAgeByEmail == null) {
            return new ArrayList<>();
        }

        return peopleByAgeByEmail.entrySet()
                .stream()
                .filter(p -> p.getKey() >= startAge && p.getKey() <= endAge)
                .map(Map.Entry::getValue)
                .flatMap(ppl -> ppl
                        .values()
                        .stream()
                ).collect(Collectors.toList());
    }

    private String getEmailDomain(String email) {
        return email.substring(email.lastIndexOf('@') + 1);
    }
}