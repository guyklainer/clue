/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author guyklainer
 */
public enum CubeNumber {
    ONE,TWO,THREE,FOUR,FIVE,SIX;
    
    public static CubeNumber getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }
}
