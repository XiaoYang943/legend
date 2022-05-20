package org.legend.model;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.util.SVGConstants;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageItem extends Item{
    String filePath;

    /**
     * Create a bufferedImage, paint a compass, then return the bufferedImage.
     * @return the buffered image
     */
    public BufferedImage paintCompass(int size) throws IOException {
        return rasterize(new File(filePath), size);
    }

    /**
     * Transform svg file to png file
     * @param svgFile the svg file
     * @return the buffered image
     */
    public static BufferedImage rasterize(File svgFile, int size) throws IOException {
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
        transcoderHints.put(ImageTranscoder.KEY_WIDTH, (float) 150 + size);
        transcoderHints.put(ImageTranscoder.KEY_HEIGHT, (float) 150 + size);

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
     * Rotate a buffered image
     * @param bimg the image to rotate
     * @param angle the angle to apply to the rotation of the image
     * @return the buffered image
     */
    public static BufferedImage rotate(BufferedImage bimg, double angle) {
        int w = bimg.getWidth();
        int h = bimg.getHeight();
        Graphics2D graphic = bimg.createGraphics();
        graphic.rotate(Math.toRadians(angle), w / 2.0, h / 2.0);
        graphic.drawImage(bimg, null, 0, 0);
        graphic.dispose();
        return bimg;
    }

}
