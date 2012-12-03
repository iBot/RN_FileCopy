/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.net.*;
import java.io.*;

/**
 *
 * @author Tobi
 */
public class UDPSender {

    private DatagramSocket clientSocket;  // UDP-Socketklasse
    private InetAddress serverIpAddress;  // IP-Adresse des Zielservers
    private int serverPort;
    

    public UDPSender(DatagramSocket clientSocket){
        this.clientSocket = clientSocket;
    }
    
    void sendPackage(FCpacket fcp, String serverName, int serverPort) {
        try {
            /* UDP-Socket erzeugen (kein Verbindungsaufbau!)
             * Socket wird an irgendeinen freien (Quell-)Port gebunden, da kein Port angegeben */
            //clientSocket = new DatagramSocket(60000, InetAddress.getByName("127.0.0.1"));
            serverIpAddress = InetAddress.getByName(serverName); // Zieladresse
            this.serverPort = serverPort;

            System.out.println("Sending Packet with Sequence Number " + fcp.getSeqNum());
            /* Sende den String als UDP-Paket zum Server */
            writeToServer(fcp);
            //System.out.println("DATA: "+new String(fcp.getData(), "UTF-8"));
            /* Socket schlie�en (freigeben)*/
            //clientSocket.close();
        } catch (IOException e) {
            System.err.println(e.toString());
            System.exit(1);
        }
    }

    private void writeToServer(FCpacket fcPacket) {
        /* Sende den Packet als UDP-Paket zum Server */
        try {
//            System.out.println("data "+ new String(fcPacket.getData(),"UTF-8"));
            /* Paket erzeugen */
            DatagramPacket sendPacket =
                    new DatagramPacket(fcPacket.getSeqNumBytesAndData(), fcPacket.getLen() + 8, serverIpAddress, serverPort);


//        System.out.println("data "+ new String(sendPacket.getData(),"UTF-8"));
            /* Senden des Pakets */
            clientSocket.send(sendPacket);

        } catch (IOException e) {
            System.err.println(e.toString());
            System.out.println("Failed to send Packet with Sequence Number " + fcPacket.getSeqNum());
        }
    }
}
