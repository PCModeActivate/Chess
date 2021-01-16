package main.java.chess.util;

/**
 * Contains all colours and colour scheme for the game.
 * Based off of the Solarized colour scheme devised by Ethan Schoonover.
 */
public final class DualColours {
    // legit solarized shades, d - dark, l - light
    private static final int baseD3 = 0xFF008000;
    private static final int baseD2 = 0xFFffffff;
    private static final int baseD1 = 0xFF000000;
    private static final int baseD0 = 0xFF000000;//009907
    private static final int baseL0 = 0xFFdc322f;
    private static final int baseL1 = 0xFFffffff;
    private static final int baseL2 = 0xFF000000;
    private static final int baseL3 = 0xFFdaa520;

    // legit solarized colours
    public static final int yellow = 0xFFb58900;
    //    private static final int red = 0xFFdc322f;
    public static final int magenta = 0xFFd33682;
    //public static final int green = 0xFF008000;
    private static final int orange = 0xFFcb4b16;
    public static final int violet = 0xFF6c71c4;
    private static final int blue = 0xFF268bd2;
    private static final int cyan = 0xFF2aa198;

    // non legit solarized shades
    // https://meyerweb.com/eric/tools/color-blend/

    // baseD2 lightened: 2/3 baseD2, 1/3 baseD1
    private static final int baseD2L = 0xFF008000;
    // baseL2 darkened:  2/3 baseL2, 1/3 baseL1
    private static final int baseL2D = 0xFFdaa520;

    // current theme: light or dark
    public static boolean lightTheme;

    /**
     * Gives the pair of colours that is used for writing text.
     * It is based off the colour scheme in general, the light theme to yellow, the dark theme to blue.
     *
     * @return the colour yellow or cyan as integer
     */
    public static int getText() {
        return lightTheme ? baseL2 :  baseD2;
    }

    /**
     * Gives the pair of colours that is used for highlighting cells.
     * It is based off of the fact that they are darker versions than those used for text.
     *
     * @return the colour orange or blue as integer
     */
    public static int getSelect() {
        return lightTheme ? orange : blue;
    }
    // 0 - 3 for main colours, 4 for the hybrid colour

    /**
     * Returns a colour from the scheme based on the currently selected theme and the numeric ID passed in.
     *
     * @param ID determines which colour pair to choose from
     * @return a colour as an integer
     */
    public static int getColour(int ID) {
        switch (ID) {
            case 0:
                return lightTheme ? baseL0 : baseD0;//Board edges
            case 1:
                return lightTheme ? baseL1 : baseD1;//Select box in main, cell edges
            case 2:
                return lightTheme ? baseL2 : baseD2;//Is background
            case 3:
                return lightTheme ? baseL3 : baseD3;//Cell background
            case 4:
                return lightTheme ? baseL2D : baseD2L;//Cell dividing colour
        }
        // compiler requires it
        return -1;
    }
}
