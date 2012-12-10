/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tobi
 */
public class UDPSender {

    private DatagramSocket clientSocket;  // UDP-Socketklasse
    private InetAddress serverIpAddress;  // IP-Adresse des Zielservers
    private int serverPort;
    private List<Long> list = new ArrayList<Long>();

    public UDPSender(DatagramSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    void sendPackage(FCpacket fcp, String serverName, int serverPort) {
        try {

            /* UDP-Socket erzeugen (kein Verbindungsaufbau!)
             * Socket wird an irgendeinen freien (Quell-)Port gebunden, da kein Port angegeben */
            //clientSocket = new DatagramSocket(60000, InetAddress.getByName("127.0.0.1"));
            serverIpAddress = InetAddress.getByName(serverName); // Zieladresse
            this.serverPort = serverPort;

//            System.out.println("Sending Packet with Sequence Number " + fcp.getSeqNum());
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
                    new DatagramPacket(fcPacket.getSeqNumBytesAndData(), fcPacket.getSeqNumBytesAndData().length, serverIpAddress, serverPort);
         
//            System.out.println(fcPacket.getSeqNum()+".length ->"+sendPacket.getData().length);
            
//            if (fcPacket.getSeqNum() != 0) {
//    //            if (puffermethode(sendPacket)) {
//                    writefilelocal(fcPacket.getData() );
//    //            }
//            }

//        System.out.println("data "+ new String(sendPacket.getData(),"UTF-8"));
            /* Senden des Pakets */
            clientSocket.send(sendPacket);

        } catch (IOException e) {
            System.err.println(e.toString());
            System.out.println("Failed to send Packet with Sequence Number " + fcPacket.getSeqNum());
        }
    }

    private void writefilelocal(byte[] data) {
        try {
            FileCopyClient.outToFile.write(data);
        } catch (IOException ex) {
            Logger.getLogger(FileCopyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean puffermethode(DatagramPacket fcp) {
        FCpacket fcReceivePacket = new FCpacket(fcp.getData(),
                fcp.getLength());
        if (list.contains(fcReceivePacket.getSeqNum())) {
            return false;
        }
        list.add(fcReceivePacket.getSeqNum());
        System.out.println("list size " + list.size());
        return true;

    }

    private byte[] getdata(DatagramPacket fcp) {
        FCpacket fcReceivePacket = new FCpacket(fcp.getData(),
                fcp.getLength());
        return fcReceivePacket.getData();

    }
}
