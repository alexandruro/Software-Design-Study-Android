package com.alexandruro.recyclinghelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ShoppingListFragment extends Fragment implements NamedFragment {

    private OnFragmentInteractionListener mListener;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        final ArrayList<ShoppingItem> shoppingList = new ArrayList<>();
        final ShoppingListAdapter adapter = new ShoppingListAdapter(getContext(), shoppingList);

        getShoppingList(shoppingList, adapter);

        ((ListView)view.findViewById(R.id.shopping_list_view)).setAdapter(adapter);

        ((Button)view.findViewById(R.id.clearShoppingListButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ((SwipeRefreshLayout)view.findViewById(R.id.swiperefreshlayout)).setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // TODO: refresh
                    }
                }
        );

        return view;
    }

    private void getShoppingList(final ArrayList<ShoppingItem> adaptedList, final ShoppingListAdapter adapter) {

        final RequestQueue queue = Volley.newRequestQueue(getActivity());

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String baseIp = settings.getString("serverIp", null);

        if(baseIp==null || baseIp.equals(" ")) {
            Toast.makeText(getActivity(), "You did not set an IP address", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://" + baseIp + "/tables/shoppings?query=%7B%22type%22%3A%22select%22%2C%22columns%22%3A%5B%22item_id%22%5D%7D";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // TODO: interpret json

                        if(response.contains("not found")) {
                            // TODO: show "empty list" message
                            Toast.makeText(getActivity(), "The shopping list is empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        /*
                        found:
                        {"entries":[{"item_id":13,"barcode":5000204689204,"value":25}]}
                         */

                        JSONArray items = null;
                        try {
                            JSONObject jsonObject = new JSONObject(response.split("\n")[1]);
                            items = jsonObject.getJSONArray("entries");
                            for(int i=0;i<items.length();i++) {
                                String barcode = ((JSONObject)items.get(i)).getString("barcode");
                                //callApi(adaptedList, adapter, "5000204689204");
                                callApi(adaptedList, adapter, barcode);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: delete this once it works
                        //serverShoppingList.add("5000204689204");
                        callApi(adaptedList, adapter, "5000204689204");
                        // TODO: throw error
                        if(error!=null && error.getMessage()!=null) {
                            Log.d("Error.Response", error.getMessage());
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getActivity(), "Unknown error..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        queue.add(request);
    }

    private void callApi(final ArrayList<ShoppingItem> shoppingList, final ShoppingListAdapter adapter, String barcode) {
        final RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url ="https://www.barcodelookup.com/restapi?barcode=" + barcode + "&key=jifwdohph0d96leisdxhdtjnc3xapk";


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final String name = response.getJSONArray("result").getJSONObject(0).getJSONObject("details").getString("product_name");
                            String imageUrl = response.getJSONArray("result").getJSONObject(0).getJSONObject("images").getString("0");

                            ImageRequest request = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap response) {
                                    shoppingList.add(new ShoppingItem(name, response));
                                    adapter.notifyDataSetChanged();
                                }
                            }, 100, 100, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Error", "Error while fetching image");
                                }
                            });
                            queue.add(request);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
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

    @Override
    public String getFragmentName() {
        return "shoppingList";
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
}
