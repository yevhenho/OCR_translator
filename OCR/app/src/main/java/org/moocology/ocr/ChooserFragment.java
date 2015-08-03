package org.moocology.ocr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

public class ChooserFragment extends DialogFragment {
    public final static String CODE_INT = "CODE_INT";
    public final static int CODE_CAMERA = 1;
    public final static int CODE_FILE = 2;

    public static ChooserFragment newInstance() {
        ChooserFragment fragment = new ChooserFragment();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.select_loading)
                .setPositiveButton(R.string.from_camera,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PackageManager pm = getActivity().getPackageManager();
                                if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) == true) {
                                    startChooseActivity(CODE_CAMERA);
                                } else
                                    Toast.makeText(getActivity(),
                                            "Your camera is not ready!",
                                            Toast.LENGTH_LONG).show();
                            }
                        })
                .setNegativeButton(R.string.from_file,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startChooseActivity(CODE_FILE);

                            }
                        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void startChooseActivity(int code) {
        Intent i = new Intent(getActivity(), PreSendActivity.class);
        i.putExtra(CODE_INT, code);
        startActivity(i);
    }
}
