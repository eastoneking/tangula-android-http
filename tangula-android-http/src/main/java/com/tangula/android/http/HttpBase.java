package com.tangula.android.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tangula.utils.JsonUtils;
import com.tangula.utils.function.BiConsumer;
import com.tangula.utils.function.Consumer;
import com.tangula.utils.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function6;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


@SuppressWarnings({"unchecked","unused"})
public abstract class HttpBase {


    public static Supplier<String> USER_ID_SUPPLIER ;

    public static Consumer<String> FUNC_LOG_VERB ;

    public static Consumer<String> FUNC_SHOW_MESSAGE ;

    public static BiConsumer<String, Throwable> FUNC_LOG_WARN ;

    public static BiConsumer<String, Throwable> FUNC_LOG_ERROR ;

    public static MediaType MEDIA_JSON = MediaType.parse("application/json; charset=utf-8");

    public static String URL_PREFIX = "";

    public static Function2<String, Object, Request> METHOD_GET;

    public static Function2<String, Object, Request> METHOD_POST_JSON ;

    public static Function2<String, Object, Request> METHOD_PUT_JSON;

    public static Function2<String, Object, Request> METHOD_DELETE_JSON;

    public static Function2<Response, Call, Unit> METHOD_COMMON_HTTP_FAIL_RESP;


    public static Function2<IOException, Call, Unit> METHOD_COMMON_HTTP_FAIL ;

    public static Function3<BizResponse<Object>, Response, Call, Unit> METHOD_COMMON_BIZ_FAIL ;

    public static void loadImage(String imageId, final Consumer<Bitmap> cb) {
        String url = URL_PREFIX + "/viewImageController/view/" + imageId;

        HttpUrl.Builder url_fac = HttpUrl.get(url).newBuilder();

        Request.Builder builder = new Request.Builder().url(url_fac.build());
        builder.addHeader("auth", USER_ID_SUPPLIER.get());

        Request request = builder.get().build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(
                request
        ).enqueue(
                new Callback() {
                    public void onFailure( @NotNull Call call, @NotNull IOException e) {
                            FUNC_LOG_ERROR.accept(e.getLocalizedMessage(), e);
                    }

                    public void onResponse( @NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody body = response.body();
                            if (body != null) {
                                byte[] bytes = body.bytes();
                                try {
                                    cb.accept(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                } catch (Exception e) {
                                    FUNC_LOG_ERROR.accept("image $url response not a picture", e);
                                    cb.accept(null);
                                }
                            } else {
                                FUNC_LOG_WARN.accept("image $url response not have a body", null);
                                cb.accept(null);
                            }
                        } else {
                            FUNC_LOG_WARN.accept("image $url response not success", null);
                            cb.accept(null);
                        }
                    }
                }
        );

    }


    protected static <T, R> void getBizSucessA(final String url, final T param, final Class<R> respType, final Function1<R, Unit> onSuccess)

    {
        getBizHttpSucessA(url, param, respType, onSuccess, (rBizResponse, response, call) -> {
            METHOD_COMMON_BIZ_FAIL.invoke((BizResponse<Object>) rBizResponse, response, call);
            return Unit.INSTANCE;
        });
    }

    protected static <T, R> void postBizSucessA(final String url, final T param, final Class<R> respType, final Function1<R, Unit> onSuccess)

    {
        postBizHttpSucessA(url, param, respType, onSuccess, (rBizResponse, response, call) -> {
            METHOD_COMMON_BIZ_FAIL.invoke((BizResponse<Object>) rBizResponse, response, call);
            return Unit.INSTANCE;
        });
    }


    public static <T, R> void getBizListSucessA(String url, T param, final Class<R> itemType, final Consumer<PagingBody<R>> onSuccess) {
        Function1<PagingBody, Unit> onSuc = body -> {
            PagingBody res = new PagingBody();
            res.setPageIndex(body.getPageIndex());
            res.setPageSize(body.getPageSize());
            res.setTotal(body.getTotal());
            final ArrayList<R> items = new ArrayList();

            for (Object o : body.getItems()) {
                items.add(JsonUtils.fromJson(JsonUtils.toJson(o), itemType));
            }

            res.setItems(items);
            onSuccess.accept(res);

            return Unit.INSTANCE;
        };
        getBizHttpSucessA(url, param, PagingBody.class, onSuc, (rBizResponse, response, call) -> {
            METHOD_COMMON_BIZ_FAIL.invoke((BizResponse) rBizResponse, response, call);
            return Unit.INSTANCE;
        });
    }


