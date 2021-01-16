package main.java.chess;

import main.java.chess.states.GameEngine;
import main.java.chess.states.StateMain;
import main.java.chess.util.Input;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class MainClass extends PApplet {

    private GameEngine gameEngine;

    public static void main(String[] args) {
        //System.out.println("abolish turing");
        PApplet.main("main.java.chess.MainClass");
    }

    @Override
    public void settings() {
        size(900, 600);//Dimensions of the run window
    }

    @Override
    public void setup() {
        Input.parent = this;

        gameEngine = GameEngine.getInstance();
        gameEngine.parent = this;
        gameEngine.start();
        gameEngine.changeState(StateMain.getInstance());
    
        textFont(createFont("Consolas", 30, true));
    }

    @Override
    public void draw() {
        Input.updateInput();
       // System.out.println(Input.getMousePosition());
        if (gameEngine.running) {
            gameEngine.update();
            gameEngine.draw();
        } else {
            gameEngine.end();
            exit();
        }
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        Input.setScroll(event.getCount());
    }
    
    @Override
    public void keyPressed(KeyEvent event) {
        //System.out.println(event.getKeyCode());
        Input.setKey(event.getKey());
    }
}
