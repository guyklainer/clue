$(function(){
    version = 0;
    
    model               = new ClueModel;
    players             = new Players;
    activePlayers       = new Players;
    playersPositions    = new PlayersPositions;
    
    board = null;
    interaction = null;
    
    getUpdate();
    
    toastr.options.timeOut = 20000;
    
    $( window ).bind( "beforeunload", function() { 
        
        if( typeof turnTimer !== "undefined" )
            clearTimeout( turnTimer );
        
        $.get( "/clue/api?type=retirement&player=" + thisPlayer.get( 'name' ) );
    });
    
    function getUpdate() {
        
        if( activePlayers.length == 1 )
            interaction.winnerLoser( currentPlayer.get( 'name' ), null, "winner" );
        
        else {
           $.ajax({
                url: "/clue/api",
                data: { type: "update", v: version++ },
                dataType: "JSON",
                success: function( response ) {
                    if( response == false ) {
                        version--;

                    } else if( response.operation == "GAME_STARTED" ) {
                        fetchModel();
                        startGame();

                    } else if( response.operation == "ROLE_CUBE" ) { 
                        toastr.success( response.object );

                    } else if( response.operation == "PLAYER_MOVE"  ) {

                        var room = model.get( 'board' ).squares[ response.object ].room ? model.get( 'board' ).squares[ response.object ].room : "corridor";   
                        toastr.info( currentPlayer.get( 'name' ) + " moved to the " + room );
                        currentPlayer.set( 'position', response.object );
                        board.render();

                    } else if( response.operation == "NEXT_PLAYER"  ) {
                        toastr.warning( response.object );
                        nextPlayer();
                        
                        if( typeof turnTimer !== "undefined" ) 
                            clearTimeout( turnTimer );
                        
                        if( currentPlayer == thisPlayer )
                            openTurnTimer();

                    } else if( response.operation == "INTERACTION_NEXT_PLAYER"  ) {
                        interaction.nextPlayer();

                    } else if( response.operation == "MAKE_ASSUMPTION"  ) {
                        toastr.success( response.object.msg );

                        var weapon = response.object.weapon;
                        var suspect = response.object.suspect;
                        var roomID = currentPlayer.get( 'position' );

                        var weaponObj = _.find( model.get( 'board' ).weaponsPositions, function( weaponInList ){
                            return weaponInList.weapon == weapon;
                        });
                        var player = _.find( players.models, function( playerInList ){
                            return playerInList.get( 'suspect' ) == suspect;
                        });
                        weaponObj.position = roomID;
                        player.set( 'position', roomID );
                        board.render();

                    }  else if( response.operation == "CHECK_ASSUMPTION"  ) {
                        if( thisPlayer == players.get( response.object.player ) )
                            interaction.chooseCardToShow( response.object.cards );

                    } else if( response.operation == "SHOW_CARD" ) {
                        if( thisPlayer != currentPlayer )
                            toastr.success( response.object.shower + " showed a card to " + currentPlayer.get( 'name' ) );
                        else
                           interaction.CardThatshowedToMe( response.object.shower, response.object.card );

                    } else if( response.operation == "NO_CARD" ) {
                            toastr.error( "No one has this card" );

                    } else if( response.operation == "WINNER"  ) {
                           if( typeof turnTimer !== "undefined" ) 
                                clearTimeout( turnTimer ); 
                           
                           interaction.winnerLoser( response.object ? response.object.player : activePlayers.at( 0 ).get( 'name' ) , response.object ? response.object.accusation : null, "winner" );

                    } else if( response.operation == "LOSER"  ) {
                           $( ".container .current-player" ).addClass( 'lost' );
                           nextPlayer();

                           activePlayers.remove( players.get( response.object.player ) );
                           interaction.winnerLoser( response.object.player, response.object ? response.object.accusation : null, "loser" );


                    } else if ( response.operation == "RETIREMENT" ) {
                        toastr.error( response.object + " quit the game" );

                        activePlayers.get( response.object ).get( "humanPlayer", false );
                        $( ".container .player[title='" + activePlayers.get( response.object ).get( 'suspect' ) + "']" ).addClass( 'lost' );

                        activePlayers.remove( activePlayers.get( response.object ) );

                    }

                    setTimeout( getUpdate, 500 );
                }
            }); 
        }
        
    }
    
    function fetchModel() {
        model.fetch({
            success: function(){
                playersPositions.fetch({ success: arrangeModel });
            }
        });
    }
    
    function startGame() {
        $(".fader .waiting-msg").hide();
        $(".fader").fadeOut();
        
    }
    
    function arrangeModel() {
        
        gameStarted         = model.get( 'gameStarted' );
        rawActivePlayers    = model.get( 'activePlayers' );
        
        model = new ClueModel( model.get( 'model' ) );
        
        var playersFromModel = model.get( 'players' );
        _.each( playersFromModel, function( player ){
            
            var newPlayer = new Player( player );
            newPlayer.set( 'position', playersPositions.get( newPlayer.get( 'name' ) ).position );
            newPlayer.set( 'id', newPlayer.get( 'name' ) );
            
            players.push( newPlayer );
            
        });
        
        _.each( rawActivePlayers, function( player ){
            activePlayers.push( players.get( player.name ) );
        });
        
        $.ajax({
            url: "/clue/api",
            data: { type: "this-player" },
            dataType: "JSON",
            success: function( data ) {
                thisPlayer = players.get( data.id );
                currentPlayer = activePlayers.at( 0 );
            
                initViews();
            }
        });
        
        model.unset( 'players' );
        playersPositions.clear();
        
    }
    
    function initViews() {
        if( !board || !interaction ) {
            board       = new Board();
            interaction = new Interaction();
            
        } else {
            board.render();
            interaction.render();
        }
        
        $( ".players .container" ).empty();
        $( ".players .container" ).append( "<h4>Players:</h4>" );
        
        _.each( players.models, function( player ){
            if( !player.get( 'justSuspect' ) )
                $( ".players .container" ).append( '<li class="player" title="' + player.get( 'suspect' ) + '">' + player.get( 'name' ) + '</li>' );
        });
        
        $( $( ".players .container li" ).get( 0 ) ).addClass( 'current-player' ).append( "<i class='icon-bullhorn icon-white'></i>" );
        
        if( currentPlayer == thisPlayer )
            openTurnTimer();
    }
    
    function nextPlayer() {
        var next = activePlayers.indexOf( currentPlayer ) + 1 == activePlayers.length ? 0 : activePlayers.indexOf( currentPlayer ) + 1;          
        currentPlayer = activePlayers.at( next );
        
        board.render();
        interaction.render();
        
        $( ".container .player" ).removeClass( 'current-player' );
        $( ".container .player" ).each( function(){
            if( $( this ).attr( 'title' ) == currentPlayer.get( 'suspect' ) ) {
                $( this ).addClass( 'current-player' );
                $( ".container .player .icon-bullhorn" ).appendTo( this );
            }
        });
    }
    
    function openTurnTimer() {
        console.log( "in turn timer" );
        turnTimer = setTimeout( function() {
            $( ".fader" ).html( "<div class='in-fader'><p>You've lost due to timeout. Sorry.</p></div>" ).fadeIn();
            $.get( "/clue/api?type=retirement&player=" + thisPlayer.get( 'name' ) );
            
        }, 120000 );
    }
});