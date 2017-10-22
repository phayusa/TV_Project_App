package com.example.msrouji.tv_app.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.msrouji.tv_app.controller.DataLoadingInterface;
import com.example.msrouji.tv_app.model.HeaderInfo;
import com.example.msrouji.tv_app.R;
import com.example.msrouji.tv_app.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Created by msrouji on 25/09/2017.
 */

public class UpdateActivity extends Activity implements DataLoadingInterface {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 341;
    private static final int request_user = 312321;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.prgress_dialog);
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            finish();
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        } else {
            new CheckVersion(this).execute(getString(R.string.ip));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // La permission est garantie
                    new CheckVersion(this).execute(getString(R.string.ip));

                } else {
                    finish();
                }
                return;
            }
        }
    }

    private class CheckVersion extends AsyncTask<String, Void, Boolean> {
        private DataLoadingInterface controller;
        private boolean correct_version;
        private boolean error;

        public CheckVersion(DataLoadingInterface controller) {
            this.controller = controller;
            correct_version = true;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0] + "TV/apk/version");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                BufferedReader buff = new BufferedReader(reader);

                if (conn.getResponseMessage().equals("OK") && buff.readLine().equals(getString(R.string.app_version))) {
                    System.err.println("Ok version");
                    return true;
                } else {
                    System.err.println("No Ok version");
                    return false;
                }

            } catch (IOException e) {
                error = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!error) {
                HashMap<HeaderInfo, ArrayObjectAdapter> data = new HashMap<>();
                if (aBoolean)
                    controller.received_datas(null);
                else {
                    data.put(new HeaderInfo("ssss", 1), null);
                    controller.received_datas(data);
                }

            } else
                controller.on_error();
        }
    }

    private class DownloadApk extends AsyncTask<String, Integer, String> {

        private ProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Dialog dialog = new Dialog(UpdateActivity.this);
            dialog.setContentView(R.layout.prgress_dialog);
            dialog.show();
            progressBar = ((ProgressBar) dialog.findViewById(R.id.loadingBar));
            //progressBar.onVisibilityAggregated(true);
        }

        protected String doInBackground(String... sUrl) {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/app-debug.apk";
            //System.err.println("Install on " + path);
            try {
                URL url = new URL(sUrl[0]);
                URLConnection connection = url.openConnection();
                // connection.setRequestProperty("X_Sender_X","32estloinTROPloin31565A/*");
                connection.connect();

                int fileLength = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(path);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("YourApp", "Well that didn't work out so well...");
                Log.e("YourApp", e.getMessage());
                path = "";
            }
            return path;
        }

        // begin the installation by opening the resulting file
        @Override
        protected void onPostExecute(String path) {
            if (path.equals("")) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(UpdateActivity.this);
                builder1.setMessage(getString(R.string.error_download_apk));
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            } else {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent correct_version!
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //Log.d("Lofting", "About to install new .apk");
                startActivityForResult(i,request_user);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //ProgressBar progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
            //progressBar.setMax(100);
            progressBar.setProgress(values[0]);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("RESULT CODE ", "" + resultCode);

        if (requestCode == MainActivity.Request_Main)
            finish();
        if (requestCode == request_user) {
            Log.d("RESULT CODE ", "" + requestCode);
            finish();
        }
    }

    @Override
    public void received_datas(HashMap<HeaderInfo, ArrayObjectAdapter> data) {
        if (data == null)
            startActivityForResult(new Intent(this, MainActivity.class), MainActivity.Request_Main);
        else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(UpdateActivity.this);
            builder1.setMessage(getString(R.string.update_available));
            builder1.setCancelable(true);
            builder1.setNegativeButton(
                    "Quitter",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            finish();
                        }
                    });
            builder1.setPositiveButton(
                    "Télécharger",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            new DownloadApk().execute(getString(R.string.ip) + "TV/apk/file/last");
                        }
                    });


            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    @Override
    public void request_data() {

    }


    @Override
    public void on_error() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(UpdateActivity.this);
        builder1.setMessage(getString(R.string.error_reach_server));

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
