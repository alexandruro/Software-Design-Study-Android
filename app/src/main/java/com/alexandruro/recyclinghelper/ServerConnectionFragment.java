package com.alexandruro.recyclinghelper;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ServerConnectionFragment extends Fragment {

    private static final String EXTRA_BARCODE = "barcode";

    private String barcode;

    private OnFragmentInteractionListener mListener;

    public ServerConnectionFragment() {
        // Required empty public constructor
    }

    public static ServerConnectionFragment newInstance() {
        return new ServerConnectionFragment();
    }

    public static ServerConnectionFragment newInstance(String barcode) {
        ServerConnectionFragment fragment = new ServerConnectionFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_BARCODE, barcode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            barcode = getArguments().getString(EXTRA_BARCODE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_server_connection, container, false);

        // add listeners for the buttons
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

        // Disable get button if we want to send a barcode
        if (barcode != null) {
            ((TextView) view.findViewById(R.id.parameterEditText)).setText(EXTRA_BARCODE);
            ((TextView) view.findViewById(R.id.valueEditText)).setText(barcode);
            view.findViewById(R.id.button_get).setEnabled(false);
        }

        return view;
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
        String ip = ((TextView) getView().findViewById(R.id.ipEditText)).getText().toString();

        new BackgroundTask(ip, "GET", this).execute();
    }

    public void doPostRequest() {
        String ip = ((TextView) getView().findViewById(R.id.ipEditText)).getText().toString();
        String parameter = ((TextView) getView().findViewById(R.id.parameterEditText)).getText().toString();
        String value = ((TextView) getView().findViewById(R.id.valueEditText)).getText().toString();

        new BackgroundTask(ip, "POST", parameter, value, this).execute();
    }

}

