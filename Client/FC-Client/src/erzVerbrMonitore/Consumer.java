package erzVerbrMonitore;

/**
 * Consumer.java
 *
 * This is the consumer thread for the bounded buffer problem.
 *
 */

import java.util.*;

public class Consumer extends Thread {
  private BoundedBuffer currentBuffer;

  public Consumer(BoundedBuffer b) {
    currentBuffer = b;
  }

  public void run() {
    Date item;

    while (!isInterrupted()) {
      // Item aus dem Buffer verbrauchen
      System.err.println("CCCCCCCCCCCCCC Consumer " + this.getName()
          + " wants to consume!");
      item = (Date) currentBuffer.remove();

      // F�r unbestimmte Zeit schlafen
      BoundedBuffer.sleeping();
    }
  }

}
