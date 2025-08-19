    // ApiClient.java
    package com.example.my_o2o_app.network;

    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;

    public class ApiClient {
        //private static final String BASE_URL = "http://10.0.2.2:5000/"; // 에뮬레이터용 주소
        private static final String BASE_URL = "http://172.29.22.244:5000/";//학교 기기용 주소
        //private static final String BASE_URL ="http://192.168.219.103:5000/";//집안용



        private static Retrofit retrofit = null;

        public static Retrofit getClient() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
    }
