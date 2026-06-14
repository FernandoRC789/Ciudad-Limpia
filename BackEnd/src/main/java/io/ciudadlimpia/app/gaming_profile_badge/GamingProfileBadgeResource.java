package io.ciudadlimpia.app.gaming_profile_badge;

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
@RequestMapping(value = "/api/gamingProfileBadges", produces = MediaType.APPLICATION_JSON_VALUE)
public class GamingProfileBadgeResource {

    private final GamingProfileBadgeService gamingProfileBadgeService;

    public GamingProfileBadgeResource(final GamingProfileBadgeService gamingProfileBadgeService) {
        this.gamingProfileBadgeService = gamingProfileBadgeService;
    }

    @GetMapping
    public ResponseEntity<List<GamingProfileBadgeDTO>> getAllGamingProfileBadges() {
        return ResponseEntity.ok(gamingProfileBadgeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GamingProfileBadgeDTO> getGamingProfileBadge(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(gamingProfileBadgeService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createGamingProfileBadge(
            @RequestBody @Valid final GamingProfileBadgeDTO gamingProfileBadgeDTO) {
        final Long createdId = gamingProfileBadgeService.create(gamingProfileBadgeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateGamingProfileBadge(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final GamingProfileBadgeDTO gamingProfileBadgeDTO) {
        gamingProfileBadgeService.update(id, gamingProfileBadgeDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGamingProfileBadge(@PathVariable(name = "id") final Long id) {
        gamingProfileBadgeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
