# CodeIT

Goal is to create a application which enables competetive coding.

### Server:
Will keep track of competitors which are sending their implementation of some BOT.
BOT's will then be matched against eachother and scored.


### Clients:
Are able to get library-file from server. The library will enable competitors
to create a BOT-class aswell as test it before uploading it to the server.


### Setup
Clone project with:
`git clone https://github.com/cthit/CodeIT.git`
From the repository root, run:  `mvn install`. Known to work with  maven version 4.11
`mvn install` will create a bin folder in the repository root.
From here you can start the server with:
`java -jar CodeIT_server.jar <path>`
The path argument is the path to a .jar file with sources. See pong-challenge module for example. 
Distribute the Client in any way you feel like.
The ratingvisualizer takes arguments. the first one is the ip of the server. The second one is port


## Pong

Currently a pong game is implemented.
The clients will be creating a Pong-Bot by implementing the GameMechanic
interface and implement the onGameTic method which returns a PongMove.
A PongMove can either be UP, DOWN or NONE.
