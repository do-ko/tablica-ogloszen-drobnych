package com.webdevlab.tablicabackend.repository;

import com.webdevlab.tablicabackend.entity.offer.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Offer, String> {
}
