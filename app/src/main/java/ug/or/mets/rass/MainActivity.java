package ug.or.mets.rass;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import ug.or.mets.rass.db.Constants;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    DrawerLayout myDrawerLayout;
    NavigationView myNavigationView;
    FragmentManager myFragmentManager;
    FragmentTransaction myFragmentTransaction;
    TextView FilterLevel;
    String SelectValue_OrgUnit,SelectValue_Entity,SelectValue_Period;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(new LayoutInflater.Factory() {
            @Override
            public View onCreateView(String name, Context context,
                                     AttributeSet attrs) {
                View v = Utils.tryInflate(name, context, attrs);
                if (v instanceof TextView) {
                    Utils.setTypeFace((TextView) v, MainActivity.this);
                }
                return v;
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         *Setup the DrawerLayout and NavigationView
         */
        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        myNavigationView = (NavigationView) findViewById(R.id.nav_drawer) ;
        FilterLevel = (TextView) findViewById(R.id.txtFilterLevel);
        FilterLevel.setOnClickListener(this );

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            this.SelectValue_OrgUnit = extras.getString("SelectValue_OrgUnit");
            this.SelectValue_Entity = extras.getString("SelectValue_Entity");
            this.SelectValue_Period = extras.getString("SelectValue_Period");

        }

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the HomeFragment as the first Fragment
         */
        myFragmentManager = getSupportFragmentManager();
        myFragmentTransaction = myFragmentManager.beginTransaction();


        HomeFragment fragInfo =  HomeFragment.newInstance(SelectValue_OrgUnit,SelectValue_Entity,SelectValue_Period);

        // myFragmentTransaction.replace(R.id.containerView, new fragInfo).commit();

        myFragmentTransaction.replace(R.id.containerView, fragInfo);
        myFragmentTransaction.commit();



        /**
         * Setup click events on the Navigation View Items.
         */
        myNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem selectedMenuItem) {
                myDrawerLayout.closeDrawers();
                if (selectedMenuItem.getItemId() == R.id.nav_home) {
                    FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new HomeFragment()).commit();
                }
                if (selectedMenuItem.getItemId() == R.id.nav_tools) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.dhis_url));
                    startActivity(browserIntent);

                }if (selectedMenuItem.getItemId()==R.id.nav_share){
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Rass App");
                    intent.putExtra(Intent.EXTRA_TEXT, Constants.rass_playstore_url);
                    startActivity(Intent.createChooser(intent, "choose one"));
                }
                return false;
            }
        });
        /**
         * Setup Drawer Toggle of the Toolbar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, myDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);
        myDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if(R.id.txtFilterLevel == view.getId()){
            intent = new Intent(this, FilterLevelActivity_Editted.class);
            startActivity(intent);
            /*
            intent = new Intent(this, TestMapActivity.class);
            startActivity(intent);*/
        }
    }


}