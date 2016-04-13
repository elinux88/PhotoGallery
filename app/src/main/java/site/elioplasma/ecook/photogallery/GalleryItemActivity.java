package site.elioplasma.ecook.photogallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by eli on 4/12/16.
 */
public class GalleryItemActivity extends SingleFragmentActivity {

    private static final String EXTRA_ITEM_ID =
            "site.elioplasma.ecook.photogallery.item_id";

    public static Intent newIntent(Context packageContext, String itemId) {
        Intent intent = new Intent(packageContext, GalleryItemActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String itemId = getIntent().getStringExtra(EXTRA_ITEM_ID);
        return GalleryItemFragment.newInstance(itemId);
    }
}
