public class NoSystemErrorInCatchExample {
    public static void main(String[] args) {
        try {
            int a = 1 / 0;
        } catch (ArithmeticException e) {
            // using empty block because technically not system error in catch
        }
    }
}