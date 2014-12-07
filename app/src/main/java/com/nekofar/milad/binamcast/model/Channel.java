package com.nekofar.milad.binamcast.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

@NamespaceList({
    @Namespace(reference = "http://www.w3.org/2005/Atom", prefix = "atom")
})
@Root(strict = false)
public class Channel {

    @ElementList(entry = "link", inline = true, required = false)
    public List<Link> links;

    @ElementList(entry = "item", inline = true, required = true)
    public List<Item> items;

    @Element (name = "pubDate", required = false)
    String pubDate;

    @Override
    public String toString() {
        return "Channel{" +
                "links=" + links +
                ", items=" + items +
                ", pubDate='" + pubDate + '\'' +
                '}';
    }

    public static class Link {
        @Text(required = false)
        public String link;
    }

    @Root(name = "item", strict = false)
    public static class Item {
        @Element(name = "title", required = true)
        String title;

        @Element(name = "link", required = true)
        String link;

        @Element(name = "description", required = true)
        String description;

        @Element(name = "encluosure", required = true)
        String encluosure;

        @Element(name = "pubDate", required = true)
        String pubDate;

        @Override
        public String toString() {
            return "Item{" +
                    "title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", description='" + description + '\'' +
                    ", encluosure='" + encluosure + '\'' +
                    ", pubDate='" + pubDate + '\'' +
                    '}';
        }
    }

}
