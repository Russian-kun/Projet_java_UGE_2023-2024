package fr.uge.TheBigAventure.display;

import java.nio.file.Path;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

/**
 * Classe servant à stocker les informations d'une image.
 */
public class Image {
  private final Path path;
  private final BufferedImage data;

  public Image(Path path) {
    this.path = path;
    BufferedImage image = null;
    try {
      image = javax.imageio.ImageIO.read(new File(path.toString()));
    } catch (IOException e) {
      e.printStackTrace();
    }
    data = image;
  }

  public Image(String path) {
    this(Path.of(path));
  }

  public Path getPath() {
    return path;
  }

  public BufferedImage getData() {
    return data;
  }

  public static BufferedImage getImage(String type) {
    return new Image(Path.of("images").resolve(type + "_0.gif")).getData();
  }
}
