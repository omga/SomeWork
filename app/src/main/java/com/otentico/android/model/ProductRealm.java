package com.otentico.android.model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class ProductRealm extends RealmObject{
    @PrimaryKey
    private String uuid;
    private Date date;
    private String nfc_uid;
    private String productData;

    public String getNfc_uid() {
        return nfc_uid;
    }

    public void setNfc_uid(String nfc_uid) {
        this.nfc_uid = nfc_uid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getProductData() {
        return productData;
    }

    public void setProductData(String productData) {
        this.productData = productData;
    }
}
