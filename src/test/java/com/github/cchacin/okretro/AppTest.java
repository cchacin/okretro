package com.github.cchacin.okretro;


import org.junit.Assert;
import org.junit.Test;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class AppTest {

    @Test
    public void name() throws Exception {

        final OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(chain -> {

            final Request request = chain.request();

            final HttpUrl.Builder urlBuilder = request.url().newBuilder();

            urlBuilder.addQueryParameter("b", "b");

            request.newBuilder().addHeader("a", "a").url(urlBuilder.build());

            return chain.proceed(request);

        }).build();

        final Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://api.github.com/")
                .build();

        final GitHubService service = retrofit.create(GitHubService.class);

        final Call<ResponseBody> repos = service.listRepos("testAuth");

        Assert.assertEquals("testAuth", repos.request().header("Authorization"));
        Assert.assertEquals("?b=b", repos.request().url().query());
        Assert.assertEquals("a", repos.request().header("a"));

        final retrofit2.Response<ResponseBody> execute = repos.execute();

    }

    public static interface GitHubService {
        @GET("users/repos")
        Call<ResponseBody> listRepos(
                @Header("Authorization") String authorization
        );
    }
}
