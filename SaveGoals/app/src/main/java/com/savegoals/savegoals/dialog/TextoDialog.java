package com.savegoals.savegoals.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.savegoals.savegoals.R;

public class TextoDialog extends DialogFragment {

    public interface TextoDialogListener {
        public void onDialogPositiveClickTextoDialog(DialogFragment dialog);
        public void onDialogNegativeClickTextoDialog(DialogFragment dialog);
    }
    TextoDialogListener listener;

    String texto;
    String titulo;
    public TextoDialog(String texto, String titulo) {
        this.texto = texto;
        this.titulo = titulo;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (TextoDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement TextoDialogListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(texto)
                .setTitle(titulo)
                .setPositiveButton(R.string.date_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Aceptar
                        listener.onDialogPositiveClickTextoDialog(TextoDialog.this);
                    }
                })
                .setNegativeButton(R.string.date_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancelar
                        listener.onDialogNegativeClickTextoDialog(TextoDialog.this);
                    }
                });

        return builder.create();
    }

}
