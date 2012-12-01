package erzVerbrMonitore;


/**
 * Producer.java
 *
 * This is the producer thread for the bounded buffer problem.
 *
 */

import java.util.*;

public class Producer extends Thread {
  private BoundedBuffer currentBuffer;

  public Producer(BoundedBuffer b) {
    currentBuffer = b;
  }

  public void run() {
    Date item;

    while (!isInterrupted()) {

      // Item erzeugen und in den Buffer einstellen
      item = new Date();
      System.err.println("PPPPPPPPPPPPPP Producer " + this.getName()
          + " produced an Item");
      currentBuffer.enter(item);

      // F�r unbestimmte Zeit schlafen
      BoundedBuffer.sleeping();
    }
  }

}
