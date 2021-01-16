package main.java.chess;

import main.java.chess.chesspiece.*;
import main.java.chess.util.*;

public class Move{
    Pieces piece;
    Coordinate origCoordinate;
    Coordinate coord;
    public Move (Pieces p, Coordinate c){
        piece = p;
        origCoordinate = new Coordinate(p.x, p.y);
        coord = c;
    }
}