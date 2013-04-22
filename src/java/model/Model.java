/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;
import model.board.Board;
import model.card.Card;
import model.player.Player;

/**
 *
 * @author guyklainer
 */
public class Model {
    
    private List<Player> players = new ArrayList<Player>();
    private List<Player> tempPlayers = new ArrayList<Player>();
    private Deck deck;    
    private List<Card> envelope;
    private Board board;
    
    public Model(){
        this.deck       = new Deck();
        this.envelope   = new ArrayList<Card>();
        this.board      = new Board();
    }

    public Deck getDeck(){
        return deck;
    }

    public List<Player> getPlayers(){
        return players;
    }
    
    public List<Card> getEnvelope(){
        return this.envelope;
    }
    
    
    public Board getBoard() {
        return this.board;
    }
    
    public void addCardToEnvelope(Card card){
        envelope.add(card);
    }

    public List<Player> getXMLPlayers() {
        return this.tempPlayers;
    }
    
}
