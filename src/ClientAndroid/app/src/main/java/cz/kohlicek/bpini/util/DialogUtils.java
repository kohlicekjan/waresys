package cz.kohlicek.bpini.util;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import cz.kohlicek.bpini.R;

/**
 * Nástroje pro rychlé vytváření dialogů
 */
public final class DialogUtils {

    private DialogUtils() {

    }

    /**
     * Vytvoří načítací dialog
     * @param context
     * @return dialog
     */
    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);

        //progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        return progressDialog;
    }

    /**
     * Vytvoří dilaog s tlačítkem zrušit
     * @param context
     * @return dialog
     */
    public static AlertDialog DialogWithCancel(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

}