package cz.kohlicek.bpini.service;


import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    private final static String PROTOCOL = "https://";

    /**
     * Vytváří službu s auth tokenem
     *
     * @param serviceClass
     * @param baseUrl
     * @param authToken
     * @param <S>
     * @return službu
     */
    public static <S> S createService(Class<S> serviceClass, final String baseUrl, final String authToken) {

        OkHttpClient okHttpClient = getOkHttpClient(authToken);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(String.format("%s%s", PROTOCOL, baseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient).build();

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
    public static <S> S createService(Class<S> serviceClass, final String baseUrl, final String username, final String password) {

        String authToken = Credentials.basic(username, password);
        return createService(serviceClass, baseUrl, authToken);
    }


    private static OkHttpClient getOkHttpClient(final String authToken) {

        try {

            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            }};

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();


            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                    .addInterceptor(new Interceptor() {
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
                    }).build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

}
