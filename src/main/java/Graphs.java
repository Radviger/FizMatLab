import lib.Window;
import lib.math.CachedFunction;
import lib.math.Function;
import lib.math.RenderMaths;
import lib.render.Texture;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;
import static lib.math.RenderMaths.ONE_MIN_EPS;

public class Graphs extends Window {
    private final Map<Function, CachedFunction> cache = new HashMap<>();
    private final double grid = 10.0;

    private double amplitude = 1.0;
    private int precision = 0;
    private boolean shift = false;
    private boolean drag = false;
    private final Texture logo = Texture.load("logo.png");

    public Graphs() {
        super(800, 800, "Graphs", true, "CambriaMath", 36);
        setIcon("graphs.png");
    }

    @Override
    protected void onKeyButton(int key, int scancode, int action, int mods) {
        super.onKeyButton(key, scancode, action, mods);
        if (action == GLFW.GLFW_RELEASE) {
            switch (key) {
                case GLFW.GLFW_KEY_SPACE:
                    timer.toggle();
                    break;
                case GLFW.GLFW_KEY_EQUAL:
                case GLFW.GLFW_KEY_KP_ADD:
                    ++precision;
                    break;
                case GLFW.GLFW_KEY_MINUS:
                case GLFW.GLFW_KEY_KP_SUBTRACT:
                    if (precision > 0) {
                        --precision;
                    }
                    break;
            }
        }
        if (key == GLFW.GLFW_KEY_LEFT_SHIFT) {
            shift = action != GLFW.GLFW_RELEASE;
        }
    }

    private boolean selected;
    private double startX, endX;

    @Override
    protected void onMouseButton(int button, int action, int mods) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            boolean wasDragging = drag;
            drag = action != GLFW.GLFW_RELEASE;
            if (wasDragging != drag) {
                selected = true;
                double shiftStep = grid * 4;
                double actualX = shift ? (int)(cursorX / shiftStep + 0.5) * shiftStep : cursorX;
                if (!wasDragging) {
                    startX = actualX;
                } else {
                    endX = actualX;
                }
                if (startX == endX) {
                    selected = false;
                }
            }
        }
    }

    @Override
    protected void onScroll(double dx, double dy) {
        amplitude += dy;
    }

    protected long factorial(int input) {
        long result = 1;

        for (int factor = 2; factor <= input; factor++) {
            result *= factor;
        }

        return result;
    }

    protected double sinSeries(double x, int n) {
        double result = 0;
        for (int i = 0; i < n; i++) {
            result += ((i & 1) == 0 ? 1 : -1) * pow(x, 2 * i + 1) / factorial(2 * i + 1);
        }
        return result;
    }

    protected double cosSeries(double x, int n) {
        double result = 0;
        for (int i = 0; i < n; i++) {
            result += ((i & 1) == 0 ? 1 : -1) * pow(x, 2 * i) / factorial(2 * i);
        }
        return result;
    }

    protected double expSeries(double x, int n) {
        double result = 0;
        for (int i = 0; i < n; i++) {
            result += pow(x, i) / factorial(i);
        }
        return result;
    }

    private double arc(double x, double radius) {
        return sqrt(1 - x * x / (radius * radius)) * radius;
    }

    @Override
    protected void onFrame(double elapsed) {
        canvas.drawGrid(0xCCCCCC, 4 * grid);
        canvas.drawAxes(0x000000);

        canvas.drawTexture(logo, width - 64, height - 64, 64, 64);

        int color = getRainbowColor(10.0);

        drawFunction(0, color, 2, x -> arc(x, 4));
        drawFunction(1, color, 2, x -> -arc(x, 4));
        //drawFunction(0, 0xDD0000, 1.5, x -> sin(x) * amplitude * timer.frequency(0.5));
        //drawFunction(1, 0x0000DD, 1.5, x -> sinSeries(x, precision) * amplitude * timer.frequency(0.5));

        //canvas.fillFunction(color, grid, x -> pow(2, x));

        double endAngle = PI * (timer.frequency(0.5) + 1.0);

        canvas.fillCircleSegment(0x77000000 | color, width / 2.0, height / 2.0, 150, 0, endAngle);

        drawCursorLine(1);
    }

    private int getRainbowColor(double transitionTime) {
        return Color.HSBtoRGB((float) (timer.seconds() % transitionTime / transitionTime), 1F, 1F);
    }

    private double toGridX(double x) {
        return RenderMaths.clamp(x / (double) canvas.width * 2 - 1, -1.0, 1.0) * grid;
    }

    private double toGridY(double y) {
        return RenderMaths.clamp(y / (double) canvas.height * 2 - 1, -1.0, 1.0) * grid;
    }

    private void drawCursorLine(double thickness) {
        if (cursorOver) {
            double shiftStep = grid * 4;
            double actualX = shift ? (int)(cursorX / shiftStep + 0.5) * shiftStep : cursorX;
            canvas.drawLine(0, actualX, 0, actualX, height, thickness);
        }
    }

    private void drawFunction(int index, int color, double thickness, Function function) {
        canvas.drawFunction(color, grid, 1.5, function);
        double shiftStep = grid * 4;
        double actualX = shift ? (int)(cursorX / shiftStep + 0.5) * shiftStep : cursorX;
        if (cursorOver) {
            double arg = 2.0 * (actualX / width - 0.5) * grid * ONE_MIN_EPS;
            double value = function.apply(arg);
            double y = height / 2.0 * (1.0 - value / grid);
            canvas.fillCircle(0x77000000 | color, actualX, y, 4.0);

            drawLabel(index, color, "y(" + format(arg) + ") = " + format(value));
        }
        if (selected) {
            double realEnd = drag ? actualX : endX;
            double start = Math.min(startX, realEnd);
            double end = Math.max(startX, realEnd);
            canvas.fillFunction(0x77000000 | color, grid, function, start, end);

            CachedFunction cached = cache.computeIfAbsent(function, CachedFunction::new);
            double sum = cached.integrate(toGridX(start), toGridX(end), 0.0001);

            drawLabel(index + 5, color, "S(" + format(toGridX(start)) + ", " + format(toGridX(end)) + ") = " + format(sum));
        }
    }

    private void drawLabel(int slot, int color, String text) {
        int fh = canvas.font.fontHeight;
        canvas.drawText(color, 10, fh + slot * (fh + 5), text, true);
    }

    private double format(double value) {
        if (value != value) {
            return value;
        }
        return Math.round((int)(value * 10000) / 10000.0 * 1000.0) / 1000.0;
    }

    public static void main(String[] args) {
        new Graphs().show();
    }
}
