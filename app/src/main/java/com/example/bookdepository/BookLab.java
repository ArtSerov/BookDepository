package com.example.bookdepository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.example.bookdepository.database.BookBaseHelper;
import com.example.bookdepository.database.BookCursorWrapper;
import com.example.bookdepository.database.BookDbSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookLab {
    private static BookLab sBookLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static BookLab get(Context context){
        if(sBookLab==null)sBookLab = new BookLab(context);
        return sBookLab;
    }
    private BookLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new BookBaseHelper(mContext).getWritableDatabase();

    }

    public void addBook(Book book){
        ContentValues values = getContentValues(book);
        mDatabase.insert(BookDbSchema.BookTable.NAME,null,values);
    }
    public void deleteBook(Book book){
        String uuidString = book.getId().toString();
        mDatabase.delete(BookDbSchema.BookTable.NAME,BookDbSchema.BookTable.Cols.UUID + "= ?",new String[]{uuidString});
    }
    public void updateBook(Book book){
        String uuidString = book.getId().toString();
        ContentValues values = getContentValues(book);
        mDatabase.update(BookDbSchema.BookTable.NAME,values, BookDbSchema.BookTable.Cols.UUID + "= ?",new String[]{
                uuidString
        });
    }

    private BookCursorWrapper queryBooks(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                BookDbSchema.BookTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new BookCursorWrapper(cursor);
    }

    public List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        BookCursorWrapper cursor = queryBooks(null,null);
        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                books.add(cursor.getBook());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return books;
    }

    public  Book getBook(UUID id){
        BookCursorWrapper cursor = queryBooks(
            BookDbSchema.BookTable.Cols.UUID + "= ?",
            new String[]{id.toString()}
        );
        try{
            if(cursor.getCount() == 0){
                return null;
            }
            cursor.moveToFirst();
            return cursor.getBook();
        }finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Book book){
        ContentValues values = new ContentValues();
        values.put(BookDbSchema.BookTable.Cols.UUID, book.getId().toString());
        values.put(BookDbSchema.BookTable.Cols.TITLE, book.getTitle());
        values.put(BookDbSchema.BookTable.Cols.DATE, book.getDate().getTime());
        values.put(BookDbSchema.BookTable.Cols.READED, book.isReaded());
        return values;
    }

    public File getPhotoFile(Book book)
    {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(externalFilesDir == null)
        {
            return null;
        }
        return new File(externalFilesDir, book.getPhotoFilename());
    }

}
