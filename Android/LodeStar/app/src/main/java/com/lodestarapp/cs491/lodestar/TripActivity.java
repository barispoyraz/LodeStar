package com.lodestarapp.cs491.lodestar;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class TripActivity extends AppCompatActivity {
    public ViewFlipper view_flipper;
    public View firstView;
    public View secondView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip2);

        String requestFromTheUrl = "http://10.0.2.2:3001?dataType=flightInfo";
        sendRequestToServer(requestFromTheUrl);



        view_flipper =   (ViewFlipper) findViewById(R.id.flipper);



        CardView card = (CardView) findViewById(R.id.my_card);
        firstView= findViewById(R.id.view1);
        secondView = findViewById(R.id.view2);
        card.setOnTouchListener(new OnSwipeTouchListener(TripActivity.this) {
            public void onSwipeLeft() {
                if (view_flipper.getCurrentView() != secondView){
                    view_flipper.setInAnimation(TripActivity.this, R.anim.left_enter);
                    view_flipper.setOutAnimation(TripActivity.this, R.anim.left_out);
                    view_flipper.showNext();
                }
            }

            public void onSwipeRight() {
                if (view_flipper.getCurrentView() != firstView){
                    view_flipper.setOutAnimation(TripActivity.this, R.anim.right_out);
                    view_flipper.setInAnimation(TripActivity.this, R.anim.right_enter);
                    view_flipper.showPrevious();
                }
            }
        });

    }

    private static final String TAG = "theMessage";

    public void sendRequestToServer(String requestFromTheUrl) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final JSONObject[] responseFromServer = new JSONObject[1];

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestFromTheUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, response.toString());
                responseFromServer[0] = response;

                parseTheJSONResponse(responseFromServer[0]);

                //mAdapter = new WeatherInformationAdapter(responseFromServer[0]);
                //mRecyclerView.setAdapter(mAdapter);
            }

            private void parseTheJSONResponse(JSONObject flightInformationFromServer) {
                String info;

                try {
                    Log.i(TAG, flightInformationFromServer.toString());
                    info = flightInformationFromServer.getString("ident");
                    TextView view =  findViewById(R.id.info_text1);
                    view.setText("You will be boarding " + info.toString() + " from");

                    TextView view1 =  findViewById(R.id.info_text3);
                    view1.setText("You will be boarding " + info.toString() + " to");

                    JSONObject or = flightInformationFromServer.getJSONObject("origin");
                    JSONObject des = flightInformationFromServer.getJSONObject("destination");

                    TextView view2 =  findViewById(R.id.info_text5);
                    view2.setText( or.getString("city").toString());

                    TextView view4 =  findViewById(R.id.info_text6);
                    view4.setText( des.getString("city").toString());

                    TextView view5 =  findViewById(R.id.info_text2);
                    view5.setText("Swipe right to see information about " + des.getString("city").toString() );

                    TextView view3 =  findViewById(R.id.info_text4);
                    view3.setText("Swipe right to see information about " + or.getString("city").toString() );

                    Log.i(TAG, info.toString());


                }catch (JSONException jsonException){
                    Log.e(TAG, "JSON Parsing error");
                }



                //findViewById(R.id.weather_progress_bar).setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Failed to get weather information");
                Log.i(TAG, error.getMessage());
                Log.i(TAG, error.getLocalizedMessage());
                Log.i(TAG, error.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }




    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureListener());
        }

        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        public void onSwipeLeft() {
        }

        public void onSwipeRight() {
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_VELOCITY_THRESHOLD = 100;
            private static final int SWIPE_DISTANCE_THRESHOLD = 100;

            public boolean onDown(MotionEvent e) {
                return true;
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float distanceX = e2.getX() - e1.getX();
                float distanceY = e2.getY() - e1.getY();
                if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (distanceX > 0)
                        onSwipeRight();
                    else
                        onSwipeLeft();
                    return true;
                }
                return false;
            }
        }
    }

}

