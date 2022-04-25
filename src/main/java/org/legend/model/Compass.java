/*
 * Legend is a library that generates a legend.
 * Legend is developed by CNRS http://www.cnrs.fr/.
 *
 * Most of the code had been picked up from Geoserver (https://github.com/geoserver/geoserver). Legend is free software;
 * you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation;
 * version 3.0 of the License.
 *
 * Legend is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details http://www.gnu.org/licenses.
 *
 *
 *For more information, please consult: http://www.orbisgis.org
 *or contact directly: info_at_orbisgis.org
 *
 */

package org.legend.model;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides methods to generate a compass rose bufferedImage
 *
 * @author Adrien Bessy filtered the SteelSeries-Swing projet created by hansolo :
 * <a href="https://github.com/HanSolo/SteelSeries-Swing/blob/master/src/main/java/eu/hansolo/steelseries/extras/Compass.java">https://github.com/HanSolo/SteelSeries-Swing/blob/master/src/main/java/eu/hansolo/steelseries/extras/Compass.java</a>
 */
public final class Compass extends JComponent {

    public Compass() {
        super();
    }

    /**
     * Returns a compatible image of the given size and transparency
     * @param WIDTH width of the image
     * @param HEIGHT height of the image
     * @param TRANSPARENCY transparency
     * @return a compatible image of the given size and transparency
     */
    public BufferedImage createImage(final int WIDTH, final int HEIGHT, final int TRANSPARENCY) {
        GraphicsConfiguration gfxConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        if (WIDTH <= 0 || HEIGHT <= 0) {
            return gfxConf.createCompatibleImage(1, 1, TRANSPARENCY);
        }
        return gfxConf.createCompatibleImage(WIDTH, HEIGHT, TRANSPARENCY);
    }

    public BufferedImage create_BIG_ROSE_POINTER_Image(final int WIDTH) {
        final BufferedImage IMAGE = createImage((int) (WIDTH * 0.0546875f), (int) (WIDTH * 0.2f), java.awt.Transparency.TRANSLUCENT);
        final Graphics2D G2 = IMAGE.createGraphics();

        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        final int IMAGE_WIDTH = IMAGE.getWidth();
        final int IMAGE_HEIGHT = IMAGE.getHeight();

        G2.setStroke(new BasicStroke(0.75f));

        // Define arrow shape of pointer
        final GeneralPath POINTER_WHITE_LEFT = new GeneralPath();
        final GeneralPath POINTER_WHITE_RIGHT = new GeneralPath();

        POINTER_WHITE_LEFT.moveTo(IMAGE_WIDTH - IMAGE_WIDTH * 0.95f, IMAGE_HEIGHT);
        POINTER_WHITE_LEFT.lineTo(IMAGE_WIDTH / 2.0f, 0);
        POINTER_WHITE_LEFT.lineTo(IMAGE_WIDTH / 2.0f, IMAGE_HEIGHT);
        POINTER_WHITE_LEFT.closePath();

        POINTER_WHITE_RIGHT.moveTo(IMAGE_WIDTH * 0.95f, IMAGE_HEIGHT);
        POINTER_WHITE_RIGHT.lineTo(IMAGE_WIDTH / 2.0f, 0);
        POINTER_WHITE_RIGHT.lineTo(IMAGE_WIDTH / 2.0f, IMAGE_HEIGHT);
        POINTER_WHITE_RIGHT.closePath();

        final Area POINTER_FRAME_WHITE = new Area(POINTER_WHITE_LEFT);
        POINTER_FRAME_WHITE.add(new Area(POINTER_WHITE_RIGHT));

        final Color STROKE_COLOR = Color.BLACK;
        final Color FILL_COLOR = Color.WHITE;

        G2.setColor(STROKE_COLOR);
        G2.fill(POINTER_WHITE_RIGHT);
        G2.setColor(FILL_COLOR);
        G2.fill(POINTER_WHITE_LEFT);
        G2.setColor(STROKE_COLOR);
        G2.draw(POINTER_FRAME_WHITE);

        G2.dispose();

        return IMAGE;
    }

