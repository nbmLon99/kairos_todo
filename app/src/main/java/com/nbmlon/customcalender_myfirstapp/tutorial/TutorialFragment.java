package com.nbmlon.customcalender_myfirstapp.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.nbmlon.customcalender_myfirstapp.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TutorialFragment#getFragment(int)} factory method to
 * create an instance of this fragment.
 */
public class TutorialFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final ArrayList<Integer> images = new ArrayList<Integer>(Arrays.asList(
            R.drawable.tutorial_1,
            R.drawable.tutorial_2,
            R.drawable.tutorial_3
    ));



    // TODO: Rename and change types of parameters
    private int mPosition;


    public TutorialFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Parameter 1.
     * @return A new instance of fragment TutorialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TutorialFragment getFragment(int position) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        ImageView iv =  view.findViewById(R.id.tutorial_iv);

        iv.setImageResource(images.get(mPosition));
        return view;
    }
}