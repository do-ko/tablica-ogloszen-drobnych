package com.webdevlab.tablicabackend.repository;

import com.webdevlab.tablicabackend.entity.offer.OfferImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferImageRepository extends JpaRepository<OfferImage, String> {
}
