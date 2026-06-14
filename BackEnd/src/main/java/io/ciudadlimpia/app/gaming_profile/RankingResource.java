package io.ciudadlimpia.app.gaming_profile;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(value = "/api/ranking", produces = MediaType.APPLICATION_JSON_VALUE)
public class RankingResource {

    private final GamingProfileService gamingProfileService;

    public RankingResource(GamingProfileService gamingProfileService) {
        this.gamingProfileService = gamingProfileService;
    }

    @GetMapping
    public ResponseEntity<List<RankingResponse>> getRanking() {
        return ResponseEntity.ok(gamingProfileService.getRanking());
    }
}