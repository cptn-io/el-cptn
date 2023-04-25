package com.elcptn.mgmtsvc.services;

import com.elcptn.common.entities.Instance;
import com.elcptn.common.exceptions.BadRequestException;
import com.elcptn.common.repositories.InstanceRepository;
import com.elcptn.mgmtsvc.dto.InstanceInputDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/* @author: kc, created on 4/25/23 */
@Service
public class InstanceService {

    private final InstanceRepository instanceRepository;

    private final WebClient webClient;

    public InstanceService(InstanceRepository instanceRepository,
                           @Value("${cptn.community.baseurl}") String baseurl) {
        this.instanceRepository = instanceRepository;
        this.webClient = WebClient.builder().baseUrl(baseurl).build();
    }

    public Instance registerInstance(InstanceInputDto instanceInputDto) {
        Instance instance = webClient.post()
                .uri("/public/instance")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(instanceInputDto))
                .retrieve()
                .onStatus(status -> status.isError(), response -> Mono.error(new BadRequestException("Error from remote HTTP " +
                        "service")))
                .bodyToMono(Instance.class).block();

        return instanceRepository.save(instance);
    }

    public Instance getInstance() {
        return instanceRepository.getInstance();
    }

}
