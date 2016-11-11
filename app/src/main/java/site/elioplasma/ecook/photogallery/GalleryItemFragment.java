package site.elioplasma.ecook.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by eli on 4/12/16.
 * Updated by eli on 11/10/16.
 */
public class GalleryItemFragment extends Fragment {

    private static final String TAG = "GalleryItemFragment";

    private static final String ARG_ITEM_ID = "item_id";

    private GalleryItem mGalleryItem;
    private ImageView mPhoto;
    private TextView mCaptionTextView;
    private TextView mUrlTextView;

    public static GalleryItemFragment newInstance(String itemId) {
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, itemId);

        GalleryItemFragment fragment = new GalleryItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String itemId = getArguments().getString(ARG_ITEM_ID);

        mGalleryItem = GalleryItemData.get(getActivity()).getGalleryItem(itemId);
        new FetchItemTask(mGalleryItem.getId()).execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery_item, container, false);

        mPhoto = (ImageView) v.findViewById(R.id.gallery_item_photo);
        mCaptionTextView = (TextView) v.findViewById(R.id.gallery_item_caption);
        mUrlTextView = (TextView) v.findViewById(R.id.gallery_item_url);

        mCaptionTextView.setText(mGalleryItem.getCaption());
        mUrlTextView.setText(mGalleryItem.getUrl());

        Picasso.with(getActivity()).load(mGalleryItem.getUrl()).into(mPhoto);

        return v;
    }

    private class FetchItemTask extends AsyncTask<Void,Void, String> {
        private String mId;

        public FetchItemTask(String id) {
            mId = id;
        }

        @Override
        protected String doInBackground(Void... params) {
            return new FlickrFetchr().getPhotoUrl(mId);
        }

        @Override
        protected void onPostExecute(String photoUrl) {
            mGalleryItem.setUrl(photoUrl);
            mUrlTextView.setText(photoUrl);
            Picasso.with(getActivity()).load(mGalleryItem.getUrl()).into(mPhoto);
        }
    }
}
