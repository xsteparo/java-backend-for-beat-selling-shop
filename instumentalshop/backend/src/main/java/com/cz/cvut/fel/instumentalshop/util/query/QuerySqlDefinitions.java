package com.cz.cvut.fel.instumentalshop.util.query;


public class QuerySqlDefinitions {

    public static final String CUSTOMER_PURCHASE_INFO_FOR_PRODUCER = """
            SELECT c.id AS customerId, u.username AS customerUsername, COUNT(pl.id) AS totalPurchases,
                                   MAX(pl.purchase_date) AS lastPurchaseDate
                            FROM customer c
                            JOIN app_user u ON c.id = u.id
                            JOIN purchased_licences pl ON c.id = pl.customer_id
                            JOIN producer_purchased_licence ppl ON pl.id = ppl.purchased_licence_id
                            WHERE ppl.producer_id = :producerId
                            GROUP BY c.id, u.username
                            ORDER BY MAX(pl.purchase_date) DESC
            """;



    public static final String FIND_BOUGHT_TRACKS_BY_CUSTOMER_ID = """
                        SELECT DISTINCT t FROM Track t\s
                        JOIN t.purchasedLicence pl\s
                        JOIN pl.producer prod\s
                        WHERE pl.customer.id = :customerId AND prod.id = :producerId
            """;

    public static final String IS_PRODUCER_RELATED_TO_REPORT = """
            SELECT CASE WHEN COUNT(lr) > 0 THEN true ELSE false END FROM LicenceReport lr
            JOIN lr.purchasedLicence pl
            JOIN pl.producer p
            WHERE lr.id = :reportId AND p.id = :userId
            """;

    public static final String IS_CUSTOMER_RELATED_TO_REPORT = """
            SELECT CASE WHEN COUNT(lr) > 0 THEN true ELSE false END FROM LicenceReport lr
            JOIN lr.purchasedLicence pl
            WHERE lr.id = :reportId AND pl.customer.id = :userId
            """;

}