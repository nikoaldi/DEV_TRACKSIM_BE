package pkg.AIS;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import pkg.Radar.Radar;

public class AISDeserializer extends ObjectMapperDeserializer<AIS> {
    public AISDeserializer(){
        super(AIS.class);
    }
}
