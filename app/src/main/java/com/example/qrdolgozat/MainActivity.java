package com.example.qrdolgozat;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button buttonScan;
    private Button buttonKiir;
    private TextView TextEredmeny;
    private Button buttonKoordinatak;
    private ImageView ImageViewQR;
    private float hosszusag;
    private float szelesseg;
    private Naplozas naplozas;
    private String koordinata;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        naplozas = new Naplozas();
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        init();
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                szelesseg = (float) location.getLatitude();
                hosszusag = (float) location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("QR Code Scanning by Valaki");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);       //!!
                integrator.initiateScan();
            }
        });
        buttonKiir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    naplozas.kiiras(szelesseg,hosszusag,koordinata);
                    Toast.makeText(MainActivity.this, "Új koordináta feljegyezve", Toast.LENGTH_SHORT).show();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        buttonKoordinatak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                koordinata = (Float.toString(szelesseg)+";" + Float.toString(hosszusag));
                try{
                    BitMatrix bitMatrix = multiFormatWriter.encode(koordinata,
                            BarcodeFormat.QR_CODE, 500,500);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    ImageViewQR.setImageBitmap(bitmap);
                }catch (WriterException e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null)
        {
            if (result.getContents() == null)
            {
                Toast.makeText(this, "Kiléptünk a scannelésből", Toast.LENGTH_SHORT).show();
            }else
            {
                TextEredmeny.setText("QR Code eredmény:" + result.getContents());

                Uri uri = Uri.parse(result.getContents());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void init()
    {
        buttonScan = findViewById(R.id.buttonScan);
        TextEredmeny = findViewById(R.id.TextEredmeny);
        buttonKiir = findViewById(R.id.buttonKiir);
        buttonKoordinatak = findViewById(R.id.buttonKoordinatak);
        ImageViewQR = findViewById(R.id.ImageViewQR);

    }
}

