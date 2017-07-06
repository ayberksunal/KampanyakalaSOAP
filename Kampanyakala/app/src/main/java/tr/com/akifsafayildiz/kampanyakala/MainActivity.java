package tr.com.akifsafayildiz.kampanyakala;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends ActionBarActivity {
    String TAG = "Response";
    Button bt;
    EditText celcius;
    String getCel;
    SoapPrimitive resultString;

    private  NotificationManager mNotificationManager;

    double xkonum=0;
    double ykonum=0;

    private TextView textView_locations;
    private LocationManager locationManager;
    private LocationListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings();
        listenLocation();

















        //locationManager.requestLocationUpdates("gps", 0, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        //locationManager.requestLocationUpdates("gps", 0, 0, listener);
    }

    public void settings()
    {
        textView_locations = (TextView) findViewById(R.id.textView);
    }
    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            calculate();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
         //   Toast.makeText(MainActivity.this, "Response: " + resultString.toString(), Toast.LENGTH_LONG).show();
            kampanyaBildirimi();

            ///

            ///

        }
    }

    public void kampanyaBildirimi()
    {
        Intent intent = new Intent(this, CampaignIntent.class);
        PendingIntent i = PendingIntent.getActivity(this, 0, intent, 0);


        Notification bildirim = new Notification.Builder(this)
                .setTicker("Yeni!")
                .setContentTitle("Kampanyakaladın!")
                .setContentText(resultString.toString())
                .setSmallIcon(R.mipmap.ic_launcher).setContentIntent(i)
                .getNotification();
        bildirim.flags = Notification.FLAG_SHOW_LIGHTS;

        NotificationManager bildirimYonetici = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        bildirimYonetici.notify(0, bildirim);

    }

    public void calculate() {
        String SOAP_ACTION = "";
        String METHOD_NAME = "getKonumFarki";
        String NAMESPACE = "http://kampanyakala/";
        String URL = "http://139.59.135.226:6153/ws/hello";

        try {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("locationx", xkonum + "");
            Request.addProperty("locationy", ykonum + "");

            SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            soapEnvelope.setOutputSoapObject(Request);

            HttpTransportSE transport = new HttpTransportSE(URL);

            transport.call(SOAP_ACTION, soapEnvelope);
            resultString = (SoapPrimitive) soapEnvelope.getResponse();

            Log.i(TAG, "Sonuc: " + resultString);
        } catch (Exception ex) {
            Log.e(TAG, "Error: " + ex.getMessage());
        }
    }

    // location çekme
    private void listenLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                textView_locations.append("\n " + location.getLongitude() + " " + location.getLatitude());
                /////////////////////////
                xkonum = location.getLongitude();
                ykonum = location.getLatitude();
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        configure_button();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
    }
}