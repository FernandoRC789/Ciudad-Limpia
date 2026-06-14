package io.ciudadlimpia.app.auth;

import io.ciudadlimpia.app.gaming_profile.GamingProfile;
import io.ciudadlimpia.app.gaming_profile.GamingProfileRepository;
import io.ciudadlimpia.app.nivel.Nivel;
import io.ciudadlimpia.app.nivel.NivelRepository;
import io.ciudadlimpia.app.rol.Rol;
import io.ciudadlimpia.app.rol.RolRepository;
import io.ciudadlimpia.app.security.JwtService;
import io.ciudadlimpia.app.usuario.Usuario;
import io.ciudadlimpia.app.usuario.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final NivelRepository nivelRepository;
    private final GamingProfileRepository gamingProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthService(UsuarioRepository usuarioRepository,
                       RolRepository rolRepository,
                       NivelRepository nivelRepository,
                       GamingProfileRepository gamingProfileRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.nivelRepository = nivelRepository;
        this.gamingProfileRepository = gamingProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public AuthResponse register(RegisterRequest request) {
        // Busca el rol CIUDADANO
        Rol rolCiudadano = rolRepository.findByNombre("ROLE_CIUDADANO")
        .orElseThrow(() -> new RuntimeException("Rol CIUDADANO no encontrado"));

        // Busca el nivel inicial (Principiante, id=1)
        Nivel nivelInicial = nivelRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Nivel inicial no encontrado"));

        // Crea el usuario
        Usuario usuario = new Usuario();
        usuario.setDni(request.getDni());
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setTelefono(request.getTelefono());
        usuario.setActivo(true);
        usuario.setRol(rolCiudadano);
        usuarioRepository.save(usuario);

        // Crea el gaming profile automáticamente
        GamingProfile gamingProfile = new GamingProfile();
        gamingProfile.setUsuario(usuario);
        gamingProfile.setNivel(nivelInicial);
        gamingProfile.setPuntosTotales(0);
        gamingProfileRepository.save(gamingProfile);

        // Genera el token
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtService.generateToken(userDetails, usuario.getId(), rolCiudadano.getNombre());

        return new AuthResponse(token, "Bearer", rolCiudadano.getNombre(), usuario.getId());
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtService.generateToken(userDetails, usuario.getId(), usuario.getRol().getNombre());

        return new AuthResponse(token, "Bearer", usuario.getRol().getNombre(), usuario.getId());
    }
}