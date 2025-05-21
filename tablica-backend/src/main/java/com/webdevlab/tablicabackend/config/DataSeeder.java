package com.webdevlab.tablicabackend.config;

import com.webdevlab.tablicabackend.entity.offer.OfferTag;
import com.webdevlab.tablicabackend.repository.OfferTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final OfferTagRepository offerTagRepository;

    @Override
    public void run(String... args) {
        if (offerTagRepository.count() == 0) {
            offerTagRepository.saveAll(List.of(
                    OfferTag.builder().tag("Electronics").build(),
                    OfferTag.builder().tag("Books").build(),
                    OfferTag.builder().tag("Clothing").build()
            ));
        }
    }
}
