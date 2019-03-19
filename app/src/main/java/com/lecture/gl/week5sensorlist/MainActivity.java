package com.lecture.gl.week5sensorlist;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener,
        SensorEventListener {

    public static String TAG_NAME="Main";
    public static int MULTIPLE_CHOICE =4;
    private HashMap<String, String> dict=null;
    private ArrayList<String> arrayList;
    private  ListView lst;
    private TextView txt_theWord;
    private String theWord;

    private TextView txt_sensor;

    private SensorManager sensorManager;

    ArrayList<String> definitions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG_NAME, "OnCreate called");

        sensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        lst = findViewById(R.id.lst_answers);
        txt_theWord = findViewById(R.id.the_word);
        txt_sensor = findViewById(R.id.txt_sensor);

        readContentOfTheFile();
        arrayList = new ArrayList<>(dict.keySet());
        /*ArrayList<String> myArray = new ArrayList<>();

        myArray.add("Galaxy S9");
        myArray.add("Galaxy S8");
        myArray.add("Galaxy S7");
        myArray.add("Galaxy On Prime 7");
        myArray.add("Huawei Mate 10");

        arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                arrayList);
        lst.setAdapter(arrayAdapter);*/
        lst.setOnItemClickListener(this);

        askQuestion();

    }



    private void readContentOfTheFile(){
        if(dict == null)
            dict = new HashMap<>();
        Scanner scanner = new Scanner(getResources().openRawResource(R.raw.dict));
        while(scanner.hasNext()){
            String line = scanner.nextLine();
            String[] parsed = line.split(";");
            if(parsed.length >=2){
                dict.put(parsed[0], parsed[1]);
            }
        }
        scanner.close();

        //read user words
        try {
            Scanner userScan = new Scanner(openFileInput(AddWordActivity.USER_DICTIONARY));
            while(userScan.hasNext()){
                String line = userScan.nextLine();
                String[] parsed = line.split(";");
                if(parsed.length >=2){
                    dict.put(parsed[0], parsed[1]);
                }
            }
            userScan.close();
        }
        catch (Exception e){
            Log.e("Main", e.getLocalizedMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG_NAME, "OnStart called");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.i(TAG_NAME, "OnSaveInstance called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG_NAME, "OnPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG_NAME, "OnStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG_NAME, "OnDestroy called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG_NAME, "OnResume called");
        Intent the_incoming_intent = getIntent();
        String the_word = the_incoming_intent.getStringExtra("word");
        String the_defn = the_incoming_intent.getStringExtra("defn");
        if(the_defn == null || the_word == null) return;
        Toast.makeText(this, the_word +" "+ the_defn+ " is added", Toast.LENGTH_SHORT).show();

    }

    private void askQuestion(){
        Collections.shuffle(arrayList);
        theWord = arrayList.get(0);

        txt_theWord.setText(theWord);

        definitions.clear();
        for(int i=0;i< MULTIPLE_CHOICE; i++){
            definitions.add(dict.get(arrayList.get(i)));
        }

        Collections.shuffle(definitions);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.my_list_view,
                R.id.txt_content,
                definitions);

        lst.setAdapter(adapter);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String user_answer = lst.getItemAtPosition(position).toString();
        if(dict.get(theWord).equals(user_answer)){
            //correct
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
        }
        else{
            //Wrong answer
            Toast.makeText(this, "Wrong Answer. Study more.", Toast.LENGTH_SHORT).show();
        }
        askQuestion();



       /*definitions.remove(position);
       ArrayAdapter<String> adapter = (ArrayAdapter<String>)lst.getAdapter();
       adapter.notifyDataSetChanged();
       */


    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType()== Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            float y = event.values[1];//y axis
            float z = event.values[2];

            txt_sensor.setText("x: " + x + ", y: " + y + ", z:" +z);

        }

    }
    /*
    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }
    */

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private static int REQ_ID = 1025;
    public void switchtowordactivity(View view) {
        Intent intent =new Intent(this, AddWordActivity.class);
        startActivityForResult(intent, REQ_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_ID){
            if(resultCode == RESULT_OK){
                if(data !=null){
                    String word= data.getStringExtra("word");
                    String defn= data.getStringExtra("defn");
                    Toast.makeText(this, word +" " +defn,Toast.LENGTH_SHORT).show();
                    Log.d(TAG_NAME, word +" " +defn);
                }
            }
        }
    }

    //update later for sensor events. Change this --> new SensorAcc()
    class SensorAcc implements  SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
