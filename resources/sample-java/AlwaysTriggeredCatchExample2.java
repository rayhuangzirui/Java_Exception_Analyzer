import java.io.IOException;

public class AlwaysTriggeredCatchExample2 {

    public void alwaysThrowsException() throws IOException {
        if (true) {
            return;
        }
        throw new IOException("This method always throws an exception");
    }


    public void indirectlyThrows() throws IOException {
        alwaysThrowsException();
    }

    public void alwaysThrowCatch() {
        try {
            indirectlyThrows();  // This will not throw an exception
        } catch (IOException e) {
            System.out.println("Caught IOException, but this is always triggered.");
        }
    }
}
