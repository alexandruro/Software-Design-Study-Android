package com.alexandruro.recyclinghelper;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DashboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment implements NamedFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        redrawPointsGoal();

        // Chart
        BarChart chart = (BarChart) getView().findViewById(R.id.chart);


        final List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, 38, "label"));
        entries.add(new BarEntry(2, 65, "label"));

        BarDataSet barDataSet = new BarDataSet(entries, "label");
        barDataSet.setColor(Color.RED);
        barDataSet.setValueTextColor(Color.BLACK);

        barDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                String percent = String.valueOf((int)value)+"%";
                String who;
                if(entry.getX()==1)
                    who = "You: ";
                else
                    who = "City: ";
                return who+percent;
            }
        });


        String[] labels = {"one", "two"};
        barDataSet.setStackLabels(labels);

        BarData barData = new BarData(barDataSet);
        chart.setData(barData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);
        leftAxis.setAxisMinimum(0);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        chart.getDescription().setText("");

        chart.getLegend().setEnabled(false);

        chart.invalidate(); // refresh



        ListView list1 = getView().findViewById(R.id.listView);
        CharSequence[] items = {"Jar of pickles"};
        list1.setAdapter(new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_list_item_1, items));

        ListView list2 = getView().findViewById(R.id.listView2);
        CharSequence[] discounts = {"50% discount for \"Big John's\" cheeseburger", "50% discount for \"Big John's\" cheeseburger", "50% discount for \"Big John's\" cheeseburger"};
        list2.setAdapter(new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_list_item_1, discounts));

    }

    public void redrawPointsGoal() {
        ((TextView)getView().findViewById(R.id.xpGoalTextView)).setText(getResources().getString(R.string.xp_goal_caption, MainActivity.getXpGoal()));
        ((TextView)getView().findViewById(R.id.xpTextView)).setText(getResources().getString(R.string.xp, MainActivity.xp));
        ColorfulRingProgressView progressView = getView().findViewById(R.id.crpv);
        progressView.setPercent((float)MainActivity.xp/MainActivity.getXpGoal()*100);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        getActivity().setTitle("Dashboard");
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

    @Override
    public String getFragmentName() {
        return "dashboard";
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
