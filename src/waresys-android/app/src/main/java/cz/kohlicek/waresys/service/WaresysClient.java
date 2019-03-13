package cz.kohlicek.waresys.service;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import cz.kohlicek.waresys.R;
import cz.kohlicek.waresys.model.Account;
import cz.kohlicek.waresys.ui.LoginActivity;

/**
 *
 */
public class WaresysClient {

    /**
     * Vytvoření služby s vyplněnými přihlašovacími údaji
     *
     * @param context
     * @return
     */
    public static WaresysService getInstance(Context context) {
        Account account = Account.getLocalAccount(context);

        return ServiceGenerator.createService(WaresysService.class, account.getHost(), account.getUsername(), account.getPassword());
    }

    /**
     * Informování o nepovedeném požadavku
     *
     * @param code
     * @param activity
     */
    public static void requestAnswerFailure(int code, Activity activity) {
        Intent intentLogin = new Intent(activity, LoginActivity.class);
        intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        switch (code) {
            case 404:
                Toast.makeText(activity, R.string.request_notfound, Toast.LENGTH_LONG).show();
                activity.onBackPressed();
                break;
            case 401:
                Toast.makeText(activity, R.string.request_unauthorized, Toast.LENGTH_LONG).show();
                Account.clearLocalAccount(activity);
                activity.startActivity(intentLogin);
                activity.finish();
                break;
            case 403:
                Toast.makeText(activity, R.string.request_forbidden, Toast.LENGTH_LONG).show();
                Account.clearLocalAccount(activity);
                activity.startActivity(intentLogin);
                activity.finish();
                break;
            default:
                Toast.makeText(activity, R.string.request_error, Toast.LENGTH_LONG).show();
                activity.onBackPressed();
                break;
        }
    }

}
