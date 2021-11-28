import lib.Window;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class MultiplePendulums extends Window {
    List<Ball> balls = new ArrayList<>();

    public MultiplePendulums() {
        super(1000, 1000, "Pendulum", true, "Cambria Math", 46);
        setIcon("pendulum.png");
        for (int i = 0; i < 8; i++) {
            balls.add(new Ball(width / 2.0, height / 2.0, 100.0 * (i + 1)));
        }
    }

    double g = 1.0;
    boolean pressed = false;

    @Override
    protected void onKeyButton(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_W) {
            if (action == GLFW.GLFW_RELEASE) {
                for (Ball ball : balls) {
                    ball.setAlpha(0.0);
                }
            }
        }
    }

    @Override
    protected void onMouseButton(int button, int action, int mods) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            pressed = action != GLFW.GLFW_RELEASE;
        }
    }

    @Override
    protected void onFrame(double elapsed) {
        for (Ball ball : balls) {
            ball.draw();
        }
    }

    private void drawLabel(int index, String text) {
        canvas.drawText(0, 10, 15 + index * (canvas.font.fontHeight + 5), text, true);
    }

    class Ball {
        double x0 = width / 2.0;
        double y0 = height / 2.0;
        double alpha = 0.0;
        double omega = 0.0;
        double len = 200.0;

        public Ball(double x0, double y0, double len) {
            this.x0 = x0;
            this.y0 = y0;
            this.len = len;
        }

        void setAlpha(double a) {
            alpha = a;
            omega = 0.0;
        }

        void draw() {
            double b = g * Math.cos(alpha) / len;
            omega += b;
            alpha += omega;

            if (pressed) {
                double dy = cursorY - y0;
                double dx = cursorX - x0;
                //l = Math.sqrt(dx * dx + dy * dy);
                double a = Math.atan2(dy, dx);
                setAlpha(a);
                canvas.drawCircle(0, x0, y0, len, 1.5);
            }
            double x = len * Math.cos(alpha) + x0;
            double y = len * Math.sin(alpha) + y0;

            canvas.drawLine(0, x0, y0, x, y, 1.5);
            canvas.fillCircle(0, x, y, 10.0);
        }
    }

    public static void main(String[] args) {
        new MultiplePendulums().show();
    }
}
