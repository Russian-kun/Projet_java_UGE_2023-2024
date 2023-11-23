package fr.uge.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import fr.uge.lexer.Lexer;
import fr.uge.lexer.Result;

public class Main {
  public static void main(String[] args) throws IOException {
    var path = Path.of("maps/demo1.map");
    var text = Files.readString(path);
    var lexer = new Lexer(text);
    Result result;
    while ((result = lexer.nextResult()) != null) {
      System.out.println(result);
    }
  }

}
