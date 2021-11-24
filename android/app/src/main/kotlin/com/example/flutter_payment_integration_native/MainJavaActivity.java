package com.example.flutter_payment_integration_native;

import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;


public class MainJavaActivity extends FlutterActivity {
    private static final String CHANNEL = "flutter/native/payment_integration";

    private static MethodCall methodCaller = null;
    private static MethodChannel.Result methodResult = null;

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
                                methodResult.success("Success");
                            }
                        }
                );
    }
}
