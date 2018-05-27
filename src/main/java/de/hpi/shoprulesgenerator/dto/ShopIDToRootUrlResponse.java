package de.hpi.shoprulesgenerator.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopIDToRootUrlResponse {

    private long oracleShopId;

    private long shopId;

    private String shopName;

    private String shopUrl;

}
