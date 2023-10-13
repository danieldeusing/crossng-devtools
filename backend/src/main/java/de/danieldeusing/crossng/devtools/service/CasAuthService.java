package de.danieldeusing.crossng.devtools.service;

import de.danieldeusing.crossng.devtools.model.CasLoginResponseDTO;
import de.danieldeusing.crossng.devtools.util.WebUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CasAuthService
{

    public static final String COOKIE_LOCALE = "org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE";
    public static final String COOKIE_DISSESSION = "DISSESSION";
    public static final String COOKIE_TGC = "TGC";
    public static final String COOKIE_XSRF_TOKEN = "XSRF-TOKEN";
    public static final String COOKIE_JSESSIONID = "JSESSIONID";
    public static final String COOKIE_GSID = "c3-gsid";
    public static final String LOCATION = "Location";

    private final RestTemplate restTemplate;
    @Value("${context.base-url}")
    private String baseUrl;
    @Value("${crossng.ldap.username}")
    private String ldapUsername;
    @Value("${crossng.ldap.password}")
    private String ldapPassword;

    public CasAuthService(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }

    public CasLoginResponseDTO casLogin(boolean isReload)
    {
        Map<String, String> cookieMap = new HashMap<>();

        String redirectUrl = initiateLoginAndGetCookies(cookieMap);

        if (!cookieMap.containsKey(COOKIE_DISSESSION))
        {
            if (isReload)
            {
                return null;
            }
            return handleMissingDissession();
        }

        redirectUrl = getXSRFToken(cookieMap, redirectUrl, false);

        redirectUrl = getTicket(cookieMap, redirectUrl);

        redirectUrl = getC3Gsid(cookieMap, redirectUrl);

        boolean success = finishLogin(cookieMap, redirectUrl);

        if (isValidationSuccessful(cookieMap) && success)
        {
            return new CasLoginResponseDTO(cookieMap.get(COOKIE_GSID));
        }
        else
        {
            return null;
        }
    }

    private HttpHeaders createHeadersWithCookies(Map<String, String> cookies, List<String> cookiesToBeMapped)
    {
        HttpHeaders headers = new HttpHeaders();

        cookiesToBeMapped.forEach(key ->
        {
            if (cookies.containsKey(key))
            {
                headers.add("Cookie", key + "=" + cookies.get(key));
            }
        });

        return headers;
    }


    private void addCookies(Map<String, String> cookieMap, ResponseEntity<String> response,
        List<String> responseCookieHeaders)
    {
        List<String> cookies = response.getHeaders().get("Set-Cookie");

        Objects.requireNonNull(cookies).forEach(cookie ->
        {
            for (String mapCookie : responseCookieHeaders)
            {
                if (cookie.contains(mapCookie + "="))
                {
                    cookieMap.put(mapCookie, extractFromCookie(mapCookie, cookie));
                }
            }
        });
    }


    private String initiateLoginAndGetCookies(Map<String, String> cookieMap)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("username", ldapUsername);
        form.add("password", ldapPassword);
        form.add("execution",
            "356d1be8-8a34-438a-8ba0-f7f56fe1d7a4_ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0lzSW10cFpDSTZJakEyTWpsa05tWmtMV1ZsWTJJdE5HSTVOeTA0T1RKaExXVTBZV0UwTkRFM01qTmtNU0o5LlNTbVN5dEJLT2hWUERZM2ZQM2RPZm9kOEFPUS1rZllQVl83WW1wQmdTazZMVjFqMGg5WFJMSTNXZlU3R1ptcXMtbUFBRkpOdGxqajlkQi1mZ2FhRC1DNHJ2SVBYRWNiU0FJanVaaml0TFVldG55R3BVOXlkTGNYN1J6OFJxWjg2bC0tT05tdEp1Z1MzQWNDcFQ2VjAzMkZtYUNfUWJvVXFyWFl3UXY3QjJGaTNPc1hGT1JXQnVFNG9MdzdCeExVaWp6TkxrWXIzTGh5VVlxdk5iUnhWZHZyUnZDMDh0UzJLanMtQmJ2bXgySWNrb1p6YUZwdi1DNlAwWXFkc3hUanJGaDdPRTZyMU5jc0tVZEVhV3l1eVNKU1hiSUJpemdtVmpoTDI3U0FXR0xod1drN2xZTHg2Qlk1UkthYURnZFBVaW5LbFB1eHNXQ1pLa2ZFY1ZVZF9teV9tYUJyMzV4NUJXbjlNZFlFOGhFYjVkNG9KT2x6amU0Vk56ZjBGVG9vZ3JmOUdHTVY2R1h1WTFXNXlZaDdDSkh6akM0cV8tTmVlNDhzZ3JTR3E5aHBLeVg0dkVTNTJOZm1fbGNNMWxRRVdXeWpaSUMtSFhPSlFHOVlMS19mVkQ2dEg1LXRWRWZsek5tZEJLeGxORVF4akhiUkE5UDZqaDFuaFc3YU5HeFlKcHhUbjE1YTNyUkg5N2JVc2dzYXpnTzl4NjhpQVhGZzJsZDdBUDN3V2YxbFBYZlAtYzdSN05RTENCV1FYYmlRNmI3eVpVUEViSW9SS2hVWEJPTGFJNGlwZ2pianZvVHpSb01aZUd0aUREUi1fbW16d0xNejM2VHdMUzRrNDF1MWZqdzZnSTlSUWZmOUVpS0V1WFN6TndUMkV4WHpPY3Nuanhfc2lnZTRPczBQcjRwbUxjX1o1LXl1VHlQamNmSDVHSVlXcUZrWkExQ2pUVzdZT3ZMbFFueWVFT3ZhVXV5c1RyNjJ0YmFiS0NEdjFveEUwOU5kRU9VTEZ6YkhsUXJ5aG0wb2ppTi05YThiT01MR0NScFB1bURuSTJmUVhRaFJjMGpLRFhwczcyc0ROdV9VQnZ2dHhPb0xHTG5SRDNSYlYyTWxuSU91N1JTZVhVa3NqbXZ4N2lvUzhldWFxNXNBRTZCUlpQQk8tLTZYNVJHNXYxQUpac1h6dmRzTWZHYTRTeVJKQVk5UDhBLTA3VWdGaEZYYThoZF9tU3A2NzdzN0NiaUZEaUp1dkdUZ2Ffd3k3cEhmdEprXzhiaElGOS1qNkJZSDFxNkJJVHk2WVlBUzRsNG5OME12TVQzem1VOXJWdmthOU9tZWl0TXNFeVJDWjVEVVBnRDBfVzBlZXdNYWtxaWNqOWppeFQ1dXZIdHZRVU1BSDhleUJNVjktSEhzYzRVWGYwVTFPS2xEeVBHdG5GcG16QVhNVjV4YWVEV0o1MXdhNnA4Y19hWDFoSmtnMFl3WDFWUjB5eHdrQkF5eEduVUUuSmpRcFBMclZrR3dkUTFqVGdzUzBKMG9CQmZIeV8tU3MySGIwcDd4YU96YnBWR1k3Z3hlUVpuYUQtbkt2TWhXQzNDbG1QMXRZVTEzSmdaOUtmQjk4X1E=");
        form.add("_eventId", "submit");

        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new HttpEntity<>(form, headers);

        ResponseEntity<String> response =
            restTemplate.postForEntity(WebUtil.getFormattedTargeturl(baseUrl, "/cas/login"), multiValueMapHttpEntity,
                String.class);

        List<String> responseCookieHeaders = List.of(COOKIE_TGC, COOKIE_DISSESSION, COOKIE_LOCALE);
        addCookies(cookieMap, response, responseCookieHeaders);

        return getLocationHeader(response);
    }

    private String getXSRFToken(Map<String, String> cookieMap, String redirectUrl, boolean isReload)
    {
        List<String> requestCookieHeaders = List.of(COOKIE_DISSESSION, COOKIE_LOCALE);
        HttpHeaders headers = createHeadersWithCookies(cookieMap, requestCookieHeaders);

        ResponseEntity<String> response = performSimpleGetRequest(headers, redirectUrl);

        List<String> responseCookieHeaders = List.of(COOKIE_XSRF_TOKEN, COOKIE_JSESSIONID);

        try
        {
            addCookies(cookieMap, response, responseCookieHeaders);
        }
        catch (NullPointerException e)
        {
            // no cookies found in response, try again one time
            if (!isReload)
            {
                getXSRFToken(cookieMap, redirectUrl, true);
            }
        }

        return getLocationHeader(response);
    }

    private String getTicket(Map<String, String> cookieMap, String redirectUrl)
    {
        List<String> requestCookieHeaders = List.of(COOKIE_TGC, COOKIE_DISSESSION, COOKIE_LOCALE);
        HttpHeaders headers = createHeadersWithCookies(cookieMap, requestCookieHeaders);

        ResponseEntity<String> response = performSimpleGetRequest(headers, redirectUrl);

        return getLocationHeader(response);
    }

    private String getC3Gsid(Map<String, String> cookieMap, String redirectUrl)
    {
        List<String> requestCookieHeaders = List.of(COOKIE_XSRF_TOKEN, COOKIE_DISSESSION, COOKIE_LOCALE);
        HttpHeaders headers = createHeadersWithCookies(cookieMap, requestCookieHeaders);

        ResponseEntity<String> response = performSimpleGetRequest(headers, redirectUrl);

        List<String> responseCookieHeaders = List.of(COOKIE_GSID);
        addCookies(cookieMap, response, responseCookieHeaders);

        return getLocationHeader(response);
    }

    private boolean finishLogin(Map<String, String> cookieMap, String redirectUrl)
    {
        List<String> requestCookieHeaders =
            List.of(COOKIE_XSRF_TOKEN, COOKIE_JSESSIONID, COOKIE_DISSESSION, COOKIE_GSID, COOKIE_LOCALE);
        HttpHeaders headers = createHeadersWithCookies(cookieMap, requestCookieHeaders);

        ResponseEntity<String> response = performSimpleGetRequest(headers, redirectUrl);

        return response.getStatusCode().is2xxSuccessful();
    }

    private CasLoginResponseDTO handleMissingDissession()
    {
        HttpHeaders headers = new HttpHeaders();

        ResponseEntity<String> response = performSimpleGetRequest(headers, "/crossng-systemmanagement/crossngLogout");

        performSimpleGetRequest(headers, getLocationHeader(response));

        return casLogin(true);
    }

    private ResponseEntity<String> performSimpleGetRequest(HttpHeaders headers, String redirectUrl)
    {
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);

        redirectUrl = WebUtil.getFormattedTargeturl(baseUrl, redirectUrl);
        redirectUrl = WebUtil.replaceHostUrlIfNeeded(baseUrl, redirectUrl);

        return restTemplate.exchange(
            redirectUrl,
            HttpMethod.GET,
            requestEntity,
            String.class
        );
    }

    private boolean isValidationSuccessful(Map<String, String> cookieMap)
    {
        String gsid = cookieMap.get(COOKIE_GSID);
        return gsid != null;
    }

    private String extractFromCookie(String cookieName, String value)
    {
        Pattern pattern = Pattern.compile(cookieName + "=([^;]+)");
        Matcher matcher = pattern.matcher(value);
        if (matcher.find())
        {
            return matcher.group(1);
        }
        return null;
    }

    private String getLocationHeader(ResponseEntity<String> response)
    {
        if (response == null)
        {
            return null;
        }

        if (response.getStatusCode() == HttpStatus.FOUND || response.getStatusCode() == HttpStatus.MOVED_PERMANENTLY)
        {
            return URLDecoder.decode(Objects.requireNonNull(response.getHeaders().getFirst(LOCATION)),
                StandardCharsets.UTF_8);
        }
        return null;
    }
}
