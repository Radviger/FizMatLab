package lib.math;

public class CachedFunction {
    private final Function function;
    private double lastStart, lastEnd, lastPrecision, lastArea;

    public CachedFunction(Function function) {
        this.function = function;
    }

    public double integrate(double start, double end, double precision) {
        if (lastStart != start || lastEnd != end || lastPrecision != precision) {
            lastStart = start;
            lastEnd = end;
            lastPrecision = precision;
            lastArea = RenderMaths.integrate(function, start, end, precision);
        }
        return lastArea;
    }
}
