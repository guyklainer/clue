/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.player;

import java.util.ArrayList;
import java.util.List;
import model.CardType;
import model.Suspect;
import model.card.Card;

/**
 *
 * @author guyklainer
 */
public class Player {
    private String          name;
    private Suspect         suspect;
    private List<Card>      cards;
    private List<Card>      viewedCards;
    private boolean         active;
    private boolean         humanPlayer;
    private boolean         justSuspect;
    
    public Player(String name, Suspect suspect, List<Card> cards, boolean isHuman){
        this.name               = name;
        this.suspect            = suspect;
        this.cards              = cards;
        this.viewedCards        = new ArrayList<Card>();
        this.active             = true;
        this.humanPlayer        = isHuman;
        this.justSuspect        = false;
    }
    
    public Player(String name, Suspect suspect, boolean isHuman){
        this.name               = name;
        this.suspect            = suspect;
        this.cards              = new ArrayList<Card>();
        this.viewedCards        = new ArrayList<Card>();
        this.active             = true;
        this.humanPlayer        = isHuman;
        this.justSuspect        = false;
    }
    
    public void addCard(Card card){
        cards.add(card);
    }
    
    public String getName(){
        return name;
    }
    
    public List<Card> getViewedCards(){
        return viewedCards;
    }

    public List<Card> getCards() {
        return cards;
    }

    public boolean find(CardType cardType, Enum value) {
        for ( Card card : cards ) {
            if ( card.getCard() == value )
                return true;
        }
        return false;
    }
    
    public void deActivate() {
        this.active = false;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isHuman() {
        return humanPlayer;
    }
    
    public Suspect getSuspect(){
        return suspect;
    }

    public void cleanUp() {
        this.active = true;
        this.cards.clear();
        this.viewedCards.clear();
    }
    
    public void setJustSuspect() {
        this.justSuspect = true;
    }
    
    public boolean isjustSuspect() {
        return this.justSuspect;
    }

    public void setAsComputer() {
        this.humanPlayer = false;
    }
}
