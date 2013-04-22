jQuery(function(){
    $ = jQuery;
    
    // players validation
    $(".create-players").submit( function(e){
        
        $(this).find('input[type="text"]').each(function(){
            
            if( $.trim( $(this).val() ) == "" ) {
                showError("Please enter name for all the players");
                $(this).focus();
                e.preventDefault();
                return false;
            }      
        });
        
        if( !checkDuplicate( $(this).find('input[type="text"]') ) ) {
            e.preventDefault();
            showError( "Each player should have different name" );
            $(this).focus();
            return false;
        }
        
        if ( !checkDuplicate( $(this).find('select') ) ) {
            e.preventDefault();
            showError( "Each player should have different suspect" );
            $(this).focus();
            return false;
        }
        
    });
    
    $(".choose-number").submit( function(e){
        if( $("#players").val() < $("#humans").val() ) {
            showError("Dude, get focus! Number of player should be greater than number of humans.");
            $(this).focus();
            e.preventDefault();
            return false;
        }
    });
    
    // choose room o move on board
    if( $(".js-board-choose").length != 0 ) {
        var forword = $(".js-board-choose").find("input[name='forword']").val();
        var backword = $(".js-board-choose").find("input[name='backword']").val();
        $(".board .room").each( function(){
            if( $(this).attr('id') == forword || $(this).attr('id') == backword )
                $(this).addClass('choose-option');
        });
    }
    
    $(".js-board-choose").submit(function(){
        if( $(".move-to-room").val() == "" ) {
            showError( "Please choose room to go to" );
            return false;
        }
    });
    
    $(".room.choose-option").click(function(){
        var room = $(this);
        $(".room.choose-option").removeClass('selected');
        room.addClass('selected');
        $(".move-to-room").val( room.attr('id') );
        if( room.hasClass('empty') )
            $(".next-step").val( 'next-player' );
        else
            $(".next-step").val( 'make-assumption' );
    });
    
    // create lists
    $('.suspect-select').ddslick({
        onSelected: function( selectedData ){
            $(".suspect").val( selectedData.selectedData.value );
        }   
    });
    $('.weapon-select').ddslick({
        onSelected: function( selectedData ){
            $(".weapon").val( selectedData.selectedData.value );
        }
    });
    $('.room-select').ddslick({
        onSelected: function( selectedData ){
            $(".room").val( selectedData.selectedData.value );
        }
    });
    
    // select card to show after assumption
    $(".choose-card-assumption .card").click(function(){
        $(".choose-card-assumption .card.choosed").removeClass('choosed').addClass('half-opacity');
        $(this).addClass('choosed').removeClass('half-opacity');
        $(".showed-card").val( $(this).attr('id') );
    });
    
    $(".choose-card-assumption").submit(function(){
        if( $(".choose-card-assumption .card.choosed").length == 0 && $(".choose-card-assumption .card.half-opacity").length != 0 ) {
            $(this).find("p.yellow").remove();
            $(this).prepend("<p class='yellow'>Please choose card to reveal!</p>");
            return false;
        }
    });
    
    $(".retire").submit( function(){
        if( confirm( "Are you sure yo want to quit?" ) )
            return true;
        else
            return false;
    });
    
    function showError( msg ) {
        $(".alert p").html( msg );
        $(".alert").slideDown();
        
        setTimeout(function(){
            $(".alert").slideUp();
        }, 5000);
    }
    
    function checkDuplicate( list, msg ) {
        var unique_values = {};
        var flag = true;
        
        $( list ).each(function(){
            if ( ! unique_values[this.value] )
                unique_values[this.value] = true;
            else
                flag = false;  
        });
        
        return flag;
    }
});