    public static <T, R> void postBizListSucessA(String url, T param, final Class<R> itemType, final Consumer<PagingBody<R>> onSuccess) {
        Function1<PagingBody, Unit> onSuc = body -> {
            PagingBody res = new PagingBody();
            res.setPageIndex(body.getPageIndex());
            res.setPageSize(body.getPageSize());
            res.setTotal(body.getTotal());
            final ArrayList<R> items = new ArrayList();

            for (Object o : body.getItems()) {
                items.add(JsonUtils.fromJson(JsonUtils.toJson(o), itemType));
            }

            res.setItems(items);
            onSuccess.accept(res);

            return Unit.INSTANCE;
        };
        postBizHttpSucessA(url, param, PagingBody.class, onSuc, (rBizResponse, response, call) -> {
            METHOD_COMMON_BIZ_FAIL.invoke((BizResponse) rBizResponse, response, call);
            return Unit.INSTANCE;
        });
    }

    protected <T, T1> void putBizSucessA(final String url, final T param, final Class<T1> respType, final Function1<T1, Unit> onSuccess)

    {
        putBizHttpSucessA(url, param, respType, onSuccess, (rBizResponse, response, call) -> {
            METHOD_COMMON_BIZ_FAIL.invoke((BizResponse<Object>) rBizResponse, response, call);
            return Unit.INSTANCE;
        });
    }


    protected <T, TR> void deleteBizSucessA(final String url, final T param, final Class<TR> respType, final Function1<TR, Unit> onSuccess)

    {
        deleteBizHttpSucessA(url, param, respType, onSuccess, (rBizResponse, response, call) -> {
            METHOD_COMMON_BIZ_FAIL.invoke((BizResponse<Object>) rBizResponse, response, call);
            return Unit.INSTANCE;
        });
    }


    protected static <T, R> void getBizHttpSucessA(final String url, final T param, final Class<R> respType, final Function1<R, Unit> onSuccess, final Function3<BizResponse<R>, Response, Call, Unit> onBizFail)

    {
        getBizA(url, param, respType, onSuccess, onBizFail, METHOD_COMMON_HTTP_FAIL_RESP, METHOD_COMMON_HTTP_FAIL);
    }

    protected static <T, R> void postBizHttpSucessA(final String url, final T param, final Class<R> respType, final Function1<R, Unit> onSuccess, final Function3<BizResponse<R>, Response, Call, Unit> onBizFail)

    {
        postBizA(url, param, respType, onSuccess, onBizFail, METHOD_COMMON_HTTP_FAIL_RESP, METHOD_COMMON_HTTP_FAIL);
    }

    protected static <T, R> void putBizHttpSucessA(final String url, final T param, final Class<R> respType, final Function1<R, Unit> onSuccess, final Function3<BizResponse<R>, Response, Call, Unit> onBizFail)

    {
        putBizA(url, param, respType, onSuccess, onBizFail, METHOD_COMMON_HTTP_FAIL_RESP, METHOD_COMMON_HTTP_FAIL);
    }


    protected static <T, R> void deleteBizHttpSucessA(final String url, final T param, final Class<R> respType, final Function1<R, Unit> onSuccess, final Function3<BizResponse<R>, Response, Call, Unit> onBizFail)

    {
        deleteBizA(url, param, respType, onSuccess, onBizFail, METHOD_COMMON_HTTP_FAIL_RESP, METHOD_COMMON_HTTP_FAIL);
    }

    protected static <T, R> void getBizA(final String url, final T param, final Class<R> respType, final Function1<R, Unit> onSuccess, final Function3<BizResponse<R>, Response, Call, Unit> onBizFail,
                                         final Function2<Response, Call, Unit> onFailResponse, final Function2<IOException, Call, Unit> onHttpFail)

    {

        httpBizA((s, t, aClass, onSuccess2, onFailResponse2, onHttpFail2) -> {
            sendHttpJsonARespJson(METHOD_GET, s, t, aClass, onSuccess2, onFailResponse2, onHttpFail2);
            return Unit.INSTANCE;
        }, url, param, respType, onSuccess, onBizFail, onFailResponse, onHttpFail);
    }

