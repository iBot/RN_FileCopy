package erzVerbrMonitore;

/**
 * BoundedBuffer.java
 */

import java.util.*;

public class BoundedBuffer {
  private static final int BUFFER_SIZE = 1;
  private LinkedList<Object> buffer;
  
  public BoundedBuffer() {
    buffer = new LinkedList<Object>();
  }
  
  // Producer und Consumer benutzen diese Methode, um sich schlafen zu legen
  public static void sleeping() {
    int sleepTime = (int) (100 * Math.random());
    
    try {
      Thread.sleep(sleepTime);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  
  // Producer ruft die Methode ENTER auf
  public synchronized void enter(Object item) {
    // Falls Puffer voll ==> Warten!
    while (buffer.size() == BUFFER_SIZE) {
      System.err.println("PPPPPPPPPPPPPP Producer "
      + Thread.currentThread().getName() + " is waiting!");
      try {
        wait();
        System.err.println("PPPPPPPPPPPPPP Producer "
        + Thread.currentThread().getName() + " is active!");
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return;
      }
    }
    // Item zum Buffer hinzuf�gen
    buffer.add(item);
    if (buffer.size() == BUFFER_SIZE)
    System.err.println("PPPPPPPPPPPPPP Producer "
    + Thread.currentThread().getName()
    + " Entered Item ----------- Buffer FULL");
    else
    System.err.println("PPPPPPPPPPPPPP Producer "
    + Thread.currentThread().getName()
    + " Entered Item +++++++++++ Buffer Size = "
    + buffer.size());
    
    // Wartenden Producer/Consumer wecken
    // Es m�ssen ALLE Threads geweckt werden, da es nur eine Wait-Queue gibt!
    notifyAll();
    
    // Bufferzugriff entsperren (Monitor-Austritt)
  }
  
  // Consumer ruft die Methode REMOVE auf
  public synchronized Object remove() {
    Object item;
    
    // Falls Puffer leer ==> Warten!
    while (buffer.size() == 0) {
      System.err.println("CCCCCCCCCCCCCC Consumer "
      + Thread.currentThread().getName() + " is waiting!");
      try {
        wait();
        System.err.println("CCCCCCCCCCCCCC Consumer "
        + Thread.currentThread().getName() + " is active!");
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return null;
      }
    }
    // Item aus dem Buffer entfernen
    item = buffer.removeFirst();
    
    if (buffer.size() == 0)
    System.err.println("CCCCCCCCCCCCCC Consumer "
    + Thread.currentThread().getName()
    + " Consumed! ---------- Buffer EMPTY");
    else
    System.err.println("CCCCCCCCCCCCCC Consumer "
    + Thread.currentThread().getName()
    + " Consumed! ++++++++++ Buffer Size = " + buffer.size());
    
    // Wartenden Producer/Consumer wecken
    // Es m�ssen ALLE Threads geweckt werden, da es nur eine Wait-Queue gibt!
    notifyAll();
    
    // Bufferzugriff entsperren (Monitor-Austritt)
    return item;
  }
  
}
