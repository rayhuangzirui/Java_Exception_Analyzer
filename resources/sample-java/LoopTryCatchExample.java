public class LoopTryCatchExample {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            try {
                // should throw exception because divide by 0
                int result = 100 / 0;
            } catch (ArithmeticException e) {
                // handle
            }
        }
    }
}
