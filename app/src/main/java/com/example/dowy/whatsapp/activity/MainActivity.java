package com.example.dowy.whatsapp.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.dowy.whatsapp.R;
import com.example.dowy.whatsapp.adapter.PagerAdapter;
import com.example.dowy.whatsapp.config.ConfiguracaoFirebase;
import com.example.dowy.whatsapp.fragment.ContactosFragment;
import com.example.dowy.whatsapp.fragment.ConversasFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
    //private TabLayout tabLayout;
    private MaterialSearchView searchView;
    private ViewPager viewPager;
    //private PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        // Configurar abas
        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                .add("Conversas",ConversasFragment.class)
                .add("Contactos", ContactosFragment.class)
                .create());

        viewPager = findViewById(R.id.viewPagerPrincipal);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);

        // tabLayout = findViewById(R.id.tabLayoutPrincipal);
       // viewPager = findViewById(R.id.viewPagerPrincipal);

        //tabLayout.addTab(tabLayout.newTab().setText("Conversas"));
        //tabLayout.addTab(tabLayout.newTab().setText("Contactos"));
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        //adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        //viewPager.setAdapter(adapter);

//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        // Configuracao do search view
        searchView = findViewById(R.id.materialSearchPrincipal);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Log.d("evento", "onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.d("evento", "onQueryTextChange");
                ConversasFragment fragment = (ConversasFragment) adapter.getPage(0);
                if (newText != null && !newText.isEmpty()) {
                    fragment.pesquisarConversa(newText);
                }
                return false;
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);


        // Configurar Botao de Pesquisa
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuSearch:
                break;
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;
            case R.id.menuSair:
                autenticacao.signOut();
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void abrirConfiguracoes() {
        Intent configuracoes = new Intent(this, ConfiguracoesActivity.class);
        startActivity(configuracoes);
    }
}
