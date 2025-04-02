import java.io.*;

public class ExampleWithIssues {

    public static void main(String[] args) {
        ExampleWithIssues example = new ExampleWithIssues();
        example.readFile("nonexistent.txt");
        example.loopWithTryCatch();
        example.propagateException();
    }

    public void emptyCatchBlock() {
        try {
            int x = 10 / 0;
        } catch (ArithmeticException e) {
            // nothing here
        }
    }

    public void loopWithTryCatch() {
        for (int i = 0; i < 10; i++) {
            try {
                Integer.parseInt("notANumber");
            } catch (NumberFormatException e) {
                System.err.println("Invalid number");
            }
        }
    }

    public void readFile(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = br.readLine();
            System.out.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void propagateException() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("another.txt"));
        String line = br.readLine();
        System.out.println(line);
    }

    public void alwaysTriggered() {
        try {
            throw new RuntimeException("This always happens");
        } catch (RuntimeException e) {
            System.out.println("Caught!");
        }
    }

    public void redundantTryCatch(String input) {
        if (input == null) {
            System.out.println("Input is null");
        } else {
            try {
                System.out.println(input.length());
            } catch (NullPointerException e) {
                System.out.println("Caught null input");
            }
        }
    }
}
