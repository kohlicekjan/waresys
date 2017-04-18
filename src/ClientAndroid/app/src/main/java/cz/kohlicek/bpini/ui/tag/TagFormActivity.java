package cz.kohlicek.bpini.ui.tag;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;

public class TagFormActivity extends AppCompatActivity {

    public static final String TAG_ID = "id_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_form);
        ButterKnife.bind(this);

        setTitle(R.string.tag_form_title_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
