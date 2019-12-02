package com.example.instagrum.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.example.instagrum.R;
import com.example.instagrum.activity.ProfileFriendsActivity;
import com.example.instagrum.adapter.AdapterSearch;
import com.example.instagrum.helper.ConfigFirebase;
import com.example.instagrum.helper.RecyclerItemClickListener;
import com.example.instagrum.helper.UserFirebase;
import com.example.instagrum.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    // Widget
    private SearchView searchViewSearch;
    private RecyclerView recyclerViewSearch;

    private List<User> userList;
    private DatabaseReference userRef;
    private AdapterSearch adapterSearch;

    private String idUserLogado;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchViewSearch    = view.findViewById(R.id.searchView);
        recyclerViewSearch  = view.findViewById(R.id.recyclerView);

        // Config initial
        userList = new ArrayList<>();
        userRef = ConfigFirebase.getRefFirebase().child("usuarios");
        idUserLogado = UserFirebase.getIdentifyUser();

        // Config RecycleView
        recyclerViewSearch.setHasFixedSize(true);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapterSearch = new AdapterSearch(userList, getActivity());
        recyclerViewSearch.setAdapter( adapterSearch );

        // Config event click
        recyclerViewSearch.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerViewSearch,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        User userSelect = userList.get(position);
                        Intent i = new Intent(getActivity(), ProfileFriendsActivity.class);
                        i.putExtra("userSelect", userSelect);
                        startActivity( i );

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        // Config Search
        searchViewSearch.setQueryHint("Localizar usuários");
        searchViewSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { // Esse método busca o usuário após clicar no botão
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { // Esse método busca o usuário conforme for digitando o nome
                String textDiTyped = newText.toUpperCase(); // Texto digitado será em caixa alta
                searchUser( textDiTyped );
                return true;
            }
        });

        return view;

    }

    private void searchUser(String text) {

        // clear list
        userList.clear();

        // Search user if you have text in search
        if ( text.length() >= 2 ) {

            Query query = userRef.orderByChild("name").startAt(text).endAt(text + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    userList.clear();

                    for ( DataSnapshot ds : dataSnapshot.getChildren() ) {

                        // Remove o usuário logado da lista
                        User user = ds.getValue(User.class);
                        if ( idUserLogado.equals( user.getId() ) ){
                            continue;
                        }

                        //userList.add( user );
                        userList.add( ds.getValue( User.class ) );
                    }

                    adapterSearch.notifyDataSetChanged();

                    /*
                    int total = listaUser.size();
                    Log.i("totalUser", "total: " + total);
                    */

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

}