package cz.kohlicek.bpini.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;
import cz.kohlicek.bpini.model.Account;
import cz.kohlicek.bpini.service.BPINIService;
import cz.kohlicek.bpini.service.ServiceGenerator;
import cz.kohlicek.bpini.util.DialogUtils;
import cz.kohlicek.bpini.util.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.input_username)
    TextInputEditText inputUsername;
    @BindView(R.id.input_host)
    TextInputEditText inputHost;
    @BindView(R.id.input_password)
    TextInputEditText inputPassword;


    @BindView(R.id.layout_username)
    TextInputLayout layoutUsername;
    @BindView(R.id.layout_host)
    TextInputLayout layoutHost;
    @BindView(R.id.layout_password)
    TextInputLayout layoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setTitle(R.string.app_name);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                login();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    private void login() {

        if (!validate()) {
            return;
        }

        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, R.string.no_connection_internet, Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog progressDialog = DialogUtils.showLoadingDialog(this);
        progressDialog.setMessage(getString(R.string.login_loggingin));

        final String host = "http://" + inputHost.getText().toString();
        String username = inputUsername.getText().toString();
        final String password = inputPassword.getText().toString();


        BPINIService bpiniService = ServiceGenerator.createService(BPINIService.class, host, username, password);

        Call<Account> call = bpiniService.getAccount();

        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Account account = response.body();

                    account.setHost(host);
                    account.setPassword(password);
                    account.saveLocalAccount(LoginActivity.this);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.login_validate_auth, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, R.string.no_connection_server, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean validate() {
        boolean valid = true;

        String host = inputHost.getText().toString();
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        if (host.isEmpty()) {
            layoutHost.setError(getString(R.string.login_validate_host));
            valid = false;
        } else {
            layoutHost.setErrorEnabled(false);
        }

        if (username.isEmpty()) {
            layoutUsername.setError(getString(R.string.login_validate_username));
            valid = false;
        } else {
            layoutUsername.setErrorEnabled(false);
        }

        if (password.isEmpty()) {
            layoutPassword.setError(getString(R.string.login_validate_password));
            valid = false;
        } else {
            layoutPassword.setErrorEnabled(false);
        }

        return valid;
    }
}
