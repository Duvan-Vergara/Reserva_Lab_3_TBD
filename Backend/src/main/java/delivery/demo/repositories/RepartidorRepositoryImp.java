package delivery.demo.repositories;

import delivery.demo.entities.RepartidorEntity;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class RepartidorRepositoryImp {

    private final Sql2o sql2o;

    public RepartidorRepositoryImp(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    //5 Obtener los 3 repartidores con mejor rendimiento (por entregas y promedio de calificación).
    public List<Map<String, Object>> obtenerTop3Repartidores() {
        String sql = """
                    SELECT
                      r.id_repartidor,
                      r.nombre,
                      COALESCE(e.entregas, 0) AS total_entregas,
                      COALESCE(c.promedio, 0) AS promedio_puntuacion
                    FROM REPARTIDOR r
                    LEFT JOIN (
                      SELECT
                        p.id_repartidor,
                        COUNT(*) AS entregas
                      FROM PEDIDO p
                      INNER JOIN DETALLE_PEDIDO d ON p.id_detalle_pedido = d.id_detalle_pedido
                      WHERE d.entregado = TRUE 
                        AND p.deleted_at IS NULL
                        AND d.deleted_at IS NULL
                      GROUP BY p.id_repartidor
                    ) e ON r.id_repartidor = e.id_repartidor
                    LEFT JOIN CALIFICACION c ON r.id_repartidor = c.id_repartidor
                    WHERE r.deleted_at IS NULL
                    ORDER BY
                      total_entregas DESC,
                      promedio_puntuacion DESC
                    LIMIT 3;
                """;

        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetchTable()
                    .asList();
        }
    }

    //Desempeño por repartidor
    public List<Map<String, Object>> obtenerDesempenoPorRepartidor() {
        String sql = """
                    SELECT\s
                      r.id_repartidor,
                      r.nombre,
                      c.total_puntos,
                      c.total_pedidos,
                      c.promedio
                    FROM\s
                      REPARTIDOR r
                    JOIN\s
                      CALIFICACION c ON r.id_repartidor = c.id_repartidor
                    ORDER BY\s
                      c.total_puntos DESC;
                """;

        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetchTable()
                    .asList();
        }
    }

    public List<Map<String, Object>> findAllRepartidores() {
        String sql = """
        SELECT * FROM REPARTIDOR
    """;

        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetchTable()
                    .asList();
        }
    }

    public void update(RepartidorEntity repartidor) {
        String sql = """
                    UPDATE REPARTIDOR
                    SET nombre = :nombre,
                        id_empresa_asociada = :idEmpresaAsociada
                    WHERE id_repartidor = :id
                    AND deleted_at IS NULL
                """;

        try (org.sql2o.Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", repartidor.getId_repartidor())
                    .addParameter("nombre", repartidor.getNombre())
                    .addParameter("idEmpresaAsociada", repartidor.getId_empresa_asociada())
                    .executeUpdate();
        }
    }

    public RepartidorEntity crearRepartidor(RepartidorEntity repartidor) {
        String sql = """
                INSERT INTO REPARTIDOR (nombre, id_empresa_asociada)
                VALUES (:nombre, :idEmpresaAsociada)
                RETURNING id_repartidor
                """;
        try (org.sql2o.Connection con = sql2o.open()) {
            Long id = con.createQuery(sql)
                    .addParameter("nombre", repartidor.getNombre())
                    .addParameter("idEmpresaAsociada", repartidor.getId_empresa_asociada())
                    .executeUpdate()
                    .getKey(Long.class);
            repartidor.setId_repartidor(id);
            return repartidor;
        }
    }

    // Distancia recorrida por cada repartidor en el último mes
    public List<Map<String, Object>> obtenerDistanciaRecorridaUltimoMes() {
        String sql = """
            SELECT 
              r.nombre AS repartidor,
              ROUND((
                SUM(
                  ST_Distance(
                    ST_StartPoint(p.ruta_estimada::geometry)::geography,
                    ST_EndPoint(p.ruta_estimada::geometry)::geography
                  )
                ) / 1000
              )::numeric, 2) AS km_recorridos
            FROM 
              pedido p
            JOIN repartidor r ON p.id_repartidor = r.id_repartidor
            JOIN detalle_pedido dp ON p.id_detalle_pedido = dp.id_detalle_pedido
            WHERE 
              dp.hora_entrega >= NOW() - INTERVAL '1 month'
            GROUP BY 
              r.nombre;
        """;

        try (org.sql2o.Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetchTable()
                    .asList();
        }
    }



}