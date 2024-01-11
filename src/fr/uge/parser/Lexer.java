package fr.uge.parser;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class allowing you to split a character string into tokens.
 *
 * Kindly offered by Mr. Forax
 */
public class Lexer {
  private static final List<Token> TOKENS = List.of(Token.values());
  // Pour chaque pattern, on fait : (regex) | (regex) | ...
  private static final Pattern PATTERN = Pattern.compile(
      TOKENS.stream()
          .map(token -> "(" + token.regex() + ")")
          .collect(joining("|")));

  private final String text;
  private final Matcher matcher;
  private int line = 0;

  private Result lastResult = null;

  public Lexer(String text) {
    this.text = Objects.requireNonNull(text);
    this.matcher = PATTERN.matcher(text);
    this.line = 0;
  }

  public Result nextResult() {
    if (!matcher.find())
      return null;
    for (var group = 1; group <= matcher.groupCount(); group++) {
      var start = matcher.start(group);
      if (start != -1) {
        var end = matcher.end(group);
        var content = text.substring(start, end);
        line += text.substring(0, end).lines().count();
        lastResult = new Result(TOKENS.get(group - 1), content, start, end);
        return lastResult;
      }
    }
    throw new AssertionError();
  }

  public Result lastResult() {
    return lastResult;
  }

  public String text() {
    return text;
  }

  public Matcher matcher() {
    return matcher;
  }

  public int line() {
    return line;
  }

}
