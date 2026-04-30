package com.silsonfit.silsonfit_api.domain.calculation.client;

import com.silsonfit.silsonfit_api.domain.calculation.entity.EdiCode;
import com.silsonfit.silsonfit_api.domain.calculation.enums.FeeType;
import com.silsonfit.silsonfit_api.domain.calculation.enums.PayType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * PublicEdiCodeClient 테스트
 *
 * MockRestServiceServer로 공공 EDI API XML 응답을 스텁 후
 * RestClient 호출 → XML 파싱 → EdiCode 반환 흐름을 검증한다.
 */
class PublicEdiCodeClientTest {

    private static final String ENDPOINT = "https://apis.data.go.kr/B551182/mdfeeCrtrInfoService";
    private static final String SERVICE_KEY = "test-service-key";
    private static final MediaType XML_UTF_8 = MediaType.parseMediaType("application/xml;charset=UTF-8");

    private MockRestServiceServer server;
    private PublicEdiCodeClient publicEdiCodeClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        this.server = MockRestServiceServer.bindTo(builder).build();
        this.publicEdiCodeClient = new PublicEdiCodeClient(SERVICE_KEY, ENDPOINT, 10, 1, builder);
    }

    @Test
    @DisplayName("진료수가 XML 응답을 EdiCode로 매핑한다")
    void fetchByCode_medical_success() {
        server.expect(requestTo(ENDPOINT + "/getDiagnossMdfeeList?serviceKey=test-service-key&numOfRow=10&pageNo=1&mdfeeCd=MRI001"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        <response>
                            <body>
                                <items>
                                    <item>
                                        <adtstaDd>20260101</adtstaDd>
                                        <cvalPnt>123.45</cvalPnt>
                                        <korNm>MRI 검사</korNm>
                                        <mdfeeCd>MRI001</mdfeeCd>
                                        <mdfeeDivNo>MRI</mdfeeDivNo>
                                        <payTpNm>급여</payTpNm>
                                        <unprc>100000</unprc>
                                    </item>
                                </items>
                            </body>
                        </response>
                        """, XML_UTF_8));

        Optional<EdiCode> result = publicEdiCodeClient.fetchByCode("MRI001");

        assertThat(result).isPresent();
        EdiCode ediCode = result.get();
        assertThat(ediCode.getCode()).isEqualTo("MRI001");
        assertThat(ediCode.getTreatmentName()).isEqualTo("MRI 검사");
        assertThat(ediCode.getFeeDivisionNumber()).isEqualTo("MRI");
        assertThat(ediCode.getPayType()).isEqualTo(PayType.PAY);
        assertThat(ediCode.getUnitPrice()).isEqualTo(100000);
        assertThat(ediCode.getRelativeValuePoint()).isEqualByComparingTo(BigDecimal.valueOf(123.45));
        assertThat(ediCode.getEffectiveStartDate()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(ediCode.getFeeType()).isEqualTo(FeeType.MEDICAL);
        server.verify();
    }

    @Test
    @DisplayName("진료/한방수가에 결과가 없으면 약국수가 응답을 EdiCode로 매핑한다")
    void fetchByCode_fallback_to_pharmacy() {
        server.expect(requestTo(ENDPOINT + "/getDiagnossMdfeeList?serviceKey=test-service-key&numOfRow=10&pageNo=1&mdfeeCd=Z3000030"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(emptyItemsXml(), XML_UTF_8));
        server.expect(requestTo(ENDPOINT + "/getCmdcMdfeeList?serviceKey=test-service-key&numOfRow=10&pageNo=1&mdfeeCd=Z3000030"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(emptyItemsXml(), XML_UTF_8));
        server.expect(requestTo(ENDPOINT + "/getPharmacyMdfeeList?serviceKey=test-service-key&numOfRow=10&pageNo=1&mdfeeCd=Z3000030"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        <response>
                            <body>
                                <items>
                                    <item>
                                        <adtstaDd>20160101</adtstaDd>
                                        <cvalPnt>3.3</cvalPnt>
                                        <korNm>복약지도료(방문당)-토요09-13</korNm>
                                        <mdfeeCd>Z3000030</mdfeeCd>
                                        <mdfeeDivNo>약</mdfeeDivNo>
                                        <payTpNm>급여</payTpNm>
                                        <unprc>260</unprc>
                                    </item>
                                </items>
                            </body>
                        </response>
                        """, XML_UTF_8));

        Optional<EdiCode> result = publicEdiCodeClient.fetchByCode("Z3000030");

        assertThat(result).isPresent();
        EdiCode ediCode = result.get();
        assertThat(ediCode.getCode()).isEqualTo("Z3000030");
        assertThat(ediCode.getTreatmentName()).isEqualTo("복약지도료(방문당)-토요09-13");
        assertThat(ediCode.getFeeDivisionNumber()).isEqualTo("약");
        assertThat(ediCode.getPayType()).isEqualTo(PayType.PAY);
        assertThat(ediCode.getUnitPrice()).isEqualTo(260);
        assertThat(ediCode.getRelativeValuePoint()).isEqualByComparingTo(BigDecimal.valueOf(3.3));
        assertThat(ediCode.getEffectiveStartDate()).isEqualTo(LocalDate.of(2016, 1, 1));
        assertThat(ediCode.getFeeType()).isEqualTo(FeeType.PHARMACY);
        server.verify();
    }

    private String emptyItemsXml() {
        return """
                <response>
                    <body>
                        <items/>
                    </body>
                </response>
                """;
    }

}
