package pkg.Radar;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import pkg.OwnShip.OwnShip;

public class RadarDeserializer extends ObjectMapperDeserializer<Radar> {
    public RadarDeserializer(){
        super(Radar.class);
    }
}