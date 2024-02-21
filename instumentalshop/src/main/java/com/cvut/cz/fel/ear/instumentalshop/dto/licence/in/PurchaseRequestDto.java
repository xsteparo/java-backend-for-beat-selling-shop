package com.cvut.cz.fel.ear.instumentalshop.dto.licence.in;

import com.cvut.cz.fel.ear.instumentalshop.domain.enums.LicenceType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseRequestDto {

    @NotNull
    private LicenceType licenceType;

}
