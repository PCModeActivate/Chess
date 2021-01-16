package main.java.chess.states;

import main.java.chess.util.Button;
import main.java.chess.util.Coordinate;
import main.java.chess.util.DualColours;
import processing.core.PApplet;
import processing.core.PConstants;
import java.time.Duration;
import java.util.ArrayList;

public class StateWin extends GameState {

    private ArrayList<Button> buttons;
    private static GameState instance;
    private static Duration time;
    
    private StateWin(PApplet parent) {
        super(parent);
    }

    /*
          ----  |     |    ----   \          /
        /       |_____|  /      \  \        /
        \       |     |  \      /   \  /\  /
          ----  |     |    ----      \/  \/
    */
    
    /**
     * Gets the singleton instance of this GameState
     *
     * @return the instance of this GameState
     */
    public static GameState getInstance() {
        if (instance == null) {
            instance = new StateWin(GameEngine.getInstance().parent);
        }
        return instance;
    }
    
    @Override
    public void start() {buttons = new ArrayList<>();

        int x = 350;
        int y = 100;

        buttons.add(new Button(
                parent,
                new Coordinate(x, y += 60), new Coordinate(160, 50), "Restart",
                () -> changeState(StateGame.getInstance())
        ));

        buttons.add(new Button(
                parent,
                new Coordinate(x, y += 60), new Coordinate(160, 50), "Menu",
                () -> changeState(StateMain.getInstance())
        ));

        buttons.add(new Button(parent,
                new Coordinate(x, y += 60), new Coordinate(160, 50), "Quit",//UJML colours
                () -> GameEngine.getInstance().exit()
        ));

    }
    
    @Override
    public void end() {
    }
    
    @Override
    public void update() {
        for (Button button : buttons) {
            button.update();
        }
    }
    @Override
    public void draw() {
        parent.fill(DualColours.getColour(1));
        parent.textAlign(PConstants.CENTER, PConstants.CENTER);
        parent.noStroke();
        parent.textSize(50);
    
        parent.text("Checkmate.", 420, 69);
        for (Button button : buttons) {
            button.draw();
        }
        
    }
}