    protected static <T, R> void postBizA(final String url, final T param, final Class<R> respType, final Function1<R, Unit> onSuccess, final Function3<BizResponse<R>, Response, Call, Unit> onBizFail,
                                          final Function2<Response, Call, Unit> onFailResponse, final Function2<IOException, Call, Unit> onHttpFail)

    {
        httpBizA((s, t, aClass, onSuccess2, onFailResponse2, onHttpFail2) -> {
            sendHttpJsonARespJson(METHOD_POST_JSON, s, t, aClass, onSuccess2, onFailResponse2, onHttpFail2);
            return Unit.INSTANCE;
        }, url, param, respType, onSuccess, onBizFail, onFailResponse, onHttpFail);

    }


    protected static <T, R> void putBizA(final String url, final T param, final Class<R> respType, final Function1<R, Unit> onSuccess, final Function3<BizResponse<R>, Response, Call, Unit> onBizFail,
                                         final Function2<Response, Call, Unit> onFailResponse, final Function2<IOException, Call, Unit> onHttpFail)

    {
        httpBizA((s, t, aClass, onSuccess2, onFailResponse2, onHttpFail2) -> {
            sendHttpJsonARespJson(METHOD_PUT_JSON, s, t, aClass, onSuccess2, onFailResponse2, onHttpFail2);
            return Unit.INSTANCE;
        }, url, param, respType, onSuccess, onBizFail, onFailResponse, onHttpFail);
    }


    protected static <T, R> void deleteBizA(final String url, final T param, final Class<R> respType, final Function1<R, Unit> onSuccess,
                                            final Function3<BizResponse<R>, Response, Call, Unit> onBizFail,
                                            final Function2<Response, Call, Unit> onFailResponse, final Function2<IOException, Call, Unit> onHttpFail)

    {
        httpBizA((s, t, aClass, onSuccess2, onFailResponse2, onHttpFail2) -> {
            sendHttpJsonARespJson(METHOD_DELETE_JSON, s, t, aClass, onSuccess2, onFailResponse2, onHttpFail2);
            return Unit.INSTANCE;
        }, url, param, respType, onSuccess, onBizFail, onFailResponse, onHttpFail);
    }

    private static <T, R> void httpBizA(
            Function6<
                    String,
                    T,
                    Class,
                    Function3<BizResponse<R>, Response, Call, Unit>,
                    Function2<Response, Call, Unit>,
                    Function2<IOException, Call, Unit>, Unit> func,
            String url, T param, final Class<R> respType, final Function1<R, Unit> onSuccess,
            final Function3<BizResponse<R>, Response, Call, Unit> onBizFail,
            final Function2<Response, Call, Unit> onFailResponse, final Function2<IOException, Call, Unit> onHttpFail
    )

