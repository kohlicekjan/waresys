package cz.kohlicek.bpini.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;
import cz.kohlicek.bpini.model.Account;
import cz.kohlicek.bpini.service.BPINIClient;
import cz.kohlicek.bpini.service.BPINIService;
import cz.kohlicek.bpini.ui.LoginActivity;
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


    private BPINIService bpiniService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);

        setTitle(R.string.password_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bpiniService = BPINIClient.getInstance(this);
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

        String passwordOld = inputPasswordOld.getText().toString();
        String passwordNew = inputPasswordNew.getText().toString();
        Call<Void> call = bpiniService.setPassword(passwordOld, passwordNew);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Account.clearLocalAccount(PasswordActivity.this);
                    startActivity(new Intent(PasswordActivity.this, LoginActivity.class));
                    finish();
                } else {

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
