/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import controller.Controller;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import model.Model;
import model.player.Player;



/**
 *
 * @author guyklainer
 */
public class Validator {
    public static boolean validateInputRange( String choise, String from, String to) {
        if ( choise.compareTo( from ) < 0 || choise.compareTo( to ) > 0 || choise.length() > 1)
            return false;
        else
            return true;
    }

    public static boolean validateYesNoQuestions(String response) {
        if ( response.equals("y") || response.equals("n") )
            return true;
        else
            return false;
    }
    
    public static boolean validateName(Model model, String name) {
        for (Player player : model.getPlayers()){
            if (name.equals(player.getName()))
                return false;
        }
        
        return true;
    }

    public static boolean validateXML( File selectedFile ) {
        if( selectedFile.getName().toLowerCase().endsWith( "xml" ) ) {
            return true;
        }
        return false;
    }

    public static boolean validateUniqueNames(Controller controller) {
        Set set = new HashSet<String>() {};
        for( Player player : controller.getPlayers() ) {
            set.add( player.getName() );
        }
        
        return set.size() == controller.getPlayers().size();
    }
    
}
