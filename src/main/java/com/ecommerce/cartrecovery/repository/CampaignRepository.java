package com.ecommerce.cartrecovery.repository;

import com.ecommerce.cartrecovery.entity.CampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<CampaignEntity, BigInteger> {
    List<CampaignEntity> findByRecoveryConfigId(BigInteger id);
}
