import lib.Window;
import lib.render.Texture;

import static java.lang.Math.*;

public class UnitCircle extends Window {
    private final Texture logo = Texture.load("logo.png");

    private final double grid = 10.0;

    public UnitCircle() {
        super(800, 800, "Unit Circle", true, "CambriaMath", 40);
        setIcon("gravity.png");
    }

    double originX = width / 2.0, originY = height / 2.0;

    @Override
    protected void onFrame(double elapsed) {
        canvas.drawGrid(0xCCCCCC, 4 * grid);
        canvas.drawAxes(0x000000);

        double radius = 4 * 4 * grid;

        canvas.drawCircle(0xAADDAA, originX, originY, radius, 2.0);

        if (cursorOver) {
            double dx = cursorX - originX;
            double dy = cursorY - originY;
            double length = Math.sqrt(dx * dx + dy * dy);
            double angle = Math.atan2(dy, -dx) + Math.PI;

            double arcX = originX + dx / length * radius;
            double arcY = originY + dy / length * radius;

            canvas.fillCircleSegment(0x66AADDAA, originX, originY, radius, -angle, 0.0);
            canvas.drawLine(0xDDAAAA, arcX, arcY, originX, arcY, 2.0);
            canvas.drawLine(0xAAAADD, arcX, arcY, arcX, originY, 2.0);
            canvas.drawLine(0xAADDAA, arcX, arcY, originX, originY, 2.0);
            canvas.fillCircle(0xAADDAA, arcX, arcY, 4.0);

            canvas.drawText(0, 10.0, 15.0, "§0Angle§7: " + (int) toDegrees(angle), true);
            canvas.drawText(0, 10.0, 15.0 + canvas.font.fontHeight + 5.0, "§1sin§7: " + format(sin(angle)), true);
            canvas.drawText(0, 10.0, 15.0 + 2.0 * canvas.font.fontHeight + 10.0, "§4cos§7: " + format(cos(angle)), true);
        }

        canvas.drawTexture(logo, width - 64, height - 64, 64, 64);
    }

    private double format(double value) {
        if (value != value) {
            return value;
        }
        return Math.round((int)(value * 10000) / 10000.0 * 1000.0) / 1000.0;
    }

    public static void main(String[] args) {
        new UnitCircle().show();
    }
}
