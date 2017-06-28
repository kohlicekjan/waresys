package cz.kohlicek.bpini.ui.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;
import cz.kohlicek.bpini.model.Account;
import cz.kohlicek.bpini.model.User;
import cz.kohlicek.bpini.service.BPINIClient;
import cz.kohlicek.bpini.service.BPINIService;
import cz.kohlicek.bpini.util.DialogUtils;
import cz.kohlicek.bpini.util.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFormActivity extends AppCompatActivity {

    public static final String USER_ID = "user_id";
    public static final int REQUEST_CODE = 4;

    @BindView(R.id.input_username)
    TextInputEditText inputUsername;

    @BindView(R.id.layout_username)
    TextInputLayout layoutUsername;

    @BindView(R.id.input_password)
    TextInputEditText inputPassword;

    @BindView(R.id.layout_password)
    TextInputLayout layoutPassword;

    @BindView(R.id.input_firstname)
    TextInputEditText inputFirstname;

    @BindView(R.id.layout_firstname)
    TextInputLayout layoutFirstname;

    @BindView(R.id.input_lastname)
    TextInputEditText inputLastname;

    @BindView(R.id.layout_lastname)
    TextInputLayout layoutLastname;

    @BindView(R.id.input_roles)
    Spinner inputRoles;

    @BindView(R.id.user_roles)
    TextView userRoles;

    @BindView(R.id.loading)
    View loading;

    @BindView(R.id.form)
    View form;

    private BPINIService bpiniService;
    private Account account;
    private String userId;
    private User user;
    private List<String> roles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bpiniService = BPINIClient.getInstance(this);
        account = Account.getLocalAccount(this);
        roles = Arrays.asList(getResources().getStringArray(R.array.user_roles_value));

        if (account.getUsername().equals("admin")) {
            inputRoles.setVisibility(View.VISIBLE);
            userRoles.setVisibility(View.GONE);
        }

        if (getIntent().hasExtra(USER_ID)) {
            setTitle(R.string.user_form_title_edit);
            userId = getIntent().getStringExtra(USER_ID);
            load(userId);
        } else {
            setTitle(R.string.user_form_title_new);
            form.setVisibility(View.VISIBLE);
            inputRoles.setSelection(roles.indexOf("user"));
        }
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

        if (user == null) {
            user = new User();
        }

        user.setUsername(inputUsername.getText().toString());
        user.setPassword(inputPassword.getText().toString());
        user.setFirstname(inputFirstname.getText().toString());
        user.setLastname(inputLastname.getText().toString());
        user.getRoles().clear();
        user.getRoles().add(roles.get(inputRoles.getSelectedItemPosition()));

        Call<User> call = null;
        if (userId != null) {
            call = bpiniService.updateUser(userId, user);
        } else {
            call = bpiniService.createUser(user);
        }

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Toast.makeText(UserFormActivity.this, R.string.form_saved, Toast.LENGTH_LONG).show();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(USER_ID, response.body().getId());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    BPINIClient.requestAnswerFailure(response.code(), UserFormActivity.this);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(UserFormActivity.this, R.string.no_connection_server, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        if (username.isEmpty() || username.length() < 3) {
            layoutUsername.setError(getString(R.string.user_form_validate_username));
            valid = false;
        } else {
            layoutUsername.setErrorEnabled(false);
        }

        if (user == null && password.isEmpty()) {
            layoutPassword.setError(getString(R.string.user_form_validate_password));
            valid = false;
        } else {
            layoutPassword.setErrorEnabled(false);
        }

        return valid;
    }

    private void load(final String id) {
        loading.setVisibility(View.VISIBLE);

        Call<User> call = bpiniService.getUser(id);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {

                    user = response.body();
                    inputUsername.setText(user.getUsername());
                    inputFirstname.setText(user.getFirstname());
                    inputLastname.setText(user.getLastname());
                    inputRoles.setSelection(roles.indexOf(user.getRoles().get(0)));


                    loading.setVisibility(View.GONE);
                    form.setVisibility(View.VISIBLE);
                } else {
                    BPINIClient.requestAnswerFailure(response.code(), UserFormActivity.this);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserFormActivity.this, R.string.no_connection_server, Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });

    }

}
