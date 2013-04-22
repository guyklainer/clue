/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.board;


import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Room;
import model.Weapon;
import model.player.Player;

/**
 *
 * @author guyklainer
 */
public class Board {
    
    private List<BoardSquare> squares;
    private Map<Player, BoardSquare> playersPositions;
    private Map<Weapon, BoardSquare> weaponsPositions;
    
    private static final int NUMBER_OF_SQUARES = 16;
    
    public Board(){
        
        squares = new ArrayList<BoardSquare>();
        playersPositions = new HashMap<Player, BoardSquare>();
        weaponsPositions = new EnumMap<Weapon, BoardSquare>(Weapon.class);
        
        List<Room> rooms = new ArrayList<Room>();
        rooms.add(Room.HALL);
        rooms.add(null);
        rooms.add(Room.LOUNGE);
        rooms.add(null);
        rooms.add(Room.DINING_ROOM);
        rooms.add(null);
        rooms.add(Room.KITCHEN);
        rooms.add(null);
        rooms.add(Room.BALLROOM);
        rooms.add(null);
        rooms.add(Room.CONSERVATORY);
        rooms.add(null);
        rooms.add(Room.BILLIARD_ROOM);
        rooms.add(Room.LIBRARY);
        rooms.add(Room.STUDY);
        rooms.add(null);

        List<Weapon> weapons = new ArrayList<Weapon>();
        weapons.add(null);
        weapons.add(null);
        weapons.add(Weapon.ROPE);
        weapons.add(null);
        weapons.add(Weapon.CANDLESTICK);
        weapons.add(null);
        weapons.add(Weapon.WRENCH);
        weapons.add(null);
        weapons.add(Weapon.DAGGER);
        weapons.add(null);
        weapons.add(Weapon.LEAD_PIPE);
        weapons.add(null);
        weapons.add(null);
        weapons.add(null);
        weapons.add(Weapon.REVOLVER);
        weapons.add(null);

        for (int i = 0; i < NUMBER_OF_SQUARES; i++){
            squares.add(new BoardSquare(rooms.get(i), weapons.get(i), i));
        }
    }
    
    public void loadPlayers(List<Player> players) {
        for (Player player : players){
            playersPositions.put(player, squares.get(0));
        }   
    }
    
    
    public void loadWeapons() {
        for (BoardSquare square : squares){
            if( square.getWeapon() != null )
                weaponsPositions.put( square.getWeapon(), square );
        }   
    }

    public Room movePlayer(Player player, int steps) {
        int squareIndex = playersPositions.get( player ).getPosition() + steps;
        
        if( squareIndex >= NUMBER_OF_SQUARES ) {
            squareIndex -= NUMBER_OF_SQUARES;
        }
            
        if( squareIndex < 0 ) {
            squareIndex += NUMBER_OF_SQUARES;
        }
                
        playersPositions.put(player, squares.get( squareIndex ) );
        
        return squares.get( squareIndex ).getRoom();
        
    }
    
    public List<BoardSquare> getSquares() {
        return squares;
    }
    
    public Map<Player, BoardSquare> getPositions() {
        return playersPositions;
    }
    
    public Map<Weapon, BoardSquare> getWeaponsPositions() {
        return weaponsPositions;
    }
    
}
