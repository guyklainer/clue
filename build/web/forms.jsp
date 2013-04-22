<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="model.card.Card"%>
<%@page import="model.player.Player"%>
<%@page import="utils.HTMLBuilder"%>
<%@page import="model.CubeNumber"%>
<%@page import="model.Room"%>
<%@page import="model.Weapon"%>
<%@page import="model.Suspect"%>
<%@page import="controller.Controller"%>
<% 
    String step = request.getParameter("next-step") == null ? "next-player" : request.getParameter("next-step");
    Controller controller = (Controller) application.getAttribute( "controller" );
    
    final int NUMBER_OF_SQUARES = 16;
%>
<center>
    <%   if( step.equals( "cube" ) ) {
            if( request.getParameter("accusation").equals( "no" ) ) { %>
               <p class='center bold'>Roll the cube</p>
               <form method='post' action='play'>
                   <input type='hidden' name='next-step' value='where-to-move'>
                   <input class="cube" type='image' src='images/cube.png'>
               </form>
               
            <%} else { %>
                <div class="fader">
                    <form method='post' action='play' class="in-fader">
                        <p>Make your accusation</P>
                        <%= HTMLBuilder.createSelectSuspects() %>
                        <%= HTMLBuilder.createSelectWeapons() %>
                        <%= HTMLBuilder.createSelectRooms() %>
                        
                        <input class="room" type="hidden"  name="room" value="">
                        <input class="weapon" type="hidden" name="weapon" value="">
                        <input class="suspect" type="hidden" name="suspect" value="">
                        <input class="next-step" type='hidden' name='next-step' value='check-accusation'>
                        <input type='image' src='images/accuse.png'>
                    </form>
                </div>
            <% } %>
    <%  } else if( step.equals( "where-to-move" ) ) { %>
            <% int steps = CubeNumber.getRandom().ordinal() + 1;
               int position = controller.getCurrentPlayerPosition();
               int forword = position + steps > 15 ? position + steps - NUMBER_OF_SQUARES : position + steps;
               int backword = position - steps < 0 ? position - steps + NUMBER_OF_SQUARES : position - steps;
            %>
            
            <p class='center bold'>Cube result: <%= steps %></p>
            <p class='center bold'>Click the room you want to move to</p>
               <form method='post' action='play' class="js-board-choose">
                   <input class="move-to-room" type="hidden" name="move-to-room" value="">
                       <input type="hidden" name="forword" value="<%= forword %>">
                   <input type="hidden" name="backword" value="<%= backword %>">
                   <input class="next-step" type='hidden' name='next-step' value='where-to-move'>
                   <input class='btn btn-primary btn-large clearfix' type='submit' value="Let's go...">
               </form>
                   
    <%  } else if( step.equals( "check-accusation" ) ) { %>
          <%
            List<Card> accusation = new ArrayList<Card>();
            accusation.add( controller.getCardByString( request.getParameter( "room" ) ) );
            accusation.add( controller.getCardByString( request.getParameter( "suspect" ) ) );
            accusation.add( controller.getCardByString( request.getParameter( "weapon" ) ) );
               
            if( controller.checkAccusation( accusation ) )
                out.write( HTMLBuilder.createWinnerForm( controller, accusation ) );
            else
                out.write( HTMLBuilder.createLoserForm( controller, accusation ) );
           %>
           
    <%  } else if( step.equals( "make-assumption" ) ) { %>
            <div class="fader">
               
               <form method='post' action='play' class="in-fader">
                   <p><%= controller.getCurrentPlayers().getSuspect().getNicerName() %>, you are at the <span class='yellow'><%= controller.getCurrentPlayerRoom().getNicerName() %></span></p>
                   <p>Make your assumption</P>
                   
                   <input type="image" class="assumption-button" src="images/blame1.jpg">
                   <%= HTMLBuilder.createSelectSuspects() %>
                   <%= HTMLBuilder.createSelectWeapons() %>
                   <div class="clearfix"></div>
                   
                   <p>Your cards</P>
                   <%
                        for( Card card : controller.getCurrentPlayers().getCards() )
                            out.write( "<div class='card " + card.getCard().name() + "' title='" + card.getCard().name() + "'></div>");
                   %>
                   
                   <%= HTMLBuilder.createViewedCards( controller ) %> 
                   
                   <div class="clearfix"></div>
                   
                   <input type="hidden" value="<%= controller.getCurrentPlayerRoom().name() %>" name="room">
                   <input class="weapon" type="hidden" name="weapon" value="">
                   <input class="suspect" type="hidden" name="suspect" value="">
                   <input class="next-step" type='hidden' name='next-step' value='check-assumption'>
                   
               </form>
            </div>
                   
    <%  } else if( step.equals( "check-assumption" ) ) { %>
            <% Room room            = Room.valueOf( request.getParameter( "room" ));
               Suspect suspect      = Suspect.valueOf( request.getParameter( "suspect" ));
               Weapon weapon        = Weapon.valueOf( request.getParameter( "weapon" ));
               Player cardHolder    = controller.findPlayerThatHaveCard( room, suspect, weapon );
            
                List<Card> foundedCards = null;
                if( cardHolder != null )
                    foundedCards = controller.getMatchedCardsToAssumption(cardHolder, room, suspect, weapon);
                
                out.write( HTMLBuilder.createAssumptionForm(controller, room, weapon, suspect, cardHolder, foundedCards) );
        
        } else if( step.equals( "quit" ) ) { %>
            <div class="fader">
                <div class="in-fader">
                    <p class='center bold'>Are you sure you want to quit?</p>
                    <form method='post' action='play' class="center">
                        <input type='hidden' name='retirement' value='1'>
                        <input class='btn btn-success btn-large clearfix' type='submit' value='Quit'>
                    </form>
                    <form method='post' action='play'>
                        <input class='btn btn-danger btn-large clearfix' type='submit' value='Cancel'>
                    </form>
                </div>
            </div>
        
       <% } else { %>
            <p class='center bold'>Do you want to make accusation?</p>
            <form method='post' action='play'>
                <label><input type='radio' name='accusation' value='yes'>Yes</label>
                <label><input type='radio' name='accusation' value='no' checked>No</label>
                <input type='hidden' name='next-step' value='cube'>
                <input class='btn btn-primary btn-large clearfix' type='submit' value='Go!'>
            </form>
    <% } %>
 </center>
    