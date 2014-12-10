package com.nekofar.milad.binamcast.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false)
public class Feed {

    @Element
    private String title;

    @Element
    private String updated;

    @ElementList(entry = "entry", inline = true)
    private List<Entry> entries;

    public String getTitle() {
        return title;
    }

    public String getUpdated() {
        return updated;
    }

    public List<Entry> getEntries() {
        return entries;
    }

}
