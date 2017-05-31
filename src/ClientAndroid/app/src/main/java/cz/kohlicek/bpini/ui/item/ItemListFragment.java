package cz.kohlicek.bpini.ui.item;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.kohlicek.bpini.R;
import cz.kohlicek.bpini.adapter.EndlessRecyclerViewScrollListener;
import cz.kohlicek.bpini.adapter.ItemAdapter;
import cz.kohlicek.bpini.model.Item;
import cz.kohlicek.bpini.service.BPINIClient;
import cz.kohlicek.bpini.service.BPINIService;
import cz.kohlicek.bpini.ui.view.EmptyRecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ItemListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, ItemAdapter.OnClickListener<Item> {

    @BindView(R.id.recycler_view)
    EmptyRecyclerView recyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSnipSwipeRefreshLayout;

    @BindView(R.id.empty_view)
    View mEmptyView;

    @BindView(R.id.stub_no_connection)
    ViewStub stub;
    Snackbar snackbar;

    FloatingActionButton fab;

    private ItemAdapter adapter;
    private BPINIService bpiniService;
    private LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        ButterKnife.bind(this, view);

        this.getActivity().setTitle(R.string.item_list_title);

        fab = (FloatingActionButton) this.getActivity().findViewById(R.id.fab_add);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(this);

        bpiniService = BPINIClient.getInstance(this.getContext());
        adapter = new ItemAdapter(this.getContext());
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

        registerForContextMenu(recyclerView);

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
            case android.support.design.R.id.snackbar_action:
                adapter.clear();
                load(0, true);
                break;
            case R.id.fab_add:
                Intent intent = new Intent(this.getContext(), ItemFormActivity.class);
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClick(View v, int position, Item data) {
        Intent intent = new Intent(this.getContext(), ItemFormActivity.class);
        intent.putExtra(ItemFormActivity.ITEM_ID, data.getId());
        startActivity(intent);
    }

    //    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        Toast.makeText(this.getContext(), adapter.selected.getName(), Toast.LENGTH_LONG).show();
//        return super.onContextItemSelected(item);
//    }


    @Override
    public void onRefresh() {
        adapter.clear();
        load(0, false);
        mSnipSwipeRefreshLayout.setRefreshing(false);
    }


    private void load(int skip, boolean loading) {
        adapter.setLoading(loading);
        if (loading) {
            recyclerView.setVisibility(View.VISIBLE);
            visibleNoConnection(false);
        }

        Call<List<Item>> call = bpiniService.getItems("-created", skip);
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body());

                    recyclerView.setVisibility(View.VISIBLE);
                    visibleNoConnection(false);
                } else {

                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                visibleNoConnection(true);
            }
        });
    }

    private void visibleNoConnection(boolean visible) {
        if (visible) {
            snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), R.string.no_connection_message, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.no_connection_repeat, this);
            snackbar.show();
            stub.setVisibility(View.VISIBLE);
        } else {
            if (snackbar != null)
                snackbar.dismiss();
            stub.setVisibility(View.GONE);
        }
    }


}
