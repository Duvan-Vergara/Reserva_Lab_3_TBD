package delivery.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import delivery.demo.repositories.EmpresaAsociadaRepositoryImp;
import java.util.List;
import java.util.Map;

@Service
public class EmpresaAsociadaService {

    @Autowired
    private EmpresaAsociadaRepositoryImp empresaAsociadaRepositoryImp;

    public List<Map<String, Object>> obtenerEntregasFallidasPorEmpresa() {
        return empresaAsociadaRepositoryImp.obtenerEntregasFallidasPorEmpresa();
    }
    public List<Map<String, Object>> obtenerEntregasExitosasPorEmpresa() {
        return empresaAsociadaRepositoryImp.obtenerEntregasExitosasPorEmpresa();
    }

    public List<Map<String, Object>> obtenerPedidoMasLejanoPorEmpresa() {
        return empresaAsociadaRepositoryImp.obtenerPedidoMasLejanoPorEmpresa();
    }

    public List<Map<String, Object>> obtenerClientesSinEmpresaCercana() {
        return empresaAsociadaRepositoryImp.obtenerClientesSinEmpresaCercana();
    }
}
