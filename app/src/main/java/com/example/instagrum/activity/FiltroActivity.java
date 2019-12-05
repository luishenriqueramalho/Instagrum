package com.example.instagrum.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagrum.R;
import com.example.instagrum.adapter.AdapterThumbnails;
import com.example.instagrum.helper.ConfigFirebase;
import com.example.instagrum.helper.RecyclerItemClickListener;
import com.example.instagrum.helper.UserFirebase;
import com.example.instagrum.model.Post;
import com.example.instagrum.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {

    // Inicializando a Lib dos filtros
    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imgSelect;
    private Bitmap image;
    private Bitmap imgFilter;
    private TextInputEditText txtDescription;
    private String idUserLogado;
    private User userLogado;
    private ProgressBar progressBar;
    private Boolean statusLoading;

    private List<ThumbnailItem> listFilter;

    private RecyclerView recyclerView;
    private AdapterThumbnails adapterThumbnails;

    private DatabaseReference userRef;
    private DatabaseReference userLogadoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        // Config initial
        listFilter = new ArrayList<>();
        idUserLogado = UserFirebase.getIdentifyUser();
        userRef = ConfigFirebase.getRefFirebase().child("usuarios");

        // Initial Component
        imgSelect       = findViewById(R.id.imgSelect);
        txtDescription  = findViewById(R.id.txtDescriptionFilter);
        recyclerView    = findViewById(R.id.recyclerFilter);
        progressBar     = findViewById(R.id.progressBarFilter);

        recDadosUserLogado();

        // Configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        // Recuperar img select
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ) {
            byte[] dadosImg = bundle.getByteArray("photoSelect");
            image = BitmapFactory.decodeByteArray(dadosImg, 0, dadosImg.length);
            imgSelect.setImageBitmap( image );
            imgFilter = image.copy( image.getConfig(), true );

            // Config recycleview
            adapterThumbnails = new AdapterThumbnails(listFilter, getApplicationContext());
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager( layoutManager );
            recyclerView.setAdapter( adapterThumbnails );

            // Add event click
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                    getApplicationContext(),
                    recyclerView,
                    new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                            ThumbnailItem item = listFilter.get( position );

                            imgFilter = image.copy( image.getConfig(), true );
                            Filter filtro = item.filter;
                            imgSelect.setImageBitmap( filtro.processFilter(imgFilter) );

                        }

                        @Override
                        public void onLongItemClick(View view, int position) {

                        }

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        }
                    }
            ));

            // Rec filter
            recFilter();

        }

    }

    private void loading(boolean state) {

        if ( state ) {
            statusLoading = true;
            progressBar.setVisibility(View.VISIBLE);
        } else {
            statusLoading = false;
            progressBar.setVisibility(View.GONE);
        }

    }

    private void recDadosUserLogado() {

        loading(true);

        userLogadoRef = userRef.child( idUserLogado );
        userLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userLogado = dataSnapshot.getValue( User.class );
                        loading(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }

    private void recFilter() {

        // Clear item
        ThumbnailsManager.clearThumbs();
        listFilter.clear();

        // Config filter normal
        ThumbnailItem item = new ThumbnailItem();
        item.image = image;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb( item );

        // List all filters
        List<Filter> filtros = FilterPack.getFilterPack(getApplicationContext());
        for ( Filter filtro: filtros ) {

            ThumbnailItem item1 = new ThumbnailItem();
            item1.image = image;
            item1.filter = filtro;
            item1.filterName = filtro.getName();

            ThumbnailsManager.addThumb( item1 );

        }

        listFilter.addAll( ThumbnailsManager.processThumbs(getApplicationContext()) );
        adapterThumbnails.notifyDataSetChanged();

    }

    private void publicPost() {

        if ( statusLoading ) {
            Toast.makeText(getApplicationContext(), "Carregando a foto, aguarde!", Toast.LENGTH_SHORT).show();
        } else {

            final Post post = new Post();
            post.setIdUser( idUserLogado );
            post.setDescription( txtDescription.getText().toString() );

            // Recuperar dados da img
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imgFilter.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] dadosImage = baos.toByteArray();

            // Save img no storage do firebase
            StorageReference storageRef = ConfigFirebase.getFirebaseStorage();
            final StorageReference imgRef = storageRef.child("images").child("posts").child(post.getId() + ".jpeg");

            UploadTask uploadTask = imgRef.putBytes( dadosImage );
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(FiltroActivity.this, "Erro ao salvar a imagem, tente novamente!", Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(FiltroActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            post.setPhoto( uri.toString() );

                            if ( post.save() ) {

                                // Att qnt post
                                int qntPost = userLogado.getPost() + 1;
                                userLogado.setPost( qntPost );
                                userLogado.attQntPost();

                                Log.i("URI FIREBASE", uri.toString());

                                Toast.makeText(FiltroActivity.this, "Sucesso ao salvar postagem!", Toast.LENGTH_SHORT).show();
                                finish();

                            }

                        }
                    });

                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filter, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch ( item.getItemId() ) {
            case R.id.ic_save_post :
                publicPost();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}