/* (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.legend.utils;

import javax.media.jai.TiledImage;
import java.awt.*;
import java.awt.image.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides utility methods for the shared handling of images by the raster map and legend
 * producers.
 *
 * @author Gabriel Roldan
 * @author Simone Giannecchini, GeoSolutions S.A.S.
 * @version $Id$
 */
public class ImageUtils {
    private static final Logger LOGGER =
            org.geotools.util.logging.Logging.getLogger("org.legend.legendGraphicDetails");

    /**
     * Forces the use of the class as a pure utility methods one by declaring a private default
     * constructor.
     */
    private ImageUtils() {
        // do nothing
    }

    /**
     * Sets up a {@link BufferedImage#TYPE_4BYTE_ABGR} if the paletteInverter is not provided, or a
     * indexed image otherwise. Subclasses may override this method should they need a special kind
     * of image
     *
     * @param width the width of the image to create.
     * @param height the height of the image to create.
     * @param palette A {@link IndexColorModel} if the image is to be indexed, or <code>
     *     null</code> otherwise.
     * @return an image of size <code>width x height</code> appropriate for the given color model,
     *     if any, and to be used as a transparent image or not depending on the <code>transparent
     *     </code> parameter.
     */
    public static BufferedImage createImage(
            int width, int height, final IndexColorModel palette, final boolean transparent) {
        // tolerance against image generation with zero width/height (can happen in various places
        // for the legend generation code, easier to handle it once here)
        height = Math.max(1, height);
        width = Math.max(1, width);

        // WARNING: whenever this method is changed, change getDrawingSurfaceMemoryUse
        // accordingly
        if (palette != null) {
            // unfortunately we can't use packed rasters because line rendering
            // gets completely
            // broken, see GEOS-1312 (https://osgeo-org.atlassian.net/browse/GEOS-1312)
            // final WritableRaster raster = palette.createCompatibleWritableRaster(width, height);
            final WritableRaster raster =
                    Raster.createInterleavedRaster(
                            palette.getTransferType(), width, height, 1, null);
            return new BufferedImage(palette, raster, false, null);
        }

        if (transparent) {
            return new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        }

        // don't use alpha channel if the image is not transparent (load testing shows this
        // image setup is the fastest to draw and encode on
        return new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    }

    /**
     * Sets up and returns a {@link Graphics2D} for the given <code>preparedImage</code>, which is
     * already prepared with a transparent background or the given background color.
     *
     * @param transparent whether the graphics is transparent or not.
     * @param bgColor the background color to fill the graphics with if its not transparent.
     * @param preparedImage the image for which to create the graphics.
     * @param extraHints an optional map of extra rendering hints to apply to the {@link
     *     Graphics2D}, other than {@link RenderingHints#KEY_ANTIALIASING}.
     * @return a {@link Graphics2D} for <code>preparedImage</code> with transparent background if
     *     <code>transparent == true</code> or with the background painted with <code>bgColor</code>
     *     otherwise.
     */
    public static Graphics2D prepareTransparency(
            final boolean transparent,
            final Color bgColor,
            final RenderedImage preparedImage,
            final Map<RenderingHints.Key, Object> extraHints) throws Exception {
        final Graphics2D graphic;

        if (preparedImage instanceof BufferedImage) {
            graphic = ((BufferedImage) preparedImage).createGraphics();
        } else if (preparedImage instanceof TiledImage) {
            graphic = ((TiledImage) preparedImage).createGraphics();
        } else if (preparedImage instanceof VolatileImage) {
            graphic = ((VolatileImage) preparedImage).createGraphics();
        } else {
            throw new Exception("Unrecognized back-end image type");
        }

        // fill the background with no antialiasing
        Map<RenderingHints.Key, Object> hintsMap;
        if (extraHints == null) {
            hintsMap = new HashMap<>();
        } else {
            hintsMap = new HashMap<>(extraHints);
        }
        hintsMap.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        graphic.setRenderingHints(hintsMap);
        if (transparent) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("setting to transparent");
            }

            int type = AlphaComposite.SRC;
            graphic.setComposite(AlphaComposite.getInstance(type));

            Color c = new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 0);
            graphic.setBackground(bgColor);
            graphic.setColor(c);
            graphic.fillRect(0, 0, preparedImage.getWidth(), preparedImage.getHeight());
            type = AlphaComposite.SRC_OVER;
            graphic.setComposite(AlphaComposite.getInstance(type));
        } else {
            graphic.setColor(bgColor);
            graphic.fillRect(0, 0, preparedImage.getWidth(), preparedImage.getHeight());
        }
        return graphic;
    }

}
