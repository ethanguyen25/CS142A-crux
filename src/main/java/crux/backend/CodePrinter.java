package crux.backend;

import java.util.*;
import java.io.*;

public class CodePrinter {
  PrintStream out;
  StringBuffer sb = new StringBuffer();

  public CodePrinter(String name) {
    try {
      out = new PrintStream(name);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  /**
   * This function immediately prints a label (non-indented).
   */

  public void printLabel(String s) {
    out.println(s);
  }

  /**
   * This function immediately prints code (indented).
   */

  public void printCode(String s) {
    out.println("    " + s);
  }

  /**
   * This function buffers a line of code to be printed later.
   */

  public void bufferCode(String s) {
    sb.append("    " + s + "\n");
  }

  /**
   * This function buffers a label to be printed later.
   */

  public void bufferLabel(String s) {
    sb.append(s + "\n");
  }

  /**
   * This function outputs all of the buffered text to the file.
   */

  public void outputBuffer() {
    out.print(sb);
    sb = new StringBuffer();
  }

  /**
   * THis function closes the file.
   */

  public void close() {
    out.close();
  }
}
