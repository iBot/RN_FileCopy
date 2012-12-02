/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import erzVerbrMonitore.Producer;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Tobi
 */
public class Puffer extends Thread {

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

    @Override
    public void run() {
    }
    
    public synchronized  void ackPackage(long seqNumber){
        throw new Error("Not implemented yet");
    }

    /**
     * Gibt die Sequenznummer des ältesten Pakets im Sendepuffer zurück
     *
     * @return Sequenznummer des ältesten Pakets im Sendepuffer
     */
    public synchronized long getSendbase() {
        return puffer.first().getSeqNum();
    }

    /**
     * Gibt die Sequenznummer des nächsten zu sendenden Pakets zurück
     *
     * @return Sequenznummer des nächsten zu sendenden Pakets
     */
    public synchronized long getNextSeqNum() {
        FCpacket last = puffer.last();
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
        if (puffer.size() == pufferSize) {
        }
    }
}
