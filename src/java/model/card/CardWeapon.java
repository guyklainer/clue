/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.card;

import model.Weapon;

/**
 *
 * @author guyklainer
 */
public class CardWeapon extends Card {
     private Weapon weapon;
    
    public CardWeapon(Weapon weapon){
        this.weapon = weapon;
    }
    
    @Override
    public Weapon getCard(){
        return weapon;
    }
}
