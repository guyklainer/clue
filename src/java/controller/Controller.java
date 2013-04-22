/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import model.*;
import model.board.BoardSquare;
import model.card.Card;
import model.card.CardRoom;
import model.card.CardSuspect;
import model.card.CardWeapon;
import model.player.Player;

/**
 *
 * @author guyklainer
 */
public class Controller {
    
    private Model                   model;
    private List<Suspect>           suspects;
    private Player                  currentPlayer;
    private List<Player>            activePlayers;
    private int                     numOfPlayers;
    private int                     numOfHumans;
    private Boolean                 gameStarted;
    private List<AJAXResponse>      responseQueue;
    
    private static final int EXIT   = -1;
    private static final int TRY    = 0;
    private static final int SUCCES = 1;

    public Controller() {

        this.model              = new Model();
        this.suspects           = new ArrayList <Suspect> ();
        this.currentPlayer      = null;
        this.activePlayers      = null;
        this.gameStarted        = false;
        this.responseQueue      = new ArrayList<AJAXResponse>();
        
        suspects.addAll(Arrays.asList(Suspect.values()));
        Collections.shuffle( suspects );
    
    }
    
    public void run() {
        
        markGameStarted();
        dealNewGame();
        createRemainedSuspects();
        loadBoard();

    }
  
    public boolean checkForComAccusation( ) {
        int     numSuspectsOptions  = getSuspectsAfterElimination().size();
        int     numWeaponsOptions   = getWeaponsAfterElimination().size();
        int     numRoomsOptions     = getRoomsAfterElimination().size();
        int     luckyNumber         = (int)(Math.random() * 10);
        boolean feelingLucky        = false;
 
        // If The Computer Has 5 or less Cards to choose From And He's Felling Lucky - Make a Guess
        if( luckyNumber == 0 && ( numSuspectsOptions + numWeaponsOptions + numRoomsOptions ) <= 5 ) 
            feelingLucky = true;
        
        return ( feelingLucky || numSuspectsOptions == 1 && numWeaponsOptions == 1 && numRoomsOptions == 1);
    }
   
    public List<Card> computerAccusation() {
        List<Card> accusation = new ArrayList<Card>();
        
        List<Room>      rooms    = getRoomsAfterElimination();
        List<Weapon>    weapons  = getWeaponsAfterElimination();
        List<Suspect>   suspects = getSuspectsAfterElimination();
        
        Collections.shuffle( rooms );
        Collections.shuffle( weapons );
        Collections.shuffle( suspects );
        
        accusation.add(model.getDeck().find( rooms.get(0) ) );
        accusation.add(model.getDeck().find( weapons.get(0) ) );
        accusation.add(model.getDeck().find( suspects.get(0) ) );

        return accusation;
    } 
     
    public boolean checkAccusation( List<Card> accusation ) {
        boolean result = true;
        
        for ( Card card : accusation )
            if ( !model.getEnvelope().contains( card ) )
                result = false;

        if ( !result )
            lost( currentPlayer );
       
        return result;
    }
    
    private List<Suspect> getSuspectsAfterElimination(){
        List<Suspect> suspectsToChooseFrom = new ArrayList<Suspect>();
        suspectsToChooseFrom.addAll(Arrays.asList(Suspect.values()));
        
        for (Card card : currentPlayer.getViewedCards()){
            if (card instanceof CardSuspect){
                suspectsToChooseFrom.remove(card.getCard());
            }
        }
        for (Card card : currentPlayer.getCards()){
            if (card instanceof CardSuspect){
                suspectsToChooseFrom.remove(card.getCard());
            }
        }
        return suspectsToChooseFrom;
    }
    
    private List<Weapon> getWeaponsAfterElimination(){
        List<Weapon> weaponsToChooseFrom = new ArrayList<Weapon>();
        weaponsToChooseFrom.addAll(Arrays.asList(Weapon.values()));
        
        for (Card card : currentPlayer.getViewedCards()){
            if (card instanceof CardWeapon){
                weaponsToChooseFrom.remove(card.getCard());
            }
        }
        for (Card card : currentPlayer.getCards()){
            if (card instanceof CardWeapon){
                weaponsToChooseFrom.remove(card.getCard());
            }
        }
        return weaponsToChooseFrom;
    }
    
    private List<Room> getRoomsAfterElimination(){
        List<Room> roomsToChooseFrom = new ArrayList<Room>();
        roomsToChooseFrom.addAll(Arrays.asList(Room.values()));
        
        for (Card card : currentPlayer.getViewedCards()){
            if (card instanceof CardRoom){
                roomsToChooseFrom.remove(card.getCard());
            }
        }
        for (Card card : currentPlayer.getCards()){
            if (card instanceof CardRoom){
                roomsToChooseFrom.remove(card.getCard());
            }
        }
        return roomsToChooseFrom;
    }
    
