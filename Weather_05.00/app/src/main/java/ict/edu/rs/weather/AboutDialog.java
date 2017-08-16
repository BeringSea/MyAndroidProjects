package ict.edu.rs.weather;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by m.spasojevic on 4/27/2016.
 */
public class AboutDialog {
    private AlertDialog alertDialog;

    public AboutDialog(final Activity activity) {
        this.alertDialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.app_name)
                .setMessage("Version, Copyright,...")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Contact Me", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setData(Uri.parse("mailto:"));
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Support");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"marko.dj.spasojevic@ict.edu.rs"});
                        intent.putExtra(Intent.EXTRA_TEXT, "Dear support,");
                        activity.startActivity(intent);
                    }
                }).create();
    }

    public void show()
    {
        this.alertDialog.show();
    }
}
