package nf.co.thehoproject.ever6;

import android.os.Environment;

import java.io.File;

/**
 * Created by shivesh on 24/6/16.
 */
public class Constants {


    public static String FIREBASE_URL ="https://ever6aff.firebaseio.com/";
    public static String FIREBASE_URL_CTRL ="https://shiveshnavin.firebaseio.com/";
    public static void checkFolder()
    {
        File f=new File((Environment.getExternalStorageDirectory().getPath().toString()+"/.vend0"));
        if(!f.exists())
        {
            f.mkdir();
        }
    }
    public static String dataFile() {
     return   (Environment.getExternalStorageDirectory().getPath().toString() + "/.vend0/.status.sta");
    }

    public static String dataFileCompleted() {
        return   (Environment.getExternalStorageDirectory().getPath().toString() + "/.vend0/.android.sta");
    }


    public static String dataFileCoin() {
        return   (Environment.getExternalStorageDirectory().getPath().toString() + "/.vend0/.vending.sta");
    }


    public static String terminal="http://thehoproject.co.nf/terminal.php?app=musiccloud1";

    public static class Stat
    {

        String app="";
        String user="";
        String status="";

    }



}
