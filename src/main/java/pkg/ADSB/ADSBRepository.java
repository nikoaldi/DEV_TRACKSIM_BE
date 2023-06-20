package pkg.ADSB;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import pkg.Radar.Radar;

@ApplicationScoped
public class ADSBRepository implements PanacheRepository<ADSB> {
}
