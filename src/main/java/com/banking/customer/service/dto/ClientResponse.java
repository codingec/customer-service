package com.banking.customer.service.dto;

import com.banking.customer.entity.Client;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponse {

    private Long id;
    private String name;
    private String documentId;
    private String email;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ClientResponse fromEntity(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .documentId(client.getDocumentId())
                .email(client.getEmail())
                .status(client.getStatus())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }
}
