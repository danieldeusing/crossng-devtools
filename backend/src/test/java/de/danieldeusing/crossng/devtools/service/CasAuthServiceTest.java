package de.danieldeusing.crossng.devtools.service;

import de.danieldeusing.crossng.devtools.model.CasLoginResponseDTO;
import de.danieldeusing.crossng.devtools.util.WebUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class CasAuthServiceTest
{
    public static final String COOKIE_LOCALE_VALUE = "TEST_EN";
    public static final String COOKIE_DISSESSION_VALUE = "TEST_DISSESSION";
    public static final String COOKIE_TGC_VALUE = "TEST_TGC";
    public static final String COOKIE_XSRF_TOKEN_VALUE = "TEST_XSRF-TOKEN";
    public static final String COOKIE_JSESSIONID_VALUE = "TEST_JSESSIONID";
    public static final String COOKIE_GSID_VALUE = "TEST_c3-gsid";
    public static final String REDIRECT_URL_VALUE = "https://test.com";

    @Value("${context.base-url}")
    private String baseUrl;
    @Value("${crossng.ldap.username}")
    private String ldapUsername;
    @Value("${crossng.ldap.password}")
    private String ldapPassword;
    @Autowired
    private CasAuthService casAuthService;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCasLogin()
    {
        String redirectUrl = mockInitiateLoginAndGetCookiesRequest();
        redirectUrl = mockGetXSRFTokenRequest(redirectUrl);
        redirectUrl = mockGetTicketRequest(redirectUrl);
        redirectUrl = mockGetC3GsidRequest(redirectUrl);
        mockFinishLoginRequest(redirectUrl);

        CasLoginResponseDTO response = casAuthService.casLogin(false);

        assertNotNull(response);
        assertEquals(COOKIE_GSID_VALUE, response.getToken());
    }

    private HttpHeaders createResponseHeaders(Map<String, List<String>> headersMap)
    {
        HttpHeaders headers = new HttpHeaders();
        for (Map.Entry<String, List<String>> entry : headersMap.entrySet())
        {
            for (String value : entry.getValue())
            {
                headers.add(entry.getKey(), value);
            }
        }
        return headers;
    }

    private String generateRandomString()
    {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private String mockInitiateLoginAndGetCookiesRequest()
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

        Map<String, List<String>> headersMap = new HashMap<>();
        List<String> setCookieValues = new ArrayList<>();
        setCookieValues.add(CasAuthService.COOKIE_TGC + "=" + COOKIE_TGC_VALUE);
        setCookieValues.add(CasAuthService.COOKIE_DISSESSION + "=" + COOKIE_DISSESSION_VALUE);
        setCookieValues.add(CasAuthService.COOKIE_LOCALE + "=" + COOKIE_LOCALE_VALUE);
        headersMap.put("Set-Cookie", setCookieValues);

        List<String> locationValues = new ArrayList<>();
        String locationUrl = REDIRECT_URL_VALUE + "/" + generateRandomString();
        locationValues.add(locationUrl);
        headersMap.put(CasAuthService.LOCATION, locationValues);

        when(restTemplate.postForEntity(eq(WebUtil.getFormattedTargeturl(baseUrl, "/cas/login")),
            eq(multiValueMapHttpEntity), eq(String.class)))
            .thenReturn(new ResponseEntity<>("", createResponseHeaders(headersMap), HttpStatus.FOUND));

        return locationUrl;
    }

    private String mockGetXSRFTokenRequest(String redirectUrl)
    {
        HttpHeaders headers = new HttpHeaders();

        List<String> cookieValues = new ArrayList<>();
        cookieValues.add(CasAuthService.COOKIE_DISSESSION + "=" + COOKIE_DISSESSION_VALUE);
        cookieValues.add(CasAuthService.COOKIE_LOCALE + "=" + COOKIE_LOCALE_VALUE);
        headers.put(HttpHeaders.COOKIE, cookieValues);

        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);

        redirectUrl = WebUtil.getFormattedTargeturl(baseUrl, redirectUrl);
        redirectUrl = WebUtil.replaceHostUrlIfNeeded(baseUrl, redirectUrl);

        Map<String, List<String>> headersMap = new HashMap<>();
        List<String> setCookieValues = new ArrayList<>();
        setCookieValues.add(CasAuthService.COOKIE_XSRF_TOKEN + "=" + COOKIE_XSRF_TOKEN_VALUE);
        setCookieValues.add(CasAuthService.COOKIE_JSESSIONID + "=" + COOKIE_JSESSIONID_VALUE);
        headersMap.put("Set-Cookie", setCookieValues);

        String locationUrl = REDIRECT_URL_VALUE + "/" + generateRandomString();
        List<String> locationValues = new ArrayList<>();
        locationValues.add(locationUrl);
        headersMap.put(CasAuthService.LOCATION, locationValues);

        when(restTemplate.exchange(eq(redirectUrl), eq(HttpMethod.GET), eq(requestEntity), eq(String.class)))
            .thenReturn(
                new ResponseEntity<>("", createResponseHeaders(headersMap), HttpStatus.FOUND));

        return locationUrl;
    }

    private String mockGetTicketRequest(String redirectUrl)
    {
        HttpHeaders headers = new HttpHeaders();

        List<String> cookieValues = new ArrayList<>();
        cookieValues.add(CasAuthService.COOKIE_TGC + "=" + COOKIE_TGC_VALUE);
        cookieValues.add(CasAuthService.COOKIE_DISSESSION + "=" + COOKIE_DISSESSION_VALUE);
        cookieValues.add(CasAuthService.COOKIE_LOCALE + "=" + COOKIE_LOCALE_VALUE);
        headers.put(HttpHeaders.COOKIE, cookieValues);

        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);

        redirectUrl = WebUtil.getFormattedTargeturl(baseUrl, redirectUrl);
        redirectUrl = WebUtil.replaceHostUrlIfNeeded(baseUrl, redirectUrl);

        Map<String, List<String>> headersMap = new HashMap<>();

        String locationUrl = REDIRECT_URL_VALUE + "/" + generateRandomString();
        List<String> locationValues = new ArrayList<>();
        locationValues.add(locationUrl);
        headersMap.put(CasAuthService.LOCATION, locationValues);

        when(restTemplate.exchange(eq(redirectUrl), eq(HttpMethod.GET), eq(requestEntity), eq(String.class)))
            .thenReturn(
                new ResponseEntity<>("", createResponseHeaders(headersMap), HttpStatus.FOUND));

        return locationUrl;
    }

    private String mockGetC3GsidRequest(String redirectUrl)
    {
        HttpHeaders headers = new HttpHeaders();

        List<String> cookieValues = new ArrayList<>();
        cookieValues.add(CasAuthService.COOKIE_XSRF_TOKEN + "=" + COOKIE_XSRF_TOKEN_VALUE);
        cookieValues.add(CasAuthService.COOKIE_DISSESSION + "=" + COOKIE_DISSESSION_VALUE);
        cookieValues.add(CasAuthService.COOKIE_LOCALE + "=" + COOKIE_LOCALE_VALUE);
        headers.put(HttpHeaders.COOKIE, cookieValues);

        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);

        redirectUrl = WebUtil.getFormattedTargeturl(baseUrl, redirectUrl);
        redirectUrl = WebUtil.replaceHostUrlIfNeeded(baseUrl, redirectUrl);

        Map<String, List<String>> headersMap = new HashMap<>();
        List<String> setCookieValues = new ArrayList<>();
        setCookieValues.add(CasAuthService.COOKIE_GSID + "=" + COOKIE_GSID_VALUE);
        headersMap.put("Set-Cookie", setCookieValues);

        String locationUrl = REDIRECT_URL_VALUE + "/" + generateRandomString();
        List<String> locationValues = new ArrayList<>();
        locationValues.add(locationUrl);
        headersMap.put(CasAuthService.LOCATION, locationValues);

        when(restTemplate.exchange(eq(redirectUrl), eq(HttpMethod.GET), eq(requestEntity), eq(String.class)))
            .thenReturn(
                new ResponseEntity<>("", createResponseHeaders(headersMap), HttpStatus.FOUND));

        return locationUrl;
    }

    private void mockFinishLoginRequest(String redirectUrl)
    {
        HttpHeaders headers = new HttpHeaders();

        List<String> cookieValues = new ArrayList<>();
        cookieValues.add(CasAuthService.COOKIE_XSRF_TOKEN + "=" + COOKIE_XSRF_TOKEN_VALUE);
        cookieValues.add(CasAuthService.COOKIE_JSESSIONID + "=" + COOKIE_JSESSIONID_VALUE);
        cookieValues.add(CasAuthService.COOKIE_DISSESSION + "=" + COOKIE_DISSESSION_VALUE);
        cookieValues.add(CasAuthService.COOKIE_GSID + "=" + COOKIE_GSID_VALUE);
        cookieValues.add(CasAuthService.COOKIE_LOCALE + "=" + COOKIE_LOCALE_VALUE);
        headers.put(HttpHeaders.COOKIE, cookieValues);

        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);

        redirectUrl = WebUtil.getFormattedTargeturl(baseUrl, redirectUrl);
        redirectUrl = WebUtil.replaceHostUrlIfNeeded(baseUrl, redirectUrl);

        Map<String, List<String>> headersMap = new HashMap<>();

        when(restTemplate.exchange(eq(redirectUrl), eq(HttpMethod.GET), eq(requestEntity), eq(String.class)))
            .thenReturn(
                new ResponseEntity<>("", createResponseHeaders(headersMap), HttpStatus.OK));
    }
}
