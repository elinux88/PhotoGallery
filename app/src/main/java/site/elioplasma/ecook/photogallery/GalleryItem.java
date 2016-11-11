package site.elioplasma.ecook.photogallery;

/**
 * Created by eli on 3/29/16.
 * Updated by eli on 11/10/16.
 */
class GalleryItem {
    private String mId;
    private String mTitle;
    private String mUrl;
    private String mOwnerId;
    private String mOwnerName;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String toString() {
        return mTitle;
    }

    String getTitle() {
        return mTitle;
    }

    void setTitle(String title) {
        mTitle = title;
    }

    String getUrl() {
        return mUrl;
    }

    void setUrl(String url) {
        mUrl = url;
    }

    String getOwnerId() {
        return mOwnerId;
    }

    void setOwnerId(String ownerId) {
        mOwnerId = ownerId;
    }

    String getOwnerName() {
        return mOwnerName;
    }

    void setOwnerName(String ownerName) {
        mOwnerName = ownerName;
    }
}
