package com.cz.cvut.fel.instumentalshop.dto.licence.in;

import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseRequestDto {

    @NotNull
    private LicenceType licenceType;

}
