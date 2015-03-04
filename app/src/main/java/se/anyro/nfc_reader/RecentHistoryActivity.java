package se.anyro.nfc_reader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.otentico.android.model.Product;
import com.otentico.android.model.ProductRealm;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class RecentHistoryActivity extends BaseFragmentActivity {

    private RealmResults<ProductRealm> mRealmResults;
    private RealmAdapter mRealmAdapter;
    private ListView listView;
    Parcelable mListViewState = null;
    private Realm mRealm;


    @Override
    protected int getActivityLayout() {
        return R.layout.scan_history_layout;
    }

    @Override
    protected int getDrawerLayout() {
        return R.id.drawer_layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(getApplicationContext());
        listView = (ListView) findViewById(R.id.recent_list);
        TextView emptyText = (TextView) findViewById(R.id.empty);
        listView.setEmptyView(emptyText);
        updateUI();
        listView.setOnItemClickListener(new ProductClickListener());
    }


    public void updateUI() {
//        mCursor = NoteLab.getInstance(getActivity().getApplicationContext()).getCursor();
//        mCursorAdapter =
        RealmQuery<ProductRealm> query = mRealm.where(ProductRealm.class);
        mRealmResults = query.findAll();
        mRealmResults.sort("date", RealmResults.SORT_ORDER_DESCENDING);
        mRealmAdapter = new RealmAdapter(this, mRealmResults, true);
        (listView).setAdapter(mRealmAdapter);
        if (mListViewState != null)
            listView.onRestoreInstanceState(mListViewState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mListViewState = listView.onSaveInstanceState();
    }

    private class ProductClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ProductRealm pr = mRealmResults.get(position);

            try {

                JSONObject prod = new JSONObject(pr.getProductData());
                Product.getInstance().populateProduct(prod);
                Intent i = new Intent(RecentHistoryActivity.this, ProductInformation.class);
                i.putExtra(MainScreen.COMPANY_NAME, "Otentico");
                i.putExtra(MainScreen.COMPANY_IMAGE_URL,
                        "http://db.avaliatech.com/uploads/54bc380d7a63bfe599e82fef.jpg");
                startActivity(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class RealmAdapter extends RealmBaseAdapter<ProductRealm> {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        public RealmAdapter(Context context, RealmResults<ProductRealm> realmResults, boolean automaticUpdate) {
            super(context, realmResults, automaticUpdate);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView title;
            TextView date;
            TextView tag;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.history_item_layout, parent, false);
            }
            ProductRealm pr = realmResults.get(position);
            title = (TextView) convertView.findViewById(R.id.name_textView);
            date = (TextView) convertView.findViewById(R.id.date_textView);
            tag = (TextView) convertView.findViewById(R.id.nfc_tag_textView);

            date.setText(simpleDateFormat.format(pr.getDate()));
            tag.setText(pr.getNfc_uid());
            try {
                JSONObject prod = new JSONObject(pr.getProductData());
                title.setText(prod.getString("desc"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }
}
