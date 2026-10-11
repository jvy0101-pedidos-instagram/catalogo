package cl.duocuc.catalogo.service;

import cl.duocuc.catalogo.exception.RecursoNoEncontradoException;
import cl.duocuc.catalogo.exception.ReglaNegocioException;
import cl.duocuc.catalogo.model.Producto;
import cl.duocuc.catalogo.repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Capa de negocio: el controller llama al service y el service al repository. */
@Service
public class ProductoService {

    private final ProductoRepository repository;

    // Inyeccion de dependencias por constructor.
    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listar() {
        return repository.findAll();
    }

    public Producto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe producto con id " + id));
    }

    @Transactional
    public Producto crear(Producto nuevo) {
        if (repository.existsBySku(nuevo.getSku())) {
            throw new ReglaNegocioException("Ya existe un producto con el SKU " + nuevo.getSku());
        }
        if (nuevo.getActivo() == null) nuevo.setActivo(true);
        nuevo.setId(null); // el id lo genera la base de datos
        return repository.save(nuevo);
    }

    @Transactional
    public Producto actualizar(Long id, Producto datos) {
        Producto existente = buscarPorId(id);
        if (repository.existsBySkuAndIdNot(datos.getSku(), id)) {
            throw new ReglaNegocioException("Ya existe un producto con el SKU " + datos.getSku());
        }
        existente.setSku(datos.getSku());
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setTalla(datos.getTalla());
        existente.setColor(datos.getColor());
        existente.setPrecio(datos.getPrecio());
        existente.setStock(datos.getStock());
        if (datos.getActivo() != null) existente.setActivo(datos.getActivo());
        return repository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        Producto existente = buscarPorId(id);
        repository.delete(existente);
    }
}
