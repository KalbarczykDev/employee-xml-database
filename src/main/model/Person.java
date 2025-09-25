package main.model;

import java.util.UUID;

public record Person(
        UUID personId,
        String firstName,
        String lastName,
        String mobile,
        String email,
        String pesel,
        Type type
) {
}