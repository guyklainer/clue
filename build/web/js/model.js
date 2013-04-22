
var prefix = "/clue";

// Models
var Player = Backbone.Model.extend({
    initialize: function( props ){
       if( props )
           this.instanceURL = prefix + props.url;
    },
    url: function( props ){
        if( this.instanceURL )
            return this.instanceURL;
        else
            return this.id ? prefix + "/api?type=player&name=" + this.get( 'name' ) : prefix + "/api?type=player";
    }
});

var ClueModel = Backbone.Model.extend({
    url: prefix + "/api?type=model"
});

var PlayersPositions = Backbone.Model.extend({
    initialize: function(){
       
    },
    url: prefix + "/api?type=positions"
});

// Collections
var Players = Backbone.Collection.extend({
    model: Player,
    url: prefix + "/api?type=players"
});

