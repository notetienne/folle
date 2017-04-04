package com.example.etienne.folle_e;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InsertCaddieFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InsertCaddieFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InsertCaddieFrag extends Fragment {

    private TextView DisplayNom ;
    private TextView DisplayPoids ;
    private TextView DisplayPrix ;
    private ImageView avatar ;


/*
        avatar.setImageDrawable(getImage(URLImage));
        DisplayNom.setText(nom);
        DisplayPrix.setText(prix + " €");
        DisplayPoids.setText(poids + "g");
        DisplayCompo.setText("Composition : "+ compoformat);



});


        }

public Drawable getImage(String url){
        try {
        URL urle = new URL(url);
        Object content = urle.getContent();

        InputStream inputstream = (InputStream)content;
        Drawable drawable = Drawable.createFromStream(inputstream, "src");
        return drawable;
        } catch (Exception e) {
        e.printStackTrace();
        return null;
        }*/



// TODO: Rename and change types of parameters
private String mParam1;
private String mParam2;

private OnFragmentInteractionListener mListener;

public InsertCaddieFrag() {
        // Required empty public constructor
        }

/**
 * Use this factory method to create a new instance of
 * this fragment using the provided parameters.
 *
 * @param param1 Parameter 1.
 * @param param2 Parameter 2.
 * @return A new instance of fragment InsertCaddieFrag.
 */
// TODO: Rename and change types and number of parameters
public static InsertCaddieFrag newInstance(String param1, String param2) {
        InsertCaddieFrag fragment = new InsertCaddieFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
        }

@Override
public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        }

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insert_caddie, container, false);

        /*DisplayNom = (TextView) findViewById(R.id.nom_art);
        TextView DisplayPoids = (TextView) findViewById(R.id.poids_art);
        TextView DisplayPrix = (TextView) findViewById(R.id.prix_art);
        ImageView avatar = (ImageView) findViewById(R.id.avatar);*/
        }

// TODO: Rename method, update argument and hook method into UI event
public void onButtonPressed(Uri uri) {
        if (mListener != null) {
        mListener.onAnnul(uri);
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
    void onAnnul(Uri uri);
}


}
