package lib.render;

import lib.buffer.BufferBuilder;
import lib.buffer.Tessellator;
import lib.math.Function;
import lib.math.RenderMaths;
import lib.render.text.TextRenderer;
import lib.vertex.DefaultVertexFormats;
import lib.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;

import static lib.math.RenderMaths.ONE_MIN_EPS;

public class Canvas {
    protected final Tessellator tessellator;
    public final int width, height;
    public final TextRenderer font;

    public Canvas(Tessellator tessellator, int width, int height, String font, int fontSize) {
        this.tessellator = tessellator;
        this.width = width;
        this.height = height;
        this.font = new TextRenderer(font, fontSize, true);
    }

    public void draw(int mode, VertexFormat format, Consumer<BufferBuilder> builder) {
        tessellator.draw(mode, format, builder);
    }

    public void fillCircle(int color, double x, double y, double radius) {
        fillCircleSegment(color, x, y, radius, 0, 2 * Math.PI);
    }

    public void drawCircle(int color, double x, double y, double radius) {
        drawCircleSegment(color, x, y, radius, 0, 2 * Math.PI);
    }

    public void fillCircleSegment(int color, double x, double y, double radius, double startAngle, double endAngle) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.draw(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR, buffer -> {
            buffer.pos(x, y, 0).color(color).endVertex();
            for (double angle = startAngle; angle <= endAngle; angle += Math.PI / 3600) {
                double dx = x + radius * Math.cos(angle);
                double dy = y + radius * Math.sin(angle);
                buffer.pos(dx, dy, 0).color(color).endVertex();
            }
            if (endAngle != 2 * Math.PI) {
                buffer.pos(x, y, 0).color(color).endVertex();
            }
        });
    }

    public void drawCircleSegment(int color, double x, double y, double radius, double startAngle, double endAngle) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.draw(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR, buffer -> {
            for (double angle = startAngle; angle <= endAngle; angle += Math.PI / 3600) {
                double dx = x + radius * Math.cos(angle);
                double dy = y + radius * Math.sin(angle);
                buffer.pos(dx, dy, 0).color(color).endVertex();
            }
            if (endAngle != 2 * Math.PI) {
                buffer.pos(width / 2.0, height / 2.0, 0).color(color).endVertex();
            }
        });
    }

    public void drawTriangle(double x1, double y1, double x2, double y2, double x3, double y3, int color) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.draw(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR, buffer -> {
            buffer.pos(x1, y1, 0).color(color).endVertex();
            buffer.pos(x2, y2, 0).color(color).endVertex();
            buffer.pos(x3, y3, 0).color(color).endVertex();
        });
    }

    public void drawGrid(int color, double size) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.draw(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR, buffer -> {
            //Horizontal
            for (double y = 0; y < height; y += size) {
                buffer.pos(0, y, 0).color(color).endVertex();
                buffer.pos(width, y, 0).color(color).endVertex();
            }

            //Vertical
            for (double x = 0; x < width; x += size) {
                buffer.pos(x, 0, 0).color(color).endVertex();
                buffer.pos(x, height, 0).color(color).endVertex();
            }
        });
    }

    public void drawAxes(int color) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.draw(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR, buffer -> {
            //Horizontal
            buffer.pos(0, height / 2.0, 0).color(color).endVertex();
            buffer.pos(width, height / 2.0, 0).color(color).endVertex();

            //Vertical
            buffer.pos(width / 2.0, 0, 0).color(color).endVertex();
            buffer.pos(width / 2.0, height, 0).color(color).endVertex();
        });
        drawText(0, width / 2.0 + 5, font.fontHeight + 5, "y", false);
        drawText(0, width - 20, height / 2.0 + font.fontHeight / 2.0 + 5, "x", false);
    }

    public void drawLine(int color, double x1, double y1, double x2, double y2, double thickness) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth((float) thickness);
        tessellator.draw(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR, buffer -> {
            buffer.pos(x1, y1, 0).color(color).endVertex();
            buffer.pos(x2, y2, 0).color(color).endVertex();
        });
        GL11.glLineWidth(1);
    }

    public void drawText(int color, double x, double y, String text, boolean shadow) {
        font.drawString(this, text, x, y, color, shadow);
    }

    public void drawCenteredText(int color, double x, double y, String text, boolean shadow) {
        font.drawCenteredString(this, text, x, y, color, shadow);
    }

    public void drawTexture(Texture texture, double x, double y, double width, double height) {
        texture.bind();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        tessellator.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR, buffer -> {
            buffer.pos(x, y, 0).tex(0, 0).color(255, 255, 255, 255).endVertex();
            buffer.pos(x + width, y, 0).tex(1, 0).color(255, 255, 255, 255).endVertex();
            buffer.pos(x + width, y + height, 0).tex(1, 1).color(255, 255, 255, 255).endVertex();
            buffer.pos(x, y + height, 0).tex(0, 1).color(255, 255, 255, 255).endVertex();
        });
    }

    public void fillFunction(int color, double grid, Function function) {
        fillFunction(color, grid, function, 0, width);
    }

    public void fillFunction(int color, double grid, Function function, double start, double end) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.draw(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR, buffer -> {
            for (double x = start; x < end; x++) {
                double arg = RenderMaths.clamp(2.0 * (x / width - 0.5) * ONE_MIN_EPS, -1.0, 1.0);
                double value = RenderMaths.clamp(function.apply(arg * grid) / grid, -1.0, 1.0);
                double y = height / 2.0 * (1.0 - value);
                buffer.pos(x, height / 2.0, 0).color(color).endVertex();
                buffer.pos(x, y, 0).color(color).endVertex();
            }
        });
    }

    public void drawFunction(int graphColor, double grid, double thickness, Function function) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth((float) thickness);
        tessellator.draw(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR, buffer -> {
            for (double x = 0; x < width; x++) {
                double arg = 2.0 * (x / width - 0.5) * ONE_MIN_EPS;
                double value = function.apply(arg * grid) / grid;
                double y = height / 2.0 * (1.0 - value);
                buffer.pos(x, y, 0).color(graphColor).endVertex();
            }
        });
        GL11.glLineWidth(1);
    }
}
