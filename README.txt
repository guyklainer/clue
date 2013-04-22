
Web application
-------------

Ex 04
Guy Klainer 300556743
Nir Elbaz 300457645

Notes:
----------

* The game was tested on chrome browser. It's suppose to be find on other browsers but we didn't check it.

* To roll the cube you need to click on it. the rolling animation is just a CSS effect.

* The submit button of some of the forms are images (<input type="image">). In the assumption form the submit button is the yellow picture with the woman on it.

* The available rooms that you can move to are marked with green square. Just click on one of them.

* When you need to choose a card to reveal, just press on one of the faded cards to select it.

* Each player has 2 minutes to make his turn. after to minutes a timeout is fired and the player will be quit.

* The log of the game his shown in a Growl like popups in the upper right corner. to dismiss the popups just hover them with the mouse or wait few seconds.

* When player quit the game( pressed the quit button, closed the browser, made his turn in more them 2 minutes ) then he become handled like computer (the computer decide which card to show on such demand ), however, when player lost the game, he still needs to select which card to show.

* In the beginning we've compiled the project on Mac OSX 10.8 with JDK 1.7
It cause a compilation problems on computers with JDK 1.6 so the final project is compiled in JDK 1.6.
The project should be working fine, but i've mentioned it FYI.


