
<%@include file="header.jsp" %> 

<% if( controller != null ) {
        if( controller.isGameStarted() ) {
            out.write( "<div class='alert alert-error'><h4>Oh snap!</h4><p>the game is full. Sorry!</p></div>" );
            return;
        
        } else
            response.sendRedirect( "new-player.jsp" );
   }
%>

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

        <div class="settings container rounded-corners">
            <h2 class="center">Create a game</h2>
            <form method="post" action="new-player.jsp" class="form-horizontal span6 offset3 center choose-number">
                <div class="control-group">
                    <label class="control-label" for="players"><strong>Number of players:</strong></label>
                    <div class="controls">
                        <select id="players" name="players">
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                            <option value="6">6</option>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="humans"><strong>Humans from them:</strong></label>
                    <div class="controls">
                        <select id="humans" name="humans">
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                            <option value="6">6</option>
                        </select>
                    </div>
                </div>
                <input type="hidden" name="init" value="player">
                <input type="submit" class="btn btn-primary btn-large" value="Continue">
            </form>
            <h2 class="clearfix center">Or, load setting from XML file</h2>
            <center>
                <form name="upload-game" id="upload-game" action="settings?type=upload" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="type" value="upload">
                    <input type="file" name="file">
                    <input type="submit" class="btn btn-primary btn-large" value="Upload">
                </form>
            </center>
        </div>
            
<%@include file="footer.jsp" %>
