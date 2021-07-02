public class Pi {
    public static void main(String[] args) {
        System.out.println("PI = " + Math.sqrt(sum(50000000)));
    }

    private static double sum(long n) {
        double sum = 0;
        for (long i = 1; i <= n; i++) {
            sum += 1.0 / (i * i);
        }
        return 6 * sum;
    }
}
