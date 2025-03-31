import java.io.IOException;

public class AlwaysTriggeredCatchExample3 {

    public void alwaysThrowsException() throws IOException {
        throw new IOException("This method always throws an exception");
    }


    public void indirectlyThrows() throws IOException {
        if (true) {
            return;  // This will not throw an exception
        }
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
