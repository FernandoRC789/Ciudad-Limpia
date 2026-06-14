package io.ciudadlimpia.app.gaming_profile;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/gamingProfiles", produces = MediaType.APPLICATION_JSON_VALUE)
public class GamingProfileResource {

    private final GamingProfileService gamingProfileService;

    public GamingProfileResource(final GamingProfileService gamingProfileService) {
        this.gamingProfileService = gamingProfileService;
    }

    @GetMapping
    public ResponseEntity<List<GamingProfileDTO>> getAllGamingProfiles() {
        return ResponseEntity.ok(gamingProfileService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GamingProfileDTO> getGamingProfile(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(gamingProfileService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createGamingProfile(
            @RequestBody @Valid final GamingProfileDTO gamingProfileDTO) {
        final Long createdId = gamingProfileService.create(gamingProfileDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateGamingProfile(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final GamingProfileDTO gamingProfileDTO) {
        gamingProfileService.update(id, gamingProfileDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGamingProfile(@PathVariable(name = "id") final Long id) {
        gamingProfileService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
