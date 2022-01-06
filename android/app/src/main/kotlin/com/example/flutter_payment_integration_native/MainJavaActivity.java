package com.example.flutter_payment_integration_native;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import co.tamara.sdk.PaymentResult;
import co.tamara.sdk.TamaraPayment;
import co.tamara.sdk.TamaraPaymentHelper;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;


public class MainJavaActivity extends FlutterActivity {
    private static final String CHANNEL = "flutter/native/payment_integration";

    private static MethodCall methodCaller = null;
    private static MethodChannel.Result methodResult = null;
    private static final String CHECKOUT_URL = "https://checkout-sandbox.tamara.co/checkout/d7ce1135-73c3-44e2-86ea-d6272d906d79?locale=ar_SA&orderId=fee6933b-26ea-4b8b-b440-f9529787f21c";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            // Note: this method is invoked on the main thread.
                            // TODO
                            methodCaller = call; //Assigning call to methodCaller
                            methodResult = result; //Assigning result to methodResult
                            //now can use methodCaller abd methodResult anywhere within the class

                            if (methodCaller.method.equals("getPaymentStatus")) {
                                TamaraPayment.Companion.startPayment(this,CHECKOUT_URL,"","","");
                            }
                        }
                );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (TamaraPaymentHelper.Companion.shouldHandleActivityResult(requestCode, resultCode, data)) {
            if (data != null) {
                PaymentResult result = TamaraPaymentHelper.Companion.getData(data);
                if (result != null) {
                    if (PaymentResult.STATUS_CANCEL == result.getStatus()) {
                        Toast.makeText(this, "Cancel", Toast.LENGTH_LONG).show();
                        methodResult.success("Cancel");
                    } else if (PaymentResult.STATUS_FAILURE == result.getStatus()) {
                        Toast.makeText(this, "Failure", Toast.LENGTH_LONG).show();
                        methodResult.success("Failure");
                    } else if (PaymentResult.STATUS_SUCCESS == result.getStatus()) {
                        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                        methodResult.success("Success");
                    }
                }
            }
        }
    }
}
