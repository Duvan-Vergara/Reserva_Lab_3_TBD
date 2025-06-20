package delivery.demo.controllers;

import delivery.demo.services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedido")
@CrossOrigin
public class PedidoController {

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @Autowired
    private final PedidoService pedidoService;

    @GetMapping("/mas-pedidos-por-categoria")
    public List<Map<String, Object>> obtenerMasPedidosPorCategoriaUltimoMes() {
        return pedidoService.obtenerMasPedidosPorCategoriaUltimoMes();
    }

    @GetMapping("/tiempos-promedio-entrega")
    public List<Map<String, Object>> obtenerTiemposPromedioEntrega() {
        return pedidoService.obtenerTiemposPromedioEntrega();
    }

    @GetMapping("/pedidos-multiples-zonas")
    public List<Map<String, Object>> obtenerPedidosQueCruzanMultiplesZonas() {
        return pedidoService.obtenerPedidosQueCruzanMultiplesZonas();
    }
    @PostMapping("/crear")
    public ResponseEntity<Long> crearPedido(@RequestBody Map<String, Object> body) {
        Long idUrgencia   = ((Number) body.get("idUrgencia")).longValue();
        Long idRepartidor = ((Number) body.get("idRepartidor")).longValue();
        Long idCliente    = ((Number) body.get("idCliente")).longValue();
        Long idMedioPago  = ((Number) body.get("idMedioPago")).longValue();
        String ubicacionEntrega = (String) body.get("ubicacionEntrega");
        String rutaEstimada     = (String) body.get("rutaEstimada");

        @SuppressWarnings("unchecked")
        List<Number> productosRaw = (List<Number>) body.get("productos");
        List<Long> productos = productosRaw.stream().map(Number::longValue).toList();

        @SuppressWarnings("unchecked")
        List<Number> cantidadesRaw = (List<Number>) body.get("cantidades");
        List<Integer> cantidades = cantidadesRaw.stream().map(Number::intValue).toList();

        Long idPedido = pedidoService.registrarPedidoConProductos(
                idUrgencia, idRepartidor, idCliente, idMedioPago,
                ubicacionEntrega, rutaEstimada, productos, cantidades);

        return ResponseEntity.ok(idPedido);
    }

    @PostMapping("/confirmar")
    public ResponseEntity<String> confirmarPedido(@RequestBody Map<String, Object> body) {
        Long idPedido = ((Number) body.get("idPedido")).longValue();
        pedidoService.confirmarPedido(idPedido);
        return ResponseEntity.ok("Pedido confirmado");
    }

    @PostMapping("/cambiar-estado")
    public ResponseEntity<Long> cambiarEstado(@RequestBody Map<String, Object> body) {
        Long idPedido = ((Number) body.get("idPedido")).longValue();
        boolean nuevoEstado = (boolean) body.get("nuevoEstado");

        boolean resultado = pedidoService.cambiarEstadoPedidoPorFuncion(idPedido, nuevoEstado);

        return ResponseEntity.ok(resultado ? idPedido : -1L);
    }

    @GetMapping("/")
    public List<Map<String, Object>> obtenerTodosLosPedidos() {
        return pedidoService.obtenerTodosLosPedidos();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarPedido(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
        pedidoService.actualizarPedido(id, campos);
        return ResponseEntity.ok("Pedido actualizado correctamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPedido(@PathVariable Long id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.ok("Pedido eliminado correctamente");
    }

    @GetMapping("/cercanos-a-empresa/{idEmpresa}")
    public ResponseEntity<List<Map<String, Object>>> obtenerPedidosMasCercanosAEmpresa(@PathVariable Long idEmpresa) {
        List<Map<String, Object>> pedidos = pedidoService.obtenerPedidosMasCercanosAEmpresa(idEmpresa);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/cercanos")
    public List<Map<String, Object>> obtenerPuntosCercanos(
            @RequestParam Long idCliente,
            @RequestParam(defaultValue = "1000") double radio) {
        return pedidoService.obtenerPuntosInteres(idCliente, radio);
    }
}
