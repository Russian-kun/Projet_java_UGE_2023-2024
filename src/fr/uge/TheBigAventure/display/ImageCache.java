package fr.uge.TheBigAventure.display;

import java.util.HashMap;
import java.util.Objects;

import fr.uge.TheBigAventure.gameObjects.Element;
import fr.uge.TheBigAventure.gameObjects.GeneralSkin;

public record ImageCache(HashMap<GeneralSkin, Image> images) {

  public ImageCache {
    Objects.requireNonNull(images);
  }

  public void add(GeneralSkin skin, Image image) {
    images.put(skin, image);
  }

  public Image getImage(Element element) {
    return images.computeIfAbsent(element.getSkin(), k -> Image.getImage(element.getSkin().toString()));
  }
}
