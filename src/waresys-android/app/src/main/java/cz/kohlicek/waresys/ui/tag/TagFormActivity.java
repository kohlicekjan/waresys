package cz.kohlicek.waresys.ui.tag;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.waresys.R;
import cz.kohlicek.waresys.model.Item;
import cz.kohlicek.waresys.model.Tag;
import cz.kohlicek.waresys.service.WaresysClient;
import cz.kohlicek.waresys.service.WaresysService;
import cz.kohlicek.waresys.util.DialogUtils;
import cz.kohlicek.waresys.util.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagFormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TextWatcher, View.OnClickListener {

    public static final String TAG_ID = "tag_id";
    public static final int REQUEST_CODE = 6;

    @BindView(R.id.tag_uid)
    TextView tagUid;
    @BindView(R.id.input_type)
    Spinner inputType;


    @BindView(R.id.form_item)
    View formItem;

    @BindView(R.id.label_item)
    TextView labelItem;
    @BindView(R.id.input_item_suggest)
    AutoCompleteTextView inputItemSuggest;
    @BindView(R.id.loading_item)
    View loadingItem;
    @BindView(R.id.input_item_empty)
    View inputItemEmpty;
    @BindView(R.id.layout_item)
    View layoutItem;
    @BindView(R.id.item_name)
    TextView itemName;
    @BindView(R.id.item_description)
    TextView itemDescription;
    @BindView(R.id.item_amount)
    TextView itemAmount;


    @BindView(R.id.form)
    View form;

    @BindView(R.id.loading)
    View loading;

    private WaresysService waresysService;
    private String tagId;
    private Tag tag;
    private List<String> types;
    private Item selectItem;
    private ArrayAdapter itemSuggestAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_form);
        ButterKnife.bind(this);

        setTitle(R.string.tag_form_title_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        waresysService = WaresysClient.getInstance(this);
        types = Arrays.asList(getResources().getStringArray(R.array.tag_types_value));

        tagId = getIntent().getStringExtra(TAG_ID);
        load(tagId);

        itemSuggestAdapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line);
        inputItemSuggest.setAdapter(itemSuggestAdapter);
        inputItemSuggest.addTextChangedListener(this);
        inputItemSuggest.setOnClickListener(this);

        inputType.setOnItemSelectedListener(this);
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        searchItem(s.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //EMPTY
    }

    @Override
    public void afterTextChanged(Editable s) {
        //EMPTY
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.input_item_suggest:
                inputItemSuggest.showDropDown();
                break;
        }
    }


    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (types.get(inputType.getSelectedItemPosition()).equals(Tag.TYPE_ITEM)) {
            formItem.setVisibility(View.VISIBLE);
        } else {
            formItem.setVisibility(View.GONE);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        //EMPTY
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
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, R.string.no_connection_internet, Toast.LENGTH_LONG).show();
            return;
        }

        if (!validate()) {
            return;
        }

        final ProgressDialog progressDialog = DialogUtils.showLoadingDialog(this);
        progressDialog.setMessage(getString(R.string.form_saving));


        String type = types.get(inputType.getSelectedItemPosition());
        tag.setType(type);
        if (type.equals(Tag.TYPE_ITEM)) {
            tag.setItem(selectItem);
        } else {
            tag.setItem(null);
        }

        Call<Tag> call = waresysService.updateTag(tag.getId(), tag);

        call.enqueue(new Callback<Tag>() {
            @Override
            public void onResponse(Call<Tag> call, Response<Tag> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Toast.makeText(TagFormActivity.this, R.string.form_saved, Toast.LENGTH_LONG).show();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(TAG_ID, response.body().getId());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    WaresysClient.requestAnswerFailure(response.code(), TagFormActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Tag> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(TagFormActivity.this, R.string.no_connection_server, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void load(final String id) {
        Call<Tag> call = waresysService.getTag(id);
        call.enqueue(new Callback<Tag>() {
            @Override
            public void onResponse(Call<Tag> call, Response<Tag> response) {
                if (response.isSuccessful()) {
                    tag = response.body();
                    tagUid.setText(tag.getUid());
                    inputType.setSelection(types.indexOf(tag.getType()));

                    if (tag.getItem() != null) {
                        inputItemEmpty.setVisibility(View.GONE);
                        BindViewItem(tag.getItem());
                    }

                    loading.setVisibility(View.GONE);
                    form.setVisibility(View.VISIBLE);
                } else {
                    WaresysClient.requestAnswerFailure(response.code(), TagFormActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Tag> call, Throwable t) {
                Toast.makeText(TagFormActivity.this, R.string.no_connection_server, Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
    }


    private void searchItem(String search) {
        inputItemEmpty.setVisibility(View.GONE);
        layoutItem.setVisibility(View.GONE);
        loadingItem.setVisibility(View.VISIBLE);

        Call<List<Item>> call = waresysService.getItems(search, 0, "-created");
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful()) {
                    List<Item> items = response.body();

                    itemSuggestAdapter.clear();
                    itemSuggestAdapter.addAll(items);

                    loadingItem.setVisibility(View.GONE);

                    if (items.size() == 1) {
                        BindViewItem(items.get(0));
                        inputItemEmpty.setVisibility(View.GONE);
                    } else {
                        selectItem = null;
                        layoutItem.setVisibility(View.GONE);
                        inputItemEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    WaresysClient.requestAnswerFailure(response.code(), TagFormActivity.this);
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(TagFormActivity.this, R.string.no_connection_server, Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });

    }

    private void BindViewItem(Item item) {
        layoutItem.setVisibility(View.VISIBLE);
        formItem.setVisibility(View.VISIBLE);

        selectItem = item;

        itemName.setText(item.getName());
        itemDescription.setText(item.getDescription());
        itemAmount.setText(item.getAmountToString());

        TextView itemCreated = layoutItem.findViewById(R.id.data_created);
        itemCreated.setText(item.getCreatedFormat("dd.MM.yyyy HH:mm"));
        TextView itemUpdated = layoutItem.findViewById(R.id.data_updated);
        itemUpdated.setText(item.getUpdatedFormat("dd.MM.yyyy HH:mm"));
    }


    private boolean validate() {
        boolean valid = true;

        String type = types.get(inputType.getSelectedItemPosition());
        if (type.equals(Tag.TYPE_ITEM) && selectItem == null) {
            inputItemSuggest.setError(getString(R.string.tag_form_validate_item));
            valid = false;
        }

        return valid;
    }

}
