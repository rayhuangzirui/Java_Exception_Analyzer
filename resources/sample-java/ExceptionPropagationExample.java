import java.io.IOException;

public class ExceptionPropagationExample {
    public void alwaysThrowsException() throws IOException {
        throw new IOException("This method always throws an exception");
    }


    public void T1() throws IOException {
        alwaysThrowsException(); // This method is affected by the exception
    }

    public void T2() throws IOException {
        T1(); // This method is also affected by the exception
    }


    public void F1() {
        return; // This method is not affected
    }
}
