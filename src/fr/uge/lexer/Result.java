package fr.uge.lexer;

import java.util.Objects;

public record Result(Token token, String content, int start, int end) {
  public Result {
    Objects.requireNonNull(token);
    Objects.requireNonNull(content);
  }
}
