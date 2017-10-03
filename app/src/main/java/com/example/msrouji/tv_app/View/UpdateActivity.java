package com.example.msrouji.tv_app.View;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.example.msrouji.tv_app.Controller.DataLoadingInterface;
import com.example.msrouji.tv_app.Model.HeaderInfo;
import com.example.msrouji.tv_app.R;

import org.videolan.libvlc.Dialog;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                //Cela signifie que la permission à déjà était
                //demandé et l'utilisateur l'a refusé
                //Vous pouvez aussi expliquer à l'utilisateur pourquoi
                //cette permission est nécessaire et la redemander

            } else {
                //Sinon demander la permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }else*/
        new CheckVersion(this).execute(getString(R.string.ip));

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
                    // La permission est refusée
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
            correct_version = false;
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
                } else {
                    System.err.println("No Ok version");
                    correct_version = true;
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
                if (!correct_version)
                    controller.received_datas(null);
                else
                    controller.on_error();

            }
        }
    }

    private class UpdateApp extends AsyncTask<String, Void, Void> {
        private Context context;

        public void setContext(Context contextf) {
            context = contextf;
        }

        @Override
        protected Void doInBackground(String... arg0) {
            try {
                URL url = new URL(arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                // String PATH = "/mnt/sdcard/Download/";
                //String PATH = Environment.getExternalStorageDirectory().getPath();
                String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, "TV_app.apk");
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                System.err.println("Install on " + outputFile.getAbsolutePath());

                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream is = c.getInputStream();

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                }
                fos.close();
                is.close();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Log.e("SSSS","ssss "+ Uri.fromFile(new File(PATH + "TV_app.apk")));
                intent.setDataAndType(Uri.fromFile(new File(PATH + "TV_app.apk")), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent correct_version!
                context.startActivity(intent);


            } catch (Exception e) {
                Log.e("UpdateAPP", "Update correct_version! " + e.getMessage());
            }
            return null;
        }

    }

    private class DownloadApk extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... sUrl) {
            //String path = Environment.getExternalStorageDirectory().getPath() + "/Downloads/app-debug.apk";
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/app-debug.apk";
            //String path = getApplicationContext().getFilesDir() + "/app-debug.apk";
            System.err.println("Install on " + path);
            try {
                URL url = new URL(sUrl[0]);
                URLConnection connection = url.openConnection();
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
            }
            return path;
        }

        // begin the installation by opening the resulting file
        @Override
        protected void onPostExecute(String path) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent correct_version!
            Log.d("Lofting", "About to install new .apk");
            startActivity(i);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            ProgressBar  progressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleSmall);
            progressBar.setMax(100);
            progressBar.setProgress(values[0]);
        }


    }

    @Override
    public void received_datas(HashMap<HeaderInfo, ArrayObjectAdapter> data) {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void request_data() {

    }

    @Override
    public void on_error() {
        new DownloadApk().execute(getString(R.string.ip) + "TV/apk/file/last");
        /*Log.d("Download", "About to install new .apk");
        UpdateApp atualizaApp = new UpdateApp();
        atualizaApp.setContext(getApplicationContext());
        atualizaApp.execute(getString(R.string.ip)+"TV/apk/file/last");*/
    }
}
