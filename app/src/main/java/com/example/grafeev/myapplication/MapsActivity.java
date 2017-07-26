package com.example.grafeev.myapplication;

////////////////////////////////////////////////////////////////////////
// Created by Grafeev Sergey
///////////////////////////////////////////////////////////////////////

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity  implements OnMapClickListener, OnMapLongClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private UiSettings mUiSetting;
    private String Otvet;
    Marker mark;                    //маркер для поиска вокруг него
    ArrayList<Marker> array = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle("Поля поблизости");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        myToolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MapsActivity.this, "clicking the toolbar!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

        );

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.testmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(MapsActivity.this, "clicking 1111!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {

            Toast.makeText(this, "qwe", Toast.LENGTH_LONG).show();
        }

        mUiSetting = mMap.getUiSettings();
        mUiSetting.setZoomControlsEnabled(true);
        mUiSetting.setMyLocationButtonEnabled(true);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
       // mMap.setOnCameraIdleListener((GoogleMap.OnCameraIdleListener) this);


        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng q = new LatLng(57.1637523, 65.5098741);
        mMap.addMarker(new MarkerOptions().position(q).title("Просто маркер"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(q));

        PolygonOptions rectOptions = new PolygonOptions()
                .add(new LatLng(37.35, -122.0),
                        new LatLng(37.45, -122.0),
                        new LatLng(37.45, -122.2),
                        new LatLng(37.35, -122.2),
                        new LatLng(37.35, -122.0));

// Get back the mutable Polygon
        Polygon polygon = mMap.addPolygon(rectOptions.strokeColor(Color.GREEN));
    }

    @Override
    public void onMapClick(LatLng point) {

       /* for(int i = 0; i < array.size(); i++)
        {
           Marker mark;
            mark = array.get(i);
            mark.remove();
        }*/


    }


    @Override
    public void onMapLongClick(LatLng point) {
        //mTapTextView.setText("tapped, point=" + point);
        Toast.makeText(this, point.toString(), Toast.LENGTH_LONG).show();

        if(mark == null)
        {
            mark = mMap.addMarker(new MarkerOptions().position(point).title("своя долгая метка"));
            //drawRect(point);
        }
        else
        {
            mark.remove();
            mark = mMap.addMarker(new MarkerOptions().position(point).title("своя долгая метка"));
           // Rectangl.remove();
            //drawRect(point);
        }


    }


    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {

            Projection projection = mMap.getProjection();

            LatLngBounds ab = projection.getVisibleRegion().latLngBounds;

            LatLng nord = ab.northeast;
            LatLng south = ab.southwest;

            double top = nord.latitude;
            double bottom = south.latitude;
            double left = south.longitude;
            double right = nord.longitude;

            for(int i = 0; i < array.size(); i++)
            {
                Marker m; m = array.get(i);
                LatLng l =  m.getPosition();
                double a1 = l.latitude;double a2 = l.longitude;

                if(a1 > top || a1 < bottom)
                    m.remove();
                if(a2 < left || a2 > right)
                    m.remove();

            }

           // mMap.clear();
            //Toast.makeText(this, "The user gestured on the map.",
            //        Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCameraIdle() {

        Projection projection = mMap.getProjection();

        LatLngBounds ab = projection.getVisibleRegion().latLngBounds;

        LatLng nord = ab.northeast;
        LatLng south = ab.southwest;


        String params = "{\"square\":\"((" + nord.latitude + ", " + nord.longitude + "), (" + south.latitude + ", " + south.longitude + "))\"}";

        new SendRequest().execute(params);

    }


    public class SendRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0)
        {
            try{

                String url = "http://46.254.17.47/get_fields";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                //add reuqest header
                con.setRequestMethod("POST");

                String urlParameters = arg0[0];
                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();


                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //print result
                //System.out.println(response.toString());

                return response.toString();
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {

            try {

                JSONArray arr = new JSONArray(result);

                for(int i = 0; i < arr.length(); i++)
                {
                    JSONObject obj = arr.getJSONObject(i);

                    String str = obj.getString("coordinates");

                    String resultStr = str.substring(str.indexOf('(') + 1, str.indexOf(','));
                    String resultStr1 = str.substring(str.indexOf(',') + 1, str.indexOf(')'));

                    double value1 = Double.parseDouble(resultStr);
                    double value2 = Double.parseDouble(resultStr1);

                    Marker mark;


                    mark  = mMap.addMarker(new MarkerOptions().position(new LatLng(value1, value2)).title(obj.getString("place")));
                    array.add(mark);
                }

              //  Toast.makeText(getApplicationContext(), a,
              //           Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
               // Toast.makeText(getApplicationContext(), result,
                //        Toast.LENGTH_LONG).show();
            }


        }
    }

}