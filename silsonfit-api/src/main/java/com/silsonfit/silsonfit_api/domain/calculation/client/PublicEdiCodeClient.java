package com.silsonfit.silsonfit_api.domain.calculation.client;

import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import com.silsonfit.silsonfit_api.domain.calculation.enums.FeeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PayType;
import com.silsonfit.silsonfit_api.global.error.BusinessException;
import com.silsonfit.silsonfit_api.global.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * 공공데이터포털 EDI 수가 코드 조회 Client
 *
 * - 진료수가/한방수가/약국수가 API를 순차 조회한다.
 * - XML 응답의 첫 번째 item을 EdiCode로 매핑한다.
 */
@Component
@Profile("prod | real-edi")
public class PublicEdiCodeClient implements EdiCodeClient {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_NO = 1;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final String serviceKey;
    private final RestClient restClient;
    private final List<Endpoint> endpoints;

    public PublicEdiCodeClient(
            @Value("${edi.public-api.service-key}") String serviceKey,
            @Value("${edi.public-api.endpoint}") String endpoint,
            RestClient.Builder restClientBuilder
    ) {
        this.serviceKey = serviceKey;
        this.restClient = restClientBuilder.baseUrl(endpoint).build();
        this.endpoints = List.of(
                new Endpoint("/getDiagnossMdfeeList", FeeType.MEDICAL),
                new Endpoint("/getCmdcMdfeeList", FeeType.KOREAN_MEDICAL),
                new Endpoint("/getPharmacyMdfeeList", FeeType.PHARMACY)
        );
    }

    @Override
    public Optional<EdiCode> fetchByCode(String code) {
        for (Endpoint endpoint : endpoints) {
            Optional<EdiCode> ediCode = fetchByEndpoint(code, endpoint);

            if (ediCode.isPresent()) {
                return ediCode;
            }
        }

        return Optional.empty();
    }

    private Optional<EdiCode> fetchByEndpoint(String code, Endpoint endpoint) {
        try {
            String response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(endpoint.path())
                            .queryParam("serviceKey", serviceKey)
                            .queryParam("numOfRow", DEFAULT_PAGE_SIZE)
                            .queryParam("pageNo", DEFAULT_PAGE_NO)
                            .queryParam("mdfeeCd", code)
                            .build()
                    )
                    .retrieve()
                    .body(String.class);

            return parse(response, endpoint.feeType());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.EDI_API_SERVER_ERROR);
        }
    }

    private Optional<EdiCode> parse(String response, FeeType feeType) {
        if (response == null || response.isBlank()) {
            return Optional.empty();
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setExpandEntityReferences(false);

            Document document = factory.newDocumentBuilder()
                    .parse(new InputSource(new StringReader(response)));
            NodeList items = document.getElementsByTagName("item");

            if (items.getLength() == 0) {
                return Optional.empty();
            }

            Element item = (Element) items.item(0);

            return Optional.of(EdiCode.create(
                    text(item, "mdfeeCd"),
                    text(item, "korNm"),
                    text(item, "mdfeeDivNo"),
                    parsePayType(text(item, "payTpNm")),
                    parseInteger(text(item, "unprc")),
                    parseBigDecimal(text(item, "cvalPnt")),
                    parseDate(text(item, "adtstaDd")),
                    feeType
            ));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.EDI_API_SERVER_ERROR);
        }
    }

    private PayType parsePayType(String value) {
        if ("급여".equals(value)) {
            return PayType.PAY;
        }

        return PayType.NON_PAY;
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Integer.valueOf(value.replace(",", ""));
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return new BigDecimal(value.replace(",", ""));
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return LocalDate.parse(value, DATE_FORMATTER);
    }

    private String text(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);

        if (nodes.getLength() == 0) {
            return null;
        }

        Node node = nodes.item(0);
        String value = node.getTextContent();

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private record Endpoint(
            String path,
            FeeType feeType
    ) {
    }
}
