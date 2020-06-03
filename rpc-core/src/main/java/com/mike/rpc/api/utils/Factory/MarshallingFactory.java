package com.mike.rpc.api.utils.Factory;

import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

public class MarshallingFactory {

    public static MarshallingDecoder getMarshallingDecoder(){
        MarshallerFactory marshallingFactory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration marshallingConfiguration = new MarshallingConfiguration();
        marshallingConfiguration.setVersion(5);
        DefaultUnmarshallerProvider defaultMarshallerProvider = new DefaultUnmarshallerProvider(marshallingFactory,marshallingConfiguration);
        return new MarshallingDecoder(defaultMarshallerProvider,1024*1024*1);
    }
    public static MarshallingEncoder getMarshallingEncode(){
        MarshallerFactory marshallingFactory = Marshalling.getProvidedMarshallerFactory("serial");
        MarshallingConfiguration marshallingConfiguration = new MarshallingConfiguration();
        marshallingConfiguration.setVersion(5);
        DefaultMarshallerProvider defaultMarshallerProvider = new DefaultMarshallerProvider(marshallingFactory,marshallingConfiguration);
        return new MarshallingEncoder(defaultMarshallerProvider);
    }
}
