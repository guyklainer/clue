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
import model.player.Player;

/**
 *
 * @author guyklainer
 */
@WebServlet(name = "InitServlet", urlPatterns = {"/settings"})
public class InitServlet extends HttpServlet {

    Controller controller = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        controller = (Controller) getServletContext().getAttribute( "controller" );
        
        if( request.getParameter( "type" ) != null && request.getParameter( "type" ).equals( "upload" ) && controller != null ) {
            request.getSession().setAttribute( "error", "The game already created by other player. Try to join this game");
            response.sendRedirect( "new-player.jsp" );
            return;
        }
        
        //two games created in the same time so send error to the second one
        if( request.getParameter( "create-game" ) != null && controller != null ) {
            request.getSession().setAttribute( "error", "The game already created by other player. Try to join this game");
            response.sendRedirect( "new-player.jsp" );
            return;
        }
        
        // create controller
        if( controller == null ) {
            controller = new Controller();
            getServletContext().setAttribute( "controller", controller );            
        }
        
        // tring to join the game after start
        if( controller.isGameStarted() ) {
            request.getSession().setAttribute( "error", "The game already started. Please try again later.");
            response.sendRedirect( "index.jsp" );
            return;
        }
        
        //upload XML file
        if( request.getParameter( "type" ) != null && request.getParameter( "type" ).equals( "upload" ) ) {
            
            getServletContext().getRequestDispatcher( "/upload" ).include( request, response );
            response.sendRedirect( "/clue/new-player.jsp" );
            
        // manual
        } else {
            
            // get number of players in game
            int players;
            int humans;
            if( request.getParameter( "humans" ) != null && request.getParameter( "players" ) != null ) {

                humans  = Integer.parseInt( request.getParameter( "humans" ) );
                players = Integer.parseInt( request.getParameter( "players" ) );
                controller.setNumOfPlayers( players );
                controller.setNumOfHumans( humans );
            } else {
                players = controller.getNumOfPlayers();
                humans = controller.getNumOfHumans();
            }        

            // add human player
            if( controller.checkAvailableName( request.getParameter( "user" ) ) && controller.checkAvailableSuspect( request.getParameter( "suspect" ) ) ) {

                Player player = controller.addHumanPlayer( request.getParameter( "user" ), request.getParameter( "suspect" ) );
                request.getSession().setAttribute( "player", player );

                if( controller.getPlayers().size() == humans ) {    

                    // create computer players
                    for( int i = 0; i < players - humans; i++ )
                        controller.addComputerPlayer( i + 1 );

                    // init controller
                    controller.run();

                    // tell the client that the game has started
                    controller.addResponseToQueue( new AJAXResponse( Operation.GAME_STARTED, controller.getCurrentPlayers() ) );
                }

            } else {
                request.getSession().setAttribute( "error", "Username or Suspect allready exist");
                response.sendRedirect("index.jsp");
                return;
            }


            // redirect to game page
            response.sendRedirect( "play" );
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
