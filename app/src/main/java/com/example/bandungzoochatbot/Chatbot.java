package com.example.bandungzoochatbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Handler;


//
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Chatbot extends AppCompatActivity {

    double PI = 3.141592653589793;
    double RADIUS = 6378.16;

    List<Brainfile> messageList;
    List<Fasilitas> fasilitasList;
    List<Koleksi> koleksiList;
    List<String> pertanyaanList;
    List<String> jawabanList;
    List<String> namafasilitasList;

    FusedLocationProviderClient fusedLocationProviderClient;

    Toolbar toolbar;

    FloatingActionButton btnSend;
    private ImageButton btnMap;

    EditText editTextMsg;
    ListView listView;

    double latitude;
    double longitude;

    Boolean IS_CONNECTED = true;

    private ChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        ListView mylistView;

        messageList = new ArrayList<>();
        fasilitasList = new ArrayList<>();
        koleksiList = new ArrayList<>();
        namafasilitasList = new ArrayList<>();
        pertanyaanList = new ArrayList<>();
        jawabanList = new ArrayList<>();

        btnSend = findViewById(R.id.btnSend);
        btnMap = findViewById(R.id.btnMap);
        editTextMsg = findViewById(R.id.editTextMsg);
        listView  = (ListView) findViewById(R.id.listView);
        adapter = new ChatAdapter(this, new ArrayList<ChatMessage>());
        listView.setAdapter(adapter);

        //Action bar
        toolbar = findViewById(R.id.toolbar_chatbot);
        setSupportActionBar(toolbar);

        // Connect to firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Brainfile");
        DatabaseReference myRefFasilitas = database.getReference("Fasilitas");
        DatabaseReference mtRefKoleksi = database.getReference("Koleksi");

        //Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Connection check + alert
        if(!isConnected(this)){
            botsReply("Layanan ini membutuhkan internet. Harap periksa koneksi anda.", false);
            IS_CONNECTED = false;
        }

        // Chaquopy Python
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        Python py = Python.getInstance();
        PyObject pyTrans = py.getModule("translate");
        PyObject pyModel = py.getModule("model");

        //Get QnA data
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                pertanyaanList.clear();
                jawabanList.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Brainfile message = data.getValue(Brainfile.class);
                    String pertanyaan = data.child("pertanyaan").getValue(String.class);
                    String jawaban = data.child("jawaban").getValue(String.class);

                    messageList.add(message);
                    pertanyaanList.add(pertanyaan);
                    jawabanList.add(jawaban);

                    Log.i("PERTANYAAN", pertanyaanList.get(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d("message", "Failed to read value.", error.toException());
            }
        });

        //inisial model
        String[] p = {};
        for (int i=0; i<pertanyaanList.size();i++){
            p[i] = String.valueOf(pertanyaanList.get(i));
        }
//        String[] array_pertanyaan = messageList.toArray(new String[0]);

        Log.i("isi string", String.valueOf(pertanyaanList.size()));
