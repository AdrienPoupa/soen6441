package controllers;

import models.HelloPlay;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;

import views.html.*;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private HttpExecutionContext httpExecutionContext;

    @Inject
    public HomeController(HttpExecutionContext ec) {
        this.httpExecutionContext = ec;
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public Result time() {
        return ok(time.render(new SimpleDateFormat("yyy/MM/dd HH:mm:ss").format(new Date())));
    }

    public CompletionStage<Result> hello(String message) {
        return HelloPlay.helloPlay(message).thenApplyAsync(answer -> {
            return ok("Hello, "+answer+"!");
        }, httpExecutionContext.current());
    }
}
