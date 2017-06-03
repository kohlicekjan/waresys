package cz.kohlicek.bpini.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;
import cz.kohlicek.bpini.model.Account;
import cz.kohlicek.bpini.ui.LoginActivity;

public class AccountActivity extends AppCompatActivity {

    @BindView(R.id.account_fullname)
    TextView accountFullname;
    @BindView(R.id.account_username)
    TextView accountUsername;
    @BindView(R.id.account_roles)
    TextView accountRoles;

    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);

        setTitle(R.string.account_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        account = Account.getLocalAccount(this);

        bindViewAccount();
    }

    private void bindViewAccount() {
        accountFullname.setText(account.getFullname());
        accountUsername.setText(account.getUsername());

        String[] roles = account.getRoles().toArray(new String[0]);
        for (int i = 0; i < roles.length; i++) {
            int role_id = getResources().getIdentifier("user_role_" + roles[i], "string", getPackageName());
            roles[i] = getResources().getString(role_id);
        }
        accountRoles.setText(TextUtils.join(", ", roles));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_logout:
                Account.clearLocalAccount(this);
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            case R.id.action_change_password:
                startActivity(new Intent(this, PasswordActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
