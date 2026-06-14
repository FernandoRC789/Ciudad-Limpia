package io.ciudadlimpia.app.badge;

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
@RequestMapping(value = "/api/badges", produces = MediaType.APPLICATION_JSON_VALUE)
public class BadgeResource {

    private final BadgeService badgeService;

    public BadgeResource(final BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @GetMapping
    public ResponseEntity<List<BadgeDTO>> getAllBadges() {
        return ResponseEntity.ok(badgeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BadgeDTO> getBadge(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(badgeService.get(id));
    }

    @PostMapping
    public ResponseEntity<Integer> createBadge(@RequestBody @Valid final BadgeDTO badgeDTO) {
        final Integer createdId = badgeService.create(badgeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateBadge(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final BadgeDTO badgeDTO) {
        badgeService.update(id, badgeDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBadge(@PathVariable(name = "id") final Integer id) {
        badgeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
