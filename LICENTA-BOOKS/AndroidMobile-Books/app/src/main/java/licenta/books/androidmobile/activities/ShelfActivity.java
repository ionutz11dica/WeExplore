package licenta.books.androidmobile.activities;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import licenta.books.androidmobile.R;
import licenta.books.androidmobile.fragments.CollectionsBooks;
import licenta.books.androidmobile.fragments.RecentlyRead;
import licenta.books.androidmobile.fragments.ShelfBooks;

public class ShelfActivity extends AppCompatActivity implements ShelfBooks.OnFragmentInteractionListener,RecentlyRead.OnFragmentInteractionListener, CollectionsBooks.OnFragmentInteractionListener {
    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    TabItem tabRecentlyReads;
    TabItem tabShelfBooks;
    TabItem tabCollectionsBooks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);
        initComp();
    }

    void initComp(){
        tabLayout = findViewById(R.id.shelfbooks_tablayout);
        tabRecentlyReads = findViewById(R.id.tabitem_recentlyReads);
        tabShelfBooks = findViewById(R.id.tabitem_shelfBooks);
        tabCollectionsBooks = findViewById(R.id.tabitem_collectionsBooks);
        viewPager = findViewById(R.id.shelfbooks_viewpager);

        pagerAdapter = new licenta.books.androidmobile.adapters.PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(1);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
