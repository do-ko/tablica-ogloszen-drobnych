package com.webdevlab.tablicabackend.repository;

import com.webdevlab.tablicabackend.dto.TagUsageDTO;
import com.webdevlab.tablicabackend.entity.offer.OfferTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferTagRepository extends JpaRepository<OfferTag, String> {
    @Query("""
                SELECT new com.webdevlab.tablicabackend.dto.TagUsageDTO(t.tag, count(o))
                FROM OfferTag t
                LEFT JOIN t.offers o
                GROUP BY t.tag
                ORDER BY COUNT(o) DESC
            """)
    Page<TagUsageDTO> findAllTagsWithUsage(Pageable pageable);

}
