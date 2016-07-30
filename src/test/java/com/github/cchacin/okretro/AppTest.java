package com.github.cchacin.okretro;


import org.junit.Assert;
import org.junit.Test;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class AppTest {

    @Test
    public void name() throws Exception {

        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("hello, world!"));
        server.start();

        final OkHttpClient client = new OkHttpClient().newBuilder().addInterceptor(chain -> {

            final Request request = chain.request();

            HttpUrl.Builder urlBuilder = request.url().newBuilder();

            urlBuilder = urlBuilder.addQueryParameter("b", "b");

            return chain.proceed(request.newBuilder().addHeader("a", "a").url(urlBuilder.build()).build());

        }).build();


        final Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(server.url(""))
                .build();

        final GitHubService service = retrofit.create(GitHubService.class);

        final Call<ResponseBody> repos = service.listRepos("testAuth");
        repos.execute();
        final RecordedRequest request1 = server.takeRequest();
        request1.getRequestLine();


        Assert.assertEquals("testAuth", request1.getHeader("Authorization"));
        Assert.assertEquals("/users/repos?b=b", request1.getPath());
        Assert.assertEquals("a", request1.getHeader("a"));


    }

    public static interface GitHubService {
        @GET("users/repos")
        Call<ResponseBody> listRepos(
                @Header("Authorization") String authorization
        );
    }
}
