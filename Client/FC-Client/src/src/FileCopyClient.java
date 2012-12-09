package src;

/* FileCopyClient.java
 Version 0.1 - Muss erg�nzt werden!!
 Praktikum 3 Rechnernetze BAI4-SS2012 HAW Hamburg
 Autoren:
 */
import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileCopyClient extends Thread {

    // -------- Constants
    public final static boolean TEST_OUTPUT_MODE = true;
    public final int SERVER_PORT = 23000;
    public final int UDP_PACKET_SIZE = 1024;
    private final int UDP_DATA_SIZE = UDP_PACKET_SIZE - 8;
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
    private long deviation = 0;
    private long estimatedRTT = timeoutValue;
    private Puffer sendePuffer;
    private UDPSender sender;
    private UDPReceiver receiver;
    private boolean finish = false;
    // ... ToDo
    // -------- Streams
    private FileInputStream inFromFile;
    public static FileOutputStream outToFile;

    // Constructor
    /**
     *
     * @param serverArg
     * @param sourcePathArg
     * @param destPathArg
     * @param windowSizeArg
     * @param errorRateArg
     */
    public FileCopyClient(String serverArg, String sourcePathArg,
            String destPathArg, int windowSizeArg, long errorRateArg) {
        servername = serverArg;
        sourcePath = sourcePathArg;
        destPath = destPathArg;
        windowSize = windowSizeArg;
        serverErrorRate = errorRateArg;
        try {
            inFromFile = new FileInputStream(sourcePath);
            outToFile = new FileOutputStream("D:\\OrangeMan2.text");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileCopyClient.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getStackTrace());
        }

    }

    /**
     *
     */
    private void runFileCopyClient() {

        DatagramSocket clientSocket = null;
        sendePuffer = new Puffer(windowSize, this);
        try {
            //sendePuffer.start();
            clientSocket = new DatagramSocket(60000, InetAddress.getByName("127.0.0.1"));
        } catch (Exception ex) {
            Logger.getLogger(FileCopyClient.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex);
        }

        this.receiver = new UDPReceiver(sendePuffer, clientSocket);
        receiver.start();

        sender = new UDPSender(clientSocket);


        FCpacket contFCpacket = makeControlPacket();

        sendePuffer.insert(contFCpacket);
        sendPackage(contFCpacket);


        FCpacket nextPackage = getNextPackage(1);
        while (nextPackage != null) {
            if (!sendePuffer.isFull()) {
                sendePuffer.insert(nextPackage);
                sendPackage(nextPackage);

                nextPackage = getNextPackage(sendePuffer.getNextSeqNum());
            }
        }
        System.out.println("<<<ALLE PAKETE IM PUFFER>>>");

        while (!sendePuffer.isEmpty()) {
        }

        receiver.stopJob();
        try {
            inFromFile.close();
        } catch (IOException ex) {
            Logger.getLogger(FileCopyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.exit(0);
    }

    private void sendPackage(FCpacket fcp) {
        fcp.setTimestamp(System.currentTimeMillis() * 1000000);
        fcp.setTimer(new FC_Timer(timeoutValue, this, fcp.getSeqNum()));
        startTimer(fcp);

        sender.sendPackage(fcp, servername, SERVER_PORT);
    }

    private FCpacket getNextPackage(long seqNum) {

        FCpacket result = null;

        byte[] packetData = readFromFile(seqNum);

        if (packetData != null) {
            result = new FCpacket(seqNum, packetData, UDP_PACKET_SIZE);
        }

        return result;
    }

    private byte[] readFromFile(long seqNum) {
        byte[] data = new byte[UDP_PACKET_SIZE];
        int nextByte = -2;
        try {

            nextByte = inFromFile.read(data);

        } catch (IOException ex) {
            Logger.getLogger(FileCopyClient.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.getStackTrace());
        }
        if (nextByte == -1) {
            data = null;
        }
        return data;
    }

    /**
     *
     * Timer Operations
     *
     * @param packet
     */
    public void startTimer(FCpacket packet) {
        /* Create, save and start timer for the given FCpacket */
        FC_Timer timer = new FC_Timer(timeoutValue, this, packet.getSeqNum());
        packet.setTimer(timer);
        timer.start();
    }

    /**
     *
     * @param packet
     */
    public void cancelTimer(FCpacket packet) {
        /* Cancel timer for the given FCpacket */
        testOut("Cancel Timer for packet" + packet.getSeqNum());

        if ((packet.getTimer() != null) && (packet.getTimer().isAlive())) {
            packet.getTimer().interrupt();
        }
    }

    /**
     * Implementation specific task performed at timeout
     *
     * @param seqNum
     */
    public void timeoutTask(long seqNum) {
        FCpacket fcp = sendePuffer.getPackageForSeqNum(seqNum);
        if (fcp != null) {
            sendPackage(fcp);
        }
    }

    /**
     *
     * Computes the current timeout value (in nanoseconds)
     *
     * @param sampleRTT
     */
    public void computeTimeoutValue(long sampleRTT) {
        estimatedRTT = new Double((1 - X) * estimatedRTT + X * sampleRTT).longValue();
        timeoutValue = estimatedRTT + (4 * computeDeviation(sampleRTT));
        //    System.out.println(String.format("estimatedRTT:%d timeout: %d", estimatedRTT, timeoutValue));
    }

    private long computeDeviation(long smapleRTT) {
        deviation = new Double((1 - X) * deviation + X * Math.abs(smapleRTT - estimatedRTT)).longValue();
        return deviation;
    }

    /**
     *
     *
     * @return FCPacket with (0 destPath;windowSize;errorRate)
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

    /**
     *
     * @param out
     */
    public void testOut(String out) {
        if (TEST_OUTPUT_MODE) {
            System.out.println("Client: " + out);
        }
    }

    private static void wrongArguments() {
        System.out.println("Bitte rufen Sie das Programm mit folgenden Parametern auf:\n\t hostname fileSourcce fileTarget windowsSize errorRate\n\twindowsSize and errorRate must be positive");
        System.exit(0);
    }

    /**
     *
     * @param argv
     * @throws Exception
     */
    public static void main(String argv[]) throws Exception {
        String hostname = "";
        String fileSource = "";
        String fileTarget = "";
        int windowsSize = -1;
        long errorLevel = -1;

        //Variablen setzen
        try {
            System.out.println(Arrays.toString(argv));
            hostname = argv[0];
            fileSource = argv[1];
            fileTarget = argv[2];
            windowsSize = Integer.parseInt(argv[3]);
            errorLevel = Long.parseLong(argv[4]);
            if ((windowsSize < 0) || (errorLevel < 0)) {
                wrongArguments();
            }
        } catch (IndexOutOfBoundsException | NumberFormatException ex) {
            wrongArguments();
        }
        FileCopyClient myClient = new FileCopyClient(hostname, fileSource, fileTarget, windowsSize, errorLevel);
        myClient.runFileCopyClient();
    }
    
    private void writefilelocal(byte[] data) {
        try {
            FileCopyClient.outToFile.write(data);
        } catch (IOException ex) {
            Logger.getLogger(FileCopyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
