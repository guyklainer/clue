<%@page import="model.card.Card"%>
<%@page import="utils.HTMLBuilder"%>
<%@page import="controller.Controller"%>
<%@page import="model.player.Player"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<% Player player            = (Player) request.getSession().getAttribute( "player" ); %>
<% Controller controller    = (Controller) application.getAttribute( "controller" ); %>

<script src="js/model.js"></script>
<script src="js/view.js"></script>
<script src="js/controller.js"></script>
<div class='interaction well pull-left rounded-corners'>
    <div class='alert alert-error hide'>
        <h4>Oh snap!</h4>
        <p></p>
    </div>
    
    <strong class='title <%= player.getSuspect() %>'>
        <%= player.getSuspect().getNicerName() + "(" + player.getName() + ")" %>
    </strong>
    
    <div class="questions">
        <center><p class="other-player hide"></p></center>
        
        <div class="assumption-accusation hide">
            <center>
                <button class='btn btn-success btn-medium clearfix make-assumption'>Assumption</button>
                <button class='btn btn-warning btn-medium clearfix make-accusation'>Accusation</button>
            </center>
        </div>
        
        <center class="role-cube hide">
            <p class='bold'>Roll the cube</p>
            <img class='cube clickable' src='images/cube.png'>
        </center>
        
        <center class="cube-result hide">
            <p class='center bold'>Cube result: <span class="cube-result-number"></span></p>
            <p class='center bold'>Click the room you want to move to</p>
            <button class='btn btn-primary btn-large clearfix'>Let's go...</button>
        </center>
        
    </div>
    
    <div class='cards'></div>
    
    <center>
        <button class='btn btn-danger btn-medium clearfix make-quit'>Quit the game</button>
    </center>
    
    <center class="fader">
        <div class="waiting-msg in-fader">
            <p>Please wait that the other players will join in...<img src="images/waiting.gif"></p>
        </div>

        <form method='post' action='play' class="in-fader hide make-assumption">
            <p class="assumption-title"></p>
            <p>Make your assumption</P>

            <input type="image" class="assumption-button" src="images/blame1.jpg">
            <%= HTMLBuilder.createSelectSuspects() %>
            <%= HTMLBuilder.createSelectWeapons() %>
            <div class="clearfix"></div>

            <div class="cards"></div>

            <div class="viewed-cards"></div>

            <div class="clearfix"></div>

            <input class="weapon" type="hidden" name="weapon" value="">
            <input class="suspect" type="hidden" name="suspect" value="">
           
        </form>
            
        <form method='post' action='play' class="accusation in-fader hide">
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
            
        <form method='post' action='play' class='in-fader choose-card-assumption hide'>
            <p class="the-title"></p>
            <div class="choose-cards"></div>
            
          <div class='clearfix'></div>
          
          <input type="hidden" class="showed-card" name="showed-card" value="">
          <input type='submit' class='btn btn-primary btn-large clearfix in-fader-submit' value='OK'>
          
        </form>
            
         <form method='post' action='play' class='in-fader card-showed-to-me hide'>
            <p class="the-title"></p>
            <div class="card-to-show"></div>
            
          <div class='clearfix'></div>
          
          <input type='submit' class='btn btn-primary btn-large clearfix in-fader-submit' value='OK'>
          
        </form>
         
         <div class='in-fader winner hide'>
            <img src='images/fireworks2.gif' style='width: 100px'>
            <p class="winner-title"></p>

            <div class='winner-cards'></div>

           <p>and he won the game!!!</p>
           <p class='yellow'>Good for him!!</p>
           <img src='images/fireworks1.gif'>
           <p>Start new game</p>
           
           <form method='post' action='/clue?details=new'>
               <input type='submit' class='btn btn-warning btn-large clearfix' value='Go'>               
           </form>
         </div>
            
         <form method='post' action='/clue/play' class='in-fader loser hide'>
            <img src='images/loser.gif'>
            <p class="loser-title"></p>
            <div class='loser-cards'></div>
            <p>and he lost the game, bummer!!!</p>
            <input type='hidden' name='next-step' value='player-lost'>
            <img src='images/cry1.gif'>
            <input type='submit' class='btn btn-primary btn-large clearfix' value='Continue'>
            <img src='images/cry2.gif'>
         </form>   
    </center>
            
 </div>