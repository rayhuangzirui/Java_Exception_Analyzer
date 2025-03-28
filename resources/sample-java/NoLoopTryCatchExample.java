public class NoLoopTryCatchExample {
    public static void main(String[] args) {
        try {
            // throw exception
            int result = 100 / 0;
        } catch (ArithmeticException e) {
            // handle exception (not in a loop)
        }
    }
}