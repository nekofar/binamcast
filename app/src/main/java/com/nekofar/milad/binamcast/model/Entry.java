package com.nekofar.milad.binamcast.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.Map;

@Root(strict = false)
public class Entry {

    @Element
    private String title;

    @Element
    private String content;

    @Element
    private String published;

    @ElementMap(entry = "link", key = "rel", value = "href", attribute = true, inline = true)
    private Map<String, String> links;

    public String getTitle() {
        return title;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public String getContent() {
        return content;
    }

    public String getPublished() {
        return published;
    }
}
