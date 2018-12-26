This is an online multi-player Scrabble game, implemented in Java using TCP, Thread, and JavaFX.

## Server

```
$ java -jar ScrabbleServer.jar [options] -p <port>
```

Default port is 55555.

## Client

```
$ java -jar ScrabbleClient.jar
```


## Component
### Server
- broadcast messages to all other players when current player acts
- prevent players from operating when other players are acting
### Client
- handle operations from the server
### Client GUI
- display user interface
- update the board
- show the score

## Implementation
- TCP
- thread-per-connection architecture

## Demo