package fr.uge.lexer;

import java.util.Objects;

/**
 * Record used to store the information of a token.
 * start and end are the start and end indices of the token.
 * So you can go back (once) in the reading if necessary.
 */
public record Result(Token token, String content, int start, int end) {
  public Result {
    Objects.requireNonNull(token);
    Objects.requireNonNull(content);
  }
}
