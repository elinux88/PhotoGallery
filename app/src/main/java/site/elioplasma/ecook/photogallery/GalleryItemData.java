package site.elioplasma.ecook.photogallery;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eli on 4/12/16.
 */
public class GalleryItemData {
    private static GalleryItemData sGalleryItemData;

    private List<GalleryItem> mGalleryItems;

    public static GalleryItemData get(Context context) {
        if (sGalleryItemData == null) {
            sGalleryItemData = new GalleryItemData(context);
        }
        return sGalleryItemData;
    }

    private GalleryItemData(Context context) {
       mGalleryItems = new ArrayList<>();
    }

    public void addGalleryItem(GalleryItem gi) {
        mGalleryItems.add(gi);
    }

    public List<GalleryItem> getGalleryItems() {
        return mGalleryItems;
    }

    public GalleryItem getGalleryItem(String id) {
        for (GalleryItem item : mGalleryItems) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
}