    public Player findPlayerThatHaveCard( Room room, Suspect suspect, Weapon weapon ) {
        Player      cardHolder               = null;
        Player      playerToCheckWith        = getNextPlayer( currentPlayer );
        List<Card>  foundCards               = null;
        
        while ( cardHolder == null && playerToCheckWith != currentPlayer ) {
            foundCards = hasCard( playerToCheckWith, room, suspect, weapon );
            
            if ( !foundCards.isEmpty() )
                cardHolder = playerToCheckWith;
            else
                playerToCheckWith = getNextPlayer( playerToCheckWith );
        }
        
        return cardHolder;
    }
    
    public List<Card> getMatchedCardsToAssumption( Player cardHolder, Room room, Suspect suspect, Weapon weapon ) {
        List<Card>  foundCards = null;
        
        moveSuspectPlayerToRoom( room, suspect );
        moveSuspectWeaponToRoom( room, weapon );
        
        foundCards = hasCard( cardHolder, room, suspect, weapon );
        
        return foundCards;
    }
    
    public void addCardToPlayerViewedCards( String viewedCard ) {
        for( Card card : model.getDeck().getCards() )
            if( card.getCard().name().equals( viewedCard ) )
                if( !currentPlayer.getViewedCards().contains( card ) )
                    currentPlayer.getViewedCards().add( card );
    }
    
    
    private List<Card> hasCard( Player player, Room room, Suspect suspect, Weapon weapon ) {
        
        List<Card> result = new ArrayList<Card>();
        
        
        if ( player.find(CardType.ROOM, room) ) 
            result.add( model.getDeck().find( room ) );
        
        if ( player.find(CardType.SUSPECT, suspect) ) 
            result.add( model.getDeck().find( suspect ) );
        
        if ( player.find(CardType.WEAPON, weapon) )
            result.add( model.getDeck().find( weapon ) );
       
        Collections.shuffle( result );
        
        return result;
    }
   
   
    private Player getNextPlayer(Player current) {
        int nextPlayer = model.getPlayers().indexOf( current ) + 1;
        
        if( nextPlayer >= model.getPlayers().size() )
            nextPlayer = 0;
        
        return model.getPlayers().get( nextPlayer );
    }
    
    private Player getActiveNextPlayer( Player current ) {
        Player nextPlayer = getNextPlayer( current );
                
        while ( !nextPlayer.isActive() )
            nextPlayer = getNextPlayer( nextPlayer );
        
        return nextPlayer;
    }
 
    private void lost( Player player ) {
        player.deActivate();
        activePlayers.remove( player );
    }
    

    public void addComputerPlayer( int index ) {
        Suspect suspect = suspects.get( 0 );
        suspects.remove( 0 );
        model.getPlayers().add( createPlayer( "computer" + index, suspect, false ) );
    }
    
    public Player addHumanPlayer( String name, String suspect ) {
        suspects.remove( Suspect.valueOf( suspect ) );
        Player player = createPlayer( name, Suspect.valueOf( suspect ), true );
        model.getPlayers().add( player );
        
        return player;
    }
    
    public Player addHumanPlayerFirst( String name, String suspect ) {
        suspects.remove( Suspect.valueOf( suspect ) );
        Player player = createPlayer( name, Suspect.valueOf( suspect ), true );
        model.getPlayers().add( 0, player );
        
        return player;
    }

    private Player createPlayer(String name, Suspect suspect, boolean isHuman) {
        return new Player(name, suspect, isHuman);
    }
    
    public void createEnvelope() {
        model.addCardToEnvelope(model.getDeck().find( Room.getRandom() ) );
        model.addCardToEnvelope(model.getDeck().find( Suspect.getRandom() ) );
        model.addCardToEnvelope(model.getDeck().find( Weapon.getRandom() ) );
    }

    private void moveSuspectWeaponToRoom(Room room, Weapon weapon) {
        BoardSquare roomSquare   = findRoomPosition( room );
        roomSquare.setWeapon( weapon );
        model.getBoard().getWeaponsPositions().put( weapon, roomSquare );
    }

    private void moveSuspectPlayerToRoom(Room room, Suspect suspect) {    
        Player suspectedPlayer   = findSuspectPlayer( suspect );
        BoardSquare roomSquare   = findRoomPosition( room );
        model.getBoard().getPositions().put( suspectedPlayer, roomSquare );
    }
    
    private Player findSuspectPlayer( Suspect suspect ) {
        for (Player player : model.getPlayers() )
              if( player.getSuspect() == suspect )
                  return player;
          
          return null;
    }
    
    private BoardSquare findRoomPosition( Room room ){
          for (BoardSquare square : model.getBoard().getSquares())
              if( square.getRoom() == room )
                  return square;
          
          return null;
    }

    private BoardSquare findSuspectWeapon(Weapon weapon) {
        for (BoardSquare square : model.getBoard().getSquares())
              if( square.getWeapon() == weapon )
                  return square;
          
          return null;
    }

    private void cleanUpPlayers() {
        for ( Player player : model.getPlayers() ) {
            player.cleanUp();
            
            if( player.isjustSuspect() )
                player.deActivate();
        }   
    }

