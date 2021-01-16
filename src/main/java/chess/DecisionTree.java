package main.java.chess;

import java.util.ArrayList;

import main.java.chess.chesspiece.*;
import main.java.chess.util.*;

public class DecisionTree {
    final int depth;
    boolean side;
    
    public DecisionTree (int depth, boolean side){
        this.depth = depth;
        this.side = side;
    }

    private class SimBoard{
        public int referenceboard[][] = new int[8][8]; 
        public ArrayList<Pieces> chesspieces = new ArrayList<>(); 
        public ArrayList<Move> allmoves = new ArrayList<>();
    }

    SimBoard simBoard = new SimBoard();
    
    /***
     * Update the referenceboard & chesspieces in this instance to the one according to the board
     * @param gameboard
     */
    public void updateBoard(Board gameboard){
        simBoard.referenceboard = gameboard.clone(gameboard.referenceboard);
        simBoard.chesspieces = gameboard.clone(gameboard.chesspieces);
    }

    /***
     * Update All Possible Moves to allmoves according to the board
     * @param gameboard
     */
    public void updateAllMoves(Board gameboard){
        simBoard.allmoves = gameboard.allMoves(simBoard.chesspieces, simBoard.referenceboard, side);
    }

    public ArrayList<Move> clone (ArrayList<Move> allmoves){
        ArrayList<Move> temp = new ArrayList<Move>();
        for (int i=0; i<allmoves.size(); i++)
            temp.add(allmoves.get(i));
        return temp;
    }

    public SimBoard clone (SimBoard original){
        SimBoard clone = new SimBoard();
        //Clone referenceboard
        for (int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                clone.referenceboard[i][j] = original.referenceboard[i][j];
            }
        }
        //Clone chesspieces
        for (int i=0; i<original.chesspieces.size(); i++)
            clone.chesspieces.add(original.chesspieces.get(i));
        //Clone allmoves
        for (int i=0; i<original.allmoves.size(); i++)
            clone.allmoves.add(original.allmoves.get(i));
        return clone;
    }

    /**
     * Recursive Function to assess the best move
     * @param gameboard, used as a one-instance call to use methods; not intended to be modified
     * @param simBoard simulation Board (Local Deep Copy)
     * @param move
     * @param depth current depth in the tree
     * @return
     */
    public Move minimax(Board gameboard, boolean side, SimBoard simBoard, Move move, int depth){
        //Clone a reference copy of simBoard every time to fall back to after recursion
        //AB Pruning needs to be done to optimise memory efficiecy
        //currently very naive
        SimBoard clone = new SimBoard();
        clone = clone(simBoard);

        int accScore = -10000;

        //Minimax Recursion here, WIP

        //fill in recursion here
        //minimax(gameboard, !side, clone, best_move, depth)
        //move (move.piece, move.coord, );
        //Return move value
        return null;
    }

    //public int allControlledTerritory (Board gameBoard, boolean side){

    //}


    //Evaluation Heuristics in this method, maybe need gameboard depending on situation
    public static int score(boolean side, SimBoard simBoard){
        int whiteScore = 0;
        int blackScore = 0;

        //Material
        for (int i=0; i<8; i++){
            for (int j=0; j<8; j++){
                switch (simBoard.referenceboard[i][j]){
                    case 1: //Pawn
                        whiteScore++;
                        break;
                    case 2: //Bishop
                        whiteScore+=3;
                        break;
                    case 3: //Horse
                        whiteScore+=3;
                        break;
                    case 4: //Rook
                        whiteScore+=5;
                        break;
                    case 5: //Queen
                        whiteScore+=10;
                        break;
                    case 9: //King
                        whiteScore+=100;
                        break;
                    case 11:
                        blackScore++;
                        break;
                    case 12:
                        blackScore+=3;
                        break;
                    case 13:
                        blackScore+=3;
                        break;
                    case 14:
                        blackScore+=5;
                        break;
                    case 15:    
                        blackScore+=10;
                        break;
                    case 19:
                        blackScore+=100;
                        break;
                }
            }
        }

        //Pawn Structure
        //assess pawn islands, weak pawns, isolated pawns
        //Right now just pawn position

        for (int i=0; i<simBoard.chesspieces.size(); i++){
            Pieces temp = simBoard.chesspieces.get(i);
            if (temp.type == Pieces.Type.Pawn){
                if (temp.side){//If whiteside
                    //temp.x
                    //WIP
                }
            }
        }

        //Territory


        if (side)
            return (whiteScore-blackScore);
        else
            return (blackScore-whiteScore);
    }
    // More Heuristics needed
    // Pawn structure, Space Controlled, Weak squares / strong squares, Minor piece imbalance, Development, King Safety, Initiative
    // https://www.quora.com/What-are-some-heuristics-for-quickly-evaluating-chess-positions

}
