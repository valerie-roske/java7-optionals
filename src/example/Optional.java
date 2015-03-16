package example;

import static com.sun.xml.internal.ws.util.StringUtils.capitalize;

interface Function<Input, Output> {
    Output apply(Input input);
}

abstract class Optional<Value> {

    public abstract Value orElse(Value other);

    public abstract Value get() throws IllegalStateException;

    public abstract <MappedValue> Optional<MappedValue> map(Function<? super Value, ? extends MappedValue> fn);

    private static final class Present<Value> extends Optional<Value> {
        private final Value value;

        private Present(Value value) {
            this.value = value;
        }

        @Override
        public Value orElse(Value other) {
            return value;
        }

        @Override
        public Value get() throws IllegalStateException {
            return value;
        }

        @Override
        public <MappedValue> Optional<MappedValue> map(Function<? super Value, ? extends MappedValue> fn) {
            return new Present<MappedValue>(fn.apply(get()));
        }
    }

    private static final class Absent<Value> extends Optional<Value> {
        private Absent() {
        }

        @Override
        public Value orElse(Value other) {
            return other;
        }

        @Override
        public Value get() throws IllegalStateException {
            throw new IllegalStateException("#get on Absent");
        }

        @Override
        public <MappedValue> Optional<MappedValue> map(Function<? super Value, ? extends MappedValue> fn) {
            return new Absent<MappedValue>();
        }
    }


    public static <Value> Optional<Value> of(Value value) {
        return value == null ? new Absent<Value>() : new Present<Value>(value);
    }

    public static <Value> Optional<Value> empty() {
        return new Absent<Value>();
    }

    public static <Value> Optional<Value> present(Value value) {
        if (value == null)
            throw new IllegalArgumentException("value cannot be null here.");
        return new Present<Value>(value);
    }

    public static final class Person {
        private final String firstName;
        private final String lastName;
        private final int age;

        private Person(String firstName, String lastName, int age) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public int getAge() {
            return age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Person person = (Person) o;

            if (age != person.age) return false;
            if (!firstName.equals(person.firstName)) return false;
            if (!lastName.equals(person.lastName)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = firstName.hashCode();
            result = 31 * result + lastName.hashCode();
            result = 31 * result + age;
            return result;
        }

        public static Person person(String firstName, String lastName, int age) {
            return new Person(firstName, lastName, age);
        }
    }

    public static Person getThePerson() {
        return Person.person("foo", "bar", 10);
    }

    public static void main(String[] args) {

        Optional<Person> thePerson = Optional.of(getThePerson());

        Optional<String> map = thePerson.map(new Function<Person, String>() {
            @Override
            public String apply(Person person) {
                String firstName = person.getFirstName();
                String lastName = person.getLastName();
                return capitalize(firstName) + " " + capitalize(lastName) + " is " + person.getAge();
            }
        });



        String message = map.get();

        System.out.println(

                map.orElse("unknown person is 0")


        );

//        if (thePerson != null)
//            System.out.format("%s %s is %d", capitalize(thePerson.getFirstName()), capitalize(thePerson.getLastName()), thePerson.getAge()).println();
//        else
//            System.out.println("Unknown Person is 0");

    }
}
