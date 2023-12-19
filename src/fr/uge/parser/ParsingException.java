package fr.uge.parser;

import java.util.List;

public class ParsingException extends Exception {
  private final List<Exception> exceptions;

  public ParsingException(List<Exception> exceptions) {
    this.exceptions = exceptions;
  }

  public List<Exception> getExceptions() {
    return exceptions;
  }
}