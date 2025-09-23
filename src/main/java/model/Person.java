package main.java.model;

public record Person(
        String personId,
        String firstName,
        String lastName,
        String mobile,
        String email,
        String pesel
) {
}