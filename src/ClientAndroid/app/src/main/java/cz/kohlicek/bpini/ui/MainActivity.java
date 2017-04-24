package cz.kohlicek.bpini.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;
import cz.kohlicek.bpini.model.Account;
import cz.kohlicek.bpini.ui.account.AccountActivity;
import cz.kohlicek.bpini.ui.device.DeviceListFragment;
import cz.kohlicek.bpini.ui.item.ItemListFragment;
import cz.kohlicek.bpini.ui.tag.TagListFragment;
import cz.kohlicek.bpini.ui.user.UserListFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private Account account;
    private Menu navMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        account = Account.getLocalAccount(this);
        navMenu = navigationView.getMenu();

        if (account == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (savedInstanceState == null) {
            navMenu.setGroupVisible(R.id.group_admin, account.isRole(Account.ROLE_ADMIN));
            onNavigationItemSelected(navMenu.getItem(0));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (account != null) {
            View header = navigationView.getHeaderView(0);

            TextView username = (TextView) header.findViewById(R.id.header_username);
            TextView fullname = (TextView) header.findViewById(R.id.header_fullname);
            username.setText(account.getUsername());
            fullname.setText(account.getFullname());
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!navMenu.getItem(0).isChecked()) {
            onNavigationItemSelected(navMenu.getItem(0));

        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);

        if (!item.isChecked()) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.nav_items:
                    fragment = new ItemListFragment();
                    break;
                case R.id.nav_tags:
                    fragment = new TagListFragment();
                    break;
                case R.id.nav_devices:
                    fragment = new DeviceListFragment();
                    break;
                case R.id.nav_users:
                    fragment = new UserListFragment();
                    break;
                case R.id.nav_account:
                    startActivity(new Intent(this, AccountActivity.class));
                    return false;
                default:
                    return false;
            }

            if (fragment != null) {
                item.setChecked(true);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            }
        }

        return true;
    }
}
