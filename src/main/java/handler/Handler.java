package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import restserver.http.ContentType;
import restserver.http.HttpStatus;
import restserver.http.Method;
import restserver.server.Request;
import restserver.server.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public abstract class Handler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            Request request = new Request(httpExchange.getRequestURI());
            String method = httpExchange.getRequestMethod();
            List<String> path = request.getPathParts();

            Response response = routeRequest(method, path, httpExchange, request);
            response.send(httpExchange);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Response routeRequest(String method, List<String> path, HttpExchange exchange, Request request) throws IOException {
        String body = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
        Map<String, String> params = request.getParams();

        if (method.equals(Method.GET.name())) {
            return handleGet(path, exchange, params);
        } else if (method.equals(Method.POST.name())) {
            return handlePost(path, exchange, body);
        } else if (method.equals(Method.PUT.name())) {
            return handlePut(path, exchange, body);
        }  else if (method.equals(Method.DELETE.name())) {
            return handleDelete(path, exchange);
        }

        return badRequest();
    }

    protected Response badRequest() {
        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "Invalid request");
    }

    protected abstract Response handleGet(List<String> path, HttpExchange exchange, Map<String, String> params);
    protected abstract Response handlePost(List<String> path, HttpExchange exchange, String body);
    protected abstract Response handlePut(List<String> path, HttpExchange exchange, String body);
    protected abstract Response handleDelete(List<String> path, HttpExchange exchange);
}
