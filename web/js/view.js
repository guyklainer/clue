var Board = Backbone.View.extend({
    el: ".board",
    
    initialize: function(){
        this.render();
    },
    render: function(){
        var roomContent = "";
        
        this.$el.find( ".room" ).each( function(){
            var room = this;
            
            players.each( function( player ) {
     
                if( room.id && player.get( 'position' ) == room.id )
                    roomContent += "<div class='" + player.get( 'suspect' ) + " on-board' title='" + player.get( 'suspect' ) + " (" + player.get( 'name' ) + ")'></div>";
            });
            
            _.each( model.get( 'board' ).weaponsPositions, function( weaponPosition ) {
                if( room.id && weaponPosition.position == room.id )
                    roomContent += "<div class='" + weaponPosition.weapon + " on-board' title='" + weaponPosition.weapon + "'></div>";
            });
            
            this.innerHTML = roomContent;
            roomContent = "";
        });
        
    },
    events: {
        "click .choose-option": "chooseRoom"
    },
    chooseRoom: function( event ){
        $( ".choose-option" ).removeClass( 'selected' );
        $( event.target ).addClass( 'selected' );
    }
});

var Interaction = Backbone.View.extend({
    el: ".interaction",
    model: Player,
    
    initialize: function() {
        this.render();
    },
    render: function() {
        if( currentPlayer.get( 'name' ) == thisPlayer.get( 'name' ) ) {
            this.$el.find( ".assumption-accusation" ).show();
            this.$el.find( ".other-player" ).hide();
        } else {
            this.$el.find( ".assumption-accusation" ).hide();
            this.$el.find( ".other-player" ).html( currentPlayer.get( 'name' ) + " is playing now" ).show();
        }
        
        var cardsContent = "<h5>Your cards</h5>";
        
        _.each( thisPlayer.get( 'cards' ), function( card ) {
            var cardClass = card.weapon ? card.weapon : card.room ? card.room : card.suspect ? card.suspect : null;
            cardsContent += "<div class='card " + cardClass  + "' title='" + cardClass + "'></div>";
        });
        
        this.$el.find( '.cards' ).html( cardsContent );
    },
    events: {
        "click  button.make-assumption":        "showDice",
        "click  .make-accusation":              "accusationDialod",
        "submit form.accusation":               "makeAccusation",
        "submit form.loser":                    "continueGame",
        "click  .make-quit":                    "quitDialog",
        "click  .cube":                         "roleDice",
        "click  .cube-result button":           "movePlayer",
        "submit form.make-assumption":          "checkAssumption",
        "click  .choose-card-assumption .card": "chooseCardAssumption",
        "submit .choose-card-assumption":       "showCard",
        "submit .card-showed-to-me":            "nextPlayer"
    },
    showDice: function() {
        this.$el.find( ".assumption-accusation" ).hide();
        this.$el.find( ".role-cube" ).fadeIn();

    },
    accusationDialod: function() {
        this.$el.find( "form.accusation" ).show();
        this.$el.find( ".fader").fadeIn();
    },
    makeAccusation: function( event ) {
      that = this;
      var data = $( event.target ).serialize();
      
      $.get( "/clue/api?type=accusation&" + data );
      
      this.$el.find( "form.accusation" ).fadeOut();
      this.$el.find( ".fader").fadeOut();
      
      return false;
    },
    quitDialog: function() {
        if( confirm( "Are you sure you want to quit?" ) ) {
            $.get( "/clue/api?type=retirement&player=" + thisPlayer.get( 'name' ) );
        }
    },
    roleDice: function() {      
        that = this;
        
        $.ajax({
            url: "/clue/api",
            data: { type: "cube" },
            dataType: "JSON",
            success: function( response ){
                 var cube = response.cube;
                 var currentPosition = thisPlayer.get( 'position' );
                 var forward = currentPosition + cube >= 16 ? currentPosition + cube - 16 : currentPosition + cube;
                 var backward = currentPosition - cube < 0 ? currentPosition - cube + 16 : currentPosition - cube;

                 $( ".room#" + forward ).addClass( 'choose-option' );
                 $( ".room#" + backward ).addClass( 'choose-option' );

                 that.$el.find( ".role-cube" ).hide();
                 that.$el.find( ".cube-result-number" ).html( cube );
                 that.$el.find( ".cube-result" ).fadeIn();
            }
        });
    },
    movePlayer: function() {
        that = this;
        
        if( $(".choose-option.selected").length == 0 )
            toastr.error( "Choose the room you want to move to ");
        
        else {
            var roomID = $(".choose-option.selected").attr('id');
            
            $.ajax({
                url: "/clue/api",
                data: { type: "move", room: roomID },
                dataType: "JSON",
                success: function( response ) {
                    that.$el.find( ".cube-result" ).hide();                    
                    
                    currentPlayer.set( 'position', roomID );
                    board.render();
                    
                    if( thisPlayer.get( 'viewedCards').length != 0 ) { 
                        var cardsContent = "<h5>Your viewed cards</h5>";
                        _.each( thisPlayer.get( 'viewedCards' ), function( card ) {
                            var cardClass = card.weapon ? card.weapon : card.room ? card.room : card.suspect ? card.suspect : null;
                            cardsContent += "<div class='card " + cardClass  + "' title='" + cardClass + "'></div>";
                        });
                        
                        that.$el.find( ".viewed-cards" ).html( cardsContent );
                    }
                    
                    if( model.get( "board" ).squares[ roomID ].room ) {
                        that.$el.find( ".make-assumption .assumption-title" ).html( thisPlayer.get( "name" ) + ", you are at the <span class='yellow'>" + model.get( "board" ).squares[ roomID ].room + "</span>" );
                        
                        that.$el.find( ".make-assumption" ).show();
                        that.$el.find( ".fader").fadeIn();
                    
                    } else
                        that.nextPlayer();
                    
                    $( ".room" ).removeClass( 'choose-option' ).removeClass( 'selected' );
                        
                }
            });
        }
    },
    nextPlayer: function() {
        $( ".fader" ).fadeOut();
        $( ".fader .hide" ).fadeOut();
        
        $.get( "/clue/api?type=next-player" );
        
        return false;
    },
    checkAssumption: function( event ) {
        var data = $( event.target ).serialize();
        var roomID = currentPlayer.get( 'position' );
        
        $( ".fader" ).fadeOut();
        $( ".in-fader" ).fadeOut();
        
        $.ajax({
            url: "/clue/api",
            type: "POST",
            dataType: "JSON",
            data: data + "&room=" + model.get( "board" ).squares[ roomID ].room + "&type=check-assumption",
            success: function( response ) {
                
            }
        });
        
        return false;
    },
    chooseCardAssumption: function( event ) {
        $( ".choose-card-assumption .card.choosed" ).removeClass( 'choosed' ).addClass( 'half-opacity' );
        $( event.target ).addClass( 'choosed' ).removeClass( 'half-opacity' );
        $(".showed-card").val( event.target.id );
    },
    chooseCardToShow: function( cards ) {
        var form = $( ".choose-card-assumption" );
        
        form.find( '.the-title' ).html( thisPlayer.get( 'name' ) + ", choose card to reveal" );
        form.find( ".choose-cards" ).empty();
        
        _.each( cards, function( card ){
            var cardName = card.weapon ? card.weapon : card.suspect ? card.suspect : card.room ? card.room : null;
            form.find( ".choose-cards" ).append( "<div class='card half-opacity " + cardName + "' id='" + cardName + "'></div>" );
        });
        
        form.show();
        this.$el.find( ".fader" ).fadeIn();
    },
    showCard: function( event ) {
        
        var that = this;
        var data = $( event.target ).serialize();
    
        $.ajax({
            url: "/clue/api?type=show-card",
            dataType: "JSON",
            type: "POST",
            data: data + "&player=" + thisPlayer.get( 'name' ),
            success: function() {
                that.$el.find( ".choose-card-assumption" ).fadeOut();
                that.$el.find( ".fader" ).fadeOut();
            }
        });
        
        return false;
    },
    CardThatshowedToMe: function( shower, card ) {
        that = this;
        if( !_.contains( thisPlayer.get( 'viewedCards' ), { room: card } ) )
            thisPlayer.get( 'viewedCards' ).push({ room: card });
        
        this.$el.find( "form.make-assumption" ).fadeOut();
        this.$el.find( ".card-showed-to-me .the-title" ).html( shower + " showed you this card" );
        this.$el.find( ".card-to-show" ).html( "<div class='card half-opacity choosed " + card + "' id='" + card + "'></div>" );
        this.$el.find( ".card-showed-to-me" ).fadeIn();
        this.$el.find( ".fader" ).fadeIn();
        
        
    },
    winnerLoser: function( player, accusation, type ) {
        var form = this.$el.find( '.' + type );
        
        form.find( "." + type + "-title" ).html( player + " made this accusation" );
        form.find( "." + type + "-cards" ).empty();
        
        if( accusation ) {
            _.each( accusation, function( card ){
                var cardName = card.room ? card.room : card.weapon ? card.weapon : card.suspect ? card.suspect : null;
                form.find( "." + type + "-cards" ).append( "<div class='card " + cardName +"'></div>" );
            });
        }
        
        form.show();
        $( ".fader" ).fadeIn();
        
    },
    continueGame: function() {
        if( this.$el.find( ".in-fader:visible" ).length == 1 ) {
            this.$el.find( ".fader" ).fadeOut();
            this.$el.find( ".in-fader" ).fadeOut();
        
        } else {
            this.$el.find( ".in-fader.loser" ).fadeOut();
        }
        
        this.nextPlayer();
        
        return false;
    }
});