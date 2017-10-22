package com.example.msrouji.tv_app.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.msrouji.tv_app.R;
import com.example.msrouji.tv_app.controller.RequestSubscriptionTime;
import com.example.msrouji.tv_app.controller.SimpleLoaderInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by msrouji on 08/10/2017.
 */

public class SubscriptionActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_time);
        new RequestSubscriptionTime(new ReceiveData()).execute(getString(R.string.ip)+"TV/user/time/");
    }

    private class ReceiveData implements SimpleLoaderInterface{
        @Override
        public void received_datas(Object data) {
            if (data instanceof String){
                SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.FRANCE);
                SimpleDateFormat outFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.FRANCE);
                try {
                    //Date d=new Date();
                    //d=  readFormat.parse(((String) data));
                    ((TextView) findViewById(R.id.text_time_subscription)).setText(outFormat.format(readFormat.parse(((String) data))));
                } catch (java.text.ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void request_data() {

        }

        @Override
        public void on_error(@Nullable String message_error) {
            finish();
            //error_occured = true;
            Intent intent = new Intent(SubscriptionActivity.this, BrowseErrorActivity.class);
            startActivity(intent);
        }
    }


}
