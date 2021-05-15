package pt.ulisboa.tecnico.cmov.shopist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pt.ulisboa.tecnico.cmov.shopist.adapter.ListOfPantriesAdapter;
import pt.ulisboa.tecnico.cmov.shopist.adapter.ListOfProductsAdapter;
import pt.ulisboa.tecnico.cmov.shopist.adapter.ListOfStoresAdapter;
import pt.ulisboa.tecnico.cmov.shopist.dialog.CreatePantryDialogFragment;
import pt.ulisboa.tecnico.cmov.shopist.dialog.CreateProductDialogFragment;
import pt.ulisboa.tecnico.cmov.shopist.dialog.CreateStoreDialogFragment;
import pt.ulisboa.tecnico.cmov.shopist.viewModel.ViewModel;

public class MainActivity extends AppCompatActivity {

    private static final int PROD_SCAN_REQ_CODE = 1;
    private static final int PANTRY_SCAN_REQ_CODE = 2;

    private DialogFragment mCreatePantryListDialog;
    private DialogFragment mCreateStoreListDialog;
    private DialogFragment mCreateProductDialog;
    private String mCurrCategory;
    private RecyclerView rvLists;
    private ListOfPantriesAdapter mPantryAdapter;
    private ListOfStoresAdapter mStoreAdapter;
    private ListOfProductsAdapter mProductsAdapter;
    private Context mContext;
    public ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);

        //if wifi available sync lists ??

        mCreatePantryListDialog = new CreatePantryDialogFragment(this);
        mCreateStoreListDialog = new CreateStoreDialogFragment(this);
        mCreateProductDialog = new CreateProductDialogFragment(this, CreateProductDialogFragment.PRODUCT);
        mContext = this;
        setUpLists();
        setUpBottomNavigation();
    }

    public void createNewList(MenuItem item) {
        if (rvLists.getAdapter() == mPantryAdapter)
            mCreatePantryListDialog.show(getSupportFragmentManager(), "Create Pantry list");
        else if (rvLists.getAdapter() == mStoreAdapter)
            mCreateStoreListDialog.show(getSupportFragmentManager(), "Create Store list");
        else if (rvLists.getAdapter() == mProductsAdapter)
            mCreateProductDialog.show(getSupportFragmentManager(), "Create Product");
    }

    public void addListWithCode(MenuItem item) {
        //TODO add Pantry from code generated by server
    }

    public void addListWithQR(MenuItem item) {
        if (rvLists.getAdapter() == mPantryAdapter) {
            Intent intent = new Intent(this, ScanCodeActivity.class);
            startActivityForResult(intent, PANTRY_SCAN_REQ_CODE);
        }
        else if (rvLists.getAdapter() == mProductsAdapter) {
            Intent intent = new Intent(this, ScanCodeActivity.class);
            startActivityForResult(intent, PROD_SCAN_REQ_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROD_SCAN_REQ_CODE && resultCode == RESULT_OK) {
            assert data != null;
            String code = data.getStringExtra("code");
            viewModel.checkIfProdExistsByCode(code).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(exists -> {
                if (!exists) {
                    CreateProductDialogFragment dialog = new CreateProductDialogFragment(this, code, CreateProductDialogFragment.PRODUCT);
                    dialog.show(getSupportFragmentManager(), "create product");
                } else {
                    runOnUiThread(() -> Toast.makeText(mContext, mContext.getString(R.string.product_already_exists), Toast.LENGTH_SHORT).show());
                }
            });
        } else if (requestCode == PANTRY_SCAN_REQ_CODE && resultCode == RESULT_OK) {
            //TODO add Pantry from QR code scanned
            // pantry will have isOwner = false
        }
    }

    public ViewModel getViewModel() {
        return viewModel;
    }

    private void setUpLists() {
        rvLists = findViewById(R.id.recyclerView);
        mPantryAdapter = new ListOfPantriesAdapter(this, viewModel.getPantries());
        mStoreAdapter = new ListOfStoresAdapter(this, viewModel.getStores());
        mProductsAdapter = new ListOfProductsAdapter(this, viewModel.getProducts());
    }

    private void setUpBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        TextView titleTextView = findViewById(R.id.textView_title);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    mCurrCategory = item.getTitle().toString();
                    titleTextView.setText(mCurrCategory);
                    updateMenuOptions(item.getItemId());
                    if (item.getItemId() == R.id.action_pantry_lists) {
                        rvLists.setAdapter(mPantryAdapter);
                        rvLists.setLayoutManager(layoutManager);
                    } else if (item.getItemId() == R.id.action_store_lists) {
                        rvLists.setAdapter(mStoreAdapter);
                        rvLists.setLayoutManager(layoutManager);
                    } else if (item.getItemId() == R.id.action_products_list) {
                        rvLists.setAdapter(mProductsAdapter);
                        rvLists.setLayoutManager(layoutManager);
                    }
                    return true;
                }
        );

        // Select initial list TODO based on location
        // If we can get the current location and associate it with  only one list, open that list by default
        //for now select Pantry list
        bottomNavigationView.setSelectedItemId(R.id.action_pantry_lists);
    }

    private void updateMenuOptions(int itemId) {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        Menu menu = toolbar.getMenu();
        MenuItem item1 = menu.findItem(R.id.action_addList);
        MenuItem item2 = menu.findItem(R.id.action_addListCode);
        MenuItem item3 = menu.findItem(R.id.action_addListQR);
        if (itemId == R.id.action_pantry_lists) {
            item1.setTitle(R.string.create_new_list);
            item1.setVisible(true);
            item2.setTitle(R.string.insert_a_code);
            item2.setVisible(true);
            item3.setTitle(R.string.scan_qr_code);
            item3.setVisible(true);
        } else if (itemId == R.id.action_store_lists) {
            item1.setTitle(R.string.create_new_list);
            item1.setVisible(true);
            item2.setVisible(false);
            item3.setVisible(false);
        } else if (itemId == R.id.action_products_list) {
            item1.setTitle(R.string.create_new_product);
            item1.setVisible(true);
            item2.setVisible(false);
            item3.setTitle(R.string.scan_a_barcode);
            item3.setVisible(true);
        }
    }
}