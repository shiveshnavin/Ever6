package nf.co.thehoproject.ever6.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import nf.co.thehoproject.ever6.Constants;
import nf.co.thehoproject.ever6.Home;
import nf.co.thehoproject.ever6.R;
import nf.co.thehoproject.ever6.adapters.Offer;
import nf.co.thehoproject.ever6.adapters.OfferWallAdapter;
import nf.co.thehoproject.ever6.utl;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OfferWall.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OfferWall#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OfferWall extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public OfferWall() {
        // Required empty public constructor
    }

    public static RecyclerView rec;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OfferWall.
     */
    // TODO: Rename and change types and number of parameters
    public static OfferWall newInstance(String param1, String param2) {
        OfferWall fragment = new OfferWall();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public static View mView;
    public static  Context ctx;
    public static Activity act;
    public static ArrayList<Offer> offers;
    public Firebase fire;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ctx=getContext();
        act=getActivity();
        mView= inflater.inflate(R.layout.fragment_offer_wall, container, false);
        rec=(RecyclerView)mView.findViewById(R.id.rec);

        rec.setLayoutManager(new LinearLayoutManager(ctx));

        Firebase.setAndroidContext(ctx);
        fire=new Firebase(Constants.FIREBASE_URL+"offers");
    /*    fire.child("Gangster_Vegas_Highly_Compressed").setValue("{" +
                "  \"coins\":\"object\"," +
                "  \"desc\":\"object\"," +
                "  \"procedure\":\"object\"," +
                "  \"link\":\"object\"," +
                "  \"timemins\":1001," +
                "}");*/
        fire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                offers=new ArrayList<Offer>();
                for (DataSnapshot da: dataSnapshot.getChildren()) {

                    try {
                        Offer of=new Offer();
                        of.title= da.getKey().replace("_"," ");
                        of.image=da.child("image").getValue(String.class);
                        of.coins=da.child("coins").getValue(String.class);
                        of.desc=da.child("desc").getValue(String.class);
                        of.procedure=da.child("procedure").getValue(String.class);
                        of.timemins =da.child("timemins").getValue(Integer.class);
                        of.link =da.child("link").getValue(String.class);
                        String comp=utl.getCompleted();
                        String play="http://play.google.com/store/apps/details?id=";
                        if(!comp.contains(of.link.replace(play,"")))
                        offers.add(of);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

                showList(offers);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        return mView;
    }

    public void showList(ArrayList<Offer> offers)
    {

        OfferWallAdapter adapter=new OfferWallAdapter(offers,mListener);

        Home.offers=offers;
        rec.removeAllViews();
        rec.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //mListener.onFragmentInteraction(uri);
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Offer offer, int pos);
    }
}
