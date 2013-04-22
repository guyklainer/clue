/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author guyklainer
 */
public class AJAXResponse {
    private Operation operation;
    private Object object;
    
    public AJAXResponse( Operation operation, Object obj ) {
        this.operation = operation;
        this.object = obj;
    }
}
