import lib.Window;
import lib.render.Texture;
import lib.vertex.DefaultVertexFormats;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class Particles extends Window {
    private final Texture logo = Texture.load("logo.png");

    public Particles() {
        super(800, 800, "Частицы", true);
    }

    List<Particle> particles = new ArrayList<>();

    double clickX, clickY;
    boolean stretching, force;

    @Override
    protected void onKeyButton(int key, int scancode, int action, int mods) {
        super.onKeyButton(key, scancode, action, mods);
        if (key == GLFW.GLFW_KEY_TAB && action != GLFW.GLFW_RELEASE) {
            for (int i = 0; i < 1000; i++) {
                calculate();
            }
        }
    }

    @Override
    protected void onMouseButton(int button, int action, int mods) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (action == GLFW.GLFW_RELEASE) {
                Particle particle = new Particle(clickX, clickY);
                double dx = clickX - cursorX;
                double dy = clickY - cursorY;
                particle.vx = dx / 100.0;
                particle.vy = dy / 100.0;
                particles.add(particle);
                stretching = false;
            } else {
                clickX = cursorX;
                clickY = cursorY;
                stretching = true;
            }
        } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            force = action != GLFW.GLFW_RELEASE;
        }
    }

    @Override
    protected void onFrame(double elapsed) {
        canvas.drawTexture(logo, width - 64, height - 64, 64, 64, 64, 64);

        if (stretching) {
            canvas.drawLine(0, clickX, clickY, cursorX, cursorY, 1.5);
        }

        GL11.glPointSize(4);
        canvas.disableTexture();
        canvas.tessellator.draw(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR, buffer -> {
            for (Particle particle : particles) {
                buffer.pos(particle.x, particle.y, 0).color(0).endVertex();
            }
        });
        calculate();
    }

    public void calculate() {
        if (force) { // Artificial force created by RMB
            for (Particle particle : particles) {
                double dx = particle.x - cursorX;
                double dy = particle.y - cursorY;
                double r2 = dx * dx + dy * dy;
                if (r2 <= (1 + 5) * (1 + 5)) {
                    continue;
                }
                double G = -1.0;

                double a = G * 50 / r2;
                double r = Math.sqrt(r2);
                double ax = a * dx / r;
                double ay = a * dy / r;
                particle.vx += ax;
                particle.vy += ay;
            }
        }

        for (int i = 0; i < particles.size(); i++) {
            Particle b1 = particles.get(i);
            for (int j = i + 1; j < particles.size(); j++) {
                Particle b2 = particles.get(j);
                double dx = b2.x - b1.x;
                double dy = b2.y - b1.y;
                double r2 = dx * dx + dy * dy;
                if (r2 <= (1 + 1) * (1 + 1)) {
                    continue;
                }
                double G = -1.0;

                double a1 = G * b2.m / r2;
                double a2 = G * b1.m / r2;
                double r = Math.sqrt(r2);
                double a1x = a1 * dx / r;
                double a1y = a1 * dy / r;
                double a2x = a2 * dx / r;
                double a2y = a2 * dy / r;
                b2.vx += a2x;
                b2.vy += a2y;
                b1.vx -= a1x;
                b1.vy -= a1y;
            }
        }

        for (Particle particle : particles) {
            particle.x += particle.vx;
            particle.y += particle.vy;
        }
    }

    public static void main(String[] args) {
        Particles particles = new Particles();
        particles.show();
    }

    static class Particle {
        double x, y;
        double vx, vy;
        double m = 0.1;

        public Particle(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
