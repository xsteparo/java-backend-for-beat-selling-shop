package com.cz.cvut.fel.instumentalshop.dto.licence.out;

import com.cz.cvut.fel.instumentalshop.domain.enums.ReportStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LicenceReportDto {

    private Long id;

    private Long purchaseId;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime reportDate;

    private ReportStatus reportStatus;

}
