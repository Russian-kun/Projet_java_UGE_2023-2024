package fr.uge.parser;

import java.util.ArrayList;
import java.util.List;

public class ParsingException extends Exception {
  private final ArrayList<Exception> exceptions;

  public ParsingException(List<Exception> exceptions) {
    if (exceptions == null)
      this.exceptions = new ArrayList<>();
    else
      this.exceptions = new ArrayList<>(exceptions);
  }

  public List<Exception> getExceptions() {
    return exceptions;
  }

  public int size() {
    return exceptions.size();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Exception e : exceptions) {
      // sb.append(e.toString());
      sb.append(e.getMessage());
      sb.append("\n");
    }
    return sb.toString();
  }

  public void addException(Exception e) {
    exceptions.add(e);
  }

  public void addAllException(List<Exception> exceptions) {
    this.exceptions.addAll(exceptions);
  }
}