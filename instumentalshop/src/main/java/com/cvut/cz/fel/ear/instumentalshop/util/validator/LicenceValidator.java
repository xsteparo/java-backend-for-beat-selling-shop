package com.cvut.cz.fel.ear.instumentalshop.util.validator;

import com.cvut.cz.fel.ear.instumentalshop.domain.*;
import com.cvut.cz.fel.ear.instumentalshop.domain.enums.LicenceType;
import com.cvut.cz.fel.ear.instumentalshop.dto.licence.in.TemplateUpdateRequestDto;

public interface LicenceValidator {

    void validatePurchaseCreateRequest(Customer customer, Track track, LicenceType licenceType);

    void validatePurchasedLicenceGetRequest(User user, PurchasedLicence purchasedLicence);

    void validateTemplateCreationRequest(Producer producer, Long trackId, LicenceType licenceType);

    void validateTemplateUpdateRequest(Producer producer, Long trackId, TemplateUpdateRequestDto requestDto);

    void validateTemplateDeleteRequest(Producer producer, Long trackId);

    void validateReportGetRequest(User user, Long reportId);

    void validateReportCreateRequest(PurchasedLicence purchasedLicence, Producer producer);

    void validateReportUpdateStatus(Producer producer, LicenceReport licenceReport);

    void validateReportDeleteRequest(Producer producer, Long reportId);

}
