package com.webdevlab.tablicabackend.repository;

import com.webdevlab.tablicabackend.entity.offer.OfferTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferTagRepository extends JpaRepository<OfferTag, String> {
}
