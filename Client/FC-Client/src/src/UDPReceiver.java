/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author Tobi
 */




public class UDPReceiver {
    
    private static final int BUFFER_SIZE = 123;
    private DatagramSocket clientSocket;  // UDP-Socketklasse
    private final BlockingQueue<String> messageQueue;
    private boolean serviceRequested = true;
    
    public UDPReceiver(BlockingQueue<String> messageQueue) {
        this.messageQueue = messageQueue;
        startJob();
    }
    
    private void startJob() {
        try {
            /* UDP-Socket erzeugen (kein Verbindungsaufbau!)
             * Socket wird an irgendeinen freien (Quell-)Port gebunden, da kein Port angegeben */
            this.clientSocket = new DatagramSocket();

            String message;

            while (serviceRequested) {
                message = readFromServer();
                this.messageQueue.add(message);
            }

            /* Socket schließen (freigeben)*/
            clientSocket.close();
        } catch (IOException e) {
            System.err.println(e.toString());
            System.exit(1);
        }
        System.out.println("UDP Client stopped!");
    }
    
    public void stopJob() {
        this.serviceRequested = false;
    }

    private String readFromServer() {
        /* Liefere den nächsten String vom Server */
        String receiveString = "";

        try {
            /* Paket für den Empfang erzeugen */
            byte[] receiveData = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, BUFFER_SIZE);

            /* Warte auf Empfang des Antwort-Pakets auf dem eigenen Port */
            clientSocket.receive(receivePacket);

            /* Paket wurde empfangen --> auspacken und Inhalt anzeigen */
            receiveString = new String(receivePacket.getData(), 0, receivePacket.getLength());
        } catch (IOException e) {
            System.err.println("Connection aborted by server!");
            serviceRequested = false;
        }
        System.out.println("UDP Client got from Server: " + receiveString);
        return receiveString;
    }
}
