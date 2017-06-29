package nf.co.thehoproject.ever6.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nf.co.thehoproject.ever6.R;
import nf.co.thehoproject.ever6.fragments.RedeemWall;
import nf.co.thehoproject.ever6.utl;

/**

 * TODO: Replace the implementation with code for your data type.
 */
public class RedeemWallAdapter extends RecyclerView.Adapter<RedeemWallAdapter.ViewHolder> {

    private final ArrayList<Redeem> mValues;
    private final RedeemWall.OnFragmentInteractionListener mListener;




    public RedeemWallAdapter(ArrayList<Redeem> items, RedeemWall.OnFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;

    }

    public static boolean downloadEnabled;

    Context ctx;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_row, parent, false);
        ctx=view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Redeem tr=mValues.get(position);
        holder.mItem = mValues.get(position);
        holder.title.setText(mValues.get(position).title);
        Typeface    tf_regular = Typeface.createFromAsset(ctx.getAssets(), "fonts/Roboto-Light.ttf");
        holder.title.setTypeface(tf_regular);


        holder.desc.setText(mValues.get(position).desc);
        tf_regular = Typeface.createFromAsset(ctx.getAssets(), "fonts/CaviarDreams.ttf");
        holder.desc.setTypeface(tf_regular);


        holder.coin.setText("   "+mValues.get(position).coins+" Coins");
        holder.coin.setTypeface(tf_regular);
        holder.coin.setTextColor(ctx.getResources().getColor(R.color.red_500));


        //   Log.d("Art Url :", "" + mValues.get(position).image);
        //Glide.with(ctx).load(mValues.get(position).image).into(holder.art);

        if(mValues.get(position).image!=null)
        if(mValues.get(position).image.length()>10)
        Picasso.with(ctx).load(""+mValues.get(position).image).into(holder.thumb);
        else
        holder.thumb.setImageResource(R.drawable.common_ic_googleplayservices);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onFragmentInteraction(holder.mItem,position);


                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView title;
        public final TextView desc,coin;

        public final ImageView thumb;


        public Redeem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.tit);
            desc = (TextView) view.findViewById(R.id.desc);
            coin = (TextView) view.findViewById(R.id.coins);
            thumb = (ImageView) view.findViewById(R.id.thumbnail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + desc.getText() + "'";
        }
    }




}
