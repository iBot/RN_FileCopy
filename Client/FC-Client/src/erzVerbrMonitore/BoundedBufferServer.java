package erzVerbrMonitore;

import java.util.*;


/**
 * BoundedBufferServer.java
 */
public class BoundedBufferServer {
  public final int NO_PRODUCER = 5;  // Anzahl Threads
  public final int NO_CONSUMER = 1;  // Anzahl Threads

  public BoundedBuffer server = new BoundedBuffer();
  
  public static void main(String[] args) {
    // Starte Simulation
    new BoundedBufferServer().startSimulation();
  }
  
  public void startSimulation() {
    // Starte und beende Threads
    LinkedList<Producer> producerList = new LinkedList<Producer>();
    LinkedList<Consumer> consumerList = new LinkedList<Consumer>();
    
    // Consumer - Threads erzeugen
    for (int i = 1; i <= NO_CONSUMER; i++) {
      Consumer current = new Consumer(server);
      current.setName("C" + i);
      consumerList.add(current);
      current.start();
    }

    // Producer - Threads erzeugen
    for (int i = 1; i <= NO_PRODUCER; i++) {
      Producer current = new Producer(server);
      current.setName("P" + i);
      producerList.add(current);
      current.start();
    }

    // Laufzeit abwarten
    try {
      Thread.sleep(10000);

      // Consumer - Threads stoppen
      for (int i = 0; i < NO_CONSUMER; i++) {
        consumerList.get(i).interrupt();
        consumerList.get(i).join();
      }

      // Producer - Threads stoppen
      for (int i = 0; i < NO_PRODUCER; i++) {
        producerList.get(i).interrupt();
        producerList.get(i).join();
      }
    } catch (InterruptedException e) {
    }

    System.err.println("-------------------- THE END -------------------");
  }
}
