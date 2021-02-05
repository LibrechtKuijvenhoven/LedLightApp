package com.example.leds;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.io.IOException;
import java.util.Arrays;

import top.defaults.colorpicker.ColorPickerView;

public class MainActivity extends AppCompatActivity {
    private final String ip = "192.168.1.229";

    private Button secondColor;
    private Button firstColor;
    private int secondButtonColor;
    private int firstButtonColor;
    private Boolean firstColorBool = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        secondColor = (Button) findViewById(R.id.secondColorButton);
        firstColor = (Button) findViewById(R.id.firstColorButton);
        secondColor.setBackgroundColor(Color.GRAY);
        firstColor.setBackgroundColor(Color.GRAY);
        change();
    }

    void change(){
        ColorPickerView colorPickerView = (ColorPickerView) findViewById(R.id.colorPicker);
        Spinner setting = (Spinner) findViewById(R.id.setting);
        setting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String chosenSetting = setting.getItemAtPosition(position).toString();
                try {
                    sendRequest(colorCode(colorPickerView.getColor()) , chosenSetting);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        colorPickerView.subscribe((color, fromUser, shouldPropagate) -> {
            String chosenSetting = setting.getSelectedItem().toString();
            if (firstColorBool){
                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                firstColor.setBackgroundColor(rgb(hexColor));
                firstButtonColor = color;
            }else{
                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                secondColor.setBackgroundColor(rgb(hexColor));
                secondButtonColor = color;
            }
            try {
                sendRequest(colorCode(firstButtonColor), chosenSetting);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    private int[] colorCode(int color){
        return new int[]{Color.blue(color), Color.green(color), Color.red(color)};
    }

    public static int rgb(String hex) {
        int color = (int) Long.parseLong(hex.replace("#", ""), 16);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        return Color.rgb(r, g, b);
    }
    private void sendRequest(int [] colorCode, String setting) throws IOException {
        System.out.println(Arrays.toString(colorCode) + " ---" + setting);
        String builded = buildUrl(colorCode, setting);
        new MakeRequest().execute(builded);

    }
    private String buildUrl (int [] colorCode, String setting){
        String uri = String.format("%s/%s/%s" ,setting , arrayToString(colorCode), arrayToString(colorCode(secondButtonColor)));;
        return String.format("http://%s:5000/%s", this.ip, uri);
    }
    private String arrayToString(int[] list){
        return Arrays.toString(list).replace("[","").replace("]","").replace(", ",",");
    }

    public void setSecondColorOn(View view){ this.firstColorBool = false; }
    public void setSecondColorOff(View view){ this.firstColorBool = true; }


}