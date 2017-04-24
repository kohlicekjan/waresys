package cz.kohlicek.bpini.ui.item;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;
import cz.kohlicek.bpini.model.Item;
import cz.kohlicek.bpini.model.User;
import cz.kohlicek.bpini.service.BPINIClient;
import cz.kohlicek.bpini.service.BPINIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemFormActivity extends AppCompatActivity {

    public static final String ITEM_ID = "item_id";
    private String itemId;

    @BindView(R.id.input_name)
    TextInputEditText inputName;
    @BindView(R.id.input_description)
    TextInputEditText inputDescription;
    @BindView(R.id.input_amount)
    TextInputEditText inputAmount;

    @BindView(R.id.layout_edit)
    LinearLayout layoutEdit;

    private BPINIService bpiniService;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_form);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bpiniService = BPINIClient.getInstance(this);


        if (getIntent().hasExtra(ITEM_ID)) {
            setTitle(R.string.item_form_title_edit);
            itemId = getIntent().getStringExtra(ITEM_ID);
            layoutEdit.setVisibility(View.VISIBLE);
        } else {
            setTitle(R.string.item_form_title_new);

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

    private void save(){
        item = new Item();
        item.setName(inputName.getText().toString());
        item.setDescription(inputDescription.getText().toString());

        Call<Item> call = bpiniService.createItem(item);

        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {

                Toast.makeText(ItemFormActivity.this,"Uloženo",Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {

            }
        });
    }

    private void load(String id){


    }


}
