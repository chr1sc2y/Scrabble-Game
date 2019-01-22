![0](https://github.com/ZintrulCre/Scrabble-Game/blob/master/Resource/0.png)

Online multi-player Scrabble game implemented in Java using TCP, Thread, and JavaFX.

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
- send message to a certain player to act
- broadcast messages to all other players when current player acts
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
![Login](https://github.com/ZintrulCre/Scrabble-Game/blob/master/Resource/Login.png)

![Desk](https://github.com/ZintrulCre/Scrabble-Game/blob/master/Resource/Desk.png)

![Board](https://github.com/ZintrulCre/Scrabble-Game/blob/master/Resource/Board.png)
