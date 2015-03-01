package se.anyro.nfc_reader;

public class TermsOfUseActivity extends BaseFragmentActivity {
    @Override
    protected int getActivityLayout() {
        return R.layout.terms_of_use_layout;
    }

    @Override
    protected int getDrawerLayout() {
        return R.id.drawer_layout;
    }
}
