# CodeIT

Goal is to create a application which enables competetive coding.

### Server:
Will keep track of competitors which are sending their implementation of some BOT.
BOT's will then be matched against eachother and scored.


### Clients:
Are able to get library-file from server. The library will enable competitors
to create a BOT-class aswell as test it before uploading it to the server.


## Pong

Currently a pong game is implemented.
The clients will be creating a Pong-Bot by implementing the GameMechanic
interface and implement the onGameTic method which returns a PongMove.
A PongMove can either be UP, DOWN or NONE.
