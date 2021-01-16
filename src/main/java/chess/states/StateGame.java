package main.java.chess.states;

import main.java.chess.Board;
import main.java.chess.util.Button;
import main.java.chess.util.DualColours;

import main.java.chess.util.Coordinate;
import processing.core.PApplet;
import processing.core.PConstants;

public class StateGame extends GameState {//The actual state of the current game
    
    private static GameState instance;
    private Button quitButton;
    private Board gameboard;
    static boolean whiteToMove = true; //true is white; false is black
    static boolean wcl=false;
    static boolean wcr=false;
    static boolean bcl=false;
    static boolean bcr=false;
    static boolean wch=false;
    static boolean bch=false;


    private StateGame(PApplet parent) {
        super(parent);
    }

    /**
     * Gets the singleton instance of this GameState
     *
     * @return the instance of this GameState
     */
    public static GameState getInstance() {
        if (instance == null) instance = new StateGame(GameEngine.getInstance().parent);
        return instance;
    }

    @Override
    public void start() {//Declares stuff
        //digits = new DigitBoard(parent);
        //board = new SudokuBoard(parent);

        //board.digitBoard = digits;
        gameboard = new Board(parent);
        gameboard.setPosition(60, 60);
    
        quitButton = new Button(parent,
                new Coordinate(815, 520), new Coordinate(80, 50), "Menu",//UJML colours
                () -> changeState(StateMain.getInstance())
        );

    }

    @Override
    public void end() {
    }

    @Override
    public void update() {
        gameboard.update();//Should pass selected number in here
        quitButton.update();
        if (gameboard.gg) {
            changeState(StateWin.getInstance());
        }
    }

    @Override
    public void draw() {
        parent.background(DualColours.getText());
        gameboard.draw();
        quitButton.draw();
    }
}
