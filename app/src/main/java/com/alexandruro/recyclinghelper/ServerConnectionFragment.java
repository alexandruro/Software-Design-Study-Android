package com.alexandruro.recyclinghelper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ServerConnectionFragment extends Fragment {

    private static final String METHOD = "method";

    private String method;

    private OnFragmentInteractionListener mListener;

    public ServerConnectionFragment() {
        // Required empty public constructor
    }

    public static ServerConnectionFragment newInstance() {
        ServerConnectionFragment fragment = new ServerConnectionFragment();
//        Bundle args = new Bundle();
//        args.putString(METHOD, method);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_server_connection, container, false);
        (view.findViewById(R.id.button_get)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGetRequest();
            }
        });
        (view.findViewById(R.id.button_post)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPostRequest();
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void doGetRequest() {

        String ip = ((TextView)getView().findViewById(R.id.ipEditText)).getText().toString();
        TextView result = ((TextView)getView().findViewById(R.id.resultTextView));

        new BackgroundTask(ip, "GET", result).execute();

    }

    public void doPostRequest() {
        String ip = ((TextView)getView().findViewById(R.id.ipEditText)).getText().toString();
        String parameter = ((TextView)getView().findViewById(R.id.parameterEditText)).getText().toString();
        String value = ((TextView)getView().findViewById(R.id.valueEditText)).getText().toString();
        TextView result = getView().findViewById(R.id.resultTextView);

        new BackgroundTask(ip, "POST", parameter, value, result).execute();

    }

}

