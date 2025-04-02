import java.io.*;
import java.util.logging.Logger;

public class ExampleWithIssuesFixed {

    private static final Logger logger = Logger.getLogger(ExampleWithIssuesFixed.class.getName());

    public static void main(String[] args) {
        ExampleWithIssuesFixed example = new ExampleWithIssuesFixed();
        example.readFile("nonexistent.txt");
        example.loopWithTryCatch();
        try {
            example.propagateException(); // throws IOException, but not handled
        } catch (IOException e) {
            // and add minimal proper logging
            logger.warning("Failed to propagate: " + e.getMessage());
        }
    }

    // Add meaningful logging or handling to empty catch block
    // Fix: Use conditional check to avoid ArithmeticException
     public void emptyCatchBlock() {
        int denominator = 0;
        if (denominator != 0) {
            int x = 10 / denominator;
        } else {
            logger.warning("Cannot divide by zero");
        }
    }

    // Move try-catch block outside the loop
    public void loopWithTryCatch() {
        for (int i = 0; i < 10; i++) {
            String input = "notANumber";
            boolean isValid = isValidInteger(input);
            if (!isValid) {
                System.err.println("Invalid number at iteration " + i);
            }
        }
    }

    private boolean isValidInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Fix: Use try-with-resources to close resource and add minimal proper logging
    public void readFile(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            System.out.println(line);
        } catch (IOException e) {
            logger.warning("IO Error while reading file: " + e.getMessage());
        }
    }

    // Fix: Declare and handle propagated exception
    public void propagateException() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("another.txt"))) {
            String line = br.readLine();
            System.out.println(line);
        }
    }

    // Fix: Remove throw-catch, replace with conditional if appropriate
    public void alwaysTriggered() {
        System.out.println("Avoid throwing RuntimeException intentionally");
    }

    // Fix: Avoid try-catch by doing conditional check
    public void redundantTryCatch(String input) {
        if (input != null) {
            System.out.println(input.length());
        } else {
            System.out.println("Input is null");
        }
    }
}

