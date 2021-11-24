import lib.Window;
import org.lwjgl.glfw.GLFW;

public class HorizonBody extends Window {
    boolean pressed, falling;
    double ballX, ballY;
    double velX, velY;

    public static void main(String[] args) {
        new HorizonBody().show();
    }

    public HorizonBody() {
        super(1000, 400, "Тело, брошенное под углом к горизонту", true);
    }

    @Override
    protected void onMouseButton(int button, int action, int mods) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (action == GLFW.GLFW_RELEASE) {
                double dx = ballX - cursorX;
                double dy = ballY - cursorY;
//                double len = Math.sqrt(dx * dx + dy * dy);
//                dx /= len;
//                dy /= len;
                velX = dx;
                velY = dy;
                pressed = false;
                falling = true;
            } else {
                ballX = cursorX;
                ballY = cursorY;
                pressed = true;
            }
        }
    }

    @Override
    protected void onFrame(double elapsed) {
        if (pressed) {
            canvas.drawVector(0, cursorX, cursorY, ballX, ballY, 1.5, 5);
            canvas.drawCircle(0, ballX, ballY, 15, 1.5);
        } else if (falling) {
            double g = 100;
            velY += g * elapsed;
            ballX += velX * elapsed;
            ballY += velY * elapsed;
            canvas.drawCircle(0xFF000000, ballX, ballY, 15, 1.5);
            canvas.drawVector(0xFF0000, ballX, ballY, ballX + velX / 10, ballY + velY / 10, 2, 5);
        }
    }
}
