package dcbrh.ph.weatherapi;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {
    private static final String TAG = "WeatherDataService";
    String cityID;
    private static Context context;
    public static final String CITY_ID_QUERY = "https://www.metaweather.com/api/location/search/?query=";
    public static final String CITY_WEATHER_REPORT_QUERY = "https://www.metaweather.com/api/location/";

    public WeatherDataService(Context context) {
        WeatherDataService.context = context;
    }

    // A Callback that will return the cityId to main
    public interface VolleyResponseListener {
        void onError(String e);
        void onResponse(String cityId);
    }

    public void getCityID(String cityName, final VolleyResponseListener responseListener) {

        cityID = "";
        String url =  CITY_ID_QUERY + cityName;

        JsonArrayRequest arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    cityID = jsonObject.getString("woeid");
                    responseListener.onResponse(cityID);

                } catch (JSONException e) { e.printStackTrace(); }
            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onError(error.getMessage());
            }});
        MySingleton.getInstance(context).addToRequestQueue(arrayRequest);
    }

    public interface ForecastByIDResponseListener {
        void onError(String e);
        void onResponse(List<WeatherModelReport> modelReport);
    }


    public void getCityForecastByID(String cityID, final ForecastByIDResponseListener responseListener) {

        List<WeatherModelReport> weatherModelReportList = new ArrayList<>();
        String url = CITY_WEATHER_REPORT_QUERY+cityID;

        // Get JSON object
        JsonObjectRequest objectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray consolidatedWeather_list = response.getJSONArray("consolidated_weather");

                    for (int i=0; i<consolidatedWeather_list.length(); i++) {

                        WeatherModelReport oneDay_weather_report = new WeatherModelReport();
                        JSONObject api_weather_report = consolidatedWeather_list.getJSONObject(i);

                        oneDay_weather_report.setId(api_weather_report.getInt("id"));
                        oneDay_weather_report.setWeather_state_name(api_weather_report.getString("weather_state_name"));
                        oneDay_weather_report.setWeather_state_abbr(api_weather_report.getString("weather_state_abbr"));
                        oneDay_weather_report.setWind_direction_compass(api_weather_report.getString("wind_direction_compass"));
                        oneDay_weather_report.setCreated(api_weather_report.getString("created"));
                        oneDay_weather_report.setApplicable_date(api_weather_report.getString("applicable_date"));
                        oneDay_weather_report.setMin_temp(api_weather_report.getLong("min_temp"));
                        oneDay_weather_report.setMax_temp(api_weather_report.getLong("max_temp"));
                        oneDay_weather_report.setThe_temp(api_weather_report.getLong("the_temp"));
                        oneDay_weather_report.setWind_speed(api_weather_report.getLong("wind_speed"));
                        oneDay_weather_report.setWind_direction(api_weather_report.getLong("wind_direction"));
                        oneDay_weather_report.setAir_pressure(api_weather_report.getLong("air_pressure"));
                        oneDay_weather_report.setHumidity(api_weather_report.getInt("humidity"));
                        oneDay_weather_report.setVisibility(api_weather_report.getLong("visibility"));
                        oneDay_weather_report.setPredictability(api_weather_report.getInt("predictability"));

                        weatherModelReportList.add(oneDay_weather_report);

                        Log.d(TAG, "onResponse: "+weatherModelReportList.toString());
                    }

                    responseListener.onResponse(weatherModelReportList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(objectRequest);
//        Log.d(TAG, "getCityForecastByID: "+objectRequest);
        // Get property "consolidated_weather"
    }

//
//    public List<WeatherModelReport> getCityForecastByName(String cityName) {
//
//    }
}
