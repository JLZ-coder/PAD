package es.ucm.fdi.googlebooksclient;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.List;

public class BookLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<BookInfo>> {

    public static final String EXTRA_QUERY = "q";
    public static final String EXTRA_PRINT_TYPE = "printType";

    @NonNull
    @Override
    public Loader<List<BookInfo>> onCreateLoader(int id, @Nullable Bundle args) {
        String query = "";
        if (args != null) {
            query = args.getString(EXTRA_QUERY);
        }

        String printtype = "";
        if (args != null) {
            printtype = args.getString(EXTRA_PRINT_TYPE);
        }

        return  new BookLoader(MainActivity.getAppContext(), query, printtype);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<BookInfo>> loader, List<BookInfo> data) {

        MainActivity.updateBooksResultList(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<BookInfo>> loader) {

    }
}