    {

        Function3<BizResponse<R>, Response, Call, Unit> resultHandler = (biz, resp, call) -> {
            if (biz != null) {
                if (biz.getStatus() == 0) {
                    FUNC_LOG_VERB.accept("resp business body type:" + respType.getName());
                    if (StringUtils.equalsAny(respType.getName(), "String", "char", "byte", "short", "double", "float", "long", "int", "boolean", "java.lang.Boolean", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Short", "java.lang.Byte", "java.lang.Character", "java.lang.String")) {
                        FUNC_LOG_VERB.accept("resp business body: ${biz.body}");
                        onSuccess.invoke( biz.getBody());
                    } else {
                        String strBody = JsonUtils.toJson(biz.getBody());
                        FUNC_LOG_VERB.accept("resp business body:  $strBody");
                        R respBody = JsonUtils.fromJson(strBody, respType);
                        onSuccess.invoke(respBody);
                    }
                } else {
                    onBizFail.invoke(new BizResponse(biz.getStatus(), biz.getStatusText(), biz.getMessage(), null), resp, call);
                }
            } else {
                onFailResponse.invoke(resp, call);
            }
            return Unit.INSTANCE;
        };

        func.invoke(URL_PREFIX + url, param, BizResponse.class, resultHandler, onFailResponse, onHttpFail);
    }


    protected static <T, R> void sendHttpJsonARespJson(Function2<String, Object, Request> method,
                                                       String url,
                                                       final T param, final Class<R> respType,
                                                       final Function3<R, Response, Call, Unit> onSuccess,
                                                       final Function2<Response, Call, Unit> onFailResponse,
                                                       final Function2<IOException, Call, Unit> onHttpFail)

    {
        OkHttpClient client = new OkHttpClient();
        client.newCall(
                method.invoke(url, param)
        ).enqueue(
                new Callback() {
                    public void onFailure( @NotNull Call call,  @NotNull IOException e) {
                        onHttpFail.invoke(e, call);
                    }

                    public void onResponse(  @NotNull Call call,  @NotNull Response response) throws IOException {
                        if (response.isSuccessful() || (response.code() >= 200 && response.code() <= 299)) {
                            ResponseBody body = response.body();
                            if (body != null) {
                                String str = body.string();
                                FUNC_LOG_VERB.accept("http resp body:$str");
                                FUNC_LOG_VERB.accept("http resp body type:" + respType.getName());
                                R obj = JsonUtils.fromJson(str, respType);
                                onSuccess.invoke(obj, response, call);
                            } else {
                                onSuccess.invoke(null, response, call);
                            }
                        } else {
                            onFailResponse.invoke(response, call);
                        }
                    }
                }
        );
    }


    public static void init() {
        USER_ID_SUPPLIER = new DefaultUserIdSupplier();

        FUNC_LOG_VERB = new DefaultConsoleMessageLog();

        FUNC_SHOW_MESSAGE = new DefaultConsoleMessageLog();

        FUNC_LOG_WARN = new DefaultConsoleErrorMessageLog();

        FUNC_LOG_ERROR =new DefaultConsoleErrorMessageLog();


        METHOD_GET = (url, param) -> {

            HttpUrl.Builder b = HttpUrl.get(url).newBuilder();

            for (Method prop : (param != null ? param : new Object()).getClass().getDeclaredMethods()) {
                String name = prop.getName();
                if (StringUtils.startsWith(name, "get") && (prop.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC) {
                    try {
                        Object value = prop.invoke(param);
                        if (value != null) {
                            name = name.substring(3);
                            String fc = new String(new char[]{name.charAt(0)});
                            name = name.replaceFirst(fc, fc.toLowerCase());
                            b.addQueryParameter(name, value.toString());
                        }
                    } catch (IllegalAccessException e) {
                        FUNC_LOG_ERROR.accept(e.getLocalizedMessage(), e);
                    } catch (InvocationTargetException e) {
                        FUNC_LOG_ERROR.accept(e.getLocalizedMessage(), e);
                    }

                }
            }

            Request.Builder req = new Request.Builder().url(b.build());
            req.addHeader("auth", USER_ID_SUPPLIER.get());
            return req.get().build();

        };

        METHOD_POST_JSON = (url, param) -> {
            Request.Builder req = new Request.Builder().url(url);
            req.addHeader("auth", USER_ID_SUPPLIER.get());
            RequestBody body = RequestBody.create(MEDIA_JSON, JsonUtils.toJson(param != null ? param : new Object()));
            return req.post(body).build();
        };

        METHOD_PUT_JSON = (url, param) -> {
            Request.Builder req = new Request.Builder().url(url);
            req.addHeader("auth", USER_ID_SUPPLIER.get());
            RequestBody body = RequestBody.create(MEDIA_JSON, JsonUtils.toJson(param != null ? param : new Object()));
            return req.put(body).build();
        };

        METHOD_DELETE_JSON = (url, param) -> {
            Request.Builder req = new Request.Builder().url(url);
            req.addHeader("auth", USER_ID_SUPPLIER.get());
            RequestBody body = RequestBody.create(MEDIA_JSON, JsonUtils.toJson(param != null ? param : new Object()));
            return req.delete(body).build();
        };

        METHOD_COMMON_HTTP_FAIL_RESP = (res, call) -> {
            Request req = call.request();
            FUNC_LOG_WARN.accept("wrong response, req:" + req.method() + " " + req.url() + ", resp:" + res.toString() + ".", null);
            return Unit.INSTANCE;
        };


        METHOD_COMMON_HTTP_FAIL = (e, call) -> {
            Request req = call.request();
            FUNC_LOG_ERROR.accept("request fail, req:" + req.method() + " " + req.url() + ".", e);
            return Unit.INSTANCE;
        };

        METHOD_COMMON_BIZ_FAIL = (objectBizResponse, resp, call) -> {
            Request req = call.request();
            FUNC_LOG_ERROR.accept("business fail, req:" + req.method() + " " + req.url() + ", resp:" + resp.toString() + ".", null);
            return Unit.INSTANCE;
        };
    }

}
