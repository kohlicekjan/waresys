package cz.kohlicek.bpini.service;


import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 */
public class ServiceGenerator {

    private static OkHttpClient.Builder httpClient;
    private static Retrofit.Builder builder;

    /**
     * Vytváří službu s auth tokenem
     *
     * @param serviceClass
     * @param baseUrl
     * @param authToken
     * @param <S>
     * @return službu
     */
    public static <S> S createService(Class<S> serviceClass, String baseUrl, final String authToken) {
        httpClient = new OkHttpClient.Builder();
        builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create());

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request.Builder builder = originalRequest.newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-type", "application/json");

                if (authToken != null)
                    builder.header("Authorization", authToken);

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

    /**
     * Vytváří službu s přihlašujícími údaji
     *
     * @param serviceClass
     * @param baseUrl
     * @param username
     * @param password
     * @param <S>
     * @return službu
     */
    public static <S> S createService(Class<S> serviceClass, String baseUrl, String username, String password) {

        String authToken = Credentials.basic(username, password);
        return createService(serviceClass, baseUrl, authToken);
    }

}
