package com.cz.cvut.fel.instumentalshop.dto.licence.in;

import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestDto {

    @NotNull
    private LicenceType licenceType;

}
