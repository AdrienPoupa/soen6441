package controllers;

import play.mvc.*;

import views.html.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 * @author Adrien Poupa
 */
public class HomeController extends Controller {

    /**
     * Show the index
     * @return CompletionStage of Result page
     */
    public CompletionStage<Result> index() {
        return CompletableFuture.completedFuture(ok(index.render()));
    }
}
