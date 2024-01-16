package fr.uge.TheBigAventure.display;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Classe servant à stocker les informations d'une image.
 */
public record Image(Path path, BufferedImage data) {

  public static Image searchImage(Path path) {
    BufferedImage image = null;
    try {
      image = javax.imageio.ImageIO.read(new File(path.toString()));
    } catch (IOException e) {
      image = null;
    }
    return new Image(path, image);
  }

  public static Image searchImage(String path) {
    return searchImage(Path.of(path));
  }

  public Path getPath() {
    return path;
  }

  public BufferedImage getData() {
    return data;
  }

  public static Image getImage(String type) {
    return searchImage(Path.of("resources/images").resolve(type + "_0.gif"));
  }

  /**
   * Retourne l'image correspondant au type passé en paramètre. Cherche l'image
   * dans le .jar
   * 
   * @param type
   * @return
   */
  public static Image getImageInJar(String type) {
    InputStream is = Image.class.getResourceAsStream("/images/" + type + "_0.gif");
    if (is == null) {
      System.err.println("Image not found " + type + "_0.gif");
      return null;
    }
    try {
      return new Image(Path.of("images").resolve(type + "_0.gif"), javax.imageio.ImageIO.read(is));
    } catch (Exception e) {
      System.err.println("Error while loading image " + type + "_0.gif");
      return null;
    }
  }

  public BufferedImage rotatedData(int angle) { // FIXME this method removes the alpha channel and crops the image
    int w = data.getWidth(), h = data.getHeight(), x = w / 2, y = h / 2;
    double rads = Math.toRadians(angle), sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
    int newWidth = (int) Math.floor(w * cos + h * sin), newHeight = (int) Math.floor(h * cos + w * sin);

    BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

    AffineTransform at = new AffineTransform();
    at.translate((newWidth - w) / 2, (newHeight - h) / 2);
    at.rotate(rads, x, y);

    java.awt.Graphics2D g2 = rotated.createGraphics();
    g2.setTransform(at);
    g2.drawImage(data, 0, 0, null);
    g2.dispose();

    return rotated;
  }
}
