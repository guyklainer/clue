<%-- 
    Document   : header
    Created on : Jan 2, 2013, 11:41:41 PM
    Author     : guyklainer
--%>

<%@page import="controller.Controller"%>
<%@page import="model.player.Player"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% Controller controller = (Controller) application.getAttribute( "controller" ); %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
        <link href="css/style.css" rel="stylesheet">
        <link href="css/toastr.min.css" rel="stylesheet">
        <script src="js/jquery.js"></script>
        <script src="js/toastr-1.1.5.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
        <script src="js/ddslick.js"></script>
        <script src="js/underscore.js"></script>
        <script src="js/backbone.js"></script>
        <script src="js/scripts.js"></script>
        <title>Clue</title>
    </head>
    <body>
    <%
        if( request.getParameter( "details" ) != null && request.getParameter( "details" ).equals( "new" ) ) {
            controller = null;
            application.setAttribute( "controller", controller );
        }
        
        if( controller != null ) {
            // top bar
            if( request.getParameter( "details" ) == null || request.getParameter( "details" ).equals( "same" ) ) {
                out.write(" <div class='players'><ul class='container'><h4>Players:</h4>");

                for ( Player player : controller.getPlayers() ) {
                    if( player.isjustSuspect() )
                        continue;

                    String extraClasses = "";
                    if( controller.getCurrentPlayers() == player )
                        extraClasses += " current-player";
                    if( !player.isActive() )
                        extraClasses += " lost";

                    if( controller.getCurrentPlayers() == player )
                        out.write("<li class='player " + extraClasses + "' title='" + player.getSuspect().name() + "'>" + player.getName() + " <i class='icon-bullhorn icon-white'></i></li>");
                    else
                        out.write("<li class='player " + extraClasses + "' title='" + player.getSuspect().name() + "'>" + player.getName() + "</li>");
                }

                out.write("</ul></div>");
            } 
        }
    %>