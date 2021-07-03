import lib.math.Series;

public class Pi {
    private static final Series PI = (n, x) -> 1.0 / (n * n);

    public static void main(String[] args) {
        double sum = PI.sum(1, 50000000).apply(0);
        System.out.println("PI = " + Math.sqrt(sum));
    }
}
