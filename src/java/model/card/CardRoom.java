/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.card;

import model.Room;


/**
 *
 * @author guyklainer
 */
public class CardRoom extends Card {
    
    private Room room;

    public CardRoom(Room room) {
        this.room = room;
    }
    
    @Override
    public Room getCard(){
        return room;
    }
}
