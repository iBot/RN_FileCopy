/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

/**
 *
 * @author Tobi
 */
public class Puffer {
    
    
    /**
     * Erstellt einen neuen Puffer
     * @param PufferSize
     */
    public Puffer(int PufferSize){
        throw new Error("Not implemented yet!");
    }
    
    
    /**
     * Gibt die Sequenznummer des ältesten Pakets im Sendepuffer zurück
     * @return Sequenznummer des ältesten Pakets im Sendepuffer
     */
    public synchronized long getSendbase(){
        //Todo: SendBase zurückgeben
        throw new Error("Not implemented yet!");
    }
    
    /**
     * Gibt die Sequenznummer des nächsten zu sendenden Pakets zurück
     * @return Sequenznummer des nächsten zu sendenden Pakets
     */
    public synchronized long getNextSeqNum(){
        //Todo: NextSeqNum zurückgeben
        throw new Error("Not implemented yet!");
    }
    
    
    
    
}
