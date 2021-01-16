package main.java.chess.states;

import main.java.chess.util.Button;
import main.java.chess.util.Coordinate;
import main.java.chess.util.DualColours;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.util.ArrayList;

public class StateMain extends GameState {

    private static GameState instance;
    private ArrayList<Button> buttons;
    PImage left;
    PImage right;

    private StateMain(PApplet parent) {
        super(parent);
    }

    /**
     * Gets the singleton instance of this GameState
     *
     * @return the instance of this GameState
     */
    public static GameState getInstance() {
        if (instance == null) instance = new StateMain(GameEngine.getInstance().parent);
        return instance;
    }

    @Override
    public void start() {
        buttons = new ArrayList<>();

        int x = 360;
        int y = 100;

        buttons.add(new Button(parent, new Coordinate(x, y += 60), new Coordinate(180, 50), "Start", () -> changeState(StateGame.getInstance())));

        buttons.add(new Button(parent, new Coordinate(x, y += 60), new Coordinate(180, 50), "Instructions", () -> changeState(StateInstruction.getInstance())));

        buttons.add(new Button(parent, new Coordinate(x, y += 60), new Coordinate(180, 50), "Theme", () -> {
            DualColours.lightTheme = !DualColours.lightTheme;
            // reload current state
            changeState(getInstance());
        }));

        buttons.add(new Button(parent, new Coordinate(x, y += 60), new Coordinate(180, 50), "Quit",//UJML colours
                () -> GameEngine.getInstance().exit()));
        // load the logo based on the theme
        left = parent.loadImage((DualColours.lightTheme ? "chessyellow.png" : "chessgreen.png"));
        left.resize(left.width / 2, left.height / 2);
        right = parent.loadImage((DualColours.lightTheme ? "chessgreenR.png" : "chessred.png"));
        right.resize(right.width / 2, right.height / 2);
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
        //  parent.image(logo, 70, parent.height / 2 - logo.height / 2);
        parent.image(left, 50, parent.height / 2 - right.height / 2);
        parent.image(right, 550, parent.height / 2 - right.height / 2);
        parent.textSize(80);
        parent.fill(DualColours.getColour(3));
        parent.textAlign(PConstants.LEFT, PConstants.CENTER);
        parent.text("CHESS", parent.width / 2 - 100, 69);
        parent.textSize(26);
        parent.text("By Timothy Chan \n and Robert Cai", parent.width / 2 - 100, 500);
        parent.textAlign(PConstants.LEFT);
        for (Button button : buttons) {
            button.draw();
        }
    }

}
