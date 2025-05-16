package com.cz.cvut.fel.instumentalshop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "track_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","track_id"}))
@Getter
@Setter
public class TrackLike {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name="track_id")
    private Track track;

    @Column(nullable = false)
    private LocalDateTime likedAt = LocalDateTime.now();
}
