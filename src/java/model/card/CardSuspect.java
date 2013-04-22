/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.card;

import model.Suspect;

/**
 *
 * @author guyklainer
 */
public class CardSuspect extends Card {
     private Suspect suspect;
    
    public CardSuspect(Suspect suspect){
        this.suspect = suspect;
    }
    
    @Override
    public Suspect getCard(){
        return suspect;
    }
    
}
