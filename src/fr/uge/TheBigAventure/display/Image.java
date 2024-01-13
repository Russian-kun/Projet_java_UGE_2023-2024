package fr.uge.TheBigAventure.display;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Classe servant à stocker les informations d'une image.
 */
public record Image(Path path, BufferedImage data) {

  public Image(Path path, BufferedImage data) {
    this.path = path;
    this.data = data;
  }

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

  }
}