    private void dealNewGame() {
        createEnvelope();
        model.getDeck().remove(model.getEnvelope());
        model.getDeck().spreadCards(model.getPlayers());
        model.getDeck().add(model.getEnvelope());
    }

    public void loadBoard() {
        model.getBoard().loadPlayers(model.getPlayers());
        model.getBoard().loadWeapons();
        currentPlayer = model.getPlayers().get(0);
        activePlayers = new ArrayList<Player>();
        
        for( Player player : model.getPlayers() )
            if( player.isActive() )
                activePlayers.add( player );
            else
                player.setJustSuspect();
    }

    public void restart() {
        List<Player> players    = model.getPlayers();
        model                   = new Model();
        
        model.getPlayers().addAll(0, players);
        cleanUpPlayers();
        
        dealNewGame();        
        loadBoard();
    }
    
    public List<Player> getPlayers() {
        return model.getPlayers();
    }
    
    public Player getCurrentPlayers() {
        return currentPlayer;
    }

    public boolean checkAvailableName( String name ) {
        for( Player player : model.getPlayers() )
            if( player.getName().equals( name ) )
                return false;
        return true;
    }

    public boolean checkAvailableSuspect( String suspect ) {
        return suspects.contains( Suspect.valueOf( suspect ) );
    }

    public Card getCardByString( String cardName ) {
        for( Card card : model.getDeck().getCards() ) {
            if( card.getCard().name().equals( cardName ) )
                return card;
        }
        
        return null;         
    }
    
    public List<Player> getRoomPlayers( int index ) {
        List<Player> players = new ArrayList<Player>();
        
        for( Player player : model.getPlayers() ) {
            if( model.getBoard().getPositions().get( player ).getPosition() == index ) {
                players.add( player );
            }
        }
        return players;
    }
    
    public List<Weapon> getRoomWeapons( int index ) {
        List<Weapon> weapons = new ArrayList<Weapon>();
        
        for( Weapon weapon : Weapon.values() ) {
            if( model.getBoard().getWeaponsPositions().get( weapon ).getPosition() == index ) {
                weapons.add( weapon );
            }
        }
        return weapons;
    }
    
    public int getCurrentPlayerPosition() {
        return model.getBoard().getPositions().get( currentPlayer ).getPosition();
    }
    
    public Room getCurrentPlayerRoom() {
        return model.getBoard().getPositions().get( currentPlayer ).getRoom();
    }
    
    public void movePlayer( int newPosition ) {
        model.getBoard().getPositions().put( currentPlayer, model.getBoard().getSquares().get( newPosition ) );
    }
    
    public void moveComputerPlayer() {
        int steps               = CubeNumber.getRandom().ordinal() + 1;
        List<Card> accusation   = null;
        Room room;
        String direction;
        
        List<String> directions = new ArrayList<String>();
        directions.add("forward");
        directions.add("backward");
        Collections.shuffle(directions);
        direction = directions.get(0);
        
        if ( direction.equals( "forward" ) )
            room = model.getBoard().movePlayer( currentPlayer, steps );
        else
            room = model.getBoard().movePlayer( currentPlayer, steps*(-1) );
    }
    
    public Weapon makeComputerWeaponAssumption() {
        List<Weapon> availableWeapon    = getWeaponsAfterElimination();
        
        Collections.shuffle( availableWeapon );
        return availableWeapon.get( 0 );
    }
    
    public Suspect makeComputerSuspectAssumption() {
        List<Suspect> availableSuspects = getSuspectsAfterElimination();
        
        Collections.shuffle( availableSuspects );
        return availableSuspects.get( 0 );
    }

    
    public boolean nextPlayer() {
        Player nextPlayer = getActiveNextPlayer( currentPlayer );
            
        currentPlayer = nextPlayer;
        if ( getActiveNextPlayer( nextPlayer ) != nextPlayer )
           return true;
         else
           return false;
    }
    
    public List<Player> getActivePlayers() {
        return this.activePlayers;
    }

    public void createRemainedSuspects() {
        for( Suspect suspect : suspects ) {
            Player player = new Player( suspect.name(), suspect, false );
            player.deActivate();
            model.getPlayers().add( player );
        }
    }
    
    public Model getModel() {
        return this.model;
    }
    
    public int getNumOfPlayers() {
        return this.numOfPlayers;
    }
    
    public void setNumOfPlayers( int num ) {
        this.numOfPlayers = num;
    }
    
    public int getNumOfHumans() {
        return this.numOfHumans;
    }
    
    public void setNumOfHumans( int num ) {
        this.numOfHumans = num;
    }
    
    public void markGameStarted() {
        this.gameStarted = true;
    }
    
    public boolean isGameStarted() {
        return this.gameStarted;
    }
    
    public List<AJAXResponse> getResponseQueue() {
        return responseQueue;
    }
    
    public void addResponseToQueue( AJAXResponse newResponse ) {
        responseQueue.add( newResponse );
    }
    
    public List<Suspect> getAvailableSuspets() {
        return this.suspects;
    }
    
}
