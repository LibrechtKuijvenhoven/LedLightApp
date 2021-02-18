package com.example.leds;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.leds.handlers.ColorHandler;

import top.defaults.colorpicker.ColorPickerView;

public class PopUpActivity extends AppCompatActivity {
/*****
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *                                                             *
 *  THIS ACTIVITY FOR THE POP UP DOES NOT WORK YET.            *
 *                                                             *
 *  STATUS: PUT ASIDE FOR NOW                                  *
 *                                                             *
 *                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *****/
    private ColorHandler led;
    public  ColorPickerView popUpColorPickerView;
    public  Button popUpchosenButton;
    private View popUpColorBar;
    private Paint colorBar;



    @SuppressLint("ClickableViewAccessibility")
    private View.OnTouchListener handleTouch = (v, event) -> {

        int x = (int) event.getX();

        return true;
    };
    protected int assembleColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.WHITE, hsv);
        hsv[2] = 1f;
        return Color.HSVToColor(hsv);
    }

    @Override
    public void onCreate(Bundle savedInstanseState){
        super.onCreate(savedInstanseState);
        setContentView(R.layout.popup);

        popUpColorPickerView = findViewById(R.id.popUpColorPicker);
        popUpchosenButton = findViewById(R.id.popUpPickedColor);
        popUpColorBar = findViewById(R.id.popUpBar);
        popUpColorBar.setOnTouchListener(handleTouch);
        colorBar = new Paint(Paint.ANTI_ALIAS_FLAG);

        System.out.println(assembleColor());

        popUpColorPickerView.subscribe((color, fromUser, shouldPropagate) -> {
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            popUpchosenButton.setBackgroundColor(led.rgb(hexColor));
        });
    }

//    protected void configurePaint(Paint colorPaint) {
//        float[] hsv = new float[3];
//        Color.colorToHSV(Color.WHITE, hsv);
//        hsv[2] = 0;
//        int startColor = Color.HSVToColor(hsv);
//        hsv[2] = 1;
//        int endColor = Color.HSVToColor(hsv);
//        Shader shader = new LinearGradient(0, 0, getWidth(), getHeight(), startColor, endColor, Shader.TileMode.CLAMP);
//        colorPaint.setShader(shader);
//    }






}
