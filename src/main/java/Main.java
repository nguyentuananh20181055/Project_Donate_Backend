
import entities.RestApi;
import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        initVertx();                                        //initialize Vertx REST API
    }


    /**
     * Web API
     */
    private static void initVertx() {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new RestApi());
    }
}
