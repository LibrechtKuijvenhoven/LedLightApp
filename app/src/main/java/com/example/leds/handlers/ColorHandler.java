package com.example.leds.handlers;

import android.graphics.Color;

public class ColorHandler {
    /**
     * Coverts color in int form to int array form
     * color composition are Blue,Green,Red
     *
     * @param color : int
     *
     * @return converted color : int[]
     */
    public int[] colorCode(int color){
        return new int[]{Color.blue(color), Color.green(color), Color.red(color)};
    }

    /**
     * Coverts color in string form to int hex
     * color composition are Red,Green,Blue
     *
     * @param hex : String
     *
     * @return converted color : int
     */
    public int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;
        return Color.rgb(r, g, b);
    }
}
