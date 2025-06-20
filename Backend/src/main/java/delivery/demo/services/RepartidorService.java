package delivery.demo.services;

import delivery.demo.entities.RepartidorEntity;
import delivery.demo.repositories.RepartidorRepositoryImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RepartidorService {

    @Autowired
    private RepartidorRepositoryImp repartidorRepositoryImp;

    public List<Map<String, Object>> obtenerTop3Repartidores() {
        return repartidorRepositoryImp.obtenerTop3Repartidores();
    }

    public List<Map<String, Object>> obtenerDesempenoPorRepartidor() {
        return repartidorRepositoryImp.obtenerDesempenoPorRepartidor();
    }

    public List<Map<String, Object>> obtenerTodos() {
        return repartidorRepositoryImp.findAllRepartidores();
    }

    public void update(RepartidorEntity repartidor) {
        repartidorRepositoryImp.update(repartidor);
    }

    public RepartidorEntity crearRepartidor(RepartidorEntity repartidor) {
        return repartidorRepositoryImp.crearRepartidor(repartidor);
    }
    public List<Map<String, Object>> obtenerDistanciaRecorridaUltimoMes(){
        return repartidorRepositoryImp.obtenerDistanciaRecorridaUltimoMes();
    }
}