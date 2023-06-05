package com.savegoals.savegoals.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.savegoals.savegoals.R;

public class ErrorDateDialog extends DialogFragment {

    public interface ErrorDateDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    ErrorDateDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ErrorDateDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement ErrorDateDialogListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.date_dialog_message)
                .setTitle(R.string.date_dialog_title)
                .setPositiveButton(R.string.date_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Aceptar
                        listener.onDialogPositiveClick(ErrorDateDialog.this);
                    }
                })
                .setNegativeButton(R.string.date_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancelar
                        listener.onDialogNegativeClick(ErrorDateDialog.this);
                    }
                });

        return builder.create();
    }

}
