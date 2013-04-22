/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import com.google.gson.Gson;
import controller.Controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.AJAXResponse;
import model.Operation;
import model.Room;
import model.Suspect;
import model.Weapon;
import model.card.Card;
import model.player.Player;

/**
 *
 * @author guyklainer
 */
@WebServlet(name = "ComputerTurnServlet", urlPatterns = {"/computer-turn"})
public class ComputerTurnServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        Controller controller = (Controller) getServletContext().getAttribute( "controller" );
        final int NUMBER_OF_SQUARES = 16;
        Object result = null;

        // handle accusation & assumption of computer
        if( controller.checkForComAccusation() ) {
            List<Card> accusation = controller.computerAccusation();

            Map<String, Object> map = new HashMap<String,Object>();
            map.put( "player", controller.getCurrentPlayers().getName() );
            map.put( "accusation", accusation );

            if( controller.checkAccusation( accusation ) ) {
                //winner
                controller.addResponseToQueue( new AJAXResponse( Operation.WINNER, map ) );

            } else {
                //loser
                controller.addResponseToQueue( new AJAXResponse( Operation.LOSER, map ) );
            }

        } else {
            
            controller.moveComputerPlayer();
            Room room = controller.getCurrentPlayerRoom();
            controller.addResponseToQueue( new AJAXResponse( Operation.PLAYER_MOVE, controller.getCurrentPlayerPosition() ) );
            
            // not in corridor
            if( room != null ) {
                Weapon weapon   = controller.makeComputerWeaponAssumption();
                Suspect suspect = controller.makeComputerSuspectAssumption();
                List<Card> foundedCards = null;
                
                Map<String, Object> map = new HashMap<String,Object>();
                map.put( "weapon", weapon.name() );
                map.put( "suspect", suspect.name() );
                map.put( "msg", controller.getCurrentPlayers().getName() + " made this assumption: " + room.getNicerName() + ", " + weapon.getNicerName() + ", " + suspect.getNicerName() );
                
                controller.addResponseToQueue( new AJAXResponse( Operation.MAKE_ASSUMPTION, map ) );
                
                Player cardHolder = controller.findPlayerThatHaveCard( room, suspect, weapon );

                // someone have card from assumption
                if( cardHolder != null && cardHolder.isHuman() ) {
                    foundedCards = controller.getMatchedCardsToAssumption( cardHolder, room, suspect, weapon );
                    
                    map = new HashMap<String,Object>();
                    map.put( "player", cardHolder.getName() );
                    map.put( "cards", foundedCards );
                    controller.addResponseToQueue( new AJAXResponse( Operation.CHECK_ASSUMPTION, map ) );
                
                // card holder is computer
                } else if ( cardHolder != null ) {
                    foundedCards = controller.getMatchedCardsToAssumption( cardHolder, room, suspect, weapon );
                    
                    controller.getCurrentPlayers().getViewedCards().add( foundedCards.get( 0 ) );
                    map = new HashMap<String,Object>();
                    map.put( "shower", cardHolder.getName() );
                    map.put( "card", foundedCards.get( 0 ).getCard().name() );
                    controller.addResponseToQueue( new AJAXResponse( Operation.SHOW_CARD, map ) );
                    
                    result = controller.nextPlayer();
                    controller.addResponseToQueue( new AJAXResponse( Operation.NEXT_PLAYER, controller.getCurrentPlayers().getName() + " is playing now" ) );

                    if( !controller.getCurrentPlayers().isHuman() ) {
                         getServletContext().getRequestDispatcher("/computer-turn").forward(request, response);
                    }
                
                // no one has this card
                } else {
                    controller.addResponseToQueue( new AJAXResponse( Operation.NO_CARD, null ) );
                    
                    result = controller.nextPlayer();
                    controller.addResponseToQueue( new AJAXResponse( Operation.NEXT_PLAYER, controller.getCurrentPlayers().getName() + " is playing now" ) );

                    if( !controller.getCurrentPlayers().isHuman() ) {
                         getServletContext().getRequestDispatcher("/computer-turn").forward(request, response);
                    }
                }
            
            } else {
                result = controller.nextPlayer();
                controller.addResponseToQueue( new AJAXResponse( Operation.NEXT_PLAYER, controller.getCurrentPlayers().getName() + " is playing now" ) );
                
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
