/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 *
 * @author Ned
 */
public class UDPReceiver extends Thread {

    private static final int BUFFER_SIZE = 8;
    private DatagramSocket clientSocket;  // UDP-Socketklasse
    private boolean serviceRequested = true;
    private long seqNumber;
    private Puffer puffer;

    public UDPReceiver(Puffer puffer, DatagramSocket clientSocket) {
        this.puffer = puffer;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
//        try {

//            System.out.println("UDP Reciver Thread is running until all Ack's Packets are received!");

            /* UDP-Socket erzeugen (kein Verbindungsaufbau!)
             * Socket wird an irgendeinen freien (Quell-)Port gebunden, da kein Port angegeben */
            //this.clientSocket = new DatagramSocket(60000, InetAddress.getByName("127.0.0.1"));

            while (serviceRequested) {
                seqNumber = readAckFromServer();
//                System.err.println("Received Ack for SeqNum: "+seqNumber);
                puffer.ackPackage(seqNumber);
            }

            /* Socket schließen (freigeben)*/
            clientSocket.close();
//        } catch (IOException e) {
//            System.err.println(e.toString());
//            System.exit(1);
//        }
//        System.out.println("UDP Reciver Thread stopped!\n last Ack Packet has this Sequence Number: " + seqNumber);
    }

    public void stopJob() {
        System.out.println("UDP RECEIVER STOPPED");
        this.serviceRequested = false;
    }

    private long readAckFromServer() {

        try {
            /* Paket für den Empfang erzeugen */
            byte[] receiveData = new byte[BUFFER_SIZE];
            DatagramPacket receiveAckPacket = new DatagramPacket(receiveData, BUFFER_SIZE);

            /* Warte auf Empfang des Antwort-Pakets auf dem eigenen Port */
            clientSocket.receive(receiveAckPacket);

            /* Paket wurde empfangen --> auspacken und Inhalt anzeigen */
            this.seqNumber = bytearray2long(receiveAckPacket.getData());

        } catch (IOException e) {
            System.err.println("Connection aborted by server!");
            serviceRequested = false;
        }
//        System.out.println("UDP Client got from Server: Packet with the number " + seqNumber + " recived successfully");
        return seqNumber;
    }

    private static long bytearray2long(byte[] b) {
        ByteBuffer buf = ByteBuffer.wrap(b);
        return buf.getLong();
    }
}
