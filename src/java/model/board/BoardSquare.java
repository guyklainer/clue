/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.board;

import model.Room;
import model.Weapon;


/**
 *
 * @author guyklainer
 */
public class BoardSquare {
    
    private Room room;
    private Weapon weapon;
    private int position;

    public BoardSquare(Room room, Weapon weapon, int position) {
        this.room = room;
        this.weapon = weapon;
        this.position = position;

    }
    
    public int getPosition() {
        return position;
    }

    public Room getRoom() {
        return this.room;
    }
    
    public Weapon getWeapon() {
        return this.weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }
}
