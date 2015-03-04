package com.otentico.android.model;

public class DrawerItem {
    /* Commented tags are expected in future updates.
	 */

    public static final int DRAWER_ITEM_TAG_SCAN_HISTORY = 26;
    public static final int DRAWER_ITEM_TAG_TERMS_OF_USE = 27;
    public static final int DRAWER_ITEM_TAG_VERSION = 28;
    public static final int DRAWER_ITEM_TAG_HOME = 29;

    public DrawerItem(int icon, int title, int tag) {
        this.icon = icon;
        this.title = title;
        this.tag = tag;
    }

    private int icon;
    private int title;
    private int tag;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
