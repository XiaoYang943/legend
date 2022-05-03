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

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.util.SVGConstants;
import org.apache.commons.io.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Provides methods to build a compass buffered image.
 *
 * @author Adrien Bessy
 */
public class Compass {

    String filePath;
    int positionX;
    int positionY;

    public Compass(String filePath){
        this.filePath = filePath;
    }

    /**
     * Create a bufferedImage, paint a compass, then return the bufferedImage.
     * @return the buffered image
     */
    public BufferedImage paintCompass() throws IOException {
        return rasterize(new File(filePath));
    }

    /**
     * Transform svg file to png file
     * @param svgFile the svg file
     * @return the buffered image
     */
    public static BufferedImage rasterize(File svgFile) throws IOException {
        final BufferedImage[] imagePointer = new BufferedImage[1];
        // Rendering hints can't be set programatically, so
        // we override defaults with a temporary stylesheet.
        // These defaults emphasize quality and precision, and
        // are more similar to the defaults of other SVG viewers.
        // SVG documents can still override these defaults.
        String css = "svg {" +
                "shape-rendering: geometricPrecision;" +
                "text-rendering:  geometricPrecision;" +
                "color-rendering: optimizeQuality;" +
                "image-rendering: optimizeQuality;" +
                "}";
        File cssFile = File.createTempFile("batik-default-override-", ".css");
        FileUtils.writeStringToFile(cssFile, css);

        TranscodingHints transcoderHints = new TranscodingHints();
        transcoderHints.put(ImageTranscoder.KEY_XML_PARSER_VALIDATING, Boolean.FALSE);
        transcoderHints.put(ImageTranscoder.KEY_DOM_IMPLEMENTATION,
                SVGDOMImplementation.getDOMImplementation());
        transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT_NAMESPACE_URI,
                SVGConstants.SVG_NAMESPACE_URI);
        transcoderHints.put(ImageTranscoder.KEY_DOCUMENT_ELEMENT, "svg");
        transcoderHints.put(ImageTranscoder.KEY_USER_STYLESHEET_URI, cssFile.toURI().toString());
        transcoderHints.put(ImageTranscoder.KEY_WIDTH, (float) 150);
        transcoderHints.put(ImageTranscoder.KEY_HEIGHT, (float) 150);

        try {
            TranscoderInput input = new TranscoderInput(Files.newInputStream(svgFile.toPath()));
            ImageTranscoder t = new ImageTranscoder() {
                @Override
                public BufferedImage createImage(int w, int h) {
                    return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                }

                @Override
                public void writeImage(BufferedImage image, TranscoderOutput out) {
                    imagePointer[0] = image;
                }
            };
            t.setTranscodingHints(transcoderHints);
            t.transcode(input, null);
        }
        catch (TranscoderException ex) {
            // Requires Java 6
            ex.printStackTrace();
            throw new IOException("Couldn't convert " + svgFile);
        }
        return imagePointer[0];
    }

    /**
     * Set position of the compass on the base frame
     * @param position the position (can be absolute position like "[40:10]")
     * @param imgWidth the width of the base frame
     * @param imgHeight the height of the base frame
     * @param compassBufferedImage the buffered image of the compass
     */
    public void setPosition(String position, int imgWidth, int imgHeight, BufferedImage compassBufferedImage) {
        switch (position){
            case "bottom":
                positionY = imgHeight - imgHeight/5;
                positionX = imgWidth/2 - compassBufferedImage.getWidth()/2;
                break;
            case "bottomLeft":
                positionY = imgHeight - imgHeight/5;
                positionX = imgWidth/60;
                break;
            case "bottomRight":
                positionY = imgHeight - imgHeight/5;
                positionX = imgWidth - imgWidth/5;
                break;
            case "top":
                positionY = compassBufferedImage.getHeight()/3;
                positionX = imgWidth/2 - compassBufferedImage.getWidth()/2;
                break;
            case "topLeft":
                positionY = imgHeight/20;
                positionX = imgWidth/60;
                break;
            case "topRight":
                positionY = imgHeight/20;
                positionX = imgWidth - imgWidth/5;
                break;
            default:
                positionX = Integer.parseInt(position.split(":")[0]);
                positionY = Integer.parseInt(position.split(":")[1]);
        }
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }
}
