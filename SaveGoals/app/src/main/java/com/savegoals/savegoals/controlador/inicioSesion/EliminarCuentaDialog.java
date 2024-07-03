package com.savegoals.savegoals.controlador.inicioSesion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.savegoals.savegoals.R;

public class EliminarCuentaDialog extends DialogFragment {

    public interface EliminarCuentaDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
    EliminarCuentaDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Busca en la jerarquía de fragments hasta encontrar uno que implemente la interfaz
            Fragment parentFragment = getParentFragment();
            while (parentFragment != null && !(parentFragment instanceof EliminarCuentaDialogListener)) {
                parentFragment = parentFragment.getParentFragment();
            }
            if (parentFragment != null) {
                listener = (EliminarCuentaDialogListener) parentFragment;
            } else {
                // Si no hay un Fragment padre, verifica la Activity
                listener = (EliminarCuentaDialogListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException((getParentFragment() != null ? getParentFragment().toString() : context.toString()) + " must implement EliminarCuentaDialogListener");
        }
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_message_eliminar_cuenta)
                .setTitle(R.string.eliminar_cuenta)
                .setPositiveButton(R.string.date_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Aceptar
                        listener.onDialogPositiveClick(EliminarCuentaDialog.this);
                    }
                })
                .setNegativeButton(R.string.date_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancelar
                        listener.onDialogNegativeClick(EliminarCuentaDialog.this);
                    }
                });

        return builder.create();
    }

}
