package com.nekofar.milad.binamcast.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Feed {

    @Element
    Channel channel;

    public Channel getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "channel=" + channel +
                '}';
    }

}
