package com.cz.cvut.fel.instumentalshop.util.validator;

import com.cz.cvut.fel.instumentalshop.domain.*;
import com.cz.cvut.fel.instumentalshop.domain.enums.LicenceType;
import com.cz.cvut.fel.instumentalshop.dto.licence.in.TemplateUpdateRequestDto;

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
