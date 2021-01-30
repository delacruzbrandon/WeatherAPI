package dcbrh.ph.weatherapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static dcbrh.ph.weatherapi.WeatherDataService.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener { // TODO continue the JAVA REST video

    private static final String TAG = "MainActivity";

    Button btn_getCityId, btn_useCityId, btn_useCityName;
    ListView lv_outputData;
    EditText et_inputData;
    String string_input;

    WeatherDataService weatherService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_getCityId = findViewById(R.id.btn_getCityId);
        btn_useCityId = findViewById(R.id.btn_useCityId);
        btn_useCityName = findViewById(R.id.btn_useCityName);

        btn_getCityId.setOnClickListener(this);
        btn_useCityId.setOnClickListener(this);
        btn_useCityName.setOnClickListener(this);

        lv_outputData = findViewById(R.id.lv_dataOutput);
        et_inputData = findViewById(R.id.et_dataInput);
        string_input = et_inputData.getText().toString();

        weatherService = new WeatherDataService(this);
    }

    @Override
    public void onClick(View view) {
        String string_inputData = et_inputData.getText().toString();

        switch (view.getId()) {
            case R.id.btn_getCityId:
                // This didn't return anything
                weatherService.getCityID(string_inputData, new VolleyResponseListener() {
                    @Override
                    public void onError(String e) {
                        Toast.makeText(MainActivity.this, e, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String cityId) {
                        et_inputData.setText(cityId);
                    }
                });
                break;

            case R.id.btn_useCityId:
                ListView listView = findViewById(R.id.lv_dataOutput);

                weatherService.getCityForecastByID(string_inputData, new ForecastByIDResponseListener() {
                    @Override
                    public void onError(String e) {
                        Toast.makeText(MainActivity.this, e, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(List<WeatherModelReport> modelReport) {
                        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, modelReport);
                        listView.setAdapter(arrayAdapter);
                        Log.i(TAG, "onResponse: "+modelReport);
                    }
                });
                break;

            case  R.id.btn_useCityName:
                Toast.makeText(this, "Use City Name "+string_inputData, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}