    public BufferedImage create_SMALL_ROSE_POINTER_Image(final int WIDTH) {
        final BufferedImage IMAGE = createImage((int) (WIDTH * 0.0546875f), (int) (WIDTH * 0.2f), java.awt.Transparency.TRANSLUCENT);
        final Graphics2D G2 = IMAGE.createGraphics();

        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

        final int IMAGE_WIDTH = IMAGE.getWidth();
        final int IMAGE_HEIGHT = IMAGE.getHeight();

        G2.setStroke(new BasicStroke(0.75f));

        // Define arrow shape of pointer
        final GeneralPath POINTER_WHITE_LEFT = new GeneralPath();
        final GeneralPath POINTER_WHITE_RIGHT = new GeneralPath();

        POINTER_WHITE_LEFT.moveTo(IMAGE_WIDTH - IMAGE_WIDTH * 0.75f, IMAGE_HEIGHT);
        POINTER_WHITE_LEFT.lineTo(IMAGE_WIDTH / 2.0f, IMAGE_HEIGHT / 2.0f);
        POINTER_WHITE_LEFT.lineTo(IMAGE_WIDTH / 2.0f, IMAGE_HEIGHT);
        POINTER_WHITE_LEFT.closePath();

        POINTER_WHITE_RIGHT.moveTo(IMAGE_WIDTH * 0.75f, IMAGE_HEIGHT);
        POINTER_WHITE_RIGHT.lineTo(IMAGE_WIDTH / 2.0f, IMAGE_HEIGHT / 2.0f);
        POINTER_WHITE_RIGHT.lineTo(IMAGE_WIDTH / 2.0f, IMAGE_HEIGHT);
        POINTER_WHITE_RIGHT.closePath();

        final Area POINTER_FRAME_WHITE = new Area(POINTER_WHITE_LEFT);
        POINTER_FRAME_WHITE.add(new Area(POINTER_WHITE_RIGHT));

        final Color STROKE_COLOR = Color.BLACK;
        final Color FILL_COLOR = Color.WHITE;

        G2.setColor(FILL_COLOR);
        G2.fill(POINTER_FRAME_WHITE);
        G2.setColor(STROKE_COLOR);
        G2.draw(POINTER_FRAME_WHITE);

        G2.dispose();

        return IMAGE;
    }

    @Override
    public String toString() {
        return "Compass";
    }

    public BufferedImage create_COMPASS_ROSE_Image(final int WIDTH) {
        if (WIDTH <= 0) {
            return null;
        }

        final BufferedImage IMAGE = createImage(WIDTH, WIDTH, java.awt.Transparency.TRANSLUCENT);
        final Graphics2D G2 = IMAGE.createGraphics();
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        //G2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        //G2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        //G2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        G2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final int IMAGE_WIDTH = IMAGE.getWidth();
        //final int IMAGE_HEIGHT = IMAGE.getHeight();

        // ******************* COMPASS ROSE *************************************************
        final Point2D COMPASS_CENTER = new Point2D.Double(IMAGE_WIDTH / 2.0f, IMAGE_WIDTH / 2.0f);
        AffineTransform transform = G2.getTransform();
        G2.setStroke(new BasicStroke(IMAGE_WIDTH * 0.01953125f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

        G2.setColor(Color.BLACK);

        for (int i = 0; i <= 360; i += 30) {
            G2.draw(new Arc2D.Double(COMPASS_CENTER.getX() - IMAGE_WIDTH * 0.263671875f, COMPASS_CENTER.getY() - IMAGE_WIDTH * 0.263671875f, IMAGE_WIDTH * 0.52734375f, IMAGE_WIDTH * 0.52734375f, i, 15, Arc2D.OPEN));
        }

        G2.setColor(Color.BLACK);
        G2.setStroke(new BasicStroke(0.5f));
        java.awt.Shape outerCircle = new Ellipse2D.Double(COMPASS_CENTER.getX() - IMAGE_WIDTH * 0.2734375f, COMPASS_CENTER.getY() - IMAGE_WIDTH * 0.2734375f, IMAGE_WIDTH * 0.546875f, IMAGE_WIDTH * 0.546875f);
        G2.draw(outerCircle);
        java.awt.Shape innerCircle = new Ellipse2D.Double(COMPASS_CENTER.getX() - IMAGE_WIDTH * 0.25390625f, COMPASS_CENTER.getY() - IMAGE_WIDTH * 0.25390625f, IMAGE_WIDTH * 0.5078125f, IMAGE_WIDTH * 0.5078125f);
        G2.draw(innerCircle);

        final java.awt.geom.Line2D LINE = new java.awt.geom.Line2D.Double(COMPASS_CENTER.getX(), IMAGE_WIDTH * 0.4018691589, COMPASS_CENTER.getX(), IMAGE_WIDTH * 0.1495327103);
        G2.setColor(Color.BLACK);

        G2.setStroke(new BasicStroke(1f));
        G2.draw(LINE);
        G2.rotate(Math.PI / 12, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 6, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 6, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 12, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 12, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 6, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 6, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 12, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 12, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 6, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 6, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 12, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 12, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 6, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 6, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);
        G2.rotate(Math.PI / 12, COMPASS_CENTER.getX(), COMPASS_CENTER.getY());
        G2.draw(LINE);

        G2.setTransform(transform);
        final BufferedImage BIG_ROSE_POINTER = create_BIG_ROSE_POINTER_Image(IMAGE_WIDTH);
        final BufferedImage SMALL_ROSE_POINTER = create_SMALL_ROSE_POINTER_Image(IMAGE_WIDTH);
        final Point2D OFFSET = new Point2D.Double(IMAGE_WIDTH * 0.475f, IMAGE_WIDTH * 0.20f);

        G2.translate(OFFSET.getX(), OFFSET.getY());

        // N
        G2.drawImage(BIG_ROSE_POINTER, 0, 0, this);

        // NE
        G2.rotate(Math.PI / 4f, COMPASS_CENTER.getX() - OFFSET.getX(), COMPASS_CENTER.getY() - OFFSET.getY());
        G2.drawImage(SMALL_ROSE_POINTER, 0, 0, this);

        // E
        G2.rotate(Math.PI / 4f, COMPASS_CENTER.getX() - OFFSET.getX(), COMPASS_CENTER.getY() - OFFSET.getY());
        G2.drawImage(BIG_ROSE_POINTER, 0, 0, this);

        // SE
        G2.rotate(Math.PI / 4f, COMPASS_CENTER.getX() - OFFSET.getX(), COMPASS_CENTER.getY() - OFFSET.getY());
        G2.drawImage(SMALL_ROSE_POINTER, 0, 0, this);

        // S
        G2.rotate(Math.PI / 4f, COMPASS_CENTER.getX() - OFFSET.getX(), COMPASS_CENTER.getY() - OFFSET.getY());
        G2.drawImage(BIG_ROSE_POINTER, 0, 0, this);

        // SW
        G2.rotate(Math.PI / 4f, COMPASS_CENTER.getX() - OFFSET.getX(), COMPASS_CENTER.getY() - OFFSET.getY());
        G2.drawImage(SMALL_ROSE_POINTER, 0, 0, this);

        // W
        G2.rotate(Math.PI / 4f, COMPASS_CENTER.getX() - OFFSET.getX(), COMPASS_CENTER.getY() - OFFSET.getY());
        G2.drawImage(BIG_ROSE_POINTER, 0, 0, this);

        // NW
        G2.rotate(Math.PI / 4f, COMPASS_CENTER.getX() - OFFSET.getX(), COMPASS_CENTER.getY() - OFFSET.getY());
        G2.drawImage(SMALL_ROSE_POINTER, 0, 0, this);

        G2.setTransform(transform);

        G2.setColor(Color.BLACK);
        G2.setStroke(new BasicStroke(IMAGE_WIDTH * 0.00953125f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        G2.draw(new Ellipse2D.Double(COMPASS_CENTER.getX() - (IMAGE_WIDTH * 0.1025f), COMPASS_CENTER.getY() - (IMAGE_WIDTH * 0.1025f), IMAGE_WIDTH * 0.205f, IMAGE_WIDTH * 0.205f));

        G2.setStroke(new BasicStroke(0.5f));
        G2.setColor(Color.BLACK);
        final java.awt.Shape OUTER_ROSE_ELLIPSE = new Ellipse2D.Double(COMPASS_CENTER.getX() - (IMAGE_WIDTH * 0.11f), COMPASS_CENTER.getY() - (IMAGE_WIDTH * 0.11f), IMAGE_WIDTH * 0.22f, IMAGE_WIDTH * 0.22f);
        G2.draw(OUTER_ROSE_ELLIPSE);
        final java.awt.Shape INNER_ROSE_ELLIPSE = new Ellipse2D.Double(COMPASS_CENTER.getX() - (IMAGE_WIDTH * 0.095f), COMPASS_CENTER.getY() - (IMAGE_WIDTH * 0.095f), IMAGE_WIDTH * 0.19f, IMAGE_WIDTH * 0.19f);
        G2.draw(INNER_ROSE_ELLIPSE);


        // ******************* TICKMARKS ****************************************************
        create_TICKMARKS(G2, IMAGE_WIDTH);

        G2.dispose();

        return IMAGE;
    }

    private void create_TICKMARKS(final Graphics2D G2, final int IMAGE_WIDTH) {
        // Store former transformation
        final AffineTransform FORMER_TRANSFORM = G2.getTransform();

        final BasicStroke MEDIUM_STROKE = new BasicStroke(0.005859375f * IMAGE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        final BasicStroke THIN_STROKE = new BasicStroke(0.00390625f * IMAGE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        final java.awt.Font BIG_FONT = new java.awt.Font("Serif", java.awt.Font.PLAIN, (int) (0.12f * IMAGE_WIDTH));
        final java.awt.Font SMALL_FONT = new java.awt.Font("Serif", java.awt.Font.PLAIN, (int) (0.06f * IMAGE_WIDTH));
        final float TEXT_DISTANCE = 0.0750f * IMAGE_WIDTH;
        final float MIN_LENGTH = 0.015625f * IMAGE_WIDTH;
        final float MED_LENGTH = 0.0234375f * IMAGE_WIDTH;
        final float MAX_LENGTH = 0.03125f * IMAGE_WIDTH;

        final Color TEXT_COLOR = Color.BLACK;
        final Color TICK_COLOR = Color.BLACK;

        // Create the watch itself
        final float RADIUS = IMAGE_WIDTH * 0.38f;
        final Point2D COMPASS_CENTER = new Point2D.Double(IMAGE_WIDTH / 2.0f, IMAGE_WIDTH / 2.0f);

        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        G2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        G2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // Draw ticks
        Point2D innerPoint;
        Point2D outerPoint;
        Point2D textPoint;
        java.awt.geom.Line2D tick;
        int tickCounter90 = 0;
        int tickCounter15 = 0;
        int tickCounter5 = 0;
        int counter = 0;

        double sinValue;
        double cosValue;

        final double STEP = (2.0d * Math.PI) / (360.0d);

        for (double alpha = 2 * Math.PI; alpha >= 0; alpha -= STEP) {
            G2.setStroke(THIN_STROKE);
            sinValue = Math.sin(alpha);
            cosValue = Math.cos(alpha);

            G2.setColor(TICK_COLOR);

            if (tickCounter5 == 5) {
                G2.setStroke(THIN_STROKE);
                innerPoint = new Point2D.Double(COMPASS_CENTER.getX() + (RADIUS - MIN_LENGTH) * sinValue, COMPASS_CENTER.getY() + (RADIUS - MIN_LENGTH) * cosValue);
                outerPoint = new Point2D.Double(COMPASS_CENTER.getX() + RADIUS * sinValue, COMPASS_CENTER.getY() + RADIUS * cosValue);
                // Draw ticks
                tick = new java.awt.geom.Line2D.Double(innerPoint.getX(), innerPoint.getY(), outerPoint.getX(), outerPoint.getY());
                G2.draw(tick);

                tickCounter5 = 0;
            }

            // Different tickmark every 15 units
            if (tickCounter15 == 15) {
                G2.setStroke(THIN_STROKE);
                innerPoint = new Point2D.Double(COMPASS_CENTER.getX() + (RADIUS - MED_LENGTH) * sinValue, COMPASS_CENTER.getY() + (RADIUS - MED_LENGTH) * cosValue);
                outerPoint = new Point2D.Double(COMPASS_CENTER.getX() + RADIUS * sinValue, COMPASS_CENTER.getY() + RADIUS * cosValue);

                // Draw ticks
                tick = new java.awt.geom.Line2D.Double(innerPoint.getX(), innerPoint.getY(), outerPoint.getX(), outerPoint.getY());
                G2.draw(tick);

                tickCounter15 = 0;
                tickCounter90 += 15;
            }

            // Different tickmark every 90 units plus text
            if (tickCounter90 == 90) {
                G2.setStroke(MEDIUM_STROKE);
                innerPoint = new Point2D.Double(COMPASS_CENTER.getX() + (RADIUS - MAX_LENGTH) * sinValue, COMPASS_CENTER.getY() + (RADIUS - MAX_LENGTH) * cosValue);
                outerPoint = new Point2D.Double(COMPASS_CENTER.getX() + RADIUS * sinValue, COMPASS_CENTER.getY() + RADIUS * cosValue);

                // Draw ticks
                tick = new java.awt.geom.Line2D.Double(innerPoint.getX(), innerPoint.getY(), outerPoint.getX(), outerPoint.getY());
                G2.draw(tick);

                tickCounter90 = 0;
            }

            // Draw text
            G2.setFont(BIG_FONT);
            G2.setColor(TEXT_COLOR);

            textPoint = new Point2D.Double(COMPASS_CENTER.getX() + (RADIUS - TEXT_DISTANCE) * sinValue, COMPASS_CENTER.getY() + (RADIUS - TEXT_DISTANCE) * cosValue);
            switch (counter) {
                case 360:
                    G2.setFont(BIG_FONT);
                    G2.fill(rotateTextAroundCenter(G2, "S", (int) textPoint.getX(), (int) textPoint.getY(), (Math.PI - alpha)));
                    break;
                case 45:
                    G2.setFont(SMALL_FONT);
                    G2.fill(rotateTextAroundCenter(G2, "SW", (int) textPoint.getX(), (int) textPoint.getY(), (Math.PI - alpha)));
                    break;
                case 90:
                    G2.setFont(BIG_FONT);
                    G2.fill(rotateTextAroundCenter(G2, "W", (int) textPoint.getX(), (int) textPoint.getY(), (Math.PI - alpha)));
                    break;
                case 135:
                    G2.setFont(SMALL_FONT);
                    G2.fill(rotateTextAroundCenter(G2, "NW", (int) textPoint.getX(), (int) textPoint.getY(), (Math.PI - alpha)));
                    break;
                case 180:
                    G2.setFont(BIG_FONT);
                    G2.fill(rotateTextAroundCenter(G2, "N", (int) textPoint.getX(), (int) textPoint.getY(), (Math.PI - alpha)));
                    break;
                case 225:
                    G2.setFont(SMALL_FONT);
                    G2.fill(rotateTextAroundCenter(G2, "NE", (int) textPoint.getX(), (int) textPoint.getY(), (Math.PI - alpha)));
                    break;
                case 270:
                    G2.setFont(BIG_FONT);
                    G2.fill(rotateTextAroundCenter(G2, "E", (int) textPoint.getX(), (int) textPoint.getY(), (Math.PI - alpha)));
                    break;
                case 315:
                    G2.setFont(SMALL_FONT);
                    G2.fill(rotateTextAroundCenter(G2, "SE", (int) textPoint.getX(), (int) textPoint.getY(), (Math.PI - alpha)));
                    break;
            }
            G2.setTransform(FORMER_TRANSFORM);

            tickCounter5++;
            tickCounter15++;

            counter++;
        }

        // Restore former transformation
        G2.setTransform(FORMER_TRANSFORM);
    }

    private final Pattern NUMBERS_ONLY = Pattern.compile("^[-+]?[0-9]+[.]?[0-9]*([eE][-+]?[0-9]+)?$");
    private final Matcher MATCHES_NUMBERS = NUMBERS_ONLY.matcher("");
    private final Rectangle2D TEXT_BOUNDARY = new Rectangle2D.Double(0, 0, 10, 10);

    /**
     * It will take the font from the given Graphics2D object and returns a shape of the given TEXT
     * that is rotated by the ROTATION_ANGLE around it's center which is defined
     * by TEXT_POSITION_X and TEXT_POSITION_Y. It will take the font's descent into account so that
     * the rotated text will be centered correctly even if it doesn't contain characters with descent.
     * @param G2 G2
     * @param TEXT text
     * @param TEXT_POSITION_X TEXT_POSITION_X
     * @param TEXT_POSITION_Y TEXT_POSITION_Y
     * @param ROTATION_ANGLE ROTATION_ANGLE
     * @return Glyph that is a shape of the given string rotated around it's center.
     */
    public Shape rotateTextAroundCenter(final Graphics2D G2, final String TEXT, final int TEXT_POSITION_X, final int TEXT_POSITION_Y, final double ROTATION_ANGLE) {
        final FontRenderContext RENDER_CONTEXT = new FontRenderContext(null, true, true);
        final TextLayout TEXT_LAYOUT = new TextLayout(TEXT, G2.getFont(), RENDER_CONTEXT);

        // Check if need to take the fonts descent into account
        final float DESCENT;
        MATCHES_NUMBERS.reset(TEXT);
        if (MATCHES_NUMBERS.matches()) {
            DESCENT = TEXT_LAYOUT.getDescent();
        } else {
            DESCENT = 0;
        }
        final Rectangle2D TEXT_BOUNDS = TEXT_LAYOUT.getBounds();
        TEXT_BOUNDARY.setRect(TEXT_BOUNDS.getMinX(), TEXT_BOUNDS.getMinY(), TEXT_BOUNDS.getWidth(), TEXT_BOUNDS.getHeight() + DESCENT / 2);

        final GlyphVector GLYPH_VECTOR = G2.getFont().createGlyphVector(RENDER_CONTEXT, TEXT);

        final java.awt.Shape GLYPH = GLYPH_VECTOR.getOutline((int) -TEXT_BOUNDARY.getCenterX(), 2 * (int) TEXT_BOUNDARY.getCenterY());

        final AffineTransform OLD_TRANSFORM = G2.getTransform();
        G2.translate(TEXT_POSITION_X, TEXT_POSITION_Y + TEXT_BOUNDARY.getHeight());

        G2.rotate(ROTATION_ANGLE, -TEXT_BOUNDARY.getCenterX() + TEXT_BOUNDARY.getWidth() / 2, TEXT_BOUNDARY.getCenterY() - (TEXT_BOUNDARY.getHeight() + DESCENT) / 2);
        G2.fill(GLYPH);

        G2.setTransform(OLD_TRANSFORM);

        return GLYPH;
    }

}