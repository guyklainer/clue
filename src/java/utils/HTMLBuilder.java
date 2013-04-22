/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import controller.Controller;
import java.util.List;
import model.Room;
import model.Suspect;
import model.Weapon;
import model.card.Card;
import model.player.Player;

/**
 *
 * @author guyklainer
 */
public class HTMLBuilder {
    
    public static String createSelectSuspects() {
        String result = "";
        result += "<select class='suspect-select'>";
        result += "<option value='" + Suspect.COLONEL_MUSTARD.name() + "' data-imagesrc='images/mustard-face.png'>" + Suspect.COLONEL_MUSTARD.getNicerName() + "</option>";
        result += "<option value='" + Suspect.MISS_SCARLETT.name() + "' data-imagesrc='images/scarlett-face.png'>" + Suspect.MISS_SCARLETT.getNicerName() + "</option>";
        result += "<option value='" + Suspect.MRS_PEACOCK.name() + "' data-imagesrc='images/peacock-face.png'>" + Suspect.MRS_PEACOCK.getNicerName() + "</option>";
        result += "<option value='" + Suspect.MRS_WHITE.name() + "' data-imagesrc='images/white-face.png'>" + Suspect.MRS_WHITE.getNicerName() + "</option>";
        result += "<option value='" + Suspect.PROFESSOR_PLUM.name() + "' data-imagesrc='images/plum-face.png'>" + Suspect.PROFESSOR_PLUM.getNicerName() + "</option>";
        result += "<option value='" + Suspect.REVEREND_GREEN.name() + "' data-imagesrc='images/green-face.png'>" + Suspect.REVEREND_GREEN.getNicerName() + "</option>";
        result += "</select>";
        
        return result;
    }
    
    public static String createSelectWeapons() {
        String result = "";
        result += "<select class='weapon-select'>";
        result += "<option value='" + Weapon.CANDLESTICK.name() + "' data-imagesrc='images/candlestick2.png'>" + Weapon.CANDLESTICK.getNicerName() + "</option>";
        result += "<option value='" + Weapon.DAGGER.name() + "' data-imagesrc='images/dagger2.png'>" + Weapon.DAGGER.getNicerName() + "</option>";
        result += "<option value='" + Weapon.LEAD_PIPE.name() + "' data-imagesrc='images/lead-pipe2.png'>" + Weapon.LEAD_PIPE.getNicerName() + "</option>";
        result += "<option value='" + Weapon.REVOLVER.name() + "' data-imagesrc='images/revolver2.png'>" + Weapon.REVOLVER.getNicerName() + "</option>";
        result += "<option value='" + Weapon.ROPE.name() + "' data-imagesrc='images/rope2.png'>" + Weapon.ROPE.getNicerName() + "</option>";
        result += "<option value='" + Weapon.WRENCH.name() + "' data-imagesrc='images/wrench.png'>" + Weapon.WRENCH.getNicerName() + "</option>";
        result += "</select>";
        
        return result;
    }
    
    public static String createSelectRooms() {
        String result = "";
        result += "<select class='room-select'>";
        result += "<option value='" + Room.BALLROOM.name() + "' data-imagesrc='images/ballroom.png'>" + Room.BALLROOM.getNicerName() + "</option>";
        result += "<option value='" + Room.BILLIARD_ROOM.name() + "' data-imagesrc='images/billiard-room.png'>" + Room.BILLIARD_ROOM.getNicerName() + "</option>";
        result += "<option value='" + Room.CONSERVATORY.name() + "' data-imagesrc='images/conseevatory.png'>" + Room.CONSERVATORY.getNicerName() + "</option>";
        result += "<option value='" + Room.DINING_ROOM.name() + "' data-imagesrc='images/dining-room.png'>" + Room.DINING_ROOM.getNicerName() + "</option>";
        result += "<option value='" + Room.HALL.name() + "' data-imagesrc='images/hall.png'>" + Room.HALL.getNicerName() + "</option>";
        result += "<option value='" + Room.KITCHEN.name() + "' data-imagesrc='images/kitchen.png'>" + Room.KITCHEN.getNicerName() + "</option>";
        result += "<option value='" + Room.LIBRARY.name() + "' data-imagesrc='images/library.png'>" + Room.LIBRARY.getNicerName() + "</option>";
        result += "<option value='" + Room.LOUNGE.name() + "' data-imagesrc='images/lounge.png'>" + Room.LOUNGE.getNicerName() + "</option>";
        result += "<option value='" + Room.STUDY.name() + "' data-imagesrc='images/study.png'>" + Room.STUDY.getNicerName() + "</option>";
        
        result += "</select>";
        
        return result;
    }
    
    public static String creatAssumptionChooseCard( Player player, List<Card> list ) {
        String result = "";
        for( Card card : list ) {
            result += "<div class='card half-opacity " + card.getCard().name() + "' id='" + card.getCard().name() + "'></div>";
        }
        
        return result;
    }
    
    public static String createViewedCards( Controller controller ) {
        String result = "";
        
        if( controller.getCurrentPlayers().getViewedCards().size() > 0 )
            result += "<p>Your viewed cards</p>";
        
        for( Card card : controller.getCurrentPlayers().getViewedCards() ) {
            result += "<div class='card " + card.getCard().name() + "' title='" + card.getCard().name() + "'></div>";
        }
                
        return result;
    }
    
    public static String createPlayerCards( Controller controller ) {
        String result = "";
        result += "<div class='cards'><h5>Your cards</h5>";
        
        for( Card card : controller.getCurrentPlayers().getCards() ) {
            result += "<div class='card " + card.getCard().name() + "' title='" + card.getCard().name() + "'></div>";
        }
        
        result += "</div>";
        return result;
    }
    
    public static String createWinnerForm( Controller controller, List<Card> accusation ) {
        String result = "";
        result += "<div class='fader'>";
        result += "<div class='in-fader'>";
        result += "<img src='images/fireworks2.gif' style='width: 100px'>";
        result += "<p>" + controller.getCurrentPlayers().getName() + " (" + controller.getCurrentPlayers().getSuspect().getNicerName() + ") ";
        if( accusation != null ) {
            result += "made this accusation</p>";
            for( Card card : accusation )
                result += "<div class='card " + card.getCard().name() + "'></div>";
        } else 
            result += "</p>";
        result += "<p>and he won the game!!!</p>";
        result += "<p class='yellow'>Good for him!!</p>";
        result += "<img src='images/fireworks1.gif'>";
        result += "<p>Start new game</p>";
        result += "<form method='post' action='/clue/play'>";
        result += "<input type='hidden' name='details' value='same'>";
        result += "<input type='submit' class='btn btn-info btn-large clearfix' value='The same players'>";
        result += "</form>";
        result += "<form method='post' action='/clue?details=new'>";
        result += "<input type='submit' class='btn btn-warning btn-large clearfix' value='Create new players'>";               
        result += "</form></div></div>";
        
        return result;
    }
    
    public static String createLoserForm( Controller controller, List<Card> accusation ) {
        String result = "";
        result += "<div class='fader'>";
        result += "<form method='post' action='/clue/play' class='in-fader'>";
        result += "<img src='images/loser.gif'>";
        result += "<p>" + controller.getCurrentPlayers().getName() + " (" + controller.getCurrentPlayers().getSuspect().getNicerName() + ") made this accusation</p>";
        if( accusation != null )
            for( Card card : accusation )
                result += "<div class='card " + card.getCard().name() + "'></div>";
        result += "<p>and he lost the game, bummer!!!</p>";
        result += "<input type='hidden' name='next-step' value='player-lost'>";
        result += "<img src='images/cry1.gif'>";
        result += "<input type='submit' class='btn btn-primary btn-large clearfix' value='Continue'>";
        result += "<img src='images/cry2.gif'>";                       
        result += "</form></div>";
        
        return result;
    }
    
    public static String createAssumptionForm( Controller controller, Room room, Weapon weapon, Suspect suspect, Player cardHolder, List<Card> foundedCards ) {
        String result = "";
        result += "<div class='fader'>";
        result += "<form method='post' action='play' class='in-fader choose-card-assumption'>";
        result += "<p>The assumption was</P>";
        result += "<div class='card " + room.name() + "'></div>";
        result += "<div class='card " + suspect.name() + "'></div>";
        result += "<div class='card " + weapon.name() + "'></div>";
        result += "<div class='clearfix'></div>";
        if( cardHolder != null && cardHolder.isHuman() ) {
            result += "<p>" + cardHolder.getName() + ", choose a card to reveal</p>";
            result += HTMLBuilder.creatAssumptionChooseCard(cardHolder, foundedCards );
            result += "<input class='showed-card' type='hidden' value='' name='showed-card'>";
        
        } else if( cardHolder != null ) {
            if( controller.getCurrentPlayers().isHuman() ) {
                result += "<p>" + controller.getCurrentPlayers().getName() + ", " + cardHolder.getName() + "showed you this card</p>";
                result += "<div class='choosed card " + foundedCards.get( 0 ).getCard().name() + "'></div>";
            } else 
                result += "<p>" + cardHolder.getName() + " showed a card to " + controller.getCurrentPlayers().getName() + "</p>";
            
            result += "<input type='hidden' value='" + foundedCards.get( 0 ).getCard().name() + "' name='showed-card'>";

         } else {
            result += "<p>No one has those cards</p>";
         }

          result += "<div class='clearfix'></div>";
          result += "<input type='submit' class='btn btn-primary btn-large clearfix in-fader-submit' value='OK'>";
          result += "<input class='next-step' type='hidden' name='next-step' value='handle-assumption'>";
          result += "</form></div>";
        
        return result; 
    }
    
    public static String createRetirementForm() {
        String result = "";
        result += "<form method='post' action='play' class='center'>";
        result += "<input class='next-step' type='hidden' name='next-step' value='quit'>";
        result += "<input type='submit' class='btn btn-danger btn-large clearfix' value='Quit the game'>";               
        result += "</form>";
        
        return result;
    }
}
