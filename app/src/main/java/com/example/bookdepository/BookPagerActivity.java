package com.example.bookdepository;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.List;
import java.util.UUID;

public class BookPagerActivity extends AppCompatActivity {

    private ViewPager mViwePager;
    private List<Book> mBooks;
    private static final String EXTRA_BOOK_ID = "com.example.bookdepository.book_id";

    public static Intent newIntent(Context packageContext, UUID bookId){
        Intent intent = new Intent(packageContext,BookPagerActivity.class);
        intent.putExtra(EXTRA_BOOK_ID,bookId);
        return  intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_pager);
        UUID bookId = (UUID)getIntent().getSerializableExtra(EXTRA_BOOK_ID);
        mViwePager = (ViewPager)findViewById(R.id.activity_book_pager_view_pager);
        mBooks = BookLab.get(this).getBooks();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViwePager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Book book = mBooks.get(position);
                return  BookFragment.newInstance(book.getId());
            }

            @Override
            public int getCount() {
                return mBooks.size();
            }
        });
        for (int i=0; i<mBooks.size();i++){
            if(mBooks.get(i).getId().equals(bookId)) {
                mViwePager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}
