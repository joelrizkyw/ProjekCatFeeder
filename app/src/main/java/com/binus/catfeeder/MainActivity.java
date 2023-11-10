package com.binus.catfeeder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.binus.catfeeder.API.BlynkApi;
import com.binus.catfeeder.Data.VirtualPin;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    private TextView txtTimeSchedule1;
    private Button btnSetSchedule1;
    private Button btnEditSchedule1;
    private Button btnDeleteSchedule1;

    private TextView txtTimeSchedule2;
    private Button btnSetSchedule2;
    private Button btnEditSchedule2;
    private Button btnDeleteSchedule2;

    private Spinner spinnerServo;
    private Button btnSetSpinner;

    private BlynkApi blynkApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] spinnerValues = new String[] {"-- Select Number of Spin --","1", "2", "3"};

        txtTimeSchedule1 = findViewById(R.id.txtTimeSchedule1);
        btnSetSchedule1 = findViewById(R.id.btnSetSchedule1);
        btnEditSchedule1 = findViewById(R.id.btnEditSchedule1);
        btnDeleteSchedule1 = findViewById(R.id.btnDeleteSchedule1);

        txtTimeSchedule2 = findViewById(R.id.txtTimeSchedule2);
        btnSetSchedule2 = findViewById(R.id.btnSetSchedule2);
        btnEditSchedule2 = findViewById(R.id.btnEditSchedule2);
        btnDeleteSchedule2 = findViewById(R.id.btnDeleteSchedule2);

        spinnerServo = findViewById(R.id.spinnerServo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                spinnerValues);
        spinnerServo.setAdapter(adapter);

        btnSetSpinner = findViewById(R.id.btnSetSpinner);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://blynk.cloud/external/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        blynkApi = retrofit.create(BlynkApi.class);

        loadBlynkData();

        btnSetSchedule1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTimeSchedule1();
            }
        });

        btnDeleteSchedule1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearTimeSchedule1();
                // txtTimeSchedule1.setText("No Time Set");
            }
        });

        btnSetSchedule2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTimeSchedule2();
            }
        });

        btnDeleteSchedule2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearTimeSchedule2();
                // txtTimeSchedule2.setText("No Time Set");
            }
        });

        btnSetSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selectedItem = spinnerServo.getSelectedItem().toString();

                String data = selectedItem.equals("-- Select Number of Spin --")
                        ? "NULL" : selectedItem;

                sendDataToBlynk(data, "v2");

                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBlynkData() {

        Call<VirtualPin> call =
                blynkApi.getVirtualPins(BuildConfig.BLYNK_TOKEN);

        call.enqueue(new Callback<VirtualPin>() {
            @Override
            public void onResponse(Call<VirtualPin> call, Response<VirtualPin> response) {

                if (!response.isSuccessful()) {

                    System.out.println("Retrofit Response: response TIDAK SUKSES");
                    return;
                }

                VirtualPin vp = response.body();

                String v0 = vp.getTimeSchedule1();
                String v1 = vp.getTimeSchedule2();
                String v2 = vp.getNumOfSpin();

                if (v0.equalsIgnoreCase("NULL")) {

                    txtTimeSchedule1.setText("No Time Set");
                }

                else {

                    txtTimeSchedule1.setText(v0);
                }

                if (v1.equalsIgnoreCase("NULL")) {

                    txtTimeSchedule2.setText("No Time Set");
                }

                else {

                    txtTimeSchedule2.setText(v1);
                }

                if (v2.equalsIgnoreCase("NULL")) {

                    spinnerServo.setSelection(0);
                }

                else {

                    spinnerServo.setSelection(Integer.parseInt(v2));
                }

                System.out.println("v0: " + vp.getTimeSchedule1());
                System.out.println("v1: " + vp.getTimeSchedule2());
                System.out.println("v2: " + vp.getNumOfSpin());
            }

            @Override
            public void onFailure(Call<VirtualPin> call, Throwable t) {

                System.out.println("Retrofit Fail: " + t.getMessage());
            }
        });
    }

    private void sendDataToBlynk(String data, String vp) {

        System.out.println("Data: " + data);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("token", BuildConfig.BLYNK_TOKEN);
        parameters.put(vp, data);
        
        Call<Void> call =
                blynkApi.sendDataToVirtualPins(parameters);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {

                    System.out.println("call onResponse: Response is NOT successful");
                    return;
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                System.out.println("call onFailure: " + t.getMessage());
            }
        });
    }

    private void clearTimeSchedule1() {

        String data = "NULL";

        sendDataToBlynk(data, "v0");

        txtTimeSchedule1.setText("No Time Set");

        Toast.makeText(MainActivity.this, "Alarm Deleted!",
                Toast.LENGTH_SHORT).show();
    }

    private void clearTimeSchedule2() {

        String data = "NULL";

        sendDataToBlynk(data, "v1");

        txtTimeSchedule2.setText("No Time Set");

        Toast.makeText(MainActivity.this, "Alarm Deleted!",
                Toast.LENGTH_SHORT).show();
    }

    private void setTimeSchedule1() {

        final Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String hour = "";
                String minutes = "";

                if (hourOfDay < 10) {

                    hour = "0" + String.valueOf(hourOfDay);
                }

                else {

                    hour = String.valueOf(hourOfDay);
                }

                if (minute < 10) {

                    minutes = "0" + String.valueOf(minute);
                }

                else {

                    minutes = String.valueOf(minute);
                }

                String data = hour + ":" + minutes;
                txtTimeSchedule1.setText(data);

                sendDataToBlynk(data, "v0");

                Toast.makeText(MainActivity.this, "Alarm Set!",
                        Toast.LENGTH_SHORT).show();
            }
        }, hour, minute, false);

        timePickerDialog.show();
    }

    private void setTimeSchedule2() {

        final Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String hour = "";
                String minutes = "";

                if (hourOfDay < 10) {

                    hour = "0" + String.valueOf(hourOfDay);
                }

                else {

                    hour = String.valueOf(hourOfDay);
                }

                if (minute < 10) {

                    minutes = "0" + String.valueOf(minute);
                }

                else {

                    minutes = String.valueOf(minute);
                }

                String data = hour + ":" + minutes;
                txtTimeSchedule2.setText(data);

                sendDataToBlynk(data, "v1");

                Toast.makeText(MainActivity.this, "Alarm Set!",
                        Toast.LENGTH_SHORT).show();
            }
        }, hour, minute, false);

        timePickerDialog.show();
    }
}