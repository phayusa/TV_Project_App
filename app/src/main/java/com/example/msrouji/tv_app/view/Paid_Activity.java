package com.example.msrouji.tv_app.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msrouji.tv_app.controller.DataLoadingInterface;
import com.example.msrouji.tv_app.controller.TokenSender;
import com.example.msrouji.tv_app.model.HeaderInfo;
import com.example.msrouji.tv_app.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by msrouji on 22/09/2017.
 */

public class Paid_Activity extends Activity {

    private ProgressDialog pg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid);
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.YEAR, 1);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        ((TextView) findViewById(R.id.text_sub_time)).setText(format.format(ca.getTime()));

    }


    public void launch_paid(View v) {
        CardInputWidget cardView = ((CardInputWidget) findViewById(R.id.card_input_widget));
        Card card = cardView.getCard();
        if (card == null) {
            Toast.makeText(getApplicationContext(), "Carte non valide", Toast.LENGTH_LONG).show();
            return;
        }
        Stripe stripe = new Stripe(getApplicationContext(), "pk_test_WdWRFyDiwmkZ4eQMLRikvXBw");
        stripe.createToken(card,
                new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        Toast.makeText(getApplicationContext(),
                                error.getLocalizedMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onSuccess(Token token) {
                        new TokenSender(new DataManageClass()).execute(getString(R.string.ip), token.getId());

                    }
                });
        pg=new ProgressDialog(Paid_Activity.this);
        pg.setMessage("Payement en cours");
        pg.setCancelable(false);
        pg.setInverseBackgroundForced(false);
        pg.show();


    }

    private class DataManageClass implements DataLoadingInterface {
        @Override
        public void received_datas(HashMap<HeaderInfo, ArrayObjectAdapter> data) {
            pg.hide();
            finish();
        }

        @Override
        public void request_data() {

        }

        @Override
        public void on_error() {
            //Toast.makeText(getApplicationContext(),"Error. Please remake the request",Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(),"Erro",Toast.LENGTH_LONG).show();
        }
    }
}
