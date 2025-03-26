public class EmptyCatchExample {
    public static void main(String[] args) {
        try {
            int result = 10 / 0; // This will throw ArithmeticException
        } catch (ArithmeticException e) {
            // Empty catch block
        }

        System.out.println("Exception is ignored");
    }
}
