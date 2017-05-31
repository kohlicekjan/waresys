package cz.kohlicek.bpini.ui.tag;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;
import cz.kohlicek.bpini.model.Item;
import cz.kohlicek.bpini.model.Tag;
import cz.kohlicek.bpini.service.BPINIClient;
import cz.kohlicek.bpini.service.BPINIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagFormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG_ID = "tag_id";

    @BindView(R.id.tag_uid)
    TextView tagUid;
    @BindView(R.id.input_type)
    Spinner inputType;


    @BindView(R.id.label_item)
    TextView labelItem;
    @BindView(R.id.input_item)
    Spinner inputItem;

    @BindView(R.id.form)
    View form;

    @BindView(R.id.loading)
    View loading;

    private BPINIService bpiniService;
    private String tagId;
    private Tag tag;
    private List<String> types;
    private List<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_form);
        ButterKnife.bind(this);

        setTitle(R.string.tag_form_title_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bpiniService = BPINIClient.getInstance(this);

        types = Arrays.asList(getResources().getStringArray(R.array.tag_types_value));


        tagId = getIntent().getStringExtra(TAG_ID);


        load(tagId);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

    }

    public void onNothingSelected(AdapterView<?> parent) {

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
        tag.setType(types.get(inputType.getSelectedItemPosition()));
        tag.setItem((Item) inputItem.getSelectedItem());

        Call<Tag> call = bpiniService.updateTag(tag.getId(), tag);

        call.enqueue(new Callback<Tag>() {
            @Override
            public void onResponse(Call<Tag> call, Response<Tag> response) {
                //chyba
            }

            @Override
            public void onFailure(Call<Tag> call, Throwable t) {

            }
        });
        Toast.makeText(TagFormActivity.this, "Tag uložen", Toast.LENGTH_LONG).show();
        finish();
    }

    private void load(final String id) {

        final List<Item> items = new ArrayList<>();

        Call<List<Item>> call2 = bpiniService.getItems("-created", 0);
        call2.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful()) {
                    items.addAll(response.body());
                    ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(TagFormActivity.this, android.R.layout.simple_spinner_item, items.toArray());

                    inputItem.setAdapter(spinnerArrayAdapter);
                }else{

                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {

            }
        });

        Call<Tag> call = bpiniService.getTag(id);

        call.enqueue(new Callback<Tag>() {
            @Override
            public void onResponse(Call<Tag> call, Response<Tag> response) {
                if (response.isSuccessful()) {
                    tag = response.body();
                    tagUid.setText(tag.getUid());
                    inputType.setSelection(types.indexOf(tag.getType()));
                    if (tag.getItem() != null)
                        inputItem.setSelection(items.indexOf(tag.getItem()));

                    loading.setVisibility(View.GONE);
                    form.setVisibility(View.VISIBLE);
                }else{

                }
            }

            @Override
            public void onFailure(Call<Tag> call, Throwable t) {

            }
        });


    }

}
