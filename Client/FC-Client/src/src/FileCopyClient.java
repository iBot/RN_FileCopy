package src;

/* FileCopyClient.java
 Version 0.1 - Muss erg�nzt werden!!
 Praktikum 3 Rechnernetze BAI4-SS2012 HAW Hamburg
 Autoren:
 */

import java.io.*;
import java.net.*;

public class FileCopyClient extends Thread {

  // -------- Constants
  public final static boolean TEST_OUTPUT_MODE = false;

  public final int SERVER_PORT = 23000;

  public final int UDP_PACKET_SIZE = 1024;

  // -------- Public parms
  public String servername;

  public String sourcePath;

  public String destPath;

  public int windowSize;

  public long serverErrorRate;
  
  // -------- Variables
  // current default timeout in nanoseconds
  private long timeoutValue = 100000000;
  
  private Puffer sendePuffer; 

  // ... ToDo


  // Constructor
  public FileCopyClient(String serverArg, String sourcePathArg,
    String destPathArg, int windowSizeArg, long errorRateArg) {
    servername = serverArg;
    sourcePath = sourcePathArg;
    destPath = destPathArg;
    windowSize = windowSizeArg;
    serverErrorRate = errorRateArg;
    sendePuffer = new Puffer(windowSize);

  }

  public void runFileCopyClient() {

      // ToDo!!


  }

  /**
  *
  * Timer Operations
  */
  public void startTimer(FCpacket packet) {
    /* Create, save and start timer for the given FCpacket */
    FC_Timer timer = new FC_Timer(timeoutValue, this, packet.getSeqNum());
    packet.setTimer(timer);
    timer.start();
  }

  public void cancelTimer(FCpacket packet) {
    /* Cancel timer for the given FCpacket */
    testOut("Cancel Timer for packet" + packet.getSeqNum());

    if ((packet.getTimer() != null) && (packet.getTimer().isAlive())) {
      packet.getTimer().interrupt();
    }
  }

  /**
   * Implementation specific task performed at timeout
   */
  public void timeoutTask(long seqNum) {
      
  // ToDo
  }


  /**
   *
   * Computes the current timeout value (in nanoseconds)
   */
  public void computeTimeoutValue(long sampleRTT) {

  // ToDo
  }


  /**
   *
   * Return value: FCPacket with (0 destPath;windowSize;errorRate)
   */
  public FCpacket makeControlPacket() {
   /* Create first packet with seq num 0. Return value: FCPacket with
     (0 destPath ; windowSize ; errorRate) */
    String sendString = destPath + ";" + windowSize + ";" + serverErrorRate;
    byte[] sendData = null;
    try {
      sendData = sendString.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return new FCpacket(0, sendData, sendData.length);
  }



  public void testOut(String out) {
    if (TEST_OUTPUT_MODE) {
      System.out.println("Client: " + out);
    }
  }
  
    private static void wrongArguments(){
        System.out.println("Biite rufen Sie das Programm mit folgenden Parametern auf:\n\t hostname fileSourcce fileTarget windowsSize errorRate\n\twindowsSize and errorRate must be positive");
        System.exit(0);
    }


  public static void main(String argv[]) throws Exception {
      String hostname = "";
       String fileSource = "";;
       String fileTarget = "";;
       int windowsSize = -1;
       long errorLevel =-1;
      
      //Variablen setzen
        try{
               hostname = argv[0];
               fileSource = argv[1];
               fileTarget = argv[2];
               windowsSize = Integer.parseInt(argv[3]);
               errorLevel = Long.parseLong(argv[4]);
               if ((windowsSize<0) || (errorLevel<0)){
                   wrongArguments();
               }
            } catch (IndexOutOfBoundsException | NumberFormatException ex){
            wrongArguments();
        }
    FileCopyClient myClient = new FileCopyClient(hostname, fileSource, fileTarget, windowsSize, errorLevel);
    myClient.runFileCopyClient();
  }

}
