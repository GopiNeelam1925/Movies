package com.example.manaadvice.popularmovietmdb.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;

import com.example.manaadvice.popularmovietmdb.R;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;

public class SortingDialogFragment extends DialogFragment {

    public static final String BROADCAST_SORT_PREFERENCE_CHANGED = "SortPreferenceChanged";
    public static final String TAG = "SortingDialogFragment";

    @Inject
    SortHelper sortHelper;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogStyle);
        builder.setTitle(getString(R.string.sort_dialog_title));
        builder.setNegativeButton(getString(R.string.action_cancel), null);
        builder.setSingleChoiceItems(R.array.pref_sort_by_labels,
                sortHelper.getSortByPreference().ordinal(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        sortHelper.saveSortByPreference(Sort.values()[which]);
                        sendSortPreferenceChangedBroadcast();
                        dialogInterface.dismiss();
                    }
                });
        return builder.create();
    }

    private void sendSortPreferenceChangedBroadcast() {
        Intent intent = new Intent(BROADCAST_SORT_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }
}

