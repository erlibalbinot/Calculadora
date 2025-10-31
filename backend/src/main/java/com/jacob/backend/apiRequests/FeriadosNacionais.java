package com.jacob.backend.apiRequests;

import com.jacob.backend.dto.FeriadosDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class FeriadosNacionais {

    public List<FeriadosDto> getFeriados(int year) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://brasilapi.com.br/api/feriados/v1/";

        ResponseEntity<List<FeriadosDto>> responseEntity = restTemplate.exchange(
                url + year,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<FeriadosDto>>() {}
        );

        return responseEntity.getBody();
    }
}
