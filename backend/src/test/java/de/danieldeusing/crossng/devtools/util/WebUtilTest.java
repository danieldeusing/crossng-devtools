package de.danieldeusing.crossng.devtools.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class WebUtilTest
{
    @Test
    void testGetFormattedTargetUrlWithLeadingSlash()
    {
        String baseUrl = "https://example.com/";
        String path = "/testPath";
        String formattedUrl = WebUtil.getFormattedTargeturl(baseUrl, path);
        assertEquals("https://example.com/testPath", formattedUrl);
    }

    @Test
    void testGetFormattedTargetUrlWithoutLeadingSlash()
    {
        String baseUrl = "https://example.com/";
        String path = "testPath";
        String formattedUrl = WebUtil.getFormattedTargeturl(baseUrl, path);
        assertEquals("https://example.com/testPath", formattedUrl);
    }

    @Test
    void testGetFormattedTargetUrlWithFullPath()
    {
        String baseUrl = "https://example.com/";
        String fullPath = "https://otherdomain.com/anotherpath";
        String formattedUrl = WebUtil.getFormattedTargeturl(baseUrl, fullPath);
        assertEquals("https://otherdomain.com/anotherpath", formattedUrl);
    }

    @Test
    void testReplaceHostUrlIfNeededSameHost()
    {
        String base = "https://example.com/path";
        String target = "https://example.com/otherpath";
        String replacedUrl = WebUtil.replaceHostUrlIfNeeded(base, target);
        assertEquals(target, replacedUrl);
    }

    @Test
    void testReplaceHostUrlIfNeededDifferentHost()
    {
        String base = "https://example.com/path";
        String target = "https://otherdomain.com/otherpath";
        String replacedUrl = WebUtil.replaceHostUrlIfNeeded(base, target);
        assertEquals("https://example.com/otherpath", replacedUrl);
    }
}
