package io.ciudadlimpia.app.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;



public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findFirstByRolId(Integer id);
    Optional<Usuario> findByEmail(String email);

}
