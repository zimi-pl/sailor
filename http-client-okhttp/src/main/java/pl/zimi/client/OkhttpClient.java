package pl.zimi.client;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class OkhttpClient implements HttpClient {
    @Override
    public Response handleRequest(Request request) {
        OkHttpClient okHttpClient = new OkHttpClient();
//        okHttpClient.newBuilder().

//        RequestBody body = RequestBody.create(json, JSON);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//        try (Response response = client.newCall(request).execute()) {
//            return response.body().string();
//        }
        return null;
    }
}
