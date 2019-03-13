package cz.kohlicek.waresys.ui.device;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.waresys.R;
import cz.kohlicek.waresys.adapter.DeviceAdapter;
import cz.kohlicek.waresys.model.Device;
import cz.kohlicek.waresys.service.WaresysClient;
import cz.kohlicek.waresys.service.WaresysService;
import cz.kohlicek.waresys.ui.view.EmptyRecyclerView;
import cz.kohlicek.waresys.ui.view.EndlessRecyclerViewScrollListener;
import cz.kohlicek.waresys.util.DialogUtils;
import cz.kohlicek.waresys.util.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DeviceListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, DeviceAdapter.OnCheckedChangeListener, DialogInterface.OnClickListener, SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

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

    private DeviceAdapter adapter;
    private WaresysService waresysService;
    private LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;

    private String search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_list, container, false);
        ButterKnife.bind(this, view);

        this.getActivity().setTitle(R.string.device_list_title);
        setHasOptionsMenu(true);

        fab = this.getActivity().findViewById(R.id.fab_add);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(null);

        waresysService = WaresysClient.getInstance(this.getContext());
        adapter = new DeviceAdapter(this.getContext());
        adapter.setOnCheckedChangeListener(this);

        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public int getFooterViewType(int defaultNoFooterViewType) {
                return 1;
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                load(search, totalItemsCount, true);
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

        search = null;
        load(null, 0, true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menuSearch);
        menuItem.setOnActionExpandListener(this);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        load(query, 0, true);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        adapter.clear();
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        load(null, 0, true);
        return true;
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
                load(search, 0, true);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                onCheckedChanged(null, !adapter.getSelected().isAllowed(), adapter.getSelectedPosition(), adapter.getSelected());
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
            Call<Void> call = waresysService.deleteDevice(adapter.getSelected().getId());
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, final int position, final Device data) {
        data.setAllowed(isChecked);

        Call<Device> call = waresysService.updateDevice(data.getId(), data);
        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                if (response.isSuccessful()) {
                    if (response.body().isAllowed()) {
                        Toast.makeText(getContext(), R.string.device_list_allowed, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), R.string.device_list_disabled, Toast.LENGTH_LONG).show();
                    }

                    adapter.set(position, response.body());
                } else {
                    WaresysClient.requestAnswerFailure(response.code(), getActivity());
                }
            }

            @Override
            public void onFailure(Call<Device> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                visibleNoConnection(true, R.string.no_connection_server);
            }
        });

    }

    @Override
    public void onRefresh() {
        load(search, 0, false);
        mSnipSwipeRefreshLayout.setRefreshing(false);
    }

    private void load(String search, int skip, boolean loading) {
        this.search = search;

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

        Call<List<Device>> call = waresysService.getDevices(search, skip, "-created");
        call.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
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
            public void onFailure(Call<List<Device>> call, Throwable t) {
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
