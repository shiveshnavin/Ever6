package nf.co.thehoproject.ever6;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nf.co.thehoproject.ever6.adapters.Offer;
import nf.co.thehoproject.ever6.adapters.Redeem;
import nf.co.thehoproject.ever6.fragments.Blog;
import nf.co.thehoproject.ever6.fragments.OfferWall;
import nf.co.thehoproject.ever6.fragments.RedeemWall;

public class Home extends AppCompatActivity implements Blog.OnFragmentInteractionListener,OfferWall.OnFragmentInteractionListener,RedeemWall.OnFragmentInteractionListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;



    public static Context ctx;
    public static Activity act;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAnalytics firebaseAnalytics;


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static TextView noCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_home);

        Constants.checkFolder();
        ctx=this;
        act=this;
        firebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
        noCoins=(TextView)findViewById(R.id.ncoins);
        Firebase.setAndroidContext(ctx);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://everythingforstudent6.blogspot.com"));
                startActivity(i);
            }
        });
         loginFB();


        firebaseAnalytics.setAnalyticsCollectionEnabled(true);

        //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 20 seconds just for the fun
        firebaseAnalytics.setMinimumSessionDuration(2000);

        //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).
        firebaseAnalytics.setSessionTimeoutDuration(1000000);
        listenFirebase();;

        try {
            Integer coins=Integer.parseInt(utl.getCoins().replace("n",""));
            mcoins=coins;
            noCoins.setText("  "+mcoins);


        }
        catch ( Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:


                    if(mViewPager.getCurrentItem()==2){
                    if (Blog.wv1.canGoBack()) {
                        Blog.wv1.goBack();
                    } else {
                        finish();
                    }
                    return true;
                    }
                    else {
                        return super.onKeyDown(keyCode, event);
                    }


            default:
                return super.onKeyDown(keyCode, event);

        }
        }
        else
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(Offer offer,int pos) {


        Sensor br=new Sensor();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        intentFilter.addDataScheme("package");
        registerReceiver(br, intentFilter);

        Intent go=new Intent(Intent.ACTION_VIEW);
        go.setData(Uri.parse(offer.link));
        startActivity(go);


    }

    @Override
    public void onFragmentInteraction(final Redeem offer, int pos) {
        Integer coins=Integer.parseInt(utl.getCoins().replace("n",""));
        Integer c=Integer.parseInt(offer.coins);
        if(coins+c<0)
        {
            utl.toast(ctx,"Not Enough Coins !");
            return;
        }



        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(""+offer.title);
        builder.setView(R.layout.dialog_output);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

  dialog.dismiss();


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

                dialog.dismiss();;

            }
        });

        final AlertDialog.Builder
                alertDialogBuilder = new AlertDialog.Builder
                (ctx);
        alertDialogBuilder.setMessage("Are you sure you want to redeem "+offer.coins+" coins ?");
        alertDialogBuilder.setPositiveButton("Yes", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface
                                                dialog, int id) {

                        dialog.dismiss();;
                        dig = builder.create();
                        dig.show();
                        final  TextView button=(TextView)dig.findViewById(R.id.search_et);
                        try {
                            Integer coins=Integer.parseInt(utl.getCoins().replace("n",""));
                            Integer add=Integer.parseInt(offer.coins);
                            mcoins=coins+add;
                            utl.log("OLD : "+coins+" ADD : "+add+" new : "+mcoins);
                            utl.setCoins(""+mcoins);
                            noCoins.setText("  "+mcoins);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        button.setText(Html.fromHtml("<b>CLICK TO COPY TO CLIPBOARD</b><br>PASSWORD : <b>"+offer.link+"</b>"));
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ClipboardManager cm = (ClipboardManager)ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                                cm.setText(offer.link);
                                Toast.makeText(ctx, "Copied to clipboard", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

               dialogInterface.dismiss();;
            }
        });


        AlertDialog alertDialog
                = alertDialogBuilder.create();


        alertDialog.show();


    }


    public static  Integer mcoins;
    public static ArrayList<Offer> offers;
    public static class Sensor extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getData().toString();

            utl.log("" + data);
            String pack = data.replace("package:", "");

            for (Offer of:Home.offers){


                if(of.link.contains(pack)&&!utl.getCompleted().contains(pack)){
                    Notificn nf = new Notificn();
                    nf.mess = "Click to Open !";
                    nf.title = "Coins Awarded : "+of.coins;
                    nf.code = (int) SystemClock.uptimeMillis();
                    nf.ring = true;
                    Intent LaunchIntent = ctx.getPackageManager().getLaunchIntentForPackage(data.replace("package:", ""));
                    addNotification(nf, LaunchIntent);
                    utl.setCompleted(pack);

                        try {
                            Integer coins=Integer.parseInt(utl.getCoins().replace("n",""));
                            Integer add=Integer.parseInt(of.coins);
                            mcoins=coins+add;
                            utl.setCoins(""+mcoins);
                            noCoins.setText("  "+mcoins);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }



                    break;
                }

        }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            Integer coins=Integer.parseInt(utl.getCoins().replace("n",""));

            noCoins.setText("  "+coins);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Notificn {

        public String mess="def";
        public String title="def";
        public boolean ring=false;
        public String link="http://thehoproject.co.nf/contact";
        public Integer code=1112;
        public Notificn()
        {

        }

        @Override
        public String toString()
        {
            return ""+mess+"\n"+ring+"\n"+link+"\n"+code;

        }

    }

    public Notificn nf;
    public static void addNotification(Notificn nf) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder( ctx)
                        .setSmallIcon(R.drawable.icoon)
                        .setContentTitle(""+nf.title)
                        .setContentText(""+nf.mess);

        String url = nf.link;
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
        notificationIntent.setData(Uri.parse(url));
        notificationIntent = new Intent(Intent.ACTION_VIEW);
        notificationIntent.setData(Uri.parse(url));
        PendingIntent contentIntent = PendingIntent.getActivity( ctx, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(nf.code, builder.build());


        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone( ctx.getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void addNotification(Notificn nf,Intent notificationIntent) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder( ctx)
                        .setSmallIcon(R.drawable.icoon)
                        .setContentTitle(""+nf.title)
                        .setContentText(""+nf.mess);

        String url = nf.link;
          PendingIntent contentIntent = PendingIntent.getActivity( ctx, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(nf.code, builder.build());

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone( ctx.getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    Firebase fire;
    public void listenFirebase()
    {
        String app=getResources().getString(R.string.app_name).replace(" ","").toLowerCase()+utl.vr;

        Firebase.setAndroidContext(ctx);
        fire= new Firebase(Constants.FIREBASE_URL_CTRL+app);/*
       fire.child("status").setValue("online");
        fire.child("message").setValue("{\"code\":1001}");*/

        Log.d("FireURl",Constants.FIREBASE_URL_CTRL+app);
        Notificn ncn=new Notificn();
        fire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                int i = 0; //Log.d("MESS", " "+snapshot.toString());

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {


/************************************************************/
                    try {
                        String st=postSnapshot.getValue().toString();
                        if(!st.contains("online")&&postSnapshot.getKey().equalsIgnoreCase("status"))
                        {
                            Intent iz=new Intent(getBaseContext(),Error.class);
                            iz.putExtra("message",st);
                            startActivity(iz);
                            finish();;
                        }

                        if(postSnapshot.getKey().contains("downloads"))
                        {

                        }

                        if(postSnapshot.getKey().contains("message")||(postSnapshot.toString().contains("ring")
                                &&postSnapshot.toString().contains("message")))
                        {

                            try{

                                Log.d(TAG, ""+postSnapshot.child("mess").getValue());
                                nf=new Notificn();
                                nf.link=postSnapshot.child("link").getValue(String.class);
                                nf.mess=postSnapshot.child("mess").getValue(String.class);
                                nf.code=postSnapshot.child("code").getValue(Integer.class);
                                nf.title=postSnapshot.child("title").getValue(String.class);
                                nf.ring=postSnapshot.child("ring").getValue(Boolean.class);
                                String pref ;
                                FileOperations fop=new FileOperations();
                                pref="";
                                String folder= Environment.getExternalStorageDirectory().getPath();
                                String lstPath=(folder+"/.notifications");
                                File pat=new File(lstPath);
                                if(pat.exists())
                                {
                                    pref=fop.read(lstPath);
                                    Log.d(TAG, "onDataChange: "+pref);
                                }

                                if(!pref.contains(""+nf.code))
                                {
                                    fop.write(lstPath,""+pref+","+nf.code);
                                    addNotification(nf);
                                }
                                else {



                                     ///utl.addPref(ctx,"code",""+pref+","+nf.code);
                                }
                                Log.d(TAG, ""+nf.toString());

                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }


                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    Log.d("RES",postSnapshot.toString());
/************************************************************/
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent i=new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("http://everythingforstudent6.blogspot.com"));
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_offer_wall, container, false);
            return rootView;
        }
    }

    public static Fragment curFragment;
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            curFragment=new PlaceholderFragment().newInstance(position+1);
            switch (position)
            {

                case 0 :
                    curFragment=new OfferWall();
                    break;

                case 1 :

                    curFragment=new RedeemWall();
                    break;

                case 2 :

                    curFragment=new Blog();
                    break;
            }
            return curFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Offer Wall";
                case 1:
                    return "Redeem Wall";
                case 2:
                    return "Blog";
            }
            return null;
        }
    }

    public String getUser()
    {
        try{
            AccountManager man= AccountManager.get(this);
            Account[] acc=man.getAccountsByType("com.google");
            List<String> pos=new LinkedList<String>();
            for(Account a:acc)
            {
                pos.add(a.name);

            }

            if(!pos.isEmpty()&&pos.get(0)!=null)
            {
                String em=pos.get(0);
                return em;
            }}catch (Exception e)
        {
            e.printStackTrace();
        }
        return "null";

    }



    Dialog dig;
    public void getPhone()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Enter Your Phone.");
        builder.setView(R.layout.dialog_input);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

                EditText button=(EditText)dig.findViewById(R.id.search_et);
                final String phone=button.getText().toString();
                if(phone.contains("@")) {
                    user=phone;
                    registerFB();
                }
                else {
                    getPhone();
                }

                dialog.dismiss();


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

                dialog.dismiss();;
                getPhone();
            }
        });
        dig = builder.create();
        dig.show();



    }

    boolean regInProgress=false;
    public void registerFB()
    {

        Log.d("RE","REGSITER : "+user);
        mAuth.createUserWithEmailAndPassword(user, utl.pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(tag, "createUserWithEmail:onComplete:" + task.isSuccessful());


                        regInProgress=true;
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        try {
                            if (!task.isSuccessful()) {
                                Toast.makeText(ctx, "AUTH FAILED "+task.getResult().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch ( Exception e) {
                            Log.d("g",""+e.getMessage());
                            if(e.getMessage().contains("email address is already in use")){
                                loginPS();
                            }
                        }
                        // ...
                    }
                });
    }
    public void loginPS()
    {
        mAuth.signInWithEmailAndPassword(user, utl.pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(ctx, "Login Failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }

    FirebaseUser fuser;
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    public static String tag ="HOME";
    public void  loginFB()
    {

        Log.d(tag,"LOGIN");
        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                utl.showDig(false,ctx);
                if (user != null) {
                    fuser=user;
                  /*  if(regInProgress)
                    {
                        regInProgress=false;
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName("{[0]}")

                            .build();
                        fuser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                utl.toast(ctx,"Welcome !");
                            }
                        });

                    }*/
                    // User is signed in
                    Log.d(tag, "onAuthStateChanged:signed_in:" + user.getEmail());
                    if (!analStart) {
                        startAnal(user.getEmail());
                        analStart=true;
                    }


                } else {
                    // User is signed out
                    Log.d(tag, "onAuthStateChanged:signed_out");


                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP||!checkIfAlreadyhavePermission()) {
                        ActivityCompat.requestPermissions(act,
                                new String[]{android.Manifest.permission.GET_ACCOUNTS},
                                1);
                    }
                    else {
                        Home.user=getUser();
                        registerFB();
                    }
                }
            }
        };
        utl.showDig(true,ctx);
        mAuth.addAuthStateListener(mAuthListener);

    }

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    user=getUser();
                    try {

                        registerFB();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission .
                    Toast.makeText(ctx, "Permission denied to read your E-Mail ", Toast.LENGTH_SHORT).show();
                    getPhone();
                }
                return;
            }

        }
    }
    public static String  user="def",TAG="TAG";

    public static boolean analStart=false;
    public void startAnal(String UID)
    {

        //Sets the user ID property.
        Log.d("STARTTED","ANALYTICS");
        firebaseAnalytics.setUserId(UID);

    }

    public void logAnalEvent(String  key,String  desc,String type)
    {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, key);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, desc);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        Log.d("ANALYTICS","LOGGED EVENT\n"+"\n"+key+"\n"+desc+"\n"+type);
    }
}
