package android.csulb.edu.gesturerecognition;

import android.content.SharedPreferences;
import android.csulb.edu.gesturerecognition.utility.Utility;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddressActivity extends AppCompatActivity {

    //private static final String FILE_NAME = "LocationStorage";
    EditText txtAddress;
    TextView addressType;
    Button saveAddress;
    String address;
    LatLng latlng;
    String address_type;
    ImageView addressIcon;

    Utility utility = new Utility();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);


        Bundle extras = getIntent().getExtras();
        address_type = extras.getString("Type");
        //Toast.makeText(getApplicationContext(),address_type,Toast.LENGTH_SHORT).show();

        setTitle(address_type + " Address");


        addressType = (TextView) findViewById(R.id.addressType);
        saveAddress = (Button) findViewById(R.id.saveAddress);
        txtAddress = (EditText) findViewById(R.id.txtAddress);
        addressIcon = (ImageView) findViewById(R.id.addressIcon);

        txtAddress.setText(getAddressString(address_type));

        addressType.setText(address_type);
        if(address_type.equals("Home"))
            addressIcon.setImageResource(R.drawable.home);
        else if(address_type.equals("Work"))
            addressIcon.setImageResource(R.drawable.work);



        saveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LongRunningGetIO1().execute();
                address=((EditText)findViewById(R.id.txtAddress)).getText().toString();
                address = address.replace(' ','+');
            }
        });

    }

    Map<String, Double> getAddress(String key) {
        SharedPreferences locationDetails = getSharedPreferences(Utility.LOCATION_FILE_NAME , MODE_PRIVATE);
        Map<String, Double> address = new HashMap<>();
        double longitude = 0.0;
        double latitude = 0.0;
        if(key == "HOME") {
            longitude = Double.valueOf(locationDetails.getString("homeLongitude", ""));
            latitude = Double.valueOf(locationDetails.getString("homeLatitude", ""));
        }
        else if(key=="WORK") {
            longitude = Double.valueOf(locationDetails.getString("workLongitude", ""));
            latitude = Double.valueOf(locationDetails.getString("workLatitude", ""));
        }
        address.put("longitude", longitude);
        address.put("latitude", latitude);

        return address;
    }

    String getAddressString(String key) {
        String addressString="";

        SharedPreferences locationDetails = getSharedPreferences(Utility.LOCATION_FILE_NAME, MODE_PRIVATE);


        if(key.equals("Home")) {
            addressString = locationDetails.getString("homeAddress", "");

        }
        else if(key.equals("Work")) {
            addressString = locationDetails.getString("workAddress", "");
        }
        return addressString;
    }

    private class LongRunningGetIO1 extends AsyncTask<Void, Void, String> {
        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
            InputStream in = entity.getContent();


            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);


                if (n > 0) out.append(new String(b, 0, n));
            }


            return out.toString();
        }


        @Override


        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            //HttpGet httpGet = new HttpGet("https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA");


            HttpGet httpGet = new HttpGet("https://maps.googleapis.com/maps/api/geocode/json?address="+address);
            String text = null;

            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);


                HttpEntity entity = response.getEntity();


                text = getASCIIContentFromEntity(entity);


            } catch (Exception e) {
                return e.getLocalizedMessage();
            }


            return text;
        }


        protected void onPostExecute(String results) {
            if (results != null) {



                //et.setText(results);
            /*JSONObject emp=(new JSONObject(results)).getJSONObject("employee");
            String empname=emp.getString("name");
            int empsalary=emp.getInt("salary");*/
                String status="";
                try {
                    status=(new JSONObject(results)).getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //boolean isValid = status=="OK"?true:false;
                if(status.equals("OK")) {
                    //TODO: Add code if address is valid

                    utility.setAddress(AddressActivity.this, address_type, address);
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                    //GeocodingLocation locationAddress = new GeocodingLocation();
                    //locationAddress.getAddressFromLocation(address, getApplicationContext(), new GeocoderHandler());


                }else
                    Toast.makeText(getApplicationContext(),"Address is invalid!",Toast.LENGTH_SHORT).show();

            }


            Button b = (Button)findViewById(R.id.saveAddress);

            b.setClickable(true);
        }

    }

    private class GeocoderHandler extends Handler {


        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    latlng = (LatLng) bundle.getSerializable("latlng");
                    if(latlng!=null)
                        setAddress(address_type, latlng.longitude, latlng.latitude, latlng.addressString);
                    else
                        Toast.makeText(getApplicationContext(), "Geocoordinates could not be retrieved", Toast.LENGTH_SHORT).show();
                    locationAddress = bundle.getString("address");

                    break;
                default:
                    locationAddress = null;
            }
                //Toast.makeText(getApplicationContext(),"Latitude: "+latlng.latitude+"\n"+"Longitude: "+latlng.longitude+"\n Address: "+latlng.addressString,Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(),locationAddress,Toast.LENGTH_SHORT).show();
            //latLongTV.setText(locationAddress);
        }

        public void setAddress(String key, double longitude, double latitude, String address) {
            SharedPreferences locationDetails = getSharedPreferences(Utility.LOCATION_FILE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor edit = locationDetails.edit();
            edit.clear();
            if(key.equals("Home")) {
                edit.putString("homeLongitude", String.valueOf(longitude));
                edit.putString("homeLatitude", String.valueOf(latitude));
                edit.putString("homeAddress", String.valueOf(address));
                //Toast.makeText(getApplicationContext(),longitude+latitude+address,Toast.LENGTH_SHORT).show();

            }
            else if(key.equals("Work")) {
                edit.putString("workLongitude", String.valueOf(longitude));
                edit.putString("workLatitude", String.valueOf(latitude));
                edit.putString("workAddress", String.valueOf(address));
                //Toast.makeText(getApplicationContext(),longitude+latitude+address,Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(),"key is null",Toast.LENGTH_SHORT).show();
            edit.commit();

        }


    }

}

