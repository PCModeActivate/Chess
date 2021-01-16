package main.java.chess;

import main.java.chess.chesspiece.*;
import main.java.chess.chesspiece.Pieces.Type;
import main.java.chess.util.*;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.io.BufferedReader;
import java.util.ArrayList;

public class Board extends DrawableElement {
    private static final int sideLength = 50;
    private static final int spacing = 10;
    private static final int combinedSpace = (sideLength + spacing);
    private Pieces selected;
    private Pieces aimPiece;
    private Pieces pawntotransform;
    private boolean changep = false;

    public int referenceboard[][] = new int[8][8]; // to check if chess piece exist, if not, int == 0
    private int cboard[][] = new int[8][8]; // Temporary Board to used for simulation
    public ArrayList<Pieces> chesspieces = new ArrayList<>(); // Set to null when a piece is taken, default object (not init) is null
    private ArrayList<Pieces> tpieces = new ArrayList<>(); //Temporary arraylist of pieces used in conjunction with cboard

    private ArrayList<PImage> drawpieces = new ArrayList<>();
    static boolean Initiative = true; //true is white; false is black
    static boolean wcl = false; //left side castle
    static boolean wcr = false;
    static boolean bcl = false; //left side castle
    static boolean bcr = false;
    static boolean wch = false; // checkmate
    static boolean bch = false;
    public boolean gg = false;
    //Add variables for special conditions
    //Castle left, king or left rook not moved yet
    //Castle right, king or right rook not moved yet
    //Array of boolean for pawn moved yet, for en passant
    // board is organized like this
    // board[x][y]
    // 0 x
    // y
    // |

    public Board(PApplet parent) {
        super(parent);
        Initiative = true;
        drawpieces.add(parent.loadImage("/chesspieces/PawnL.png"));
        drawpieces.add(parent.loadImage("/chesspieces/PawnL.png"));
        drawpieces.add(parent.loadImage("/chesspieces/BishopL.png"));
        drawpieces.add(parent.loadImage("/chesspieces/KnightL.png"));
        drawpieces.add(parent.loadImage("/chesspieces/RookL.png"));
        drawpieces.add(parent.loadImage("/chesspieces/QueenL.png"));
        drawpieces.add(parent.loadImage("/chesspieces/QueenL.png"));
        drawpieces.add(parent.loadImage("/chesspieces/QueenL.png"));
        drawpieces.add(parent.loadImage("/chesspieces/QueenL.png"));
        drawpieces.add(parent.loadImage("/chesspieces/KingL.png"));

        drawpieces.add(parent.loadImage("/chesspieces/PawnD.png"));
        drawpieces.add(parent.loadImage("/chesspieces/PawnD.png"));
        drawpieces.add(parent.loadImage("/chesspieces/BishopD.png"));
        drawpieces.add(parent.loadImage("/chesspieces/KnightD.png"));
        drawpieces.add(parent.loadImage("/chesspieces/RookD.png"));
        drawpieces.add(parent.loadImage("/chesspieces/QueenD.png"));
        drawpieces.add(parent.loadImage("/chesspieces/QueenD.png"));
        drawpieces.add(parent.loadImage("/chesspieces/QueenD.png"));
        drawpieces.add(parent.loadImage("/chesspieces/QueenD.png"));
        drawpieces.add(parent.loadImage("/chesspieces/KingD.png"));
        for (int i = 0; i < drawpieces.size(); i++) {
            drawpieces.get(i).resize(drawpieces.get(i).width / 4, drawpieces.get(i).height / 4);
        }
        initialSetup();
    }

    public void initialSetup(){
        // Hard Code the board entries here
        // loop through 8, to add 16 pawns
        // Board array: 1 is pawn, 2 is bishop, 3 is horse, 4 is rook, 5 is queen, 9 is king
        chesspieces.add(new Rook(0, 0, true));
        referenceboard[0][0] = 4;
        chesspieces.add(new Rook(7, 0, true));
        referenceboard[7][0] = 4;
        chesspieces.add(new Rook(0, 7, false));
        referenceboard[0][7] = 14;
        chesspieces.add(new Rook(7, 7, false));
        referenceboard[7][7] = 14;
        // Rooks

        for (int i = 0; i < 8; i++) {
            chesspieces.add(new Pawn(i, 1, true));
            referenceboard[i][1] = 1;
            chesspieces.add(new Pawn(i, 6, false));
            referenceboard[i][6] = 11;
        }
        selected = chesspieces.get(6);
        aimPiece = chesspieces.get(6);
        // Pawns

        chesspieces.add(new Horse(1, 0, true));
        referenceboard[1][0] = 3;
        chesspieces.add(new Horse(6, 0, true));
        referenceboard[6][0] = 3;
        chesspieces.add(new Horse(1, 7, false));
        referenceboard[1][7] = 13;
        chesspieces.add(new Horse(6, 7, false));
        referenceboard[6][7] = 13;
        // Horses

        chesspieces.add(new Bishop(2, 0, true));
        referenceboard[2][0] = 2;
        chesspieces.add(new Bishop(5, 0, true));
        referenceboard[5][0] = 2;
        chesspieces.add(new Bishop(2, 7, false));
        referenceboard[2][7] = 12;
        chesspieces.add(new Bishop(5, 7, false));
        referenceboard[5][7] = 12;
        // Bishop

        chesspieces.add(new Queen(3, 0, true));
        referenceboard[3][0] = 5;
        chesspieces.add(new Queen(3, 7, false));
        referenceboard[3][7] = 15;
        // Queen

        chesspieces.add(new King(4, 0, true));
        referenceboard[4][0] = 9;
        chesspieces.add(new King(4, 7, false));
        referenceboard[4][7] = 19;
        // King


    }

    /**
     * Prints the current board arrangement to the console.
     */
    private void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(referenceboard[j][i] + " ");
            }
            System.out.println();
        }
        System.out.println("");
        System.out.println("");
    }

    private void checkCastling() {//Check before av_moves
        wcl = false;
        wcr = false;
        bcl = false;
        bcr = false;
        boolean twk = false;
        boolean twlr = false;
        boolean twrr = false;
        boolean tbk = false;
        boolean tblr = false;
        boolean tbrr = false;
        for (int i = 0; i < chesspieces.size(); i++) {
            if (chesspieces.get(i).side && chesspieces.get(i).type == Pieces.Type.King && !chesspieces.get(i).firstmove) {
                twk = true;
            }
            else if (!chesspieces.get(i).side && chesspieces.get(i).type == Pieces.Type.King && !chesspieces.get(i).firstmove) {
                tbk = true;
            }
            else if (chesspieces.get(i).side && chesspieces.get(i).x == 0 && chesspieces.get(i).y == 0 && chesspieces.get(i).type == Pieces.Type.Rook && !chesspieces.get(i).firstmove) {
                twlr = true;
            }
            else if (chesspieces.get(i).side && chesspieces.get(i).x == 7 && chesspieces.get(i).y == 0 && chesspieces.get(i).type == Pieces.Type.Rook && !chesspieces.get(i).firstmove) {
                twrr = true;
            }
            else if (!chesspieces.get(i).side && chesspieces.get(i).x == 0 && chesspieces.get(i).y == 7 && chesspieces.get(i).type == Pieces.Type.Rook && !chesspieces.get(i).firstmove) {
                tblr = true;
            }
            else if (!chesspieces.get(i).side && chesspieces.get(i).x == 7 && chesspieces.get(i).y == 7 && chesspieces.get(i).type == Pieces.Type.Rook && !chesspieces.get(i).firstmove) {
                tbrr = true;
            }
        }
        if (twk && twlr && referenceboard[1][0] == 0 && referenceboard[2][0] == 0 && referenceboard[3][0] == 0) {
            wcl = true;
        }
        if (twk && twrr && referenceboard[5][0] == 0 && referenceboard[6][0] == 0) {
            wcr = true;
        }
        if (tbk && tblr && referenceboard[1][7] == 0 && referenceboard[2][7] == 0 && referenceboard[3][7] == 0) {
            bcl = true;
        }
        if (tbk && tbrr && referenceboard[5][7] == 0 && referenceboard[6][7] == 0) {
            bcr = true;
        }
    }

    /**
     * Selects a piece and deselects the previous piece
     *
     * @param tp the new piece to select
     */
    private void selectCell(Pieces tp) {
        if (selected.CellStatus == Pieces.cellStatus.CLICKED && tp == selected) {
            //System.out.println("unSelect");
            //aimPiece.CellStatus = Pieces.cellStatus.SELECTED;
            selected = aimPiece;
            selected.CellStatus = Pieces.cellStatus.SELECTED;
            if (indanger(true, selected)) {
                selected.CellStatus = Pieces.cellStatus.CONFLICTED;
                // then set selected to the currently hovered cell
            }
        }
        else if (selected.CellStatus == Pieces.cellStatus.CLICKED && tp != selected){
            selected.CellStatus = Pieces.cellStatus.UNSELECTED;
            selected = tp;
            aimPiece = selected;
            if (indanger(true, selected)) {
                selected.CellStatus = Pieces.cellStatus.CONFLICTED;
            }
            selected.CellStatus = Pieces.cellStatus.CLICKED;
            //System.out.println("reSelect");
        }
        //^unselect selected piece
        else if (tp != selected && selected.CellStatus != Pieces.cellStatus.CLICKED) {
            //System.out.println("Select");
            //      System.out.println("normal hover");
            selected.CellStatus = Pieces.cellStatus.UNSELECTED;
            if (indanger(true, selected)) {
                selected.CellStatus = Pieces.cellStatus.CONFLICTED;
            }
            selected = tp;
            aimPiece = selected;
            selected.CellStatus = Pieces.cellStatus.SELECTED;
            // then set selected to the currently hovered cell
        }
        //^Unclicked select cell, normal hover
    }

    private void capturepiece(Pieces tp) {
        //from x, from y, x captured, y captured, position of captured piece in arraylist
        referenceboard[tp.x][tp.y] = referenceboard[selected.x][selected.y];
        referenceboard[selected.x][selected.y] = 0;
        selected.x = tp.x;
        selected.y = tp.y;
        for (int i = 0; i < chesspieces.size(); i++) {
            if (tp == chesspieces.get(i)) {
                if (tp.type == Pieces.Type.King) {
                    gg = true;
                }
                chesspieces.remove(i);
                break;
            }
        }
        if (selected.side && selected.type == Pieces.Type.Pawn && selected.y == 7) {
            pawntotransform = selected;
            changep = true;
        } else if (!selected.side && selected.type == Pieces.Type.Pawn && selected.y == 0) {
            pawntotransform = selected;
            changep = true;
        } else Initiative = !Initiative;
    }

    private boolean indanger(boolean Temp, Pieces temp) {
        if (Temp) {
            //Checks the current board if temp
            ArrayList<Coordinate> ta = new ArrayList<>();
            for (int i = 0; i < chesspieces.size(); i++) {
                if (chesspieces.get(i).side != Initiative) {
                    ta.clear();
                    ta = av_move(chesspieces, referenceboard, chesspieces.get(i));
                }
                if (ta != null && ta.size() != 0) {
                    while (ta.size() > 0) {
                        if (ta.get(0).x == temp.x && ta.get(0).y == temp.y) {
                            return true;
                        }
                        ta.remove(0);
                    }
                }
            }
            return false;
        } 
        else {
            //Checks the simulated board if not temp
            ArrayList<Coordinate> ta = new ArrayList<>();
            for (int i = 0; i < tpieces.size(); i++) {
                if (tpieces.get(i).side != Initiative) {
                    ta.clear();
                    ta = av_move(tpieces, cboard, tpieces.get(i));
                }
                if (ta != null && ta.size() != 0) {
                    while (ta.size() > 0) {
                        if (ta.get(0).x == temp.x && ta.get(0).y == temp.y) {
                            return true;
                        }
                        ta.remove(0);
                    }
                }
            }
            return false;
        }
    }

    private void pawnTransformation(Pieces p, char ch) {
        //ty % 4 == 0 ... Bishop; ty % 4 == 1 ... Rook; ty % 4 == 2 ... Horse;  ty % 4 == 3 ... Queen;
        if (ch == 'b') {
            for (int i = 0; i < chesspieces.size(); i++) {
                if (p.equals(chesspieces.get(i))) {
                    chesspieces.remove(i);
                    p.type = Pieces.Type.Bishop;
                    chesspieces.add(p);
                    if (p.side) referenceboard[p.x][p.y] = 2;
                    else referenceboard[p.x][p.y] = 12;
                }
            }
        } else if (ch == 'r') {
            for (int i = 0; i < chesspieces.size(); i++) {
                if (p.equals(chesspieces.get(i))) {
                    chesspieces.remove(i);
                    p.type = Pieces.Type.Rook;
                    p.firstmove = true;
                    chesspieces.add(p);
                    if (p.side) referenceboard[p.x][p.y] = 4;
                    else referenceboard[p.x][p.y] = 14;
                }
            }
        } else if (ch == 'k') {
            for (int i = 0; i < chesspieces.size(); i++) {
                if (p.equals(chesspieces.get(i))) {
                    chesspieces.remove(i);
                    p.type = Pieces.Type.Horse;
                    chesspieces.add(p);
                    if (p.side) referenceboard[p.x][p.y] = 3;
                    else referenceboard[p.x][p.y] = 13;
                }
            }
        } else if (ch == 'q') {
            for (int i = 0; i < chesspieces.size(); i++) {
                if (p.equals(chesspieces.get(i))) {
                    chesspieces.remove(i);
                    p.type = Pieces.Type.Queen;
                    chesspieces.add(p);
                    if (p.side) referenceboard[p.x][p.y] = 9;
                    else referenceboard[p.x][p.y] = 19;
                }
            }
        }
        Initiative = !Initiative;
    }

    public boolean isInCheck (boolean side){
        //Check by side
        for (int i = 0; i < chesspieces.size(); i++) {
            Pieces currentPiece = chesspieces.get(i);
            if (currentPiece.side == side){    
                if (indanger(true, currentPiece) && currentPiece.type == Type.King) {
                    return true;
                }
            }
        }
        return false;    
    }

    public boolean isInCheck (Pieces piece){
        //Check by piece
       if (indanger(true, piece) && piece.type == Type.King) {
            return true;
        }
        return false;    
    }


    //TBD: divorce ui from simulation in update() in order to simulate ai move & replace human move

    @Override
    public void update() {
        // iterate over every cell in the board
        if (!gg) {
            if (!changep) {
                int index;
                for (int i = 0; i < chesspieces.size(); i++) {
                    //Make sure if anything is in check
                    Pieces currentPiece = chesspieces.get(i);
                    if (isInCheck(currentPiece)) {
                        if (currentPiece.side) wch = true;
                        else bch = true;
                        break;
                    } 
                    else {
                        if (currentPiece.side) wch = false;
                        else bch = false;
                    }
                    //This is where checking flag setting ends

                    if (selected != currentPiece && !indanger(true, currentPiece))
                        currentPiece.CellStatus = Pieces.cellStatus.UNSELECTED;
                    else if (selected != currentPiece) currentPiece.CellStatus = Pieces.cellStatus.CONFLICTED;
                    else if (currentPiece.CellStatus != Pieces.cellStatus.CLICKED)
                        currentPiece.CellStatus = Pieces.cellStatus.SELECTED;
                }

                if (bch) {
                    ArrayList<Coordinate> lastresort = new ArrayList<>();
                    for (int i = 0; i < chesspieces.size(); i++) {
                        if (!chesspieces.get(i).side) {
                            lastresort = av_move(chesspieces, referenceboard, chesspieces.get(i));
                            for (int j = lastresort.size() - 1; j >= 0; j--) {
                                if (!isLegal(chesspieces.get(i), lastresort.get(j).x, lastresort.get(j).y)) {
                                    lastresort.remove(j);
                                }
                            }
                            if (lastresort.size() != 0) {
                                break;
                            }
                        }
                    }
                    if (lastresort.size() == 0) {
                        gg = true;
                    }
                }
                if (wch) {
                    ArrayList<Coordinate> lastresort = new ArrayList<>();
                    for (int i = 0; i < chesspieces.size(); i++) {
                        if (chesspieces.get(i).side) {
                            lastresort = av_move(chesspieces, referenceboard, chesspieces.get(i));
                            for (int j = lastresort.size() - 1; j >= 0; j--) {
                                if (!isLegal(chesspieces.get(i), lastresort.get(j).x, lastresort.get(j).y)) {
                                    lastresort.remove(j);
                                }
                            }
                            if (lastresort.size() != 0) {
                                break;
                            }
                        }
                    }
                    if (lastresort.size() == 0) {
                        gg = true;
                    }
                }
                if (!gg) {
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            int cellX = this.position.x + i * combinedSpace;
                            int cellY = this.position.y + j * combinedSpace;
                            index = -1;
                            Pieces currentPiece;
                            if ((parent.mouseX > cellX && parent.mouseX < cellX + sideLength) && (parent.mouseY > cellY && parent.mouseY < cellY + sideLength)) {
                                //Mouse is hovering over this position
                                for (int c = 0; c < chesspieces.size(); c++) {
                                    // Selects the currentpiece from the chess piece
                                    if (chesspieces.get(c).x == i && chesspieces.get(c).y == 7 - j) {
                                        index = c;
                                        break;
                                    }
                                }
                                //       System.out.println("CellX" + cellX + ", CellY" + cellY + ", X" + i + ", Y" + j);
                                if (index != -1) {
                                    //Given that the mouse is hovering over a piece
                                    currentPiece = chesspieces.get(index);
                                    //currentPiece is the piece the mouse is hovering over
                                    if (selected.CellStatus == Pieces.cellStatus.CLICKED) {
                                        if (selected.side == Initiative && currentPiece.side != Initiative) {
                                            if (isLegal(selected, i, 7 - j)) {
                                                if (Input.getMouseButton(Input.Button.LEFT, Input.Event.PRESS)) {
                                                    capturepiece(currentPiece);
                                                    //System.out.println("Situation 2");
                                                    selected.CellStatus = Pieces.cellStatus.SELECTED;
                                                }
                                            }
                                        }

                                        //Unclick the selected piece
                                        else if (selected.side == Initiative && selected.CellStatus == Pieces.cellStatus.CLICKED && currentPiece.side == Initiative) {
                                            //System.out.println("Situation 4");
                                            if (Input.getMouseButton(Input.Button.LEFT, Input.Event.PRESS)) {
                                                //Unselected the selected cell and select currentpiece
                                                selectCell(currentPiece);
                                            }
                                        }
                                    } else if (currentPiece.side == Initiative) {
                                        //normal select or click over a hovering piece
                                        //System.out.println("Situation 5");
                                        selectCell(currentPiece);
                                        if (Input.getMouseButton(Input.Button.LEFT, Input.Event.PRESS)) {
                                            //System.out.println("Situation 3");
                                            selected.CellStatus = Pieces.cellStatus.CLICKED;
                                        }
                                    }
                                    //selected piece
                                } else {
                                    //Aimpiece is irrelevant, as the mouse is not hovering over anything in this case.
                                    //System.out.println("Situation 1");
                                    if (selected.CellStatus == Pieces.cellStatus.CLICKED && Input.getMouseButton(Input.Button.LEFT, Input.Event.PRESS)) {
                                        Coordinate tp = new Coordinate(i, 7 - j);
                                        if (isLegal(selected, i, 7 - j)) {
                                            move(selected, tp, chesspieces, referenceboard);
                                            selected.CellStatus = Pieces.cellStatus.SELECTED;
                                            aimPiece = selected;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (Input.getKeyEvent(Input.Event.PRESS)) {
                    char key = (char) Input.keyIsHeld;
                    if (key == 'q' || key == 'k' || key == 'r' || key == 'b') {
                        pawnTransformation(pawntotransform, key);
                        changep = false;
                    }
                }
            }
        }
    }

    public ArrayList<Pieces> clone (ArrayList<Pieces> chesspieces){
        ArrayList<Pieces> tPieces = new ArrayList<>();
        for (int i = 0; i < chesspieces.size(); i++) {
            if (chesspieces.get(i).type == Pieces.Type.King)
                tPieces.add(new King(chesspieces.get(i).x, chesspieces.get(i).y, chesspieces.get(i).side));
            else if (chesspieces.get(i).type == Pieces.Type.Rook)
                tPieces.add(new Rook(chesspieces.get(i).x, chesspieces.get(i).y, chesspieces.get(i).side));
            else if (chesspieces.get(i).type == Pieces.Type.Horse)
                tPieces.add(new Horse(chesspieces.get(i).x, chesspieces.get(i).y, chesspieces.get(i).side));
            else if (chesspieces.get(i).type == Pieces.Type.Queen)
                tPieces.add(new Queen(chesspieces.get(i).x, chesspieces.get(i).y, chesspieces.get(i).side));
            else if (chesspieces.get(i).type == Pieces.Type.Pawn)
                tPieces.add(new Pawn(chesspieces.get(i).x, chesspieces.get(i).y, chesspieces.get(i).side));
            else if (chesspieces.get(i).type == Pieces.Type.Bishop)
                tPieces.add(new Bishop(chesspieces.get(i).x, chesspieces.get(i).y, chesspieces.get(i).side));
        }
        return tPieces;
    }

    public int[][] clone (int [][] temp){
       int [][] tempboard = new int [8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tempboard[i][j] = referenceboard[i][j];
            }
        }
        return tempboard;
    }

    public boolean isLegal (Pieces temp, Coordinate coord){
        return isLegal (temp, coord.x, coord.y);
    }

    private boolean isLegal(Pieces temp, int a, int b) {
        //This function takes a piece, and a set of coordinates
        //If the set of coordinates is within the piece's moves, then it is legal to move to the coordinates.
        //This function is implemented in the check-for-check function to see if there are any legal moves to get the king out of check
        //This function is also implemented in the update function to ensure that the pieces dont just go everywhere
        // a = x & b = y for the move coordinate (duh)
        ArrayList<Coordinate> ta = av_move(chesspieces, referenceboard, temp);
        //Brute forces all inputs
        tpieces.clear();

        //Cloning the chesspiece into tpieces
        tpieces = clone (chesspieces);
        int movei = -1, capturei = -1, kingi = -1;
        //Sets up some variables to check if this move will leave the king in check

        if (ta != null && ta.size() != 0) {
            while (ta.size() > 0) {
                //System.out.println("if "+ta.get(0).x+"=="+a+" && "+ta.get(0).y +"== "+b);
                if (ta.get(0).x == a && ta.get(0).y == b) {
                    //The piece can physically move to this spot
                    for (int i = 0; i < tpieces.size(); i++) {
                        //Temp.side is the side that we can move, it is also Initiative
                        if (temp.x == tpieces.get(i).x && temp.y == tpieces.get(i).y && temp.side == tpieces.get(i).side) {
                            movei = i;
                        } else if (tpieces.get(i).x == a && tpieces.get(i).y == b && tpieces.get(i).side != temp.side) {
                            capturei = i;
                        }
                        if (tpieces.get(i).type == Pieces.Type.King && tpieces.get(i).side == Initiative) {
                            kingi = i;
                        }
                        //Finding the variables
                    }

                    if (movei != -1) {
                        Pieces tm = tpieces.get(movei);
                        Pieces tk = tpieces.get(kingi);
                        //Checks to get the locations of the piece that will be moved and the king
                        //Duplicate referenceboard onto cboard
                        cboard = clone (referenceboard);
                        cboard[a][b] = cboard[tm.x][tm.y];
                        cboard[tm.x][tm.y] = 0;
                        if (movei == kingi) {
                            tk.x = a;
                            tk.y = b;
                            //If the king is moved, then also change the king's x vlaue
                        }
                        tm.x = a;
                        tm.y = b;
                        if (capturei != -1) {
                            tpieces.remove(capturei);
                        }
                        if (indanger(false, tk)) {
                            return false;
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
                ta.remove(0);
            }
        }
        return false;
    }

    //Output all available moves of a piece given a board and a piece
    public ArrayList<Coordinate> av_move(ArrayList<Pieces> alist, int rboard[][], Pieces p) {
        ArrayList<Coordinate> av_moves = new ArrayList<Coordinate>();
        if (p.type.equals(Pieces.Type.King)) {
            checkCastling();
            int tx = p.x;
            int ty = p.y;
            if (tx < 7 && (rboard[p.x + 1][p.y] == 0 || rboard[p.x + 1][p.y] / 10 != rboard[p.x][p.y] / 10)) {
                av_moves.add(new Coordinate(p.x + 1, p.y));
            }
            if (tx > 0 && (rboard[p.x - 1][p.y] == 0 || rboard[p.x - 1][p.y] / 10 != rboard[p.x][p.y] / 10)) {
                av_moves.add(new Coordinate(p.x - 1, p.y));
            }

            if (ty < 7) {
                if (rboard[p.x][p.y + 1] == 0 || rboard[p.x][p.y + 1] / 10 != rboard[p.x][p.y] / 10) {
                    av_moves.add(new Coordinate(p.x, p.y + 1));
                }
                if (tx < 7 && (rboard[p.x + 1][p.y + 1] == 0 || rboard[p.x + 1][p.y + 1] / 10 != rboard[p.x][p.y] / 10)) {
                    av_moves.add(new Coordinate(p.x + 1, p.y + 1));
                }
                if (tx > 0 && (rboard[p.x - 1][p.y + 1] == 0 || rboard[p.x - 1][p.y + 1] / 10 != rboard[p.x][p.y] / 10)) {
                    av_moves.add(new Coordinate(p.x - 1, p.y + 1));
                }
            }
            if (ty > 0) {
                if ((rboard[p.x][p.y - 1] == 0 || rboard[p.x][p.y - 1] / 10 != rboard[p.x][p.y] / 10)) {
                    av_moves.add(new Coordinate(p.x, p.y - 1));
                }
                if (tx < 7 && (rboard[p.x + 1][p.y - 1] == 0 || rboard[p.x + 1][p.y - 1] / 10 != rboard[p.x][p.y] / 10)) {
                    av_moves.add(new Coordinate(p.x + 1, p.y - 1));
                }
                if (tx > 0 && (rboard[p.x - 1][p.y - 1] == 0 || rboard[p.x - 1][p.y - 1] / 10 != rboard[p.x][p.y] / 10)) {
                    av_moves.add(new Coordinate(p.x - 1, p.y - 1));
                }
            }

            if (p.side) {
                if (wcl) {
                    av_moves.add(new Coordinate(2, 0));
                }
                if (wcr) {
                    av_moves.add(new Coordinate(6, 0));
                }
            } else {
                if (bcl) {
                    av_moves.add(new Coordinate(2, 7));
                }
                if (bcr) {
                    av_moves.add(new Coordinate(6, 7));
                }
            }
        }
        if (p.type.equals(Pieces.Type.Queen)) {
            int tx = p.x;
            int ty = p.y;
            while (tx < 7 && ty < 7 && rboard[tx + 1][ty + 1] == 0) {
                av_moves.add(new Coordinate(tx + 1, ty + 1));
                tx++;
                ty++;
                //Diagonal right up
            }
            if (tx < 7 && ty < 7 && rboard[tx + 1][ty + 1] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx + 1, ty + 1));
            }
            tx = p.x;
            ty = p.y;
            while (tx < 7 && ty > 0 && rboard[tx + 1][ty - 1] == 0) {
                av_moves.add(new Coordinate(tx + 1, ty - 1));
                tx++;
                ty--;
                //Diagonal right down
            }
            if (tx < 7 && ty > 0 && rboard[tx + 1][ty - 1] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx + 1, ty - 1));
            }
            tx = p.x;
            ty = p.y;
            while (tx > 0 && ty < 7 && rboard[tx - 1][ty + 1] == 0) {
                av_moves.add(new Coordinate(tx - 1, ty + 1));
                tx--;
                ty++;
                //Diagonal left up
            }
            if (tx > 0 && ty < 7 && rboard[tx - 1][ty + 1] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx - 1, ty + 1));
            }
            tx = p.x;
            ty = p.y;
            while (tx > 0 && ty > 0 && rboard[tx - 1][ty - 1] == 0) {
                av_moves.add(new Coordinate(tx - 1, ty - 1));
                tx--;
                ty--;
                //Diagonal left down
            }
            if (tx > 0 && ty > 0 && rboard[tx - 1][ty - 1] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx - 1, ty - 1));
            }

            tx = p.x;
            ty = p.y;
            while (tx < 7 && rboard[tx + 1][ty] == 0) {
                av_moves.add(new Coordinate(tx + 1, ty));
                tx++;
            }
            if (tx < 7 && rboard[tx + 1][ty] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx + 1, ty));
            }
            tx = p.x;
            ty = p.y;

            while (tx > 0 && rboard[tx - 1][ty] == 0) {
                av_moves.add(new Coordinate(tx - 1, ty));
                tx--;
            }
            if (tx > 0 && rboard[tx - 1][ty] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx - 1, ty));
            }
            tx = p.x;
            ty = p.y;

            while (ty > 0 && rboard[tx][ty - 1] == 0) {
                av_moves.add(new Coordinate(tx, ty - 1));
                ty--;
            }
            if (ty > 0 && rboard[tx][ty - 1] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx, ty - 1));
            }
            tx = p.x;
            ty = p.y;

            while (ty < 7 && rboard[tx][ty + 1] == 0) {
                av_moves.add(new Coordinate(tx, ty + 1));
                ty++;
            }
            if (ty < 7 && rboard[tx][ty + 1] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx, ty + 1));
            }
            tx = p.x;
            ty = p.y;
        }

        if (p.type.equals(Pieces.Type.Bishop)) {
            int tx = p.x;
            int ty = p.y;
            while (tx < 7 && ty < 7 && rboard[tx + 1][ty + 1] == 0) {
                av_moves.add(new Coordinate(tx + 1, ty + 1));
                tx++;
                ty++;
            }
            if (tx < 7 && ty < 7 && rboard[tx + 1][ty + 1] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx + 1, ty + 1));
            }
            tx = p.x;
            ty = p.y;
            while (tx < 7 && ty > 0 && rboard[tx + 1][ty - 1] == 0) {
                av_moves.add(new Coordinate(tx + 1, ty - 1));
                tx++;
                ty--;
            }
            if (tx < 7 && ty > 0 && rboard[tx + 1][ty - 1] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx + 1, ty - 1));
            }
            tx = p.x;
            ty = p.y;
            while (tx > 0 && ty < 7 && rboard[tx - 1][ty + 1] == 0) {
                av_moves.add(new Coordinate(tx - 1, ty + 1));
                tx--;
                ty++;
            }
            if (tx > 0 && ty < 7 && rboard[tx - 1][ty + 1] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx - 1, ty + 1));
            }
            tx = p.x;
            ty = p.y;
            while (tx > 0 && ty > 0 && rboard[tx - 1][ty - 1] == 0) {
                av_moves.add(new Coordinate(tx - 1, ty - 1));
                tx--;
                ty--;
            }
            if (tx > 0 && ty > 0 && rboard[tx - 1][ty - 1] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx - 1, ty - 1));
            }
        }

        if (p.type.equals(Pieces.Type.Horse)) {
            if (p.x + 1 < 8 && p.y + 2 < 8) {
                if (rboard[p.x + 1][p.y + 2] == 0 || rboard[p.x + 1][p.y + 2] / 10 != rboard[p.x][p.y] / 10)
                    av_moves.add(new Coordinate(p.x + 1, p.y + 2));
            }
            if (p.x + 2 < 8 && p.y + 1 < 8) {
                if (rboard[p.x + 2][p.y + 1] == 0 || rboard[p.x + 2][p.y + 1] / 10 != rboard[p.x][p.y] / 10)
                    av_moves.add(new Coordinate(p.x + 2, p.y + 1));
            }

            if (p.x + 1 < 8 && p.y - 2 > -1) {
                if (rboard[p.x + 1][p.y - 2] == 0 || rboard[p.x + 1][p.y - 2] / 10 != rboard[p.x][p.y] / 10)
                    av_moves.add(new Coordinate(p.x + 1, p.y - 2));
            }
            if (p.x + 2 < 8 && p.y - 1 > -1) {
                if (rboard[p.x + 2][p.y - 1] == 0 || rboard[p.x + 2][p.y - 1] / 10 != rboard[p.x][p.y] / 10)
                    av_moves.add(new Coordinate(p.x + 2, p.y - 1));
            }

            if (p.x - 1 > -1 && p.y - 2 > -1) {
                if (rboard[p.x - 1][p.y - 2] == 0 || rboard[p.x - 1][p.y - 2] / 10 != rboard[p.x][p.y] / 10)
                    av_moves.add(new Coordinate(p.x - 1, p.y - 2));
            }
            if (p.x - 2 > -1 && p.y - 1 > -1) {
                if (rboard[p.x - 2][p.y - 1] == 0 || rboard[p.x - 2][p.y - 1] / 10 != rboard[p.x][p.y] / 10)
                    av_moves.add(new Coordinate(p.x - 2, p.y - 1));
            }

            if (p.x - 1 > -1 && p.y + 2 < 8) {
                if (rboard[p.x - 1][p.y + 2] == 0 || rboard[p.x - 1][p.y + 2] / 10 != rboard[p.x][p.y] / 10)
                    av_moves.add(new Coordinate(p.x - 1, p.y + 2));
            }
            if (p.x - 2 > -1 && p.y + 1 < 8) {
                if (rboard[p.x - 2][p.y + 1] == 0 || rboard[p.x - 2][p.y + 1] / 10 != rboard[p.x][p.y] / 10)
                    av_moves.add(new Coordinate(p.x - 2, p.y + 1));
            }
        }

        if (p.type.equals(Pieces.Type.Rook)) {
            checkCastling();
            int tx = p.x;
            int ty = p.y;
            while (tx < 7 && rboard[tx + 1][ty] == 0) {
                av_moves.add(new Coordinate(tx + 1, ty));
                tx++;
            }
            if (tx < 7 && rboard[tx + 1][ty] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx + 1, ty));
            }
            tx = p.x;
            ty = p.y;

            while (tx > 0 && rboard[tx - 1][ty] == 0) {
                av_moves.add(new Coordinate(tx - 1, ty));
                tx--;
            }
            if (tx > 0 && rboard[tx - 1][ty] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx - 1, ty));
            }
            tx = p.x;
            ty = p.y;

            while (ty > 0 && rboard[tx][ty - 1] == 0) {
                av_moves.add(new Coordinate(tx, ty - 1));
                ty--;
            }
            if (ty > 0 && rboard[tx][ty - 1] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx, ty - 1));
            }
            tx = p.x;
            ty = p.y;

            while (ty < 7 && rboard[tx][ty + 1] == 0) {
                av_moves.add(new Coordinate(tx, ty + 1));
                ty++;
            }
            if (ty < 7 && rboard[tx][ty + 1] / 10 != rboard[p.x][p.y] / 10) {
                av_moves.add(new Coordinate(tx, ty + 1));
            }

            if (p.side) {
                if (p.x == 0 && wcl) {
                    av_moves.add(new Coordinate(0, 3));
                }
                if (p.x == 7 && wcr) {
                    av_moves.add(new Coordinate(0, 5));
                }
            } else {
                if (p.x == 0 && bcl) {
                    av_moves.add(new Coordinate(6, 3));
                }
                if (p.x == 7 && bcr) {
                    av_moves.add(new Coordinate(6, 5));
                }
            }
        }
        if (p.type.equals(Pieces.Type.Pawn)) {
            if (p.side) { // White pawn
                if (p.y == 1) {
                    if (rboard[p.x][p.y + 1] == 0) {
                        av_moves.add(new Coordinate(p.x, p.y + 1));
                        if (rboard[p.x][p.y + 2] == 0) {
                            av_moves.add(new Coordinate(p.x, p.y + 2));
                        }
                    }
                } else if (p.y < 7) {
                    if (rboard[p.x][p.y + 1] == 0) {
                        av_moves.add(new Coordinate(p.x, p.y + 1));
                    }
                }
                // En passant Moves
                if (p.y == 4) {
                    if (p.x < 7) {
                        if (rboard[p.x + 1][p.y] == 11) {
                            for (int i = alist.size() - 1; i >= 0; i--) {
                                if (alist.get(i).x == p.x + 1 && alist.get(i).y == p.y && alist.get(i).firstmove && alist.get(i).type.equals(Pieces.Type.Pawn)) {
                                    av_moves.add(new Coordinate(p.x + 1, p.y + 1));
                                }
                            }
                        }
                    }
                    if (p.x > 0) {
                        if (rboard[p.x - 1][p.y] == 11) {
                            for (int i = alist.size() - 1; i >= 0; i--) {
                                if (alist.get(i).x == p.x - 1 && alist.get(i).y == p.y && alist.get(i).firstmove && alist.get(i).type.equals(Pieces.Type.Pawn)) {
                                    av_moves.add(new Coordinate(p.x - 1, p.y + 1));
                                }
                            }
                        }
                    }
                }
                // Check normal attacks
                if (p.x < 7 && p.y < 7 && rboard[p.x + 1][p.y + 1] / 10 != rboard[p.x][p.y] / 10 && rboard[p.x + 1][p.y + 1] != 0) {
                    av_moves.add(new Coordinate(p.x + 1, p.y + 1));
                }
                if (p.x > 0 && p.y < 7 && rboard[p.x - 1][p.y + 1] / 10 != rboard[p.x][p.y] / 10 && rboard[p.x - 1][p.y + 1] != 0) {
                    av_moves.add(new Coordinate(p.x - 1, p.y + 1));
                }
            } else { // Black pawn
                if (p.y == 6) {
                    if (rboard[p.x][p.y - 1] == 0) {
                        av_moves.add(new Coordinate(p.x, p.y - 1));
                        if (rboard[p.x][p.y - 2] == 0) {
                            av_moves.add(new Coordinate(p.x, p.y - 2));
                        }
                    }
                } else if (p.y > 0) {
                    if (rboard[p.x][p.y - 1] == 0) {
                        av_moves.add(new Coordinate(p.x, p.y - 1));
                    }
                }
                // En passant Moves
                if (p.y == 3) {
                    if (p.x < 7) {
                        if (rboard[p.x + 1][p.y] == 1) {
                            for (int i = alist.size() - 1; i >= 0; i--) {
                                if (alist.get(i).x == p.x + 1 && alist.get(i).y == p.y && alist.get(i).firstmove && alist.get(i).type.equals(Pieces.Type.Pawn)) {
                                    av_moves.add(new Coordinate(p.x + 1, p.y - 1));
                                }
                            }
                        }
                    }
                    if (p.x > 0) {
                        if (rboard[p.x - 1][p.y] == 1) {
                            for (int i = alist.size() - 1; i >= 0; i--) {
                                if (alist.get(i).x == p.x - 1 && alist.get(i).y == p.y && alist.get(i).firstmove && alist.get(i).type.equals(Pieces.Type.Pawn)) {
                                    av_moves.add(new Coordinate(p.x - 1, p.y - 1));
                                }
                            }
                        }
                    }
                }
                // Check taking moves
                if (p.x < 7 && p.y > 0 && rboard[p.x + 1][p.y - 1] / 10 != rboard[p.x][p.y] / 10 && rboard[p.x + 1][p.y - 1] != 0) {
                    av_moves.add(new Coordinate(p.x + 1, p.y - 1));
                }
                if (p.x > 0 && p.y > 0 && rboard[p.x - 1][p.y - 1] / 10 != rboard[p.x][p.y] / 10 && rboard[p.x - 1][p.y - 1] != 0) {
                    av_moves.add(new Coordinate(p.x - 1, p.y - 1));
                }
            }
        }
        return av_moves;
    }

    /**
     * A fully OOB-fied move that actually modifies the input parameter (kinda) to make a move
     * @param p
     * @param newcoord
     * @param chesspieces
     * @param referenceboard
     */    
    public void move(Pieces p, Coordinate newcoord, ArrayList<Pieces> chesspieces, int [][] referenceboard) {
        if (p.type.equals(Pieces.Type.King)) {
            checkCastling();
            p.firstmove = true;
            if (p.side) {
                if (wcl && newcoord.x == 2) {
                    for (int i = chesspieces.size() - 1; i >= 0; i--) {
                        if (chesspieces.get(i).x == 0 && chesspieces.get(i).y == 0 && chesspieces.get(i).type.equals(Pieces.Type.Rook) && chesspieces.get(i).side && !chesspieces.get(i).firstmove) {
                            chesspieces.get(i).x = 3;
                            referenceboard[0][0] = 0;
                            referenceboard[3][0] = 4;
                        }
                    }
                }
                if (wcr && newcoord.x == 6) {
                    for (int i = chesspieces.size() - 1; i >= 0; i--) {
                        if (chesspieces.get(i).x == 7 && chesspieces.get(i).y == 0 && chesspieces.get(i).type.equals(Pieces.Type.Rook) && chesspieces.get(i).side && !chesspieces.get(i).firstmove) {
                            chesspieces.get(i).x = 5;
                            referenceboard[7][0] = 0;
                            referenceboard[5][0] = 4;
                        }
                    }
                }
            }
            if (!p.side) {
                if (bcl && newcoord.x == 2) {
                    for (int i = chesspieces.size() - 1; i >= 0; i--) {
                        if (chesspieces.get(i).x == 0 && chesspieces.get(i).y == 7 && chesspieces.get(i).type.equals(Pieces.Type.Rook) && !chesspieces.get(i).side && !chesspieces.get(i).firstmove) {
                            chesspieces.get(i).x = 3;
                            referenceboard[0][7] = 0;
                            referenceboard[3][7] = 14;
                        }
                    }
                }
                if (bcr && newcoord.x == 6) {
                    for (int i = chesspieces.size() - 1; i >= 0; i--) {
                        if (chesspieces.get(i).x == 7 && chesspieces.get(i).y == 7 && chesspieces.get(i).type.equals(Pieces.Type.Rook) && !chesspieces.get(i).side && !chesspieces.get(i).firstmove) {
                            chesspieces.get(i).x = 5;
                            referenceboard[7][7] = 0;
                            referenceboard[5][7] = 14;
                        }
                    }
                }
            }
            referenceboard[p.x][p.y] = 0;
            p.x = newcoord.x;
            p.y = newcoord.y;
            if (referenceboard[p.x][p.y] != 0) {
                for (int i = chesspieces.size() - 1; i >= 0; i--) {
                    if (chesspieces.get(i).side != p.side && chesspieces.get(i).x == p.x && chesspieces.get(i).y == p.y) {
                        chesspieces.remove(i);
                    }
                }
            }
            if (p.side) referenceboard[p.x][p.y] = 9;
            else referenceboard[p.x][p.y] = 19;

        }
        if (p.type.equals(Pieces.Type.Queen)) {
            p.firstmove = true;
            referenceboard[p.x][p.y] = 0;
            p.x = newcoord.x;
            p.y = newcoord.y;
            if (referenceboard[p.x][p.y] != 0) {
                for (int i = chesspieces.size() - 1; i >= 0; i--) {
                    if (chesspieces.get(i).side != p.side && chesspieces.get(i).x == p.x && chesspieces.get(i).y == p.y) {
                        chesspieces.remove(i);
                    }
                }
            }
            if (p.side) {
                referenceboard[p.x][p.y] = 5;
            } else {
                referenceboard[p.x][p.y] = 15;
            }
        }

        if (p.type.equals(Pieces.Type.Bishop)) {
            p.firstmove = true;
            referenceboard[p.x][p.y] = 0;
            p.x = newcoord.x;
            p.y = newcoord.y;
            if (referenceboard[p.x][p.y] != 0) {
                for (int i = chesspieces.size() - 1; i >= 0; i--) {
                    if (chesspieces.get(i).side != p.side && chesspieces.get(i).x == p.x && chesspieces.get(i).y == p.y) {
                        chesspieces.remove(i);
                    }
                }
            }
            if (p.side) {
                referenceboard[p.x][p.y] = 2;
            } else {
                referenceboard[p.x][p.y] = 12;
            }
        }

        if (p.type.equals(Pieces.Type.Horse)) {
            p.firstmove = true;
            referenceboard[p.x][p.y] = 0;
            p.x = newcoord.x;
            p.y = newcoord.y;
            if (referenceboard[p.x][p.y] != 0) {
                for (int i = chesspieces.size() - 1; i >= 0; i--) {
                    if (chesspieces.get(i).side != p.side && chesspieces.get(i).x == p.x && chesspieces.get(i).y == p.y) {
                        chesspieces.remove(i);
                    }
                }
            }
            if (p.side) {
                referenceboard[p.x][p.y] = 3;
            } else {
                referenceboard[p.x][p.y] = 13;
            }
        }

        if (p.type.equals(Pieces.Type.Rook)) {
            p.firstmove = true;
            if (p.side) {
                if (wcl && newcoord.x == 3) {
                    for (int i = chesspieces.size() - 1; i >= 0; i--) {
                        if (chesspieces.get(i).side && chesspieces.get(i).type.equals(Pieces.Type.King) && !chesspieces.get(i).firstmove) {
                            chesspieces.get(i).x = 2;
                            referenceboard[2][0] = 9;
                            referenceboard[4][0] = 0;
                        }
                    }
                }

                if (wcr && newcoord.x == 5) {
                    for (int i = chesspieces.size() - 1; i >= 0; i--) {
                        if (chesspieces.get(i).side && chesspieces.get(i).type.equals(Pieces.Type.King) && !chesspieces.get(i).firstmove) {
                            chesspieces.get(i).x = 6;
                            referenceboard[6][0] = 9;
                            referenceboard[4][0] = 0;
                        }
                    }
                }
            } else {
                if (bcl && newcoord.x == 3) {
                    for (int i = chesspieces.size() - 1; i >= 0; i--) {
                        if (!chesspieces.get(i).side && chesspieces.get(i).type.equals(Pieces.Type.King) && !chesspieces.get(i).firstmove) {
                            chesspieces.get(i).x = 2;
                            referenceboard[2][7] = 19;
                            referenceboard[4][7] = 0;
                        }
                    }
                }

                if (bcr && newcoord.x == 5) {
                    for (int i = chesspieces.size() - 1; i >= 0; i--) {
                        if (!chesspieces.get(i).side && chesspieces.get(i).type.equals(Pieces.Type.King) && !chesspieces.get(i).firstmove) {
                            chesspieces.get(i).x = 6;
                            referenceboard[6][7] = 19;
                            referenceboard[4][7] = 0;
                        }
                    }
                }
            }
            referenceboard[p.x][p.y] = 0;
            p.x = newcoord.x;
            p.y = newcoord.y;
            if (referenceboard[p.x][p.y] != 0) {
                for (int i = chesspieces.size() - 1; i >= 0; i--) {
                    if (chesspieces.get(i).side != p.side && chesspieces.get(i).x == p.x && chesspieces.get(i).y == p.y) {
                        chesspieces.remove(i);
                    }
                }
            }
            if (p.side) {
                referenceboard[p.x][p.y] = 4;
            } else {
                referenceboard[p.x][p.y] = 14;
            }
        }
        if (p.type.equals(Pieces.Type.Pawn)) {
            if (p.side) {
                if (p.y == 1 && newcoord.y == 3) {
                    p.firstmove = true;
                } else {
                    p.firstmove = false;
                }
                if (p.y == 4) {
                    if (newcoord.x == p.x + 1 && referenceboard[p.x + 1][p.y + 1] == 0) {
                        if (referenceboard[p.x + 1][p.y] == 11) {
                            //Attack to left
                            for (int i = chesspieces.size() - 1; i >= 0; i--) {
                                if (chesspieces.get(i).x == p.x + 1 && chesspieces.get(i).y == p.y && chesspieces.get(i).firstmove) {
                                    chesspieces.remove(i);
                                    referenceboard[p.x + 1][p.y] = 0;
                                }
                            }
                        }
                    } else if (newcoord.x == p.x - 1 && referenceboard[p.x - 1][p.y + 1] == 0) {
                        if (referenceboard[p.x - 1][p.y] == 11) {
                            //Attack to left
                            for (int i = chesspieces.size() - 1; i >= 0; i--) {
                                if (chesspieces.get(i).x == p.x - 1 && chesspieces.get(i).y == p.y && chesspieces.get(i).firstmove) {
                                    chesspieces.remove(i);
                                    referenceboard[p.x - 1][p.y] = 0;
                                }
                            }
                        }
                    }
                }
            } else {
                if (p.y == 6 && newcoord.y == 4) {
                    p.firstmove = true;
                } else {
                    p.firstmove = false;
                }
                if (p.y == 3) {
                    if (newcoord.x == p.x + 1 && referenceboard[p.x + 1][p.y - 1] == 0) {
                        if (referenceboard[p.x + 1][p.y] == 1) {
                            for (int i = chesspieces.size() - 1; i >= 0; i--) {
                                if (chesspieces.get(i).x == p.x + 1 && chesspieces.get(i).y == p.y && chesspieces.get(i).firstmove) {
                                    chesspieces.remove(i);
                                    referenceboard[p.x + 1][p.y] = 0;
                                }
                            }
                        }
                    } else if (newcoord.x == p.x - 1 && referenceboard[p.x - 1][p.y - 1] == 0) {
                        if (referenceboard[p.x - 1][p.y] == 1) {
                            for (int i = chesspieces.size() - 1; i >= 0; i--) {
                                if (chesspieces.get(i).x == p.x - 1 && chesspieces.get(i).y == p.y && chesspieces.get(i).firstmove) {
                                    chesspieces.remove(i);
                                    referenceboard[p.x - 1][p.y] = 0;
                                }
                            }
                        }
                    }
                }
            }
            referenceboard[p.x][p.y] = 0;
            p.x = newcoord.x;
            p.y = newcoord.y;
            //Update chesspieces to remove piece
            if (referenceboard[p.x][p.y] != 0) {
                for (int i = chesspieces.size() - 1; i >= 0; i--) {
                    if (chesspieces.get(i).side != p.side && chesspieces.get(i).x == p.x && chesspieces.get(i).y == p.y) {
                        chesspieces.remove(i);
                    }
                }
            }
            for (int i = chesspieces.size() - 1; i >= 0; i--) {
                if (chesspieces.get(i).type == Pieces.Type.Pawn && chesspieces.get(i) != p && chesspieces.get(i).firstmove) {
                    chesspieces.get(i).firstmove = false;
                }
            }
            if (p.side && p.y == 7) {
                pawntotransform = p;
                changep = true;
            } else if (!p.side && p.y == 0) {
                pawntotransform = p;
                changep = true;
            }

            //Update referenceboard
            if (p.side) {
                referenceboard[p.x][p.y] = 1;
            } else {
                referenceboard[p.x][p.y] = 11;
            }
        }
        Initiative = !Initiative;
    }

    /**
     * Get all possible moves for a specific side, returns an arraylist of moves
     * @param alist
     * @param rboard
     * @param side
     * @return 
     */
    public ArrayList<Move> allMoves(ArrayList<Pieces> alist, int rboard[][], boolean side){
        ArrayList<Move> allmoves = new ArrayList<Move>();
        for (int i=0; i<alist.size(); i++){
            if (alist.get(i).side == side){ //If same side
                ArrayList<Coordinate> moveofpiece = av_move(alist, rboard, alist.get(i));
                for (int j=0; j<moveofpiece.size(); j++){
                    allmoves.add(new Move(alist.get(i),moveofpiece.get(j)));
                }
            }
        }
        return allmoves;
    }

    @Override
    public void draw() {
        parent.fill(DualColours.getColour(3));
        if (Initiative) parent.text("White's Turn", 600, 69);
        else parent.text("Black's Turn", 600, 69);

        if (changep) {
            parent.textSize(26);
            parent.text("Please enter a \ncharacter for \npawn transformation", 600, 150);
            parent.text("Q for queen;\nK for knight;\nB for bishop;\nR for rook.", 600, 300);
            parent.textSize(21);
        }

        ArrayList<Coordinate> ap = av_move(chesspieces, referenceboard, selected);
        for (int i = ap.size() - 1; i >= 0; i--) {
            if (!isLegal(selected, ap.get(i).x, ap.get(i).y)) {
                ap.remove(i);
            }
        }
        parent.pushStyle();
        int index;

        parent.strokeWeight(1);
        parent.rectMode(PConstants.CORNER);

        // draw backing
        parent.noStroke();
        parent.fill(DualColours.getColour(4));
        parent.rect(this.position.x - spacing, this.position.y - spacing, 8 * combinedSpace + spacing, 8 * combinedSpace + spacing);

        // draw border around each box
        parent.strokeWeight(spacing / 2 + 1);
        parent.noFill();
        parent.stroke(DualColours.getColour(1));
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int boxX = this.position.x - (spacing / 2) + x * combinedSpace - 1;
                int boxY = this.position.y - (spacing / 2) + y * combinedSpace - 1;
                parent.rect(boxX, boxY, combinedSpace + 1, combinedSpace);
            }
        }
        parent.noStroke();
        // draw each cell
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                index = -1;
                int cellX = this.position.x + x * combinedSpace;
                int cellY = this.position.y + y * combinedSpace;
                for (int i = 0; i < chesspieces.size(); i++) {
                    if (x == chesspieces.get(i).x && 7 - y == chesspieces.get(i).y) {
                        index = i;
                        break;
                    }
                }
                // cell backing
                Coordinate tp = new Coordinate(x, 7 - y);
                //boolean
                parent.fill(DualColours.getColour(4));
                if (ap.contains(tp)) {
                    //Legal move
                    parent.fill(0xFF0000FF);
                } else if (index != -1) {
                    parent.fill(chesspieces.get(index).CellStatus.getColour());
                }
                parent.rect(cellX, cellY, sideLength, sideLength);

                // draw regular pieces
                //  parent.textFont(parent.createFont("Consolas", 30, true));
                if (referenceboard[x][7 - y] % 10 != 0) {
                    parent.image(drawpieces.get(referenceboard[x][7 - y]), cellX, cellY);
                }
            }
        }
        parent.popStyle();
    }
}
