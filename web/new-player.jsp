
<%@page import="model.Suspect"%>
<%@include file="header.jsp" %>   

<center><img src="images/logo.png"></center>
<div class="alert alert-error <% if( request.getSession().getAttribute( "error" ) == null ) { %> hide <% } %>">
    <h4>Oh snap!</h4>
    <p>
    <% if( request.getSession().getAttribute( "error" ) != null ) {
            out.write( (String) request.getSession().getAttribute( "error" ) );
            request.setAttribute( "error", null );
        }
    %>
    </p>
</div>

<% 
    int numOfHumans;
    int numOfPlayers;
    boolean createGame = false;
    if( request.getParameter( "humans" ) != null && request.getParameter( "players" ) != null ) {
        numOfHumans = Integer.parseInt( request.getParameter( "humans" ) );
        numOfPlayers = Integer.parseInt( request.getParameter( "players" ) );
        createGame = true;
    } else {
        numOfHumans = controller.getNumOfHumans();
        numOfPlayers = controller.getNumOfPlayers();
    }
    
    if( numOfHumans > numOfPlayers ) {
        request.getSession().setAttribute( "error", "Number of players need to be greater or equal to number of humans");
        response.sendRedirect("index.jsp");
    }       
%>
<div class="container">
    
    <% if( controller != null ){ %>
     
    <h2>There is an open game already.</h2>
    <h3>These players signed in:</h3>
    <ul class="waiting-list">
    <%
        for( Player player : controller.getPlayers() )
            out.write( "<li><div class='waiting-player title " + player.getSuspect() + "'></div><p class='center bold'>" + player.getName() + "</p></li>" );
    %>
    </ul>
    
    <% } %>
    
    <% if( controller != null && controller.getModel().getXMLPlayers().size() > 0 ) { %>
        <h3>Choose one player from this list:</h3>
        <ul class="options-list">
        <%
            for( Player player : controller.getModel().getXMLPlayers() )
                out.write( "<li><a href='add-player?name=" + player.getName() + "'><div class='option-player title " + player.getSuspect() + "'></div><p class='center bold'>" + player.getName() + "</p></a></li>" );
        %>
        </ul>
    
    <% } else { %>
        <h2 class="center">Create a player</h2>
        <form method="post" action="settings" class="form-horizontal center create-players">              
            <span class="inputs">
                <div class='well half center'>
                    <input type='text' name='user' placeholder='Name'>
                    <select name='suspect' placeholder='Suspect'>
                        <% for( Suspect suspect : Suspect.values() )
                                if( controller == null || controller.checkAvailableSuspect( suspect.name() ) )
                                    out.write( "<option value='" + suspect.name() + "'>" + suspect.getNicerName() + "</option>" );
                        %>
                    </select>
                </div>
            </span>

            <div class="clearfix"></div> 

            <input type="hidden" name="players" value="<%= numOfPlayers %>">
            <input type="hidden" name="humans" value="<%= numOfHumans %>">
            <% if( createGame ) { %>
            <input type="hidden" name="create-game" value="true">
            <% } %>
            <a href="/clue" class="btn btn-danger btn-large">Go Back</a>    
            <input type="submit" class="btn btn-success btn-large" value="Let's play!">
        </form>
    
     <% } %>
</div>