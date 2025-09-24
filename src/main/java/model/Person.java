package main.java.model;

public record Person(
        Long personId,
        String firstName,
        String lastName,
        String mobile,
        String email,
        String pesel,
        Type type
) {
}