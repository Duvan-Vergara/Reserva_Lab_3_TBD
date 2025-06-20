package delivery.demo.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "historial_repartidores")
public class historial_repartidores {

    @Id
    private String id;
    private int repartidor_id;
    private List<Ruta> rutas;
    public static class Ruta {
        private double lat;
        private double lng;
        private Instant timestamp;
    }

}
