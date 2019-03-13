package cz.kohlicek.waresys.ui.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.waresys.R;
import cz.kohlicek.waresys.model.Account;
import cz.kohlicek.waresys.service.WaresysClient;
import cz.kohlicek.waresys.service.WaresysService;
import cz.kohlicek.waresys.ui.LoginActivity;
import cz.kohlicek.waresys.util.DialogUtils;
import cz.kohlicek.waresys.util.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordActivity extends AppCompatActivity {

    @BindView(R.id.input_password_old)
    TextInputEditText inputPasswordOld;
    @BindView(R.id.input_password_new)
    TextInputEditText inputPasswordNew;
    @BindView(R.id.input_password_again)
    TextInputEditText inputPasswordAgain;


    @BindView(R.id.layout_password_old)
    TextInputLayout layoutPasswordOld;
    @BindView(R.id.layout_password_new)
    TextInputLayout layoutPasswordNew;
    @BindView(R.id.layout_password_again)
    TextInputLayout layoutPasswordAgain;


    private WaresysService waresysService;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);

        setTitle(R.string.password_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        waresysService = WaresysClient.getInstance(this);
        account = Account.getLocalAccount(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void save() {

        if (!validate()) {
            return;
        }

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, R.string.no_connection_internet, Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog progressDialog = DialogUtils.showLoadingDialog(this);
        progressDialog.setMessage(getString(R.string.form_saving));


        String passwordOld = inputPasswordOld.getText().toString();
        String passwordNew = inputPasswordNew.getText().toString();
        Call<Void> call = waresysService.setPassword(passwordOld, passwordNew);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Toast.makeText(PasswordActivity.this, R.string.password_successful, Toast.LENGTH_LONG).show();
                    Account.clearLocalAccount(PasswordActivity.this);

                    Intent intent = new Intent(PasswordActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    WaresysClient.requestAnswerFailure(response.code(), PasswordActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PasswordActivity.this, R.string.no_connection_server, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validate() {
        boolean valid = true;

        String passwordOld = inputPasswordOld.getText().toString();
        String passwordNew = inputPasswordNew.getText().toString();
        String passwordAgain = inputPasswordAgain.getText().toString();

        if (passwordOld.isEmpty() || !passwordOld.equals(account.getPassword())) {
            layoutPasswordOld.setError(getString(R.string.password_validate_password_old));
            valid = false;
        } else {
            layoutPasswordOld.setErrorEnabled(false);
        }

        if (passwordNew.isEmpty()) {
            layoutPasswordNew.setError(getString(R.string.password_validate_password_new));
            valid = false;
        } else {
            layoutPasswordNew.setErrorEnabled(false);
        }

        if (passwordAgain.isEmpty() || !passwordAgain.equals(passwordNew)) {
            layoutPasswordAgain.setError(getString(R.string.password_validate_password_again));
            valid = false;
        } else {
            layoutPasswordAgain.setErrorEnabled(false);
        }

        return valid;
    }
}
