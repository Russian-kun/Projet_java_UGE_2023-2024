package fr.uge.parser;

/**
 * List of tokens used by the lexer.
 */
public enum Token {
  HEADER("data:|size:|encodings:|\\[element\\]"),
  ZONE("\\( *[0-9]+ *\\, *[0-9]+ *\\) *\\( *[0-9]+ *x *[0-9]+ *\\)"),
  POSITION("\\( *[0-9]+ *\\, *[0-9]+ *\\)"),
  // Adding capturing groups seem crash the parser?
  // TRADE("[A-Za-z]+ -> [A-Za-z]+( [A-Za-z]+) *,{0,1}"),
  // We separate the trade
  TRADE_WITH_NAME("[A-Za-z]+ -> [A-Za-z]+ *[A-Za-z]+ *,{0,1}"),
  TRADE("[A-Za-z]+ -> [A-Za-z]+ *,{0,1}"),
  IDENTIFIER("[A-Za-z]+"),
  NUMBER("[0-9]+"),
  LEFT_PARENS("\\("),
  RIGHT_PARENS("\\)"),
  LEFT_BRACKET("\\["),
  RIGHT_BRACKET("\\]"),
  COMMA(","),
  COLON(":"),
  QUOTE("\"\"\"[^\"]+\"\"\""),
  QUOTETEXT("\"[^\"]+\""),
  ;
  // NEWLINE("\\n"),

  private final String regex;

  Token(String regex) {
    this.regex = regex;
  }

  String regex() {
    return regex;
  }
}
