package com.mo.core.repositories.jpa;

import com.mo.core.model.auctions.Auction;
import com.mo.core.model.needs.AbstractUserNeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

    @Query("select a from Auction a left join fetch a.products where a.id = :id")
    Optional<Auction> findByIdWithProducts(@Param("id") Long id);

    @Query("select a.need from Auction a where a.id = :auctionId")
    AbstractUserNeed findNeedByAuctionId(@Param("auctionId") Long auctionId);
    
    // Récupère toutes les enchères actives dont la date de fin est avant "dateTime"
    List<Auction> findByEndAtBeforeAndIsActivedTrue(LocalDateTime dateTime);
}