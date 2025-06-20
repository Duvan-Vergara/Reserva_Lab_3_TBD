package delivery.demo.controllers;

import delivery.demo.entities.RepartidorEntity;
import delivery.demo.services.RepartidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/repartidor")
@CrossOrigin

public class RepartidorController {
    @Autowired
    private RepartidorService repartidorService;

    @GetMapping("/top-3")
    public List<Map<String, Object>> obtenerTop3Repartidores() {
        return repartidorService.obtenerTop3Repartidores();
    }

    @GetMapping("/desempenios")
    public List<Map<String, Object>> obtenerDesempenoPorRepartidor() {
        return repartidorService.obtenerDesempenoPorRepartidor();
    }
    @GetMapping("/distancia-recorrida")
    public List<Map<String, Object>> obtenerDistanciaRecorridaUltimoMes() {
        return repartidorService.obtenerDistanciaRecorridaUltimoMes();
    }

    @GetMapping("/")
    public List<Map<String, Object>> obtenerTodos() {
        return repartidorService.obtenerTodos();
    }

    @PutMapping("/actualizar")
    public void actualizarRepartidor(@RequestBody RepartidorEntity repartidor) {
        repartidorService.update(repartidor);
    }

    @PostMapping("/crear")
    public void crearRepartidor(@RequestBody RepartidorEntity repartidor) {
        repartidorService.crearRepartidor(repartidor);
    }
}