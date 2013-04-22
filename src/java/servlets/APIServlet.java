/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import com.google.gson.Gson;
import controller.Controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.AJAXResponse;
import model.CubeNumber;
import model.Operation;
import model.Room;
import model.Suspect;
import model.Weapon;
import model.board.BoardSquare;
import model.card.Card;
import model.player.Player;

/**
 *
 * @author guyklainer
 */
@WebServlet(name = "APIServlet", urlPatterns = {"/api"})
public class APIServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

            Controller controller = (Controller) getServletContext().getAttribute( "controller" ); 
            Object result = null;
            
            if ( request.getParameter( "type" ).equals( "this-player" ) ){
                Player player = ( Player ) request.getSession().getAttribute( "player" );
                
                Map<String, String> map = new HashMap<String,String>();
                map.put( "id", player.getName() );
                result = map;
            
            } else if( request.getParameter( "type" ).equals( "next-player" ) ){
                
                result = controller.nextPlayer();
                controller.addResponseToQueue( new AJAXResponse( Operation.NEXT_PLAYER, controller.getCurrentPlayers().getName() + " is playing now" ) );
                
                if( !controller.getCurrentPlayers().isHuman() ) {
                     getServletContext().getRequestDispatcher("/computer-turn").forward(request, response);
                }
                    
            } else if( request.getParameter( "type" ).equals( "model" ) ){

                result = controller;

            } else if( request.getParameter( "type" ).equals( "positions" ) ){

                Map<Player, BoardSquare> positions = controller.getModel().getBoard().getPositions();
                Map<String, BoardSquare> resPositions = new HashMap<String, BoardSquare>();

                for( Player player : controller.getPlayers() ) {

                    resPositions.put( player.getName(), positions.get( player ) );
                }
                
                result = resPositions;
            
            } else if( request.getParameter( "type" ).equals( "update" ) ) {
                
                int version = Integer.parseInt( request.getParameter( "v" ) );
                if( !controller.getResponseQueue().isEmpty() && version < controller.getResponseQueue().size() )
                    result = controller.getResponseQueue().get( version );
                else
                    result = false;
            
            } else if( request.getParameter( "type" ).equals( "cube" ) ) {
                int steps = CubeNumber.getRandom().ordinal() + 1;
                controller.addResponseToQueue( new AJAXResponse( Operation.ROLE_CUBE, controller.getCurrentPlayers().getName() + " got " + steps + " on the cube" ) );
                
                Map<String, Integer> map = new HashMap<String,Integer>();
                map.put( "cube", steps );
                result = map;
                
            } else if( request.getParameter( "type" ).equals( "move" ) ) {
                
                controller.movePlayer( Integer.parseInt( request.getParameter( "room" ) ) );
                controller.addResponseToQueue( new AJAXResponse( Operation.PLAYER_MOVE, controller.getCurrentPlayerPosition() ) );
                
            } else if( request.getParameter( "type" ).equals( "check-assumption" ) ) {
                
                Room room            = Room.valueOf( request.getParameter( "room" ));
                Suspect suspect      = Suspect.valueOf( request.getParameter( "suspect" ));
                Weapon weapon        = Weapon.valueOf( request.getParameter( "weapon" ));
                
                Map<String, Object> map = new HashMap<String,Object>();
                map.put( "weapon", weapon.name() );
                map.put( "suspect", suspect.name() );
                map.put( "msg", controller.getCurrentPlayers().getName() + " made this assumption: " + room.getNicerName() + ", " + weapon.getNicerName() + ", " + suspect.getNicerName() );
                
                controller.addResponseToQueue( new AJAXResponse( Operation.MAKE_ASSUMPTION, map ) );
                
                Player cardHolder    = controller.findPlayerThatHaveCard( room, suspect, weapon );
                
                List<Card> foundedCards = null;
                if( cardHolder != null && cardHolder.isHuman() ) {
                    foundedCards = controller.getMatchedCardsToAssumption( cardHolder, room, suspect, weapon );
                
                    map = new HashMap<String,Object>();
                    map.put( "player", cardHolder.getName() );
                    map.put( "cards", foundedCards );
                    controller.addResponseToQueue( new AJAXResponse( Operation.CHECK_ASSUMPTION, map ) );
                
                } else if( cardHolder != null ) {
                    // is computer
                    foundedCards = controller.getMatchedCardsToAssumption( cardHolder, room, suspect, weapon );
                    controller.getCurrentPlayers().getViewedCards().add( foundedCards.get( 0 ) );
                    
                    map = new HashMap<String,Object>();
                    map.put( "shower", cardHolder.getName() );
                    map.put( "card", foundedCards.get( 0 ).getCard().name() );
                    controller.addResponseToQueue( new AJAXResponse( Operation.SHOW_CARD, map ) );
                    
                } else {
                    controller.addResponseToQueue( new AJAXResponse( Operation.NO_CARD, null ) );
                    
                    result = controller.nextPlayer();
                    controller.addResponseToQueue( new AJAXResponse( Operation.NEXT_PLAYER, controller.getCurrentPlayers().getName() + " is playing now" ) );

                    if( !controller.getCurrentPlayers().isHuman() ) {
                         getServletContext().getRequestDispatcher("/computer-turn").forward(request, response);
                    }
                }
                
            } else if( request.getParameter( "type" ).equals( "show-card" ) ) {
                
                controller.getCurrentPlayers().getViewedCards().add( controller.getCardByString( request.getParameter( "showed-card" ) ) );
                
                Map<String, Object> map = new HashMap<String,Object>();
                map.put( "shower", request.getParameter( "player" ) );
                map.put( "card", request.getParameter( "showed-card" ) );
                controller.addResponseToQueue( new AJAXResponse( Operation.SHOW_CARD, map ) );
                if( !controller.getCurrentPlayers().isHuman() ) {
                    
                    result = controller.nextPlayer();
                    controller.addResponseToQueue( new AJAXResponse( Operation.NEXT_PLAYER, controller.getCurrentPlayers().getName() + " is playing now" ) );
                    
                    if( !controller.getCurrentPlayers().isHuman() ) {
                        getServletContext().getRequestDispatcher("/computer-turn").forward(request, response);
                    }
                }
                
            } else if( request.getParameter( "type" ).equals( "retirement" ) ) {
                Player playerToRemove = null;
                        
                for( Player player : controller.getPlayers() )
                    if( player.getName().equals( request.getParameter( "player" ) ) )
                        playerToRemove = player;
                
                if( playerToRemove != null ) {
                    playerToRemove.deActivate();
                    playerToRemove.setAsComputer();
                    controller.getActivePlayers().remove( playerToRemove );
                    controller.addResponseToQueue( new AJAXResponse( Operation.RETIREMENT, playerToRemove.getName() ) );
                }
                
                // the current player quit
                if( playerToRemove == controller.getCurrentPlayers() ) {
                    if( controller.nextPlayer() ) {
                        controller.addResponseToQueue( new AJAXResponse( Operation.NEXT_PLAYER, controller.getCurrentPlayers().getName() + " is playing now" ) );
                
                        if( !controller.getCurrentPlayers().isHuman() ) {
                             getServletContext().getRequestDispatcher("/computer-turn").forward(request, response);
                        }
                        
                    } else
                        controller.addResponseToQueue( new AJAXResponse( Operation.WINNER, null ) );
                
                // not current player quit
                } else {
                    
                    // current player left alone
                    if( controller.getActivePlayers().size() == 1 )
                        controller.addResponseToQueue( new AJAXResponse( Operation.WINNER, null ) );
                }
            
            } else if( request.getParameter( "type" ).equals( "accusation" ) ) {
                List<Card> accusation = new ArrayList<Card>();
                accusation.add(controller.getCardByString( request.getParameter( "room" ) ) );
                accusation.add(controller.getCardByString( request.getParameter( "weapon" ) ) );
                accusation.add(controller.getCardByString( request.getParameter( "suspect" ) ) );
                
                Map<String, Object> map = new HashMap<String,Object>();
                map.put( "player", controller.getCurrentPlayers().getName() );
                map.put( "accusation", accusation );
                
                if( controller.checkAccusation( accusation ) ) {
                    //winner
                    controller.addResponseToQueue( new AJAXResponse( Operation.WINNER, map ) );

                } else {
                    //loser
                    controller.addResponseToQueue( new AJAXResponse( Operation.LOSER, map ) );
                    controller.nextPlayer();
                    
                    if( !controller.getCurrentPlayers().isHuman() ) {
                        getServletContext().getRequestDispatcher("/computer-turn").forward(request, response);
                    }
                }
            }
            
            if( result != null ) {
                Gson gson = new Gson();
                String json = gson.toJson( result );
                out.print( json ); 
            }   
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
