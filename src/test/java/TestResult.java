package test.java;

public record TestResult(
        boolean success,
        String message
) {
    public static TestResult succeed() {
        return new TestResult(true, null);
    }

    public static TestResult failed(String message) {
        return new TestResult(false, message);
    }
}