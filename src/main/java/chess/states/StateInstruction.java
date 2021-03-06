package main.java.chess.states;

import main.java.chess.util.Button;
import main.java.chess.util.Coordinate;
import main.java.chess.util.DualColours;
import processing.core.PApplet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class StateInstruction extends GameState {
    
    private static GameState instance;
    private Button quitButton;
    
    private ArrayList<String> instructions;
    
    private StateInstruction(PApplet parent) {
        super(parent);
    }
    
    /**
     * Gets the singleton instance of this GameState
     *
     * @return the instance of this GameState
     */
    public static GameState getInstance() {
        if (instance == null) {
            instance = new StateInstruction(GameEngine.getInstance().parent);
        }
        return instance;
    }
    
    @Override
    public void start() {
        quitButton = new Button(parent,
                new Coordinate(815, 520), new Coordinate(80, 50), "Menu",//UJML colours
                () -> changeState(StateMain.getInstance())
        );
    
        instructions = new ArrayList<>();
        // read the instructions file
        try {
            // open a file stream to the file of interest
            BufferedReader br = new BufferedReader(parent.createReader("instruct.txt"));
            String line;
            // while there are lines to read, add lines to the list
            while ((line = br.readLine()) != null)
                instructions.add(line);
    
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file");
        } catch (IOException ex) {
            System.out.println("Error reading file");
        }
    }
    
    
    @Override
    public void end() {
    
    }
    
    @Override
    public void update() {
        quitButton.update();
    }
    
    @Override
    public void draw() {
        parent.fill(DualColours.getText());
        parent.rect(0, 0, 900, 600);
        
        quitButton.draw();
        
        //put the instructions here by using text file streaming
        parent.fill(DualColours.getColour(3));
        parent.textSize(14);
        
        int y = 50;
        for (String line : instructions) {
            parent.text(line, 50, y);
            y += 20;
        }
    }
}
