import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class RedundantCatchExample {
    public static void main(String[] args) {
        File file = new File("example.txt");

        try {
            FileInputStream fis = new FileInputStream(file);
            System.out.println("File found and opened.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }
}
