package gui;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {

    private static final Map<String, Image> pieceImageCache = new HashMap<>();

    public static Image getPieceImage(final String imagePath) {
        if (!pieceImageCache.containsKey(imagePath)) {
            try (InputStream imageStream = ImageCache.class.getResourceAsStream(imagePath)) {
                if (imageStream != null) {
                    pieceImageCache.put(imagePath, new Image(imageStream));
                } else {
                    System.err.println("Asset missing: " + imagePath);
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return pieceImageCache.get(imagePath);
    }
}
