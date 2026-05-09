package com.ecommerce.cartrecovery.service;

import com.ecommerce.cartrecovery.entity.CampaignEntity;
import com.ecommerce.cartrecovery.repository.CampaignRepository;
import com.ecommerce.cartrecovery.request.CampaignRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;

    public CampaignEntity createCampaign(CampaignRequest request) {
        CampaignEntity entity = CampaignEntity.builder()
                .campaignName(request.getCampaignName())
                .description(request.getDescription())
                .recoveryConfigId(request.getRecoveryConfigId())
                .template(request.getTemplate())
                .trafficPercentage(request.getTrafficPercentage() != null ? request.getTrafficPercentage() : 100)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        return campaignRepository.save(entity);
    }

    public List<CampaignEntity> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    public CampaignEntity getCampaignById(BigInteger id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
    }

    public CampaignEntity updateCampaign(BigInteger id, CampaignEntity updatedCampaign) {
        CampaignEntity existing = getCampaignById(id);

        existing.setCampaignName(updatedCampaign.getCampaignName());
        existing.setTrafficPercentage(updatedCampaign.getTrafficPercentage());
        existing.setRecoveryConfigId(updatedCampaign.getRecoveryConfigId());
        existing.setTemplate(updatedCampaign.getTemplate());
        existing.setIsActive(updatedCampaign.getIsActive());
        existing.setStartDate(updatedCampaign.getStartDate());
        existing.setEndDate(updatedCampaign.getEndDate());

        return campaignRepository.save(existing);
    }

    public void deleteCampaign(BigInteger id) {
        campaignRepository.deleteById(id);
    }

    public List<CampaignEntity> getAllCampaignsByStageConfigId(BigInteger id) {
        return campaignRepository.findByRecoveryConfigId(id);
    }
}