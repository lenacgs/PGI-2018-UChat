package com.pgi.uchat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;

public class DialogTwoButtons extends DialogFragment {

    public AlertDialog dialog;
    public int auxType = -1;

    public static final int TYPE_ERROR_LOAD_DATA = 17;


    public static final int TYPE_CONFIRM_PERMISSIONS = 99;

    public static DialogTwoButtons newInstance(int type, int title, int message, int buttonPos,
                                               int buttonNeg) {
        DialogTwoButtons frag = new DialogTwoButtons();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("title", title);
        args.putInt("message", message);
        args.putInt("buttonPos", buttonPos);
        args.putInt("buttonNeg", buttonNeg);
        frag.setArguments(args);
        return frag;
    }

    public static DialogTwoButtons newInstance(int type, int title, String message, int buttonPos,
                                               int buttonNeg) {
        DialogTwoButtons frag = new DialogTwoButtons();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("title", title);
        args.putInt("buttonPos", buttonPos);
        args.putInt("buttonNeg", buttonNeg);
        args.putString("messageStr", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final int type = getArguments().getInt("type");
        int title = getArguments().getInt("title");
        int message = getArguments().getInt("message");
        String messageStr = getArguments().getString("messageStr");
        int buttonPos = getArguments().getInt("buttonPos");
        int buttonNeg = getArguments().getInt("buttonNeg");

        String displayMessage = (messageStr == null ? getString(message) : messageStr);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(displayMessage)

                .setPositiveButton(buttonPos, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        switch (type) {
                            case TYPE_CONFIRM_PERMISSIONS:
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                                getActivity().finish();
                                break;
                        }

                    }
                }).setNegativeButton(buttonNeg, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                switch (type) {
                    case TYPE_CONFIRM_PERMISSIONS:
                        ((MainActivity) getActivity()).finish();
                        break;
                }
            }
        });
        // Create the AlertDialog object and return it
        dialog = builder.create();
        auxType = type;
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(auxType == TYPE_ERROR_LOAD_DATA)
            dialog.getButton(Dialog.BUTTON_NEGATIVE).setEnabled(false);
    }
}