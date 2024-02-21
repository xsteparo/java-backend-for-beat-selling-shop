package com.cvut.cz.fel.ear.instumentalshop.util.query;


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

    public static final String CHECK_IF_PRODUCER_IS_LEAD = """
            SELECT COUNT(p) > 0 FROM ProducerTrackInfo p WHERE p.track.id = :trackId AND p.producer.id = :producerId AND p.ownsPublishingTrack = true
            """;

    public static final String FIND_PRODUCER_TRACK_INFO_BY_TRACK_ID_AND_PRODUCER_ID_AND_AGREED_STATUS = """
            SELECT p FROM ProducerTrackInfo p WHERE p.track.id = :trackId AND p.producer.id = :producerId AND p.agreedForSelling = :agreedStatus
            """;

    public static final String FIND_PRODUCER_TRACK_INFO_BY_PRODUCER_ID_AND_AGREED_STATUS = """
            SELECT p FROM ProducerTrackInfo p WHERE p.producer.id = :producerId AND p.agreedForSelling = :agreedForSelling
            """;

    public static final String CHECK_IF_PRODUCER_CAN_DELETE_TRACK = """
            SELECT COUNT(pti) > 0 FROM ProducerTrackInfo pti WHERE pti.track.id = :trackId AND pti.producer.id = :producerId AND pti.ownsPublishingTrack = true
            """;

    public static final String FIND_TRACKS_BY_PRODUCER_ID = """
            SELECT pti.track FROM ProducerTrackInfo pti WHERE pti.producer.id = :producerId
            """;

    public static final String FIND_BOUGHT_TRACKS_BY_CUSTOMER_ID = """
                        SELECT DISTINCT t FROM Track t\s
                        JOIN t.purchasedLicence pl\s
                        JOIN pl.producers prod\s
                        WHERE pl.customer.id = :customerId AND prod.id = :producerId
            """;

    public static final String IS_PRODUCER_RELATED_TO_REPORT = """
            SELECT CASE WHEN COUNT(lr) > 0 THEN true ELSE false END FROM LicenceReport lr
            JOIN lr.purchasedLicence pl
            JOIN pl.producers p
            WHERE lr.id = :reportId AND p.id = :userId
            """;

    public static final String IS_CUSTOMER_RELATED_TO_REPORT = """
            SELECT CASE WHEN COUNT(lr) > 0 THEN true ELSE false END FROM LicenceReport lr
            JOIN lr.purchasedLicence pl
            WHERE lr.id = :reportId AND pl.customer.id = :userId
            """;

    public static final String FIND_PRODUCER_INCOMES = """
            SELECT t.id AS trackId, t.name AS trackName, ROUND(SUM(lt.price * (pti.profit_percentage / 100)), 2) AS salaryFromTrack
                        FROM producer_track_info pti
                        JOIN track t ON pti.track_id = t.id
                        JOIN purchased_licences pl ON t.id = pl.track_id
                        JOIN licence_template lt ON pl.licence_template_id = lt.id
                        WHERE pti.producer_id = :producerId
                        GROUP BY t.id, t.name
            """;
}