/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author guyklainer
 */
public enum Weapon {
    CANDLESTICK,
    DAGGER,
    LEAD_PIPE,
    REVOLVER,
    ROPE,
    WRENCH;
    
    public static Weapon getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }
    
    public String getNicerName() {
        String[] tokens = this.name().toLowerCase().split("_");
        String name = "";
        for(int i = 0; i < tokens.length; i++){
            char capLetter = Character.toUpperCase(tokens[i].charAt(0));
            name +=  " " + capLetter + tokens[i].substring(1, tokens[i].length());
        }
        
        return name;
    }
}