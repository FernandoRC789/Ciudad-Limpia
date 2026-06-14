package io.ciudadlimpia.app.cupon;

import io.ciudadlimpia.app.events.BeforeDeleteCupon;
import io.ciudadlimpia.app.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CuponService {

    private final CuponRepository cuponRepository;
    private final ApplicationEventPublisher publisher;

    public CuponService(final CuponRepository cuponRepository,
            final ApplicationEventPublisher publisher) {
        this.cuponRepository = cuponRepository;
        this.publisher = publisher;
    }

    public List<CuponDTO> findAll() {
        final List<Cupon> cupons = cuponRepository.findAll(Sort.by("id"));
        return cupons.stream()
                .map(cupon -> mapToDTO(cupon, new CuponDTO()))
                .toList();
    }

    public CuponDTO get(final Integer id) {
        return cuponRepository.findById(id)
                .map(cupon -> mapToDTO(cupon, new CuponDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final CuponDTO cuponDTO) {
        final Cupon cupon = new Cupon();
        mapToEntity(cuponDTO, cupon);
        return cuponRepository.save(cupon).getId();
    }

    public void update(final Integer id, final CuponDTO cuponDTO) {
        final Cupon cupon = cuponRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(cuponDTO, cupon);
        cuponRepository.save(cupon);
    }

    public void delete(final Integer id) {
        final Cupon cupon = cuponRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteCupon(id));
        cuponRepository.delete(cupon);
    }

    private CuponDTO mapToDTO(final Cupon cupon, final CuponDTO cuponDTO) {
        cuponDTO.setId(cupon.getId());
        cuponDTO.setNombre(cupon.getNombre());
        cuponDTO.setDescripcion(cupon.getDescripcion());
        cuponDTO.setCostoPuntos(cupon.getCostoPuntos());
        cuponDTO.setStock(cupon.getStock());
        cuponDTO.setFechaExpiracion(cupon.getFechaExpiracion());
        cuponDTO.setActivo(cupon.getActivo());
        cuponDTO.setCreatedAt(cupon.getCreatedAt());
        return cuponDTO;
    }

    private Cupon mapToEntity(final CuponDTO cuponDTO, final Cupon cupon) {
        cupon.setNombre(cuponDTO.getNombre());
        cupon.setDescripcion(cuponDTO.getDescripcion());
        cupon.setCostoPuntos(cuponDTO.getCostoPuntos());
        cupon.setStock(cuponDTO.getStock());
        cupon.setFechaExpiracion(cuponDTO.getFechaExpiracion());
        cupon.setActivo(cuponDTO.getActivo());
        cupon.setCreatedAt(cuponDTO.getCreatedAt());
        return cupon;
    }

    public List<CuponDTO> findDisponibles() {
        return cuponRepository.findByActivoTrueAndStockGreaterThan(0)
                .stream()
                .map(cupon -> mapToDTO(cupon, new CuponDTO()))
                .toList();
    }

}
