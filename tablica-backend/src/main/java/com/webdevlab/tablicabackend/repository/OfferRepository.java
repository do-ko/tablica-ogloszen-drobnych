package com.webdevlab.tablicabackend.repository;

import com.webdevlab.tablicabackend.domain.enums.OfferStatus;
import com.webdevlab.tablicabackend.entity.offer.Offer;
import com.webdevlab.tablicabackend.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferRepository extends JpaRepository<Offer, String> {
    @Query("""
                SELECT DISTINCT o FROM Offer o
                LEFT JOIN o.tags t
                WHERE (:status IS NULL OR o.status = :status)
                  AND (:user IS NULL OR o.seller = :user)
                  AND (
                      LOWER(o.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                      LOWER(o.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                      LOWER(t.tag) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            """)
    Page<Offer> searchOffers(
            @Param("status") OfferStatus status,
            @Param("user") User user,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
