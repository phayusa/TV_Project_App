package com.example.msrouji.tv_app.View;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.view.View;
import android.widget.Toast;

import com.example.msrouji.tv_app.Controller.DataLoadingInterface;
import com.example.msrouji.tv_app.Controller.TokenSender;
import com.example.msrouji.tv_app.Model.HeaderInfo;
import com.example.msrouji.tv_app.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import java.util.HashMap;

/**
 * Created by msrouji on 22/09/2017.
 */

public class Paid_Activity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid);
    }


    public void launch_paid(View v) {
        CardInputWidget cardView = ((CardInputWidget) findViewById(R.id.card_input_widget));
        Card card = cardView.getCard();
        if (card == null) {
            Toast.makeText(getApplicationContext(), "Please select a valid card", Toast.LENGTH_LONG).show();
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
                        new TokenSender(new DataManageClass()).execute(getString(R.string.ip),token.getId());

                    }
                });

    }

    private class DataManageClass implements DataLoadingInterface{
        @Override
        public void received_datas(HashMap<HeaderInfo, ArrayObjectAdapter> data) {
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
