package com.alexandruro.recyclinghelper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AccountFragment.OnFragmentInteractionListener,
        DashboardFragment.OnFragmentInteractionListener,
        ServerConnectionFragment.OnFragmentInteractionListener,
        ApiCallFragment.OnFragmentInteractionListener,
        ShoppingListFragment.OnFragmentInteractionListener,
        BackgroundTaskResult {

    // request codes for starting BarcodeCaptureActivity
    public static final int BARCODE_TO_TEST = 0;
    public static final int BARCODE_TO_SEND = 1;

    // for testing purposes
    private static final int DEFAULT_XP = 35;

    static int xp;

    public static int getXpGoal() {
        return 200;
    }

    public void setXp(int newXp) {
        xp = newXp;


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("xp", xp);
        editor.commit();

        NamedFragment currentFragment = (NamedFragment) getSupportFragmentManager().findFragmentById(R.id.mainFrame);
        if (currentFragment.getFragmentName().equals("dashboard"))
            ((DashboardFragment) currentFragment).redrawPointsGoal();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // FAB
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBarcodeCaptureActivity(true, false, BARCODE_TO_SEND);
            }
        });

        // Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_dashboard);

        // Set initial fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, new DashboardFragment());
        ft.commit();

        // Restore preference
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        xp = settings.getInt("xp", DEFAULT_XP);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_TO_TEST || requestCode == BARCODE_TO_SEND)

            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    if (requestCode == BARCODE_TO_SEND) {
//                        Fragment fragment = ServerConnectionFragment.newInstance(barcode.displayValue);
//                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                        ft.replace(R.id.mainFrame, fragment);
//                        ft.commitAllowingStateLoss();

                        Toast.makeText(this, "Sending barcode to the server..", Toast.LENGTH_SHORT).show();

                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                        final String baseIp = settings.getString("serverIp", null);

                        //localhost:4444/tables/items?query={"type":"select","columns":["item_id"],"conditions":[{"column":"barcode","compareOperator":"=","compareValue":6}]}
                        //localhost:4444/tables/items?query={"type":"select","columns":["item_id","name","IF(value>0,TRUE,FALSE) as recyclable],"conditions":[{"column":"barcode","compareOperator":"=","compareValue":6}]}
                        final String query = String.format("{\"type\":\"select\",\"columns\":[\"item_id\",\"name\",\"value\"," +
                                "\"IF(value > 0, TRUE, FALSE) as recyclable\"],\"conditions\":[{\"column\":\"barcode\"," +
                                "\"compareOperator\":\"=\",\"compareValue\":\"%1$s\"}]}", barcode.displayValue);

                        String encodedquery = null;
                        try {
                            encodedquery = URLEncoder.encode(query, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String encodedurl = "http://" + baseIp + "/tables/items?query=" + encodedquery;

                        final RequestQueue queue = Volley.newRequestQueue(this);
                        StringRequest getRequest = new StringRequest(Request.Method.GET, encodedurl,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if(response.contains("could not find"))
                                            Toast.makeText(MainActivity.this, "Could not find item in database", Toast.LENGTH_LONG).show();
                                        else if(response.contains("NOT RECYCLABLE"))
                                            Toast.makeText(MainActivity.this, "The item is not recyclable", Toast.LENGTH_LONG).show();
                                        else {
                                            //Toast.makeText(MainActivity.this, "The item is recyclable", Toast.LENGTH_LONG).show();
                                            try {
                                                JSONObject jsonObject = new JSONObject(response.split("\n")[2]);
                                                JSONObject entry = (JSONObject)jsonObject.getJSONArray("entries").get(0);
                                                int value = entry.getInt("value");
                                                Toast.makeText(MainActivity.this, "The item is recyclable. " + value + " points were added to your account.", Toast.LENGTH_LONG).show();
                                                setXp(xp + value);
                                                int user_id = entry.getInt("item_id");

                                                // Add to shopping list
                                                String query = String.format("{\"type\":\"insert\",\"columns\":[\"user_id\",\"item_id\"],\"values\":[1,%1$d]}", user_id);
                                                String encodedquery = null;
                                                try {
                                                    encodedquery = URLEncoder.encode(query, "UTF-8");
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }
                                                String encodedurl = "http://" + baseIp + "/tables/shoppings?query=" + encodedquery;
                                                query_add_shopping(queue, encodedurl);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(MainActivity.this, "it failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (error != null && error.getMessage() != null) {
                                            Log.d("Error.Response", error.getMessage());
                                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(MainActivity.this, "An unknown error occurred..", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );

                        queue.add(getRequest);


//                        BackgroundTask backgroundTask = new BackgroundTask(null
//                                , "POST", "barcode", barcode.displayValue, this);
//                        backgroundTask.execute();

                    } else
                        Toast.makeText(this, barcode.displayValue, Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(this, "No barcode", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "Error: barcode detection unsuccessful", Toast.LENGTH_SHORT).show();
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void query_add_shopping(RequestQueue queue, String url) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, "Added to shopping list", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Could not add to shopping list", Toast.LENGTH_LONG).show();
                    }
                });
        queue.add(request);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            // Testing menu
            case R.id.nav_test_http:
                fragment = ServerConnectionFragment.newInstance();
                break;
            case R.id.nav_test_barcode:
                startBarcodeCaptureActivity(true, false, BARCODE_TO_TEST);
                break;
            case R.id.nav_test_barcode_to_http:
                startBarcodeCaptureActivity(true, false, BARCODE_TO_SEND);
                break;
            case R.id.nav_send:

                // Create notification channel
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    String id = "reminder_channel";
                    CharSequence name = "Reminders";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel mChannel = new NotificationChannel(id, name, importance);

                    mChannel.setDescription("This channel contains reminders to not forget to recycle.");
                    mChannel.enableLights(true);
                    // Sets the notification light color for notifications posted to this
                    // channel, if the device supports this feature.
                    mChannel.setLightColor(Color.RED);
                    mChannel.enableVibration(true);
                    //mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    mNotificationManager.createNotificationChannel(mChannel);
                }

                // The id of the channel.
                String CHANNEL_ID = "reminder_channel";
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_menu_send)
                                .setContentTitle("Recycle something!")
                                .setContentText("You have not recycled in a while!");

                Intent resultIntent = new Intent(this, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(0, mBuilder.build());

                break;

            case R.id.nav_test_add_xp:
                setXp(xp + 20);
                break;

            case R.id.nav_test_subtract_xp:
                setXp(xp - 20);
                break;

            case R.id.nav_test_api_call:
                fragment = new ApiCallFragment();
                break;

            case R.id.nav_test_shopping_list:
                fragment = new ShoppingListFragment();
                break;

            // Main menu
            case R.id.nav_dashboard:
                fragment = new DashboardFragment();
                break;
            case R.id.nav_account:
                fragment = new AccountFragment();
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startBarcodeCaptureActivity(boolean autoFocus, boolean useFlash, int requestCode) {
        Intent intent = new Intent(this, BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash);

        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void backgroundTaskResult(boolean success, String result) {
        if (success) {
            if (result.equals("0"))
                Toast.makeText(this, "The item is not recyclable.", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this, result + " points will be added to your account", Toast.LENGTH_SHORT).show();
                setXp(xp + 10);
            }
        } else {
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
    }
}
