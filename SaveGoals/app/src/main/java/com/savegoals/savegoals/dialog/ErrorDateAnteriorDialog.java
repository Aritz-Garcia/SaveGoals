package com.savegoals.savegoals.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.savegoals.savegoals.R;

public class ErrorDateAnteriorDialog extends DialogFragment {

    public interface ErrorDateAnteriorDialogListener {
        public void onDialogPositiveClickAnterior(DialogFragment dialog);
        public void onDialogNegativeClickAnterior(DialogFragment dialog);
    }
    ErrorDateAnteriorDialog.ErrorDateAnteriorDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ErrorDateAnteriorDialog.ErrorDateAnteriorDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement ErrorDateDialogListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.date_anterior_dialog_message)
                .setTitle(R.string.date_dialog_title)
                .setPositiveButton(R.string.date_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Aceptar
                        listener.onDialogPositiveClickAnterior(ErrorDateAnteriorDialog.this);
                    }
                })
                .setNegativeButton(R.string.date_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancelar
                        listener.onDialogNegativeClickAnterior(ErrorDateAnteriorDialog.this);
                    }
                });

        return builder.create();
    }
}
