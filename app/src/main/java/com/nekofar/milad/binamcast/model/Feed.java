package com.nekofar.milad.binamcast.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict=false,name="Rss")
public class Feed {

    @Element
    Channel channel;

    public Channel getChannel() {
        return channel;
    }

    @Root(strict = false)
    public static class Channel {

        @Element
        public String title;

        @Element (name = "lastBuildDate", required = false)
        public String lastBuildDate;

        @ElementList(entry = "item", inline = true, required = true)
        public List<Item> items;

        public List<Item> getItems() {
            return items;
        }

        @Root(name = "item", strict = false)
        public static class Item {
            @Element(name = "title", required = true)
            public String title;

            @Element(name = "link", required = true)
            public String link;

            @Element(name = "description", required = true)
            public String description;

            @Element(name = "encoded", required = false, data = true)
            public String encoded;

            @Element(name = "pubDate", required = true)
            public String pubDate;

            @ElementList(name = "enclosure", required = false, inline = true)
            public List<Enclosure> enclosures;

            public List<Enclosure> getEnclosures() {
                return enclosures;
            }

            @Root(name = "enclosure", strict = false)
            public static class Enclosure {
                @Attribute
                public String url;

                @Attribute
                public String length;

                @Attribute
                public String type;
            }
        }

    }


}
