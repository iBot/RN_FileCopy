package src;

/* FileCopyClient.java
 Version 0.1 - Muss erg�nzt werden!!
 Praktikum 3 Rechnernetze BAI4-SS2012 HAW Hamburg
 Autoren:
 */

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileCopyClient extends Thread {

  // -------- Constants
  public final static boolean TEST_OUTPUT_MODE = false;

  public final int SERVER_PORT = 23000;

  public final int UDP_PACKET_SIZE = 1024;
  
  private final double X = 0.1;

  // -------- Public parms
  public String servername;

  public String sourcePath;

  public String destPath;

  public int windowSize;

  public long serverErrorRate;
  
  // -------- Variables
  // current default timeout in nanoseconds
  private long timeoutValue = 100000000;
  
  private long deviation =0;
  
  private long estimatedRTT = 0;
  
  private Puffer sendePuffer; 
  
  private UDPSender sender;

  // ... ToDo

  
  // -------- Streams
  private FileInputStream inFromFile;

  // Constructor
  public FileCopyClient(String serverArg, String sourcePathArg,
    String destPathArg, int windowSizeArg, long errorRateArg) {
    servername = serverArg;
    sourcePath = sourcePathArg;
    destPath = destPathArg;
    windowSize = windowSizeArg;
    serverErrorRate = errorRateArg;
    sendePuffer = new Puffer(windowSize);
        try {
            inFromFile = new FileInputStream(sourcePath);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileCopyClient.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getStackTrace());
        }

  }

  public void runFileCopyClient() {

      // ToDo!!
     //Kontroll Paket senden
      FCpacket contFCpacket = makeControlPacket();
      sendPackage(contFCpacket);
      
      
      

      

  }
  
  
    private void sendPackage(FCpacket fcp){
        fcp.setTimestamp(System.currentTimeMillis());
        fcp.setTimer(new FC_Timer(timeoutValue, this, fcp.getSeqNum()));
        sender.sendPackage(fcp);
    }

  
  private FCpacket getNextPackage(long seqNum){
      
      byte[] packetData = readFromFile(seqNum);
      
      
      
      FCpacket result = new FCpacket(seqNum,packetData, 1024);
      return result;
  }
  
     private byte[] readFromFile(long seqNum){
        byte[] data = new byte[UDP_PACKET_SIZE];
        try {
            inFromFile.read(data, (int)seqNum, UDP_PACKET_SIZE);
        } catch (IOException ex) {
            Logger.getLogger(FileCopyClient.class.getName()).log(Level.SEVERE, null, ex);
             System.err.println(ex.getStackTrace());
        }
        return data;
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
      FCpacket fcp = sendePuffer.getPackageForSeqNum(seqNum);
      sendPackage(fcp);
  }
  



  /**
   *
   * Computes the current timeout value (in nanoseconds)
   */
  public void computeTimeoutValue(long sampleRTT) {
        timeoutValue = estimatedRTT + 4 * computeDeviation(sampleRTT);
  }

    private long computeDeviation(long smapleRTT){
        deviation = new Double((1-X)*deviation + X*Math.abs(smapleRTT-estimatedRTT)).longValue();
        return deviation;
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
