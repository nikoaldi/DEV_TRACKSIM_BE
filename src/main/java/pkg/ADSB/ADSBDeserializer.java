package pkg.ADSB;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import pkg.AIS.AIS;

public class ADSBDeserializer extends ObjectMapperDeserializer<ADSB> {
    public ADSBDeserializer(){
        super(ADSB.class);
    }
}
