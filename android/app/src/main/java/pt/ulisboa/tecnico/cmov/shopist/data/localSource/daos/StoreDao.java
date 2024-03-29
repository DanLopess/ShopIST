package pt.ulisboa.tecnico.cmov.shopist.data.localSource.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import pt.ulisboa.tecnico.cmov.shopist.data.localSource.dbEntities.Store;
import pt.ulisboa.tecnico.cmov.shopist.data.localSource.relations.StoreProduct;

@Dao
public interface StoreDao {

    @Query("SELECT * FROM stores")
    Observable<List<Store>> getStores();

    @Query("SELECT * FROM stores where storeId == :id")
    Observable<Store> getStore(Long id);

    @Transaction
    @Query("SELECT * FROM stores")
    Observable<List<StoreProduct>> getStoresProducts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Store store);

    @Delete
    void delete(Store store);
}
