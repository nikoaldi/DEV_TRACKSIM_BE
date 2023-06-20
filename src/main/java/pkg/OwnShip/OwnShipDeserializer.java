package pkg.OwnShip;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class OwnShipDeserializer  extends ObjectMapperDeserializer<OwnShip> {
    public OwnShipDeserializer(){
        super(OwnShip.class);
    }
}
