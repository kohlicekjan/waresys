package cz.kohlicek.waresys.ui.item;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.waresys.R;
import cz.kohlicek.waresys.model.Item;
import cz.kohlicek.waresys.service.WaresysClient;
import cz.kohlicek.waresys.service.WaresysService;
import cz.kohlicek.waresys.util.DialogUtils;
import cz.kohlicek.waresys.util.InputFilterUtils;
import cz.kohlicek.waresys.util.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemFormActivity extends AppCompatActivity {

    public static final String ITEM_ID = "item_id";
    public static final int REQUEST_CODE = 7;

    @BindView(R.id.input_name)
    TextInputEditText inputName;
    @BindView(R.id.input_description)
    TextInputEditText inputDescription;
    @BindView(R.id.input_amount)
    TextInputEditText inputAmount;

    @BindView(R.id.layout_name)
    TextInputLayout layoutName;
    @BindView(R.id.layout_description)
    TextInputLayout layoutDescription;
    @BindView(R.id.layout_amount)
    TextInputLayout layoutAmount;

    @BindView(R.id.layout_edit)
    LinearLayout layoutEdit;

    @BindView(R.id.loading)
    View loading;

    @BindView(R.id.form)
    View form;


    private WaresysService waresysService;
    private String itemId;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_form);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        waresysService = WaresysClient.getInstance(this);


        if (getIntent().hasExtra(ITEM_ID)) {
            setTitle(R.string.item_form_title_edit);
            itemId = getIntent().getStringExtra(ITEM_ID);
            load(itemId);
        } else {
            setTitle(R.string.item_form_title_new);
            form.setVisibility(View.VISIBLE);
        }

        inputAmount.setFilters(new InputFilter[]{new InputFilterUtils.MinMax(0, Integer.MAX_VALUE)});
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


        Call<Item> call = null;
        if (item == null) {
            item = new Item();
            item.setName(inputName.getText().toString());
            item.setDescription(inputDescription.getText().toString());
            call = waresysService.createItem(item);

        } else {
            item.setName(inputName.getText().toString());
            item.setDescription(inputDescription.getText().toString());
            item.setAmount(Integer.parseInt(inputAmount.getText().toString()));

            call = waresysService.updateItem(itemId, item);
        }

        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Toast.makeText(ItemFormActivity.this, R.string.form_saved, Toast.LENGTH_LONG).show();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(ITEM_ID, response.body().getId());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    WaresysClient.requestAnswerFailure(response.code(), ItemFormActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ItemFormActivity.this, R.string.no_connection_server, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String name = inputName.getText().toString();
        String description = inputDescription.getText().toString();
        String amount = inputAmount.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            layoutName.setError(getString(R.string.item_form_validate_name));
            valid = false;
        } else if (name.length() > 255) {
            layoutName.setError(getString(R.string.item_form_validate_name_max_len));
            valid = false;
        } else {
            layoutName.setErrorEnabled(false);
        }

        if (description.length() > 2000) {
            layoutName.setError(getString(R.string.item_form_validate_description_max_len));
            valid = false;
        } else {
            layoutName.setErrorEnabled(false);
        }

        if (item != null && amount.isEmpty()) {
            layoutAmount.setError(getString(R.string.item_form_validate_amount));
            valid = false;
        } else {
            layoutAmount.setErrorEnabled(false);
        }

        return valid;
    }


    private void load(final String id) {
        layoutEdit.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);

        Call<Item> call = waresysService.getItem(id);

        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                if (response.isSuccessful()) {
                    item = response.body();
                    inputName.setText(item.getName());
                    inputDescription.setText(item.getDescription());
                    inputAmount.setText(Integer.toString(item.getAmount()));

                    loading.setVisibility(View.GONE);
                    form.setVisibility(View.VISIBLE);
                } else {
                    WaresysClient.requestAnswerFailure(response.code(), ItemFormActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {
                Toast.makeText(ItemFormActivity.this, R.string.no_connection_server, Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });

    }


}
