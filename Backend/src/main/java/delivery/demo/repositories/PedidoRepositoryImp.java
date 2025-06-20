package delivery.demo.repositories;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PedidoRepositoryImp {

    private final Sql2o sql2o;

    public PedidoRepositoryImp(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public List<Map<String, Object>> obtenerMasPedidosPorCategoriaUltimoMes() {
        String sql = """
                    SELECT 
                      c.nombre,
                      ps.id_producto_servicio,
                      SUM(pp.cantidad) AS total_pedidos
                    FROM PEDIDO_PRODUCTO pp
                    JOIN PRODUCTO_SERVICIO ps ON pp.id_producto_servicio = ps.id_producto_servicio
                    JOIN CATEGORIA c ON ps.id_categoria = c.id_categoria
                    JOIN PEDIDO p ON pp.id_pedido = p.id_pedido
                    WHERE p.hora_pedido >= NOW() - INTERVAL '1 month'  
                    GROUP BY c.nombre, ps.id_producto_servicio
                    ORDER BY c.nombre, total_pedidos DESC
                """;

        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql)
                    .executeAndFetchTable()
                    .asList(); // Devuelve una lista de filas como Map<String, Object>
        }
    }

    //4 Calcular el tiempo promedio entre pedido y entrega por repartidor.
    public List<Map<String, Object>> obtenerTiemposPromedioEntrega() {
        String sql = """
                    SELECT 
                      r.id_repartidor,
                      r.nombre,
                      AVG(EXTRACT(EPOCH FROM (d.hora_entrega - p.hora_pedido))) AS tiempo_promedio_entrega
                    FROM 
                      PEDIDO p
                    JOIN 
                      DETALLE_PEDIDO d ON p.id_detalle_pedido = d.id_detalle_pedido
                    JOIN 
                      REPARTIDOR r ON p.id_repartidor = r.id_repartidor
                    WHERE 
                      d.entregado = TRUE
                    GROUP BY 
                      r.id_repartidor, r.nombre
                    ORDER BY 
                      tiempo_promedio_entrega
                """;

        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetchTable()
                    .asList();
        }
    }

    public List<Map<String, Object>> obtenerTodosLosPedidos() {
        String sql = """
        SELECT * FROM PEDIDO
        WHERE deleted_at IS NULL
    """;

        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetchTable()
                    .asList();
        }
    }

    public void actualizarPedido(Long id, Map<String, Object> campos) {
        StringBuilder sql = new StringBuilder("UPDATE PEDIDO SET ");
        campos.forEach((key, value) -> sql.append(key).append(" = :").append(key).append(", "));
        sql.append("updated_at = NOW() WHERE id_pedido = :id AND deleted_at IS NULL");

        try (Connection con = sql2o.open()) {
            var query = con.createQuery(sql.toString());
            campos.forEach(query::addParameter);
            query.addParameter("id", id).executeUpdate();
        }
    }

    public void eliminarPedido(Long id) {
        String sql = """
        UPDATE PEDIDO
        SET deleted_at = NOW()
        WHERE id_pedido = :id AND deleted_at IS NULL
    """;

        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        }
    }

    //Consulta 1
    public List<Map<String, Object>> obtenerPedidosMasCercanosAEmpresa(Long idEmpresa) {
        String sql = """
        SELECT\s
            p.id_pedido,
            ST_Distance(
                ea.ubicacion_empresa_asociada::geography,
                p.ubicacion_entrega::geography
            ) AS distancia_metros
        FROM\s
            EMPRESA_ASOCIADA ea
        JOIN\s
            PEDIDO p ON TRUE
        JOIN\s
            DETALLE_PEDIDO dp ON p.id_detalle_pedido = dp.id_detalle_pedido
        WHERE\s
            ea.id_empresa_asociada = :idEmpresa
            AND dp.entregado = false
        ORDER BY\s
            distancia_metros ASC
        LIMIT 5;
    """;

        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("idEmpresa", idEmpresa)
                    .executeAndFetchTable()
                    .asList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Retorna una lista vacía en caso de error
        }
    }

    //consulta 5
    public List<Map<String, Object>> obtenerPedidosQueCruzanMultiplesZonas() {
        String sql = """
                SELECT 
                  p.id_pedido,
                  COUNT(DISTINCT z.id_zona) AS zonas_cruzadas
                FROM 
                  pedido p
                JOIN zona_cobertura z
                  ON ST_Intersects(p.ruta_estimada, z.zona_geom)
                GROUP BY 
                  p.id_pedido
                HAVING 
                  COUNT(DISTINCT z.id_zona) > 2;
            """;

        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetchTable()
                    .asList();
        }
    }

    //EXTRA 3
    public List<Map<String, Object>> obtenerPuntosInteresCercanosPorCliente(Long idCliente, double radioMetros) {
        String sql = """
        SELECT 
          poi.id,
          poi.nombre,
          poi.tipo,
          ST_Distance(
            poi.ubicacion,
            geography(ST_Transform(c.ubicacion_cliente, 4326))
          ) AS distancia_metros
        FROM 
          cliente c
        JOIN 
          punto_interes poi ON TRUE
        WHERE 
          c.id_cliente = :idCliente
          AND ST_DWithin(
                poi.ubicacion,
                geography(ST_Transform(c.ubicacion_cliente, 4326)),
                :radio
              )
        ORDER BY 
          distancia_metros
        LIMIT 5;
    """;

        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("idCliente", idCliente)
                    .addParameter("radio", radioMetros)
                    .executeAndFetchTable()
                    .asList();
        }
    }



}
