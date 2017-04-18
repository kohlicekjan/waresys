package cz.kohlicek.bpini.ui.user;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;

public class UserFormActivity extends AppCompatActivity {

    public static final String USER_ID = "user_id";
    @BindView(R.id.input_username)
    TextInputEditText inputUsername;
    @BindView(R.id.input_password)
    TextInputEditText inputPassword;
    @BindView(R.id.layout_username)
    TextInputLayout layoutUsername;
    @BindView(R.id.layout_password)
    TextInputLayout layoutPassword;
    private String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);
        ButterKnife.bind(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(USER_ID)) {
            setTitle(R.string.user_form_title_edit);
            id_user = getIntent().getStringExtra(USER_ID);
        } else {
            setTitle(R.string.user_form_title_new);

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

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
