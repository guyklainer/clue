/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import controller.Controller;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.AJAXResponse;
import model.Operation;
import model.card.Card;
import model.player.Player;

/**
 *
 * @author guyklainer
 */
@WebServlet(name = "AddPlayerServlet", urlPatterns = {"/add-player"})
public class AddPlayerServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        Controller controller = (Controller) getServletContext().getAttribute( "controller" );
        Player playerToRemove = null;
        
        if( controller.isGameStarted() ) {
            request.getSession().setAttribute( "error", "The game already started. Please try again later.");
            response.sendRedirect( "index.jsp" );
            return;
        }
        
        if( request.getParameter( "name" ) != null ) {
            for( Player player : controller.getModel().getXMLPlayers() ) {
                if( player.getName().equals( request.getParameter( "name" ) ) ) {
                    
                    Player newPlayer = controller.addHumanPlayerFirst( player.getName(), player.getSuspect().name() );
                    
                    for( Card card : player.getCards() )
                        newPlayer.addCard( card );
                    
                    request.getSession().setAttribute( "player", newPlayer );
                    playerToRemove = player;
                    break;
                }
                    
            }
            
            // someone else catched this player
            if( playerToRemove == null ) {
                request.getSession().setAttribute( "error", "This player already used. Try different player" );
                response.sendRedirect( "new-player.jsp" );
                return;
            }
            
            controller.getModel().getXMLPlayers().remove( playerToRemove );
                    
            if( controller.getModel().getXMLPlayers().isEmpty() ) {    

                // init controller
                controller.markGameStarted();
                controller.createRemainedSuspects();
                controller.loadBoard();

                // tell the client that the game has started
                controller.addResponseToQueue( new AJAXResponse( Operation.GAME_STARTED, controller.getCurrentPlayers() ) );
            }
        }
        
        // redirect to game page
        response.sendRedirect( "play" );
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
