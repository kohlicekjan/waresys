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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Toast;

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


public class ItemListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {//ItemAdapter.ClickListener,

    @BindView(R.id.recycler_view)
    EmptyRecyclerView recyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSnipSwipeRefreshLayout;

    @BindView(R.id.empty_view)
    View mEmptyView;

    @BindView(R.id.stub_no_connection)
    ViewStub stub;
    FloatingActionButton fab;
    Snackbar snackbar;
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


        fab = (FloatingActionButton) this.getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(this);

        bpiniService = BPINIClient.getInstance(this.getContext());
        adapter = new ItemAdapter(this.getContext());
        linearLayoutManager = new LinearLayoutManager(this.getActivity());


        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public int getFooterViewType(int defaultNoFooterViewType) {
                return 1;
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                load(page, true);
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


        snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Žádné připojení", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Opakovat", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setVisibility(View.VISIBLE);
                onRefresh();
            }
        });

        load(1, true);

        return view;
    }

    public void onClick(View view) {
        Intent intent = new Intent(this.getContext(), ItemFormActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }


//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.bLogin:
//                String username = etUsername.getText().toString();
//                String password = etPassword.getText().toString();
//
//                User user = new User(username, password);
//
//                authenticate(user);
//                break;
//            case R.id.tvRegisterLink:
//                Intent registerIntent = new Intent(Login.this, Register.class);
//                startActivity(registerIntent);
//                break;
//        }
//    }

//    @Override
//    public void onClick(Item item) {
//        Toast.makeText(ItemListFragment.this.getContext(), item.getName(), Toast.LENGTH_LONG).show();
//    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Toast.makeText(ItemListFragment.this.getContext(), "Vybrano", Toast.LENGTH_LONG).show();

        return super.onContextItemSelected(item);
    }


    @Override
    public void onRefresh() {
        adapter.clear();
        load(1, false);
        mSnipSwipeRefreshLayout.setRefreshing(false);
    }


    private void load(int page, boolean loading) {
        adapter.setLoading(loading);
        Call<List<Item>> call = bpiniService.getItems("-created", page);
        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful()) {
                    adapter.addAll(response.body());

                    recyclerView.setVisibility(View.VISIBLE);
                    stub.setVisibility(View.GONE);
                    snackbar.dismiss();
                } else {
                    onFailure(null, null);
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                stub.setVisibility(View.VISIBLE);
                snackbar.show();
            }
        });
    }



}
