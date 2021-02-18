 package com.example.leds;

 import android.annotation.SuppressLint;
 import android.content.Context;
 import android.content.Intent;
 import android.graphics.Color;
 import android.os.Bundle;
 import android.view.CollapsibleActionView;
 import android.view.LayoutInflater;
 import android.view.Menu;
 import android.view.MenuInflater;
 import android.view.MenuItem;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.AdapterView;
 import android.widget.Button;
 import android.widget.PopupWindow;
 import android.widget.SeekBar;
 import android.widget.Spinner;
 import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.appcompat.widget.Toolbar;
 import androidx.constraintlayout.widget.ConstraintLayout;

 import com.example.leds.handlers.ColorHandler;
 import com.example.leds.handlers.HTTPHandler;

 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.nio.charset.StandardCharsets;
 import java.util.Arrays;

 import top.defaults.colorpicker.ColorPickerView;

public class MainActivity extends AppCompatActivity  {

    //initiate the buttons
    private Button secondColor;
    private Button firstColor;

    //initiate the variables for the chosen colors
    private int secondButtonColor;
    private int firstButtonColor;

    private String lastUsedLink = "";
    private boolean connection = false;

    private Boolean firstColorBool = true;

    private final ColorHandler led = new ColorHandler();
    private SeekBar brightnessBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                if (chosenSetting.equals("Custom")){
                    showPopup();
                }
                sendRequest(buildUrl(led.colorCode(firstButtonColor), chosenSetting));
            }
            public void onNothingSelected(AdapterView<?> parent) {    }
        });
        //subscribe to changes of the chosen color
        colorPickerView.subscribe((color, fromUser, shouldPropagate) -> {
            String chosenSetting = setting.getSelectedItem().toString();
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            setColor(color, hexColor);
            sendRequest(buildUrl(led.colorCode(firstButtonColor), chosenSetting));
        });
    }

    /**
     * Shows topbar menu
     *
     * @param menu : Menu
     *
     * @return True
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options,menu);

        MenuItem barItem = menu.findItem(R.id.brightness);
        barItem.expandActionView();
        barItem.getActionView().findViewById(R.id.brightness).setMinimumWidth(100);
        SeekBar bar = (SeekBar)barItem.getActionView();
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                System.out.println(progress);
                sendRequest(String.format("http://%s:5000/Brightness/%s", getResource(), progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return true;
    }


    /**
     * Selects which topbar button is pressed and executes appropriate method
     *
     * @param item : MenuItem
     *
     * @return True | False : Bool
     *
     * */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        System.out.println(item);
        switch (item.getItemId()){
            case R.id.turnServerOff:
                sendRequest(String.format("http://%s:5000/shutdown", getResource()));
                return true;
            case R.id.brightness:
//                showBrightnessBar(item);
                return true;
            case R.id.connect:
                onConnectionChange(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Show a bar to control the brightness of ledstrip
     *
     * @param item : MenuItem
     *
     * @return Nothing
     * */
    private void showBrightnessBar(MenuItem item){
        brightnessBar = new SeekBar(this);
        item.setActionView(brightnessBar);
        item.expandActionView();
        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                System.out.println(progress);
                sendRequest(String.format("http://%s:5000/Brightness/%s", getResource(), progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }



    /**
     * Show the popup for custom led controll
     *
     * @return Nothing
     *
     * */
    public void showPopup(){
        LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.popup, findViewById(R.id.sendButton), true);
        new PopupWindow(layout, ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT,true);

        Intent intent = new Intent(this, PopUpActivity.class);
        startActivity(intent);

    }

    /**
     * Set connection on or off
     *
     * @param item : MenuItem
     *
     * @return Nothing
     * */
    private void onConnectionChange( MenuItem item){
        if(connection){
            item.setIcon(R.drawable.ic_menu_toggle_off_filled);
            connection = false;
            Toast.makeText(this,"Connection broken.",Toast.LENGTH_SHORT).show();
        }else {
            item.setIcon(R.drawable.ic_menu_toggle_on_filled);
            connection = true;
            Toast.makeText(this,"Connection made.",Toast.LENGTH_SHORT).show();
        }

        item.setChecked(connection);

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
            firstColor.setBackgroundColor(led.rgb(hexColor));
            firstButtonColor = color;
        }else{
            secondColor.setBackgroundColor(led.rgb(hexColor));
            secondButtonColor = color;
        }
    }

    /**
     * parses given url to the HTTPHandler
     *
     * @param link : String
     *
     * @return Nothing
     */
    private void sendRequest(String link){
        if(connection) {
            if (!link.equals(lastUsedLink)) {
                lastUsedLink = link;
                new HTTPHandler().execute(link);
            }
        }
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
        String uri = String.format("%s/%s/%s" ,setting , arrayToString(colorCode), arrayToString(led.colorCode(secondButtonColor)));
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
    private String getResource() {
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