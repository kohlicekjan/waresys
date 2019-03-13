package cz.kohlicek.waresys.ui.tag;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.waresys.R;
import cz.kohlicek.waresys.model.Account;
import cz.kohlicek.waresys.model.Tag;
import cz.kohlicek.waresys.service.WaresysClient;
import cz.kohlicek.waresys.service.WaresysService;
import cz.kohlicek.waresys.ui.LoginActivity;
import cz.kohlicek.waresys.ui.item.ItemFormActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagReaderActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tag_uid)
    public TextView tagUid;
    @BindView(R.id.tag_type)
    public TextView tagType;
    @BindView(R.id.item_name)
    public TextView itemName;
    @BindView(R.id.item_description)
    public TextView itemDescription;
    @BindView(R.id.item_amount)
    public TextView itemAmount;
    @BindView(R.id.loading)
    View loading;
    @BindView(R.id.layout_tag)
    View layoutTag;
    @BindView(R.id.layout_item)
    View layoutItem;
    @BindView(R.id.label_tag)
    View labelTag;
    @BindView(R.id.label_item)
    View labelItem;

    private Account account;
    private WaresysService waresysService;
    private NfcAdapter nfcAdapter;
    private Tag tag;
    private Snackbar snackbar;

    @NonNull
    public static String bytesToHex(byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_reader);
        ButterKnife.bind(this);

        setTitle(R.string.tag_reader_title);

        account = Account.getLocalAccount(this);
        if (account == null) {
            Toast.makeText(getApplicationContext(), R.string.tag_reader_nfc_not_login, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        waresysService = WaresysClient.getInstance(this);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, R.string.tag_reader_nfc_no_support, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        snackbar = Snackbar.make(this.findViewById(R.id.root_view), R.string.tag_reader_nfc_disabled, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.tag_reader_nfc_enabled, this);

        layoutItem.setOnClickListener(this);
        layoutTag.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tag_reader_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_close:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.snackbar_action:
                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                break;
            case R.id.layout_tag:
                intent = new Intent(this, TagFormActivity.class);
                intent.putExtra(TagFormActivity.TAG_ID, tag.getId());
                startActivityForResult(intent, TagFormActivity.REQUEST_CODE);
                break;
            case R.id.layout_item:
                intent = new Intent(this, ItemFormActivity.class);
                intent.putExtra(ItemFormActivity.ITEM_ID, tag.getItem().getId());
                startActivityForResult(intent, ItemFormActivity.REQUEST_CODE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TagFormActivity.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String result = data.getStringExtra(TagFormActivity.TAG_ID);
                    load(tag.getUid());
                }
                break;
            case ItemFormActivity.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String result = data.getStringExtra(ItemFormActivity.ITEM_ID);
                    load(tag.getUid());
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) {
                snackbar.show();
            } else {
                snackbar.dismiss();
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            IntentFilter filter = new IntentFilter();
            filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
            filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
            filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, null);
        }
        onNewIntent(getIntent());
    }

    @Override
    protected void onPause() {
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction() == null)
            return;

        switch (intent.getAction()) {
            case NfcAdapter.ACTION_TAG_DISCOVERED:
                android.nfc.Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (tag != null) {
                    String tagUid = bytesToHex(tag.getId());
                    load(tagUid);
                }
                break;
        }
    }

    private void load(String tagUid) {
        layoutItem.setVisibility(View.GONE);
        labelItem.setVisibility(View.GONE);
        layoutTag.setVisibility(View.GONE);
        labelTag.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

        Call<Tag> call = waresysService.getTagByUid(tagUid);

        call.enqueue(new Callback<Tag>() {
            @Override
            public void onResponse(Call<Tag> call, Response<Tag> response) {
                if (response.isSuccessful()) {
                    tag = response.body();
                    BindViewTag(tag);
                } else {
                    loading.setVisibility(View.GONE);
                    WaresysClient.requestAnswerFailure(response.code(), TagReaderActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Tag> call, Throwable t) {
                loading.setVisibility(View.GONE);
                Toast.makeText(TagReaderActivity.this, R.string.no_connection_server, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void BindViewTag(Tag tag) {
        loading.setVisibility(View.GONE);
        layoutTag.setVisibility(View.VISIBLE);
        labelTag.setVisibility(View.VISIBLE);

        tagUid.setText(tag.getUid());
        int type_id = getResources().getIdentifier("tag_type_" + tag.getType(), "string", TagReaderActivity.this.getPackageName());
        tagType.setText(getResources().getString(type_id));

        switch (tag.getType()) {
            case Tag.TYPE_UNKNOWN:
                tagType.setTextColor(getColor(android.R.color.holo_blue_dark));
                break;
            case Tag.TYPE_MODE:
                tagType.setTextColor(getColor(android.R.color.holo_orange_dark));
                break;
            default:
                tagType.setTextColor(getColor(android.R.color.black));
        }

        TextView tagCreated = (TextView) layoutTag.findViewById(R.id.data_created);
        tagCreated.setText(tag.getCreatedFormat("dd.MM.yyyy HH:mm"));
        TextView tagUpdated = (TextView) layoutTag.findViewById(R.id.data_updated);
        tagUpdated.setText(tag.getUpdatedFormat("dd.MM.yyyy HH:mm"));

        if (tag.getItem() != null) {
            layoutItem.setVisibility(View.VISIBLE);
            labelItem.setVisibility(View.VISIBLE);
            itemName.setText(tag.getItem().getName());
            itemDescription.setText(tag.getItem().getDescription());
            itemAmount.setText(tag.getItem().getAmountToString());

            TextView itemCreated = (TextView) layoutItem.findViewById(R.id.data_created);
            itemCreated.setText(tag.getItem().getCreatedFormat("dd.MM.yyyy HH:mm"));
            TextView itemUpdated = (TextView) layoutItem.findViewById(R.id.data_updated);
            itemUpdated.setText(tag.getItem().getUpdatedFormat("dd.MM.yyyy HH:mm"));
        }
    }

}
