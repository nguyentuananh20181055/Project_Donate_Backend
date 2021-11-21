package entities;

import com.google.gson.Gson;
import config.BaseConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.sql.Timestamp;
import java.util.*;

public class RestApi extends AbstractVerticle {
    Gson gson = new Gson();
    public RestApi() {
        super();
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);

        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("x-request-with");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        allowedHeaders.add("X-PINGARUNER");

        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.PATCH);
        allowedMethods.add(HttpMethod.PUT);

        router.route().handler(CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));

        router.route().handler(BodyHandler.create());

        router.post("/insert").handler(this::handleInsert);
        router.get("/getListDonate").handler(this::getRecord);
        router.post("/modify").handler(this::handleModify);
        router.post("/insertDonate").handler(this::handleInsertdonate);
        router.get("/getHighestDonate").handler(this::handleGetHigestDonate);
        router.get("/getTotalDonate").handler(this::handleGetTotalDonate);

        vertx.createHttpServer().requestHandler(router).listen(BaseConfig.getInstance().port);
    }

    private void handleGetHigestDonate(RoutingContext routingContext) {
        String result = "";
        JsonObject body = routingContext.getBodyAsJson();

        result = gson.toJson(DatabaseManager.getInstance().getHighestDonate(body.getString("startDate"), body.getString("endDate"), body.getInteger("number")));
        HttpServerResponse response = routingContext.response();
        response.setStatusCode(200).end(result);

    }

    private void handleGetTotalDonate(RoutingContext routingContext) {
        String result = "";
        JsonObject body = routingContext.getBodyAsJson();

        result = String.valueOf(DatabaseManager.getInstance().getTotalDonate(body.getString("startDate"), body.getString("endDate")));

        HttpServerResponse response = routingContext.response();
        response.setStatusCode(200).end(result);
    }

    private void handleInsertdonate(RoutingContext routingContext) {
        JsonObject body = routingContext.getBodyAsJson();
        DonateInfo info = new Gson().fromJson(String.valueOf(body), DonateInfo.class);

        DatabaseManager.getInstance().insertTable(info);
        HttpServerResponse response = routingContext.response();
        response.setStatusCode(200).end("Success");
    }

    private void handleInsert(RoutingContext routingContext) {
        JsonObject body = routingContext.getBodyAsJson();
        DonateInfo info = new Gson().fromJson(String.valueOf(body), DonateInfo.class);

        DatabaseManager.getInstance().insertTable(info);
        HttpServerResponse response = routingContext.response();
        response.setStatusCode(200).end("Success");
    }

    private void getRecord(RoutingContext routingContext) {
        String result = "";
        JsonObject body = routingContext.getBodyAsJson();
        result = gson.toJson(DatabaseManager.getInstance().getListDonate(body.getString("startDate"), body.getString("endDate")));

        HttpServerResponse response = routingContext.response();
        response.setStatusCode(200).end(result);
    }
    private void handleModify(RoutingContext routingContext) {
        io.vertx.core.json.JsonObject body = routingContext.getBodyAsJson();
        // code here
        HttpServerResponse response = routingContext.response();
        response.setStatusCode(200).end("Success");
    }

}
