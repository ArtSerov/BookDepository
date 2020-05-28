package com.example.bookdepository;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class BookListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mBookRecyclerView;
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 4;
    static boolean isReadGranted = false;
    private BookAdapter mAdapter;
    private int mPosition;
    private boolean mSubtitleVisible;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list,container,false);
        mBookRecyclerView = (RecyclerView)view.findViewById(R.id.book_recycle_view);
        mBookRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(savedInstanceState!=null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return view;
    }
    private void updateUI(){
        BookLab bookLab = BookLab.get(getActivity());
        List<Book> books = bookLab.getBooks();
        if(books.size()==0){
            Toast.makeText(getContext(),R.string.empty_list,Toast.LENGTH_SHORT).show();
        }
        if(mAdapter==null) {
            mAdapter = new BookAdapter(books);
            mBookRecyclerView.setAdapter(mAdapter);
        }
        else{
            mAdapter.setBooks(books);
            mAdapter.notifyItemChanged(mPosition);
            mAdapter.notifyItemChanged(books.size());
        }
        updateSubtitle();
    }
    public void onResume(){
        super.onResume();
        updateUI();
    }

    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Book mBook;
        private TextView mTitleTextView;
        private TextView mDataTextView;
        private CheckBox mReadedCheckBox;
        public BookHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView)itemView.findViewById(R.id.list_item_book_title_text_view);
            mDataTextView = (TextView)itemView.findViewById(R.id.list_item_book_date_text_view);
            mReadedCheckBox = (CheckBox)itemView.findViewById(R.id.list_item_book_readed_check_box);
        }
        public void bindBook(Book book){
            mBook = book;
            mTitleTextView.setText(mBook.getTitle());
            mDataTextView.setText(DateFormat.getDateInstance().format(mBook.getDate()));
            mReadedCheckBox.setChecked(mBook.isReaded());
        }

        @Override
        public void onClick(View v) {
            Intent intent = BookPagerActivity.newIntent(getActivity(),mBook.getId());
            startActivity(intent);
            mPosition = getAdapterPosition();

        }
    }
    private class BookAdapter extends RecyclerView.Adapter<BookHolder>{
        private List<Book> mBooks;
        public BookAdapter(List<Book> books){
            mBooks = books;
        }


        public void setBooks(List<Book> books){
            mBooks = books;
        }
        @Override
        public BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_book,parent,false);
            return new BookHolder(view);
        }

        @Override
        public void onBindViewHolder( BookHolder holder, int position) {
            Book book = mBooks.get(position);
            holder.bindBook(book);
        }

        @Override
        public int getItemCount() {
            return mBooks.size();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_book_list,menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_new_book:
                Book book = new Book();
                BookLab.get(getActivity()).addBook(book);
                Intent intent = BookPagerActivity.newIntent(getActivity(),book.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void updateSubtitle(){
        BookLab bookLab = BookLab.get(getActivity());
        int bookCount = bookLab.getBooks().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural,bookCount,bookCount);
        if(!mSubtitleVisible){
            subtitle=null;
        }
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                isReadGranted = true;
            else
                isReadGranted = false;
        }
    }
}
