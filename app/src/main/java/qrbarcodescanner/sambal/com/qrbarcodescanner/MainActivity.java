package qrbarcodescanner.sambal.com.qrbarcodescanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static int REQUEST_CAMERA = 1;
    private  ZXingScannerView scannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(MainActivity.this, "Permission is granted", Toast.LENGTH_LONG).show();
            }
            else
            {
                requestPermission();
            }
        }

    }

    private boolean checkPermission()
    {

        return (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permission[], int grantResults[]){
       // switch (requestCode)
        if(requestCode==REQUEST_CAMERA)
        {
            //case REQUEST_CAMERA :
                if (grantResults.length > 0)
                {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted)
                    {
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        {
                            if (shouldShowRequestPermissionRationale(CAMERA))
                            {
                                displayAlertMessage("You need to aloow access for both permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                //break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        scannerView.resumeCameraPreview(MainActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkPermission())
            {
                if (scannerView == null)
                {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }
            else
            {
                requestPermission();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void  displayAlertMessage (String message, DialogInterface.OnClickListener listener)
    {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    public void handleResult(final Result result) {

//        final AlertDialog alert;
        final String scanResult = result.getText();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        alert = builder.create();
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scannerView.resumeCameraPreview(MainActivity.this);
            }
        });


        builder.setNeutralButton("Visit", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(scanResult.contains("http"))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
                    startActivity(intent);
                }
                else
                {

                    Toast.makeText(getApplicationContext(), "This is not a valid link", Toast.LENGTH_LONG).show();
                    scannerView.resumeCameraPreview(MainActivity.this);
//                  //alert.hide();
//
//                  final AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
//                    builder.setTitle("This is not a valid link");
//                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            scannerView.resumeCameraPreview(MainActivity.this);
//                        }
//                    });
//
//                    final AlertDialog alertDialog = builder1.create();
//                    alertDialog.show();
                }
            }
        });
        builder.setMessage(scanResult);
        final AlertDialog alert = builder.create();
        alert.show();

    }
}