//        Log.i("isi string", pertanyaanList.get(0));
//        PyObject pyquestion = pyModel.callAttr("initial", p);

        myRefFasilitas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fasilitasList.clear();
                namafasilitasList.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Fasilitas fasilitas = data.getValue(Fasilitas.class);
                    String namafasilitas = data.child("nama").getValue(String.class);

                    fasilitasList.add(fasilitas);
                    namafasilitasList.add(namafasilitas);
                    Log.i("nama_fasilitas", namafasilitas);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d("message", "Failed to read value.", error.toException());
            }
        });

        mtRefKoleksi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                koleksiList.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Koleksi fasilitas = data.getValue(Koleksi.class);
                    String namafasilitas = data.child("nama").getValue(String.class);

                    koleksiList.add(fasilitas);
                    fasilitasList.add(fasilitas);
                    namafasilitasList.add(namafasilitas);
                    Log.i("nama_fasilitas", namafasilitas);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d("message", "Failed to read value.", error.toException());
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMsg.getText().toString();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(Chatbot.this, "Masukan pertanyaan anda di sini", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendMessage(message, false);
                //BOT reply
                String answer;
                if(isAskingLocation(message)){
                    if(isExistinDatabase(fasilitasList, message)){
                        //TODO: get information from list and send it using adapter as reply
                        seeLocation(fasilitasList, message);
                    }
                    else {
                        answer = "Fasilitas tidak diketahui, silahkan tanya kembali";
                        botsReply(answer, false);
                    }
                }
                else {
                    if (IS_CONNECTED){
                        PyObject pyObj = pyTrans.callAttr("trans_en", message);
                        String query = pyObj.toString();
                        botsReply(pyObj.toString(), false); //sampai sini bisa
//                    PyObject pyIndex = pyModel.callAttr("getIndex", query, pyquestion);
//                    botsReply(getAnswer(pyIndex.toInt(), jawabanList), false);
                    }
                    else {
                        botsReply("Tidak dapat mendapatkan jawaban. Harap periksa koneksi anda.", false);
                    }
                }

                editTextMsg.setText("");
                listView.setSelection(adapter.getCount()-1);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });

    }



    private String getAnswer(int index, List<String> jawabanlist){
        //TODO:getAnswer
        if(jawabanlist.isEmpty()){
            return "Aplikasi tidak terhubung internet, harap cek koneksi anda";
        }
        if(index==10000){
            return "Chatbot tidak paham dengan pertanyaan anda, silahkan tanya kembali.";
        }
        else {
           return jawabanlist.get(0);
        }
    }

    private void botsReply(String message, boolean isLocation){
        ChatMessage chatMessage = new ChatMessage(false, message, isLocation);
        adapter.add(chatMessage);
    }

    private void sendMessage(String message, boolean isLocation){
        ChatMessage chatMessage = new ChatMessage( true, message, isLocation);
        adapter.add(chatMessage);
    }

    private Boolean isAskingLocation(String message){
        if(message.toLowerCase().contains("terdekat")){
            return true;
        }
        return false;
    }

    private Boolean isExistinDatabase(List<Fasilitas> fasilitas, String query){
        //TODO:lowercase semua string
        for(int i=0;i<fasilitas.size();i++){
            if(query.toLowerCase().contains(fasilitas.get(i).getNama().toLowerCase())){
                Log.i("CEK_LOKASI", fasilitas.get(i).getNama());
                return true;
            }
        }
        return false;
    }

    private void seeLocation(List<Fasilitas> fasilitas, String query){
        //TODO:lowercase semua string
        ArrayList<Fasilitas> fasilitas1 = new ArrayList<>();
        for(int i=0;i<fasilitas.size();i++){
            if(query.toLowerCase().contains(fasilitas.get(i).getNama().toLowerCase())){
                fasilitas1.add(fasilitas.get(i));
            }
        String  id = getClosestFacilicy(fasilitas1);
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("ID_FACILITY", fasilitas.get(i).getId());

        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        }
    }

    private String getClosestFacilicy(List<Fasilitas> fasilitas){
        String id = null;
        final double dLat = 0;
        final double dLon = 0;
        if(ActivityCompat.checkSelfPermission(Chatbot.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location!=null){
                        Geocoder geocoder = new Geocoder(Chatbot.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.i("USER_POSITION_LAT", String.valueOf(latitude));
                        Log.i("USER_POSITION_LONG", String.valueOf(longitude));
                        double closerDistance = 9999;
                        for(int i=0;i<fasilitas.size();i++) {
                            Log.i("Fasilitas check", fasilitas.get(i).getNama());
                            double dLat = Radians(fasilitas.get(i).getLatitude() - latitude);
                            double dLon = Radians(fasilitas.get(i).getLongitude() - longitude);
                            double distance = RADIUS * ((Math.sin(dLat/2) * Math.sin(dLat/2)) + Math.cos(Radians(latitude)) * Math.cos(Radians(fasilitas.get(i).getLatitude())) * (Math.sin(dLon/2) * Math.sin(dLon)));
                            Log.i("JARAK", String.valueOf(distance));
                            if(distance < closerDistance){
                                Long id = fasilitas.get(i).getId();
                            }
                        }
                    }
                }
            });
            return id;
        }
        return id;
    }

    private boolean isConnected(Chatbot chatbot){
        ConnectivityManager connectivityManager = (ConnectivityManager) chatbot.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(connectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(connectivityManager.TYPE_MOBILE);

        if((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())){
            return true;
        }
        return false;
    }

    public double Radians(double x){
        return  x * PI / 180;
    }

}
