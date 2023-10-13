package de.danieldeusing.crossng.devtools.util;

import lombok.SneakyThrows;

import java.net.URI;

public abstract class WebUtil
{
    public static String getFormattedTargeturl(String baseUrl, String path)
    {
        if (path.startsWith("/") && (baseUrl.endsWith("/")))
        {
            return baseUrl.substring(0, baseUrl.length() - 1) + path;
        }

        if (path.startsWith("http"))
        {
            return path;
        }

        return baseUrl + path;
    }

    @SneakyThrows
    public static String replaceHostUrlIfNeeded(String base, String target)
    {
        URI uri = new URI(target);
        URI baseUri = new URI(base);

        if (!uri.getHost().equals(baseUri.getHost()))
        {
            uri =
                new URI(baseUri.getScheme(), baseUri.getUserInfo(), baseUri.getHost(), baseUri.getPort(), uri.getPath(),
                    uri.getQuery(), uri.getFragment());
        }

        return uri.toString();
    }
}
