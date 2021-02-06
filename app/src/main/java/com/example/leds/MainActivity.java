package com.example.leds;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import top.defaults.colorpicker.ColorPickerView;

public class MainActivity extends AppCompatActivity {

    //initiate the buttons
    private Button secondColor;
    private Button firstColor;

    //initiate the variables for the chosen colors
    private int secondButtonColor;
    private int firstButtonColor;

    private Boolean firstColorBool = true;
    private ClassLoader ClassLoaderUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find buttons by their id
        secondColor = findViewById(R.id.secondColorButton);
        firstColor = findViewById(R.id.firstColorButton);

        //Find elements for setting and color by their id
        ColorPickerView colorPickerView = findViewById(R.id.colorPicker);
        Spinner setting = findViewById(R.id.setting);

        //set standard background on buttons
        secondColor.setBackgroundColor(Color.GRAY);
        firstColor.setBackgroundColor(Color.GRAY);

        //set listener for setting
        setting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String chosenSetting = setting.getItemAtPosition(position).toString();
                sendRequest(colorCode(firstButtonColor), chosenSetting);
            }
            public void onNothingSelected(AdapterView<?> parent) {    }
        });
        //subscribe to changes of the chosen color
        colorPickerView.subscribe((color, fromUser, shouldPropagate) -> {
            String chosenSetting = setting.getSelectedItem().toString();
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            setColor(color, hexColor);
            sendRequest(colorCode(firstButtonColor), chosenSetting);
        });
    }

    /**
     * Sets color based on which button is selected
     *
     * @param color : int
     * @param hexColor : String
     *
     * @return Nothing
     */
    private void setColor(int color, String hexColor){
        if (firstColorBool){
            firstColor.setBackgroundColor(rgb(hexColor));
            firstButtonColor = color;
        }else{
            secondColor.setBackgroundColor(rgb(hexColor));
            secondButtonColor = color;
        }
    }

    /**
     * Coverts color in int form to int array form
     * color composition are Blue,Green,Red
     *
     * @param color : int
     *
     * @return converted color : int[]
     */
    private int[] colorCode(int color){
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
    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color) & 0xFF;
        return Color.rgb(r, g, b);
    }

    /**
     * Calls buildUrl() and with given url parses url to the HTTPHandler
     *
     *
     * @param colorCode : int[]
     * @param setting : String
     *
     * @return Nothing
     */
    private void sendRequest(int [] colorCode, String setting){
        String builded = buildUrl(colorCode, setting);
        new HTTPHandler().execute(builded);
    }

    /**
     * Builds url with given setting and colorcode
     * Example: http://{IP_ADRESS}/{SETTING}/{FIRSTCOLOR}/{SECONDCOLOR}
     *
     * @param colorCode : int[]
     * @param setting = String
     *
     * @return Url : String
     */
    private String buildUrl (int [] colorCode, String setting){
        String uri = String.format("%s/%s/%s" ,setting , arrayToString(colorCode), arrayToString(colorCode(secondButtonColor)));
        return String.format("http://%s:5000/%s", getResource(), uri);
    }



    /**
     * Converts array to string
     *
     * @param list : int[]
     *
     * @return String of array : String
     */
    private String arrayToString(int[] list){
        return Arrays.toString(list).replace("[","").replace("]","").replace(", ",",");
    }

    /**
     * Get the ip addres from assets/config.txt
     *
     * @return ip : String
     */
    String getResource() {
        String read = null;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open("config.txt"), StandardCharsets.UTF_8))) {
            read = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return read;
    }

    /*
     * controls which color should be changed
     */
    public void setSecondColorOn(View view){ this.firstColorBool = false; }
    public void setSecondColorOff(View view){ this.firstColorBool = true; }

}