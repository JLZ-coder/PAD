package es.ucm.fdi.googlebooksclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BookInfo {
    private String mtitle;
    private String mauthors;
    private URL minfoLink;

    public BookInfo(String title, String authors, String infoLink) {
        mtitle = title;
        mauthors = authors;
        try {
            minfoLink = new URL(infoLink);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getMtitle() {
        return mtitle;
    }

    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }

    public String getMauthors() {
        return mauthors;
    }

    public void setMauthors(String mauthors) {
        this.mauthors = mauthors;
    }

    public URL getMinfoLink() {
        return minfoLink;
    }

    public void setMinfoLink(URL minfoLink) {
        this.minfoLink = minfoLink;
    }

    public static List<BookInfo> fromJsonResponse(String s) {
        List<BookInfo> lista = new ArrayList<BookInfo>();

        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            int i = 0;
            String title = null;
            String authors = null;
            String info = null;
            while (i < itemsArray.length()) {
                // Get the current item information.
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                    info = volumeInfo.getString("infoLink");
                    if (authors != null && title != null && info != null) {
                        lista.add(new BookInfo(title, authors, info));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Move to the next item.
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return lista;

    }
}
