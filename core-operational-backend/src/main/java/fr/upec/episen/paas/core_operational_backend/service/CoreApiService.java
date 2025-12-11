package fr.upec.episen.paas.core_operational_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fr.upec.episen.paas.core_operational_backend.dto.StudentDTO;

@Service
public class CoreApiService {

    private final LoadBalancedCoreClient lbClient;
    private final RestTemplate restTemplate = new RestTemplate();

    public CoreApiService(LoadBalancedCoreClient lbClient) {
        this.lbClient = lbClient;
    }

    public StudentDTO fetchStudent(long studentId) {
        String coreUrl = lbClient.getNextUpCore();
        try {
            return restTemplate.getForObject(coreUrl + "/student/" + studentId, StudentDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch student from core: " + coreUrl, e);
        }
    }
}
