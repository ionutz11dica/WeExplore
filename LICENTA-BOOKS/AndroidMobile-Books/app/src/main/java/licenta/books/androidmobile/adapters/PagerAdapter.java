package licenta.books.androidmobile.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import licenta.books.androidmobile.fragments.RecentlyRead;
import licenta.books.androidmobile.fragments.ShelfBooks;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int noOfFragments;


    public PagerAdapter(FragmentManager fm, int noOfFragments) {
        super(fm);
        this.noOfFragments = noOfFragments;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                 RecentlyRead recentlyRead = new RecentlyRead();
                 return recentlyRead;
            case 1:
                ShelfBooks shelfBooks = new ShelfBooks();
                return shelfBooks;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return noOfFragments;
    }
}
