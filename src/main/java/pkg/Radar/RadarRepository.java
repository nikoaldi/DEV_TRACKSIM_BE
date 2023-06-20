package pkg.Radar;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@ApplicationScoped
public class RadarRepository implements PanacheRepository<Radar> {
}
