/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.card.Card;
import model.card.CardRoom;
import model.card.CardSuspect;
import model.card.CardWeapon;
import model.player.Player;

/**
 *
 * @author guyklainer
 */
public final class Deck {
    List<Card> cards;
    
    public Deck(){
        cards = new ArrayList<Card>();
        
        for (Room room : Room.values()){
            cards.add( new CardRoom(room) );
        }
        for (Suspect suspect : Suspect.values()){
            cards.add( new CardSuspect(suspect) );
        }
        for (Weapon weapon : Weapon.values()){
            cards.add( new CardWeapon(weapon) );
        }
        
        shuffle();
    }
    
    public Card find( Enum value ){
        for (Card card : cards){
            if (card.getCard() == value) {
                return card;
            }
        }
        return null;
        
    }

    public void remove(Card cardToRemove) {
        cards.remove(cardToRemove);
    }
    
    public void add(Card cardToAdd) {
        cards.add(cardToAdd);
    }
    
    public void remove(List<Card> envelope) {
        for (Card card : envelope){
            cards.remove(card);
        }
    }
    
    public void add(List<Card> envelope) {
        for (Card card : envelope){
            cards.add(card);
        }
    }
    
    public void shuffle(){
       Collections.shuffle(cards);
    }

    public void spreadCards(List<Player> players) {
        List<Player> playingPlayers = new ArrayList<Player>();
        
        for( Player player : players ) {
            if( !player.isjustSuspect() )
                playingPlayers.add( player );
        }
        
        int numOfPlayers    = playingPlayers.size();
        int counter         = 0;
        
        for (Card card : cards){
            if ( counter == numOfPlayers ) {
                counter = 0;
            }
            playingPlayers.get( counter++ ).addCard( card );
        }
    }

    public List<Card> getCards() {
        return this.cards;
    }
}
