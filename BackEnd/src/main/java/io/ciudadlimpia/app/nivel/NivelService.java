package io.ciudadlimpia.app.nivel;

import io.ciudadlimpia.app.events.BeforeDeleteNivel;
import io.ciudadlimpia.app.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class NivelService {

    private final NivelRepository nivelRepository;
    private final ApplicationEventPublisher publisher;

    public NivelService(final NivelRepository nivelRepository,
            final ApplicationEventPublisher publisher) {
        this.nivelRepository = nivelRepository;
        this.publisher = publisher;
    }

    public List<NivelDTO> findAll() {
        final List<Nivel> nivels = nivelRepository.findAll(Sort.by("id"));
        return nivels.stream()
                .map(nivel -> mapToDTO(nivel, new NivelDTO()))
                .toList();
    }

    public NivelDTO get(final Integer id) {
        return nivelRepository.findById(id)
                .map(nivel -> mapToDTO(nivel, new NivelDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final NivelDTO nivelDTO) {
        final Nivel nivel = new Nivel();
        mapToEntity(nivelDTO, nivel);
        return nivelRepository.save(nivel).getId();
    }

    public void update(final Integer id, final NivelDTO nivelDTO) {
        final Nivel nivel = nivelRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(nivelDTO, nivel);
        nivelRepository.save(nivel);
    }

    public void delete(final Integer id) {
        final Nivel nivel = nivelRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteNivel(id));
        nivelRepository.delete(nivel);
    }

    private NivelDTO mapToDTO(final Nivel nivel, final NivelDTO nivelDTO) {
        nivelDTO.setId(nivel.getId());
        nivelDTO.setNombre(nivel.getNombre());
        nivelDTO.setDescripcion(nivel.getDescripcion());
        nivelDTO.setPuntosMinimos(nivel.getPuntosMinimos());
        nivelDTO.setIcono(nivel.getIcono());
        return nivelDTO;
    }

    private Nivel mapToEntity(final NivelDTO nivelDTO, final Nivel nivel) {
        nivel.setNombre(nivelDTO.getNombre());
        nivel.setDescripcion(nivelDTO.getDescripcion());
        nivel.setPuntosMinimos(nivelDTO.getPuntosMinimos());
        nivel.setIcono(nivelDTO.getIcono());
        return nivel;
    }

}
