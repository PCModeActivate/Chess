package main.java.chess.chesspiece;

import main.java.chess.util.DualColours;
import java.util.function.IntSupplier;


public abstract class Pieces {
    public int x, y;
    public boolean side;
    public Enum type;
    public boolean firstmove = false;
    public cellStatus CellStatus = cellStatus.UNSELECTED;
    public Pieces(int b, int c, boolean s, Type t) {
        // side == white, !side == black
        x = b;
        y = c;
        side = s;
        type = t;
    }

    public enum cellStatus {
        UNSELECTED(0x00FFFFFF), SELECTED(DualColours::getSelect), CLICKED(DualColours.violet), CONFLICTED(DualColours.magenta);
        public final IntSupplier supplier;

        cellStatus(IntSupplier supplier) {
            this.supplier = supplier;
        }

        cellStatus(int colour) {
            this.supplier = () -> colour;
        }

        public int getColour() {
            return supplier.getAsInt();
        }
    }
    public enum Type {
        King, Queen, Rook, Bishop, Horse, Pawn
    }
}