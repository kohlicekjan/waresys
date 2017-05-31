package cz.kohlicek.bpini.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;
import cz.kohlicek.bpini.model.Tag;
import cz.kohlicek.bpini.model.User;
import cz.kohlicek.bpini.service.BPINIClient;
import cz.kohlicek.bpini.service.BPINIService;
import cz.kohlicek.bpini.ui.tag.TagFormActivity;
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

    @BindView(R.id.loading)
    View loading;

    @BindView(R.id.form)
    View form;

    private BPINIService bpiniService;
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
        roles=Arrays.asList(getResources().getStringArray(R.array.user_roles_value));


        if (getIntent().hasExtra(USER_ID)) {
            setTitle(R.string.user_form_title_edit);
            userId = getIntent().getStringExtra(USER_ID);
            loading.setVisibility(View.VISIBLE);
            load(userId);
        } else {
            setTitle(R.string.user_form_title_new);
            form.setVisibility(View.VISIBLE);
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
        Call<User> call = null;
        if (user == null) {
            user = new User();
            user.setUsername(inputUsername.getText().toString());
            user.setPassword(inputPassword.getText().toString());
            user.setFirstname(inputFirstname.getText().toString());
            user.setLastname(inputLastname.getText().toString());
            user.getRoles().add(roles.get(inputRoles.getSelectedItemPosition()));
            call = bpiniService.createUser(user);

        } else {
            user.setUsername(inputUsername.getText().toString());
            user.setPassword(inputPassword.getText().toString());
            user.setFirstname(inputFirstname.getText().toString());
            user.setLastname(inputLastname.getText().toString());
            user.getRoles().clear();
            user.getRoles().add(roles.get(inputRoles.getSelectedItemPosition()));

            call = bpiniService.updateUser(userId, user);
        }

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Intent returnIntent = new Intent();
                //returnIntent.putExtra("result","asdas");
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void load(final String id) {


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
                }else{

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }

}
