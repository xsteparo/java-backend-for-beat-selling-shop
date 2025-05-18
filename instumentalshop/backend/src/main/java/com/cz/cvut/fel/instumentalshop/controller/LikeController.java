package com.cz.cvut.fel.instumentalshop.controller;

import com.cz.cvut.fel.instumentalshop.domain.Customer;
import com.cz.cvut.fel.instumentalshop.service.AuthenticationService;
import com.cz.cvut.fel.instumentalshop.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Kontroler pro správu lajků skladeb.
 */
@RestController
@RequestMapping("/api/v1/tracks")
@RequiredArgsConstructor
public class LikeController {

    private final AuthenticationService authenticationService;

    private final LikeService likeService;

    /**
     * Označí skladbu lajkem aktuálního zákazníka.
     *
     * @param trackId ID skladby, kterou se má označit lajkem
     * @return HTTP 200 OK při úspěchu
     */
    @PostMapping("/{trackId}/like")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Void> like(@PathVariable Long trackId) {
        Customer customer = (Customer) authenticationService.getRequestingUserFromSecurityContext();
        likeService.likeTrack(customer.getId(), trackId);
        return ResponseEntity.ok().build();
    }

    /**
     * Odebere lajka ze skladby pro aktuálního zákazníka.
     *
     * @param trackId ID skladby, ze které se má lajka odebrat
     * @return HTTP 200 OK při úspěchu
     */
    @DeleteMapping("/{trackId}/like")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<Void> unlike(@PathVariable Long trackId) {
        Customer customer = (Customer) authenticationService.getRequestingUserFromSecurityContext();
        likeService.unlikeTrack(customer.getId(), trackId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/like/me")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<List<Long>> getMyLikes() {
        Customer customer = (Customer) authenticationService.getRequestingUserFromSecurityContext();
        List<Long> likedIds = likeService.findLikedTrackIdsByUser(customer.getId());
        return ResponseEntity.ok(likedIds);
    }
}
