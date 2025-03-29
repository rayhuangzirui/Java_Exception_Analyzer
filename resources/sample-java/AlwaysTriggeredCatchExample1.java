import java.io.IOException;

public class AlwaysTriggeredCatchExample1 {

    public void alwaysThrowsException() throws IOException {
        throw new IOException("This method always throws an exception");
    }


    public void indirectlyThrows() throws IOException {
        alwaysThrowsException();
    }


    public void redundantTryCatch() {
        try {
            indirectlyThrows();  // This will always throw an exception
        } catch (IOException e) {
            System.out.println("Caught IOException, but this is always triggered.");
        }
    }
}
