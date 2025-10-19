package service;

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

    private Response routeRequest(String method, List<String> path, HttpExchange exchange, Request request) throws IOException {
        String body = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

        if (method.equals(Method.GET.name())) {
            return handleGet(path, request);
        } else if (method.equals(Method.POST.name())) {
            return handlePost(path, body);
        } else if (method.equals(Method.PUT.name())) {
            return handlePut(path, body);
        }  else if (method.equals(Method.DELETE.name())) {
            return handleDelete(path);
        }

        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[]");
    }

    protected Response badRequest() {
        return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[]");
    }

    protected abstract Response handleGet(List<String> path, Request request);
    protected abstract Response handlePost(List<String> path, String body);
    protected abstract Response handlePut(List<String> path, String body);
    protected abstract Response handleDelete(List<String> path);
}
