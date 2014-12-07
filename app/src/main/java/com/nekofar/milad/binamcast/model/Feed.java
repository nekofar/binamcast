package com.nekofar.milad.binamcast.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict=false,name="Rss")
public class Feed {

    @Attribute
    String version;

    @Element
    Channel channel;

    public String getVersion() {
        return version;
    }

    public Channel getChannel() {
        return channel;
    }

}
