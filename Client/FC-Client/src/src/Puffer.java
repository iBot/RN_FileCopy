/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import erzVerbrMonitore.Producer;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Tobi
 */
public class Puffer {

    private SortedSet<FCpacket> puffer;
    private final int pufferSize;
    private final FileCopyClient producer;
    //private final UDPReceiver consumer;

    /**
     * Erstellt einen neuen Puffer
     *
     * @param pufferSize
     */
    public Puffer(int pufferSize, FileCopyClient producer) {
        this.producer = producer;
        this.pufferSize = pufferSize;
        this.puffer = new TreeSet<>();
    }

//    @Override
//    public void run() {
//        while (!interrupted()){
//            if (puffer.first().isValidACK()) {
//                puffer.remove(puffer.first());
//            }
//        }
//    }
    public synchronized FCpacket getFirst() {
        return puffer.first();
    }

    public synchronized void removeFirst() {
        puffer.remove(puffer.first());
    }

    public synchronized void ackPackage(long seqNumber) {
        
        FCpacket fcp = getPackageForSeqNum(seqNumber);
        System.out.println("--Ack: "+fcp.getSeqNum());
        if (fcp != null) {
            fcp.setValidACK(true);
            producer.cancelTimer(fcp);
            producer.computeTimeoutValue(System.currentTimeMillis()*1000-fcp.getTimestamp());
            System.out.print("---Delete from Buffer: ");
            while(getFirst().isValidACK()){
                System.out.print(getFirst().getSeqNum()+"; ");
                removeFirst();
            }
            System.out.println();
            
        }
        

    }

    /**
     * Gibt die Sequenznummer des ältesten Pakets im Sendepuffer zurück
     *
     * @return Sequenznummer des ältesten Pakets im Sendepuffer
     */
    public synchronized long getSendbase() {
        return puffer.first().getSeqNum();
    }

    public synchronized boolean isFull(){
        return pufferSize < puffer.size();
    }

    /**
     * Gibt die Sequenznummer des nächsten zu sendenden Pakets zurück
     *
     * @return Sequenznummer des nächsten zu sendenden Pakets
     */
    public synchronized long getNextSeqNum() {
        FCpacket last = puffer.last();
        System.out.println("NextSeqNum = "+(last.getSeqNum() + last.getLen()));
        return last.getSeqNum() + last.getLen();
    }

    public synchronized FCpacket getPackageForSeqNum(long seqNum) {
        FCpacket result = null;
        for (FCpacket fCpacket : puffer) {
            if (fCpacket.getSeqNum() == seqNum) {
                result = fCpacket;
            }
        }
        
        return result;
    }

    public synchronized void insert(FCpacket newPackage) {
        puffer.add(newPackage);
    }
}
