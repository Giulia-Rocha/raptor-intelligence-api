package com.ford.raptorapi.dto.response;

import lombok.Data;

@Data
public class WarrantyResponse {
    private String basicWarranty;
    private String powertrainWarranty;
    private String corrosionWarranty;
    private String roadsideAssistance;
}
