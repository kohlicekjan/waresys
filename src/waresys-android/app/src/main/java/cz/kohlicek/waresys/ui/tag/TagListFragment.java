package cz.kohlicek.waresys.ui.tag;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.waresys.R;
import cz.kohlicek.waresys.adapter.BaseAdapter;
import cz.kohlicek.waresys.adapter.TagAdapter;
import cz.kohlicek.waresys.model.Tag;
import cz.kohlicek.waresys.service.WaresysClient;
import cz.kohlicek.waresys.service.WaresysService;
import cz.kohlicek.waresys.ui.view.EmptyRecyclerView;
import cz.kohlicek.waresys.ui.view.EndlessRecyclerViewScrollListener;
import cz.kohlicek.waresys.util.DialogUtils;
import cz.kohlicek.waresys.util.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TagListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, BaseAdapter.OnClickListener<Tag>, DialogInterface.OnClickListener {

    @BindView(R.id.recycler_view)
    EmptyRecyclerView recyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSnipSwipeRefreshLayout;

    @BindView(R.id.empty_view)
    View mEmptyView;

    @BindView(R.id.no_connection)
    View noConnection;

    private Snackbar snackbar;
    private FloatingActionButton fab;

    private TagAdapter adapter;
    private WaresysService waresysService;
    private LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag_list, container, false);
        ButterKnife.bind(this, view);

        this.getActivity().setTitle(R.string.tag_list_title);

        fab = this.getActivity().findViewById(R.id.fab_add);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(null);

        waresysService = WaresysClient.getInstance(this.getContext());
        adapter = new TagAdapter(this.getContext());
        adapter.setOnClickListener(this);

        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public int getFooterViewType(int defaultNoFooterViewType) {
                return 1;
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                load(totalItemsCount, true);
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setEmptyView(mEmptyView);


        mSnipSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        mSnipSwipeRefreshLayout.setOnRefreshListener(this);

        load(0, true);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (snackbar != null)
            snackbar.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.snackbar_action:
                snackbar.dismiss();
                load(0, true);
                break;
        }
    }

    @Override
    public void onClick(View v, int position, Tag data) {
        Intent intent = new Intent(this.getContext(), TagFormActivity.class);
        intent.putExtra(TagFormActivity.TAG_ID, data.getId());
        startActivityForResult(intent, TagFormActivity.REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TagFormActivity.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String result = data.getStringExtra(TagFormActivity.TAG_ID);
                    onRefresh();
                }
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(this.getContext(), TagFormActivity.class);
                intent.putExtra(TagFormActivity.TAG_ID, adapter.getSelected().getId());
                startActivityForResult(intent, TagFormActivity.REQUEST_CODE);
                return true;
            case 2:
                AlertDialog dialog = DialogUtils.DialogWithCancel(getContext());
                dialog.setMessage(getString(R.string.dialog_message_delete));
                dialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.context_menu_delete), this);
                dialog.show();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            Call<Void> call = waresysService.deleteTag(adapter.getSelected().getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), R.string.form_deleted, Toast.LENGTH_LONG).show();
                        adapter.remove(adapter.getSelected());
                    } else {
                        WaresysClient.requestAnswerFailure(response.code(), getActivity());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), R.string.no_connection_server, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onRefresh() {
        load(0, false);
        mSnipSwipeRefreshLayout.setRefreshing(false);
    }


    private void load(int skip, boolean loading) {
        if (skip == 0)
            adapter.clear();

        if (!NetworkUtils.isNetworkConnected(this.getContext())) {
            recyclerView.setVisibility(View.GONE);
            visibleNoConnection(true, R.string.no_connection_internet);
            return;
        }

        adapter.setLoading(loading);
        if (loading) {
            recyclerView.setVisibility(View.VISIBLE);
            visibleNoConnection(false, 0);
        }

        Call<List<Tag>> call = waresysService.getTags(skip, "-created");
        call.enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body());

                    recyclerView.setVisibility(View.VISIBLE);
                    visibleNoConnection(false, 0);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    visibleNoConnection(true, R.string.request_error);
                }
            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                visibleNoConnection(true, R.string.no_connection_server);
            }
        });
    }

    private void visibleNoConnection(boolean visible, int stringId) {
        if (visible) {
            snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), R.string.no_connection_message, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.no_connection_repeat, this);
            snackbar.show();
            ((TextView) noConnection.findViewById(R.id.text_no_connection)).setText(getString(stringId));
            noConnection.setVisibility(View.VISIBLE);
        } else {
            if (snackbar != null)
                snackbar.dismiss();
            noConnection.setVisibility(View.GONE);
        }
    }

}
