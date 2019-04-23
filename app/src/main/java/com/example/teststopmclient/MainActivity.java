package com.example.teststopmclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import org.java_websocket.WebSocket;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Cashier";
//    private StompClient mStompClient;
//    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        compositeDisposable = new CompositeDisposable();

        Button btn = findViewById(R.id.button1);
        btn.setOnClickListener(v -> {
            connectScoket();
        });
    }

    public void connectScoket() {
        new LongOperation().execute("");
    }


    private class LongOperation extends AsyncTask<String, Void, String> {
        private StompClient mStompClient;
        String TAG="LongOperation";
        @Override
        protected String doInBackground(String... params) {

            mStompClient = Stomp.over(WebSocket.class, "ws://192.168.0.4:6614/stomp/websocket");
            //mStompClient = Stomp.over(WebSocket.class, "ws://fuelware-env.mkp8vm63pa.ap-south-1.elasticbeanstalk.com:6614/stomp/websocket");
            mStompClient.connect();

            mStompClient.topic("/topic/greetingsUmarSayyed").subscribe(topicMessage -> {
                Log.d(TAG, topicMessage.getPayload());
            });

            mStompClient.send("/app/hello", "My first STOMP message!").subscribe();


            mStompClient.lifecycle().subscribe(lifecycleEvent -> {
                switch (lifecycleEvent.getType()) {

                    case OPENED:
                        Log.d(TAG, "Stomp connection opened");
                        break;

                    case ERROR:
                        Log.e(TAG, "Error", lifecycleEvent.getException());
                        break;

                    case CLOSED:
                        Log.d(TAG, "Stomp connection closed");
                        break;
                }
            });
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }

    }


    /*public void connectScoket() {
        if (mStompClient != null) {
            Log.d(TAG, "Already Connected");
            return;
        }
        Log.d(TAG, "Starting StompClient");
        mStompClient = Stomp.over(WebSocket.class, "ws://fuelware-env.mkp8vm63pa.ap-south-1.elasticbeanstalk.com:6614/stomp/websocket");
        //mStompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);

        resetSubscriptions();
        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .doOnError(error -> Log.d(TAG, "The error message is: " + error.getMessage()))
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.i(TAG, "Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.e(TAG, "Stomp connection closed");
                            destroy();
                            mStompClient = null;
                            //connectScoket();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.e(TAG, "Stomp failed server heartbeat");
                            break;
                    }
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
        compositeDisposable.add(dispLifecycle);
        subscribeNotifications();
        mStompClient.connect();
    }
    private void subscribeNotifications() {

        Disposable dispTopic = mStompClient.topic("/topic/44236Umar")
                .subscribeOn(Schedulers.io())
                .doOnError(error -> Log.d(TAG, "The error message is: " + error.getMessage()))
                .onErrorReturn(error ->
                        StompMessage.from(error.getMessage())
                )
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());

                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
        Log.d(TAG, "Subscribe " + "/topic/asdjfksajfk");
        compositeDisposable.add(dispTopic);
    }


    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    public void destroy() {
        Log.d(TAG, "Destroying socket stomp client");
        if (mStompClient != null) {
            mStompClient.disconnect();
        }
        if (compositeDisposable != null) compositeDisposable.dispose();
        mStompClient = null;
    }*/


    /*private void setup() {

//        List<StompHeader> headers = new ArrayList<>();
//        headers.add(new StompHeader("admin", "guest"));
//        headers.add(new StompHeader("admin", "guest"));
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://fuelware-env.mkp8vm63pa.ap-south-1.elasticbeanstalk.com:6614/stomp"+"/websocket");

        mStompClient.withClientHeartbeat(10000).withServerHeartbeat(10000);

        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(error -> new LifecycleEvent(LifecycleEvent.Type.ERROR))
                .doOnError(throwable -> Log.e(TAG, "Throwable " + throwable.getMessage()))
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            //MLog.showToast(this, "Stomp connection opened");
                            Log.e(TAG, "Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            //MLog.showToast(this, "Stomp connection error");
                            break;
                        case CLOSED:
                            mStompClient = null;
                            Log.e(TAG, "Stomp connection closed");
                            //MLog.showToast(this, "Stomp connection closed");
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.e(TAG, "Stomp failed server heartbeat");
                            //MLog.showToast(this, "Stomp  failed server heartbeat");
                            break;
                    }
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });

        compositeDisposable.add(dispLifecycle);

        // Receive greetings
        Disposable dispTopic = mStompClient.topic("/topic/6jrllu01nnmiz0xep6ic")
                .subscribeOn(Schedulers.io())
                .onErrorReturn(error ->
                        StompMessage.from(error.getMessage())
                )
                //.observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> Log.e(TAG, "Throwable " + throwable.getMessage()))
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                    //addItem(mGson.fromJson(topicMessage.getPayload(), EchoModel.class));
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });

        compositeDisposable.add(dispTopic);

        mStompClient.connect();

    }*/
}

