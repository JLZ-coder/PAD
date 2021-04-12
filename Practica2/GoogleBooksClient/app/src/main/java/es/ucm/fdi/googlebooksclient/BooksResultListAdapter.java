package es.ucm.fdi.googlebooksclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BooksResultListAdapter extends RecyclerView.Adapter<BooksResultListAdapter.BookHolder> {
    private ArrayList<BookInfo> mBooksData;
    private LayoutInflater mInflater;

    public BooksResultListAdapter(Context context, ArrayList<BookInfo> booksdata) {
        mInflater = LayoutInflater.from(context);
        mBooksData = booksdata;
    }

    public class BookHolder extends RecyclerView.ViewHolder {
        private TextView mtitle;
        private TextView mauthor;
        private TextView minfo;
        private BooksResultListAdapter mAdapter;

        public BookHolder(View itemView, BooksResultListAdapter adapter) {
            super(itemView);
            mtitle = itemView.findViewById(R.id.book_title);
            mauthor = itemView.findViewById(R.id.book_author);
            minfo = itemView.findViewById(R.id.book_info);
            this.mAdapter = adapter;
        }
    }

    @NonNull
    @Override
    public BooksResultListAdapter.BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.book_info, parent, false);
        return new BookHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksResultListAdapter.BookHolder holder, int position) {
        // Retrieve the data for that position
        BookInfo mCurrent = mBooksData.get(position);
        // Add the data to the view
        holder.mtitle.setText(mCurrent.getMtitle());
        holder.mauthor.setText(mCurrent.getMauthors());
        holder.minfo.setText(mCurrent.getMinfoLink().toString());
    }

    @Override
    public int getItemCount() {
        return mBooksData.size();
    }

    public void setBooksData(List<BookInfo> data) {
        mBooksData = new ArrayList<BookInfo>(data);
    }
}
