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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class ShoppingListFragment extends Fragment implements NamedFragment {

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swipeRefreshLayout;

    private RequestQueue queue;

    private ArrayList<ShoppingItem> shoppingList;
    private ShoppingListAdapter adapter;

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

        // Views
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        getActivity().setTitle("Shopping list");

        shoppingList = new ArrayList<>();
        adapter = new ShoppingListAdapter(getContext(), shoppingList);
        queue = Volley.newRequestQueue(getActivity());

        ((ListView) view.findViewById(R.id.shopping_list_view)).setAdapter(adapter);


        (view.findViewById(R.id.clearShoppingListButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        swipeRefreshLayout = view.findViewById(R.id.swiperefreshlayout);

        getShoppingList();

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getShoppingList();
                    }
                }
        );

        return view;
    }

    private void getShoppingList() {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String baseIp = settings.getString("serverIp", null);

        if (baseIp == null || baseIp.equals(" ")) {
            Toast.makeText(getActivity(), "You did not set an IP address", Toast.LENGTH_SHORT).show();
            return;
        }

        final String query = String.format("{\"type\":\"select\",\"columns\":[\"item_id\"],\"conditions\":[{\"column\":\"user_id\",\"compareOperator\":\"=\",\"compareValue\":%d}]}", 2);

        String encodedquery = null;
        try {
            encodedquery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String encodedurl = "http://" + baseIp + "/tables/shoppings?query=" + encodedquery;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, encodedurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("response", response.toString());

                        JSONArray items = null;
                        try {
                            items = response.getJSONArray("entries");
                            for (int i = 0; i < items.length(); i++) {
                                //String item_id = ((JSONObject)items.get(i)).getString("item_id");
                                String name = ((JSONObject)items.get(i)).getString("name");
                                String imageLink = ((JSONObject)items.get(i)).getString("image_web_link");

                                ShoppingItem item = new ShoppingItem(name, imageLink);
                                retrieveImage(item);
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: delete this once it works
                        //serverShoppingList.add("5000204689204");
                        //callApi(adaptedList, adapter, "5000204689204");
                        // TODO: throw error
                        if (error != null && error.getMessage() != null) {
                            Log.d("Error.Response", error.getMessage());
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            if (getContext() != null)
                                Toast.makeText(getContext(), "Unknown error..", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        queue.add(request);

    }

    private void retrieveImage(final ShoppingItem item) {

        String url = item.getImageLink();

        ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                item.setImage(response);
                shoppingList.add(item);
                adapter.notifyDataSetChanged();
            }
        }, 100, 100, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", "Error while fetching image");
                Toast.makeText(getActivity(), "Error while fetching image", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
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
        if (queue != null) {
            queue.cancelAll(this);
        }
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
