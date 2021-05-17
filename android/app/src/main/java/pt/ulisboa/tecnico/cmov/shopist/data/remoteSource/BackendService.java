package pt.ulisboa.tecnico.cmov.shopist.data.remoteSource;

import android.content.Context;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import pt.ulisboa.tecnico.cmov.shopist.data.localSource.dbEntities.Pantry;
import pt.ulisboa.tecnico.cmov.shopist.data.localSource.dbEntities.Product;
import pt.ulisboa.tecnico.cmov.shopist.dto.Beacon;
import pt.ulisboa.tecnico.cmov.shopist.dto.BeaconTime;
import pt.ulisboa.tecnico.cmov.shopist.dto.PantryDto;
import pt.ulisboa.tecnico.cmov.shopist.dto.QueueTimeRequestDTO;
import pt.ulisboa.tecnico.cmov.shopist.dto.QueueTimeResponseDTO;
import pt.ulisboa.tecnico.cmov.shopist.dto.ProductRating;
import pt.ulisboa.tecnico.cmov.shopist.util.ShopISTUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackendService {

    private final BackendAPI backendAPI;
    private static BackendService backendServiceInstance;

    private long cacheSize = 10 * 1024 * 1024; // 10 MB

    private Cache cache;
    private Context mContext;

    private BackendService(Context context) {
        cache = new Cache(context.getCacheDir(), cacheSize);
        mContext = context;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(getOfflineInterceptor())
                .addNetworkInterceptor(getOnlineInterceptor())
                .cache(cache)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BackendAPI.BASE_URL)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        backendAPI = retrofit.create(BackendAPI.class);

        Log.d("BACKENDSERVICE", "BackendService running");
    }

    private Interceptor getOnlineInterceptor() {
        return chain -> {
            okhttp3.Response response = chain.proceed(chain.request());
            int maxAge = 60; // read from cache for 60 seconds even if there is internet connection
            return response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .removeHeader("Pragma")
                    .build();
        };
    }

    private Interceptor getOfflineInterceptor() {
        return chain -> {
            Request request = chain.request();
            if (!ShopISTUtils.hasNetwork(mContext)) {
                int maxStale = 60 * 60 * 24 * 7; // Offline cache available for 7 days
                request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("Pragma")
                        .build();
            }
            return chain.proceed(request);
        };
    }


    public static BackendService getInstance(Context context) {
        if(backendServiceInstance == null) {
            backendServiceInstance = new BackendService(context);
        }
        return backendServiceInstance;
    }

    public static BackendService getInstance() {
        return backendServiceInstance;
    }

    public Observable<Beacon> postBeacon(Beacon beacon) {
        return backendAPI.postBeacon(beacon);
    }

    public Observable<List<Pantry>> getPantries() {
        return backendAPI.getPantries();
    }

    public Observable<List<Product>> getProducts() {
        return Observable.just(null);
    }

    public PantryDto getPantry(String uuid) {
        final PantryDto[] pantryDto = {null};
        backendAPI.getPantryByUUID(uuid).enqueue(new Callback<PantryDto>() {
            @Override
            public void onResponse(Call<PantryDto> call, Response<PantryDto> response) {
                pantryDto[0] = response.body();
            }

            @Override
            public void onFailure(Call<PantryDto> call, Throwable t) {
                Log.e("BackendService", "Failed to get pantry from server");

            }
        });
        return pantryDto[0];
    }

    public Single<String> postPantryDto(PantryDto pantryDto) {
        return backendAPI.createPantry(pantryDto);
    }

    public void putPantryDto(PantryDto pantryDto) {
        backendAPI.updatePantry(pantryDto).enqueue(new Callback<PantryDto>() {
            @Override
            public void onResponse(@NonNull Call<PantryDto> call, @NonNull Response<PantryDto> response) {
                // Doesnt need the response yet
            }

            @Override
            public void onFailure(@NonNull Call<PantryDto> call, @NonNull Throwable t) {
                Log.e("BackendService", "Failed to update pantry on server");
            }
        });
    }

    // post pantry
    // put pantry
    // get pantrybyuuid

    public Observable<UUID> postInTime(BeaconTime beaconTime) {
        return backendAPI.postInTime(beaconTime);
    }

    public Observable<UUID> postOutTime(BeaconTime beaconTime) {
        return backendAPI.postOutTime(beaconTime);
    }

    public Observable<QueueTimeResponseDTO> getQueueTime(QueueTimeRequestDTO queueTimeRequestDTO) {
        return backendAPI.getQueueTime(queueTimeRequestDTO);
    }

    public Observable<ProductRating> getProductRating(String barcode) {
        return backendAPI.getProductRating(barcode);
    }

    public ProductRating postProductRating(String barcode, Integer rating, Integer prev) {
        final ProductRating[] productRating = {null};
        backendAPI.addProductRating(barcode, rating, prev).enqueue(new Callback<ProductRating>() {
            @Override
            public void onResponse(@NonNull Call<ProductRating> call, @NonNull Response<ProductRating> response) {
                productRating[0] = response.body();
            }

            @Override
            public void onFailure(@NonNull Call<ProductRating> call, @NonNull Throwable t) {
                Log.e("BackendService", "Failed to add product rating to server");
            }
        });
        return productRating[0];
    }
}
