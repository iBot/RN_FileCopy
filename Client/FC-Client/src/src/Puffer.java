/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

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
    private long nextSeqNum;
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
        this.nextSeqNum = -1;

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
        if (puffer.size()>0){
        puffer.remove(puffer.first());
        }
        notifyAll();
    }

    public synchronized void ackPackage(long seqNumber) {

        FCpacket fcp = getPackageForSeqNum(seqNumber);

        if (fcp != null) {
//            System.out.println("--Ack: "+fcp.getSeqNum());
            fcp.setValidACK(true);
//            System.out.println("Puffer before; size : "+puffer.size()+" -> " + puffer);
            producer.cancelTimer(fcp);
            producer.computeTimeoutValue(System.nanoTime() - fcp.getTimestamp());
//            System.out.print("---Delete from Buffer: ");
            while (!isEmpty() && getFirst().isValidACK()) {
//                System.out.print(getFirst().getSeqNum()+"; ");
                removeFirst();
            }
//            System.out.println();
//            System.out.println("Puffer after; size : "+puffer.size()+" -> " + puffer);
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

    public synchronized boolean isFull() {
        return puffer.size() >= pufferSize;
    }

    /**
     * Gibt die Sequenznummer des nächsten zu sendenden Pakets zurück
     *
     * @return Sequenznummer des nächsten zu sendenden Pakets
     */
    public synchronized long getNextSeqNum() {
        return nextSeqNum;
//        FCpacket last = puffer.last();
//        System.out.println("NextSeqNum = "+(last.getSeqNum() + 1));
//        return last.getSeqNum() + 1;

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
        while (puffer.size() == pufferSize) {
            try {
                wait();
             } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        puffer.add(newPackage);
        nextSeqNum = newPackage.getSeqNum() + 1;

        // Wartenden Producer/Consumer wecken
        // Es m�ssen ALLE Threads geweckt werden, da es nur eine Wait-Queue gibt!
        notifyAll();
    }

    public synchronized boolean isEmpty() {
        return puffer.isEmpty();
    }

    public synchronized void waitForEmptyPuffer() {

        while (!puffer.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        notifyAll();
    }
}
