package restserver.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private String urlContent;
    private String pathname;
    private List<String> pathParts;
    private Map<String, String> params;

    public Request(URI url) {
        this.setUrlContent(url.toString());

        this.setPathname(url.getPath());
        this.setParams(url.getQuery());
    }

    public String getUrlContent(){
        return this.urlContent;
    }

    private void setUrlContent(String urlContent) {
        this.urlContent = urlContent;
    }

    public String getPathname() {
        return pathname;
    }

    private void setPathname(String pathname) {
        this.pathname = pathname;
        String[] stringParts = pathname.split("/");
        this.pathParts = new ArrayList<>();
        for (String part :stringParts)
        {
            if (part != null &&
                    part.length() > 0)
            {
                this.pathParts.add(part);
            }
        }

    }
    public Map<String, String> getParams() {
        return params;
    }

    private void setParams(String params) {
        if (params != null) {
            String[] paramsOfQuery = params.split("&");
            Map<String, String> valuesByKeys = new HashMap<>();
            for (String param : paramsOfQuery) {
                String[] paramValues = param.split("=");
                valuesByKeys.put(paramValues[0], paramValues[1]);
            }

            this.params = valuesByKeys;
        }
    }

    public List<String> getPathParts() {
        return pathParts;
    }

    private void setPathParts(List<String> pathParts) {
        this.pathParts = pathParts;
    }
}
