package com.cz.cvut.fel.instumentalshop.service;

import com.cz.cvut.fel.instumentalshop.domain.Track;

import java.util.List;

public interface RecommendationService {

    List<Track> getTopTracks(int count);


    List<Track> getNewTracks(int count);


    List<Track> getTrendingTracks(int count);


}
