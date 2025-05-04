package com.cz.cvut.fel.instumentalshop.domain;

import com.cz.cvut.fel.instumentalshop.dto.producer.out.ProducerPurchaseStatisticDto;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.cz.cvut.fel.instumentalshop.util.query.QuerySqlDefinitions.CUSTOMER_PURCHASE_INFO_FOR_PRODUCER;

@Entity
@SqlResultSetMapping(
        name = "CustomerPurchaseInfoMapping",
        classes = @ConstructorResult(
                targetClass = ProducerPurchaseStatisticDto.class,
                columns = {
                        @ColumnResult(name = "customerId", type = Long.class),
                        @ColumnResult(name = "customerUsername", type = String.class),
                        @ColumnResult(name = "totalPurchases", type = Integer.class),
                        @ColumnResult(name = "lastPurchaseDate", type = LocalDateTime.class)
                }
        )
)
@NamedNativeQuery(
        name = "Customer.getCustomerPurchaseInfoForProducer",
        query = CUSTOMER_PURCHASE_INFO_FOR_PRODUCER,
        resultSetMapping = "CustomerPurchaseInfoMapping"

)
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@DiscriminatorValue("Customer")
public class Customer extends User{

    @OneToMany(mappedBy = "customer")
    private List<PurchasedLicence> orders;

    private BigDecimal balance = BigDecimal.valueOf(0);

}
