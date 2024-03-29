package pt.ulisboa.tecnico.cmov.shopist.data.remoteSource;

import java.util.List;
import java.util.UUID;

import io.reactivex.rxjava3.core.Observable;
import pt.ulisboa.tecnico.cmov.shopist.data.dto.Beacon;
import pt.ulisboa.tecnico.cmov.shopist.data.dto.BeaconTime;
import pt.ulisboa.tecnico.cmov.shopist.data.dto.PantryDto;
import pt.ulisboa.tecnico.cmov.shopist.data.dto.ProductPrice;
import pt.ulisboa.tecnico.cmov.shopist.data.dto.ProductRating;
import pt.ulisboa.tecnico.cmov.shopist.data.dto.QueueTimeRequestDTO;
import pt.ulisboa.tecnico.cmov.shopist.data.dto.QueueTimeResponseDTO;
import pt.ulisboa.tecnico.cmov.shopist.data.localSource.dbEntities.Pantry;
import pt.ulisboa.tecnico.cmov.shopist.data.localSource.dbEntities.Product;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface BackendAPI {
    String BASE_URL = "http://daniellopes.ddns.net/";

    String pantryUrl = "/api/pantry";
    String ratingUrl = "/api/product/ratings";
    String priceUrl = "/api/product/prices";

    @POST("/api/beacon")
    @Headers("Cache-Control: no-cache")
    Observable<Beacon> postBeacon(@Body Beacon beacon);

    @GET(pantryUrl)
    Observable<List<Pantry>> getPantries();

    @GET("/api/product/")
    Observable<List<Product>> getProducts();

    @GET(pantryUrl)
    Call<PantryDto> getPantryByUUID(@Query("uuid") String uuid);

    @GET(pantryUrl)
    @Headers("Cache-Control: no-cache")
    Call<PantryDto> getRefreshedPantryByUUID(@Query("uuid") String uuid);

    @POST(pantryUrl)
    @Headers("Cache-Control: no-cache")
    Call<PantryDto> createPantry(@Body PantryDto pantryDto);

    @PUT(pantryUrl)
    @Headers("Cache-Control: no-cache")
    Call<PantryDto> updatePantry(@Body PantryDto pantryDto);

    @POST("/api/beacon/in")
    @Headers("Cache-Control: no-cache")
    Observable<UUID> postInTime(@Body BeaconTime beaconTime);

    @POST("/api/beacon/out")
    @Headers("Cache-Control: no-cache")
    Observable<UUID> postOutTime(@Body BeaconTime beaconTime);

    @POST("/api/beacon/queueTime")
    @Headers("Cache-Control: no-cache")
    Observable<QueueTimeResponseDTO> getQueueTime(@Body QueueTimeRequestDTO requestDTO);

    @GET(ratingUrl)
    Observable<ProductRating> getProductRating(@Query("barcode") String barcode);

    @POST(ratingUrl)
    @Headers("Cache-Control: no-cache")
    Call<ProductRating> addProductRating(@Query("barcode") String barcode,
                                @Query("rating") Integer rating,
                                @Query("prev") Integer prev);

    @GET(priceUrl)
    Observable<ProductPrice> getProductPrice(@Query("barcode") String barcode);

    @POST(priceUrl)
    @Headers("Cache-Control: no-cache")
    Call<ProductPrice> addProductPrice(@Query("barcode") String barcode,
                                         @Query("price") Double price);
}
