import cn.hutool.core.date.DatePattern;
import com.freesia.dto.SleepCommentHeaderDto;
import com.freesia.net.builder.HttpBuilder;
import com.freesia.net.component.HttpClientComponent;
import com.freesia.net.dto.HttpClientDto;
import com.freesia.util.UEmpty;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpiderTest {
    @Test
    public void testTrustpilot() {
        int maxPage = 31;
        String targetUrl = "";
        List<SleepCommentHeaderDto> sleepCommentHeaderDtoList = new ArrayList<>();
        for (int i = 1; i <= maxPage; i++) {
            if (i == 1) {
                targetUrl = "https://www.trustpilot.com/review/eightsleep.com";
            } else {
                targetUrl = "https://www.trustpilot.com/review/eightsleep.com?page=" + i;
            }
            Connection connect = Jsoup.connect(targetUrl).timeout(6000).maxBodySize(0);
            connect.header("Accept-Encoding", "gzip, deflate, br");
            Element body = null;
            try {
                body = connect.get().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (body != null) {
                SleepCommentHeaderDto dto = new SleepCommentHeaderDto();
                dto.setSource("TrustPilot");

                Elements elementList = body.select("div.styles_wrapper__ie3f0");
                if (UEmpty.isNotEmpty(elementList)) {
                    SimpleDateFormat sdf = new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN);
                    for (int j = 0; j < elementList.size(); j++) {
                        Element element = elementList.get(i);
                        Element userNameElement = element.selectFirst(".styles_reviewCardInnerHeader__8Xqy8 > aside > div > a > span");
                        String username = userNameElement.text();
                        System.out.println("userName: " + username);
                        String operateTime = element.selectFirst("div > article > div > div.styles_reviewCardInnerHeader__8Xqy8 > div > time").attr("datetime");
                        System.out.println("operateTime: " + operateTime);
                        String levelStr = element.selectFirst("article > div > section > div.styles_reviewHeader__DzoAZ").attr("data-service-review-rating");
                        if (UEmpty.isNotEmpty(levelStr)) {
                            int level = Integer.parseInt(levelStr);
                            dto.setLevel(levelStr);
                            System.out.println("level: " + level);
                        }
                        String title = element.selectFirst("article > div > section > div.styles_reviewContent__tuXiN > a > h2").text();
                        System.out.println("title: " + title);
                        String content = element.selectFirst("article > div > section > div.styles_reviewContent__tuXiN > p").text();
                        System.out.println("content: " + content);
                        dto.setUserId(null);
                        dto.setUserName(username);
                        dto.setContent(content);
                        try {
                            dto.setOperateTime(sdf.parse(operateTime));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        dto.setFloor(String.valueOf(j + 1));
                        dto.setContentType("COMMENT");
                        dto.setUrl(targetUrl);
                    }
                }
                sleepCommentHeaderDtoList.add(dto);
            }
        }
//        saveUpdateBatch(sleepCommentHeaderDtoList);
    }

    @Test
    public void testRaddit() {
        String targetUrl = "https://www.reddit.com/r/EightSleep/comments/1s0u90m/pod_cannot_cool_down_to_desired_temperature/";
        Connection connect = Jsoup.connect(targetUrl)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)))
                .timeout(60000)
                .maxBodySize(0);
        connect.header("Accept-Encoding", "gzip, deflate, br, zstd");
        connect.header("Cookie", "edgebucket=0jAP0bjyrL68fc5xQz; loid=000000002arjrf7wha.2.1774230916993.Z0FBQUFBQnB3SjJFQjE1c3h5QU1HdWstWktLSV94WWRLMWw5YTN1SXU2YkNhZ1Ytb3hwX2FKdlVGaFQwMm5YdGZFTmZwYzZaTFducVJvam5sX3BYNmY4UTd3OHlZX2p0VW1jRXlrRmRBNFdhY28zWS1GUUEwYmw0X1RZWU5iUkU4VlJuMXFnaHZRWE8; csrf_token=7ee96c6fdf4e34d760015cfc3668eab6; csv=2; _gcl_au=1.1.954077742.1774230920; reddit_session=eyJhbGciOiJSUzI1NiIsImtpZCI6IlNIQTI1NjpsVFdYNlFVUEloWktaRG1rR0pVd1gvdWNFK01BSjBYRE12RU1kNzVxTXQ4IiwidHlwIjoiSldUIn0.eyJzdWIiOiJ0Ml8yYXJqcmY3d2hhIiwiZXhwIjoxNzg5ODY5MzI4Ljc5ODI1NCwiaWF0IjoxNzc0MjMwOTI4Ljc5ODI1NCwianRpIjoiRzd5eFRTczFKdU1SaTdKVlQ3ZEdCMkFWZURnM1RnIiwiYXQiOjEsImNpZCI6ImNvb2tpZSIsImxjYSI6MTc3NDIzMDkxNjk5Mywic2NwIjoiZUp5S2pnVUVBQURfX3dFVkFMayIsImZsbyI6NywiYW1yIjpbInNzbyJdfQ.xPfBmfKlKdM6OqDhwwxTifH2OUoR3g4K0J_S8TvZaowfquu113_42tE61KkV_IChaxeiGusFMYuhbhhT_sGRQlkL7VbYQEHUJ3046krACIgNAia_f6Ce9w8kpDnZIGO_W78qmEvwDLTfKma58Z08ALIxm-S1NSl6nrFmo4GNso0jKanOzjQ3et5K9I6c8UzIEjDLa4pORvODDgkP7035g5Kw1xmS9obQCXuTCJCeE19T-jxS1DwUISrXhWGj4G1Orm5sE80vtk4ZmT0HXVv69PKuACPBz9f3I1t4qEIO-h8NE2CQG94-mE9s6d1T3swLRhTloIxgxXTw4HE9sKH5mg; token_v2=eyJhbGciOiJSUzI1NiIsImtpZCI6IlNIQTI1NjpzS3dsMnlsV0VtMjVmcXhwTU40cWY4MXE2OWFFdWFyMnpLMUdhVGxjdWNZIiwidHlwIjoiSldUIn0.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNzc0MzE3MzI5LjIzODAwNSwiaWF0IjoxNzc0MjMwOTI5LjIzODAwNSwianRpIjoia0VTLXVfZWRCd1lEaWwydG1FWjMzUHIwYmJBYkNBIiwiY2lkIjoiMFItV0FNaHVvby1NeVEiLCJsaWQiOiJ0Ml8yYXJqcmY3d2hhIiwiYWlkIjoidDJfMmFyanJmN3doYSIsImF0IjoxLCJsY2EiOjE3NzQyMzA5MTY5OTMsInNjcCI6ImVKeGtrZEdPdERBSWhkLUZhNV9nZjVVX20wMXRjWWFzTFFhb2szbjdEVm9jazcwN2NENHBIUDlES29xRkRDWlhncW5BQkZnVHJUREJSdVQ5bkxtM2cyaU5lOHRZc1puQ0JGbXdGRHJrbUxHc2lRUW1lSklheXhzbW9JTE55Rnl1dEdOTkxUMFFKcWhjTXJlRkhwYzJvYmtiaTU2ZEdGVzVyRHlvc1ZmbDB0akdGTFlueGpjYnF3MnB1QzZuTWtuTFF2a3NYdlRqTjlXMzl2bXpfU2EwSjhPS3F1bUIzaGxKQ0c0c2ZwaW0zZDlUazU2dEN4YTE5M3FRMnVkNjNLNTkxaXcwTzdlZjZfbHJJeG1YWTJoLUp2dDMxeS1oQTQ4OEx6UHFBRWFzNFVjWmRtUWRfbFVIVUxtZ0pHTUo0dE1JNU1ybDIzOEp0bXZUdjhidEV6OThNLUttTl96V0ROUnpDZUxRcF9IMUd3QUFfXzhRMWVUUiIsInJjaWQiOiJPb00xRnBRVkNHcGhpMk5iaVE5c2tYNWhyLWFWYUp4dm5sMFJseWdlY1dnIiwiZmxvIjoyfQ.eQzkzD5caRkqeJV74W_521bli0ykSnoB8yfv1lCR80V7-hUtHrA6MyXZK1Xw4z7ODi3-wbOPZEXbF2P2mPQCATjxygGrWtVJbtW0OkasLHbKrpRjdKj2KSYmMMpvxTp4yaZm0M7h1tATI1cajv1P2glaZ7tPjRY1ZPExgx_EONBLMMjq7e2ipitzUuB7FKZdaozzJl_e3tTdutn0SwNyBE9TTWPWJvx3G-VKywwIOJyCGLXsn_mnGPSqeJnB2sYNOUZyu-kBoTE7J8R4o4Qty0MXvFEyYs-f2-g4kCsLDEdWMasXQCsDQlCGalhWkt8ZMLgssLusVDi495Cau4-CJw; reddit_supported_media_codecs=video/avc%2Cvideo/vp9; eu_cookie={%22opted%22:true%2C%22nonessential%22:true}; reddit_chat_view=closed; seeker_session=false; subreddit_sort=AfgU2Qc=; t2_2arjrf7wha_recentclicks3=t3_1s0u90m%2Ct3_1s0mlh6%2Ct3_1rl5k2k; g_state={\"i_l\":0,\"i_ll\":1774238815679,\"i_e\":{\"enable_itp_optimization\":0},\"i_b\":\"xWx1thXMy2x8jc5THep9jfgT/ddtmYgESeiSMxLOXLg\"}; session_tracker=mhbpjeddqkfoqakege.0.1774238825729.Z0FBQUFBQnB3THhwWlBDNTBnUFpGWkVCMkU1Rm15RUZpLU9FVHlPTkxud2p3cTJhUUhmTjBDTGF0WnpHOW5HM1ZERW8yREtvZF9uVkc4elFHNHNkWU13bDB2QjNvMWpXT0h6bkNobU1IQ2R2N213UkZnMmJicnFBamR1d1pCVm02dWI4Zi1lVEh6TGU");
        connect.header("Referer", "https://www.reddit.com/r/EightSleep/comments/1s0u90m/pod_cannot_cool_down_to_desired_temperature/");
        connect.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0");
        Element body = null;
        try {
            body = connect.get().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (body != null) {
            Elements elements = body.select("#t3_1s0u90m-post-rtjson-content > p");
            if (UEmpty.isNotEmpty(elements)) {
                for (Element element : elements) {
                    System.out.println(element.text());
                }
            }
        }

    }

    @Test
    public void testBBB() {
        String targetUrl = "";
        for (int i = 1; i <= 2; i++) {
            targetUrl = "https://www.bbb.org/us/ny/new-york/profile/mattress/eight-0121-166228/customer-reviews?page=" + i;
            Connection connect = Jsoup.connect(targetUrl)
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)))
                    .timeout(60000)
                    .maxBodySize(0);
//            connect.header("Accept", "*/*");
            connect.header("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
            connect.header("accept-encoding", "gzip, deflate, br");
            connect.header("content-type", "text/html; charset=utf-8");
            connect.header("Cookie", "iabbb_user_culture=en-us; iabbb_user_location=Cherryland_CA_USA; iabbb_user_bbb=1116; iabbb_find_location=Cherryland_CA_USA; iabbb_session_id=e6eb15f4-88a4-4a24-9f82-b83a37709804; AMCVS_CB586B8557EA40917F000101%40AdobeOrg=1; _ga=GA1.1.1677767890.1774233284; _evga_f108={%22uuid%22:%22487f845cb1f47503%22}; s_cc=true; _fbp=fb.1.1774233284428.417049412401313480; _sfid_03fc={%22anonymousId%22:%22487f845cb1f47503%22%2C%22consents%22:[]}; iabbb_user_postalcode=94541; AMCV_CB586B8557EA40917F000101%40AdobeOrg=179643557%7CMCIDTS%7C20536%7CMCMID%7C31995776247252270270214319852973150098%7CMCAAMLH-1774850886%7C3%7CMCAAMB-1774850886%7C6G1ynYcLPuiQxYZrsz_pkqfLG9yMXBpb2zX5dvJdYQJzPXImdj0y%7CMCOPTOUT-1774253286s%7CNONE%7CMCAID%7CNONE%7CMCSYNCSOP%7C411-20543%7CvVersion%7C5.5.0; iabbb_cookies_preferences_set=true; iabbb_cookies_policy=%7B%22necessary%22%3Atrue%2C%22functional%22%3Atrue%2C%22performance%22%3Atrue%2C%22marketing%22%3Atrue%7D; cf_clearance=EjddYoUx0I8Xj1eum60WmrJtDMi8FnTCN7.LOB5nxjI-1774247005-1.2.1.1-7.xOjUSTNy7xd8przT8eicdcg81ocWJ1X4d4P1diEFQzutbE.GSMx2jSVyM7wfZYY7jJI1.6EsUqBZ9.5TWX5qwdMb2KT5sVGm1jYqNo6EPY8w73tOhRZc1lry6pk2odDQuR0C5z8E.zX.t90bsIb9kEWwh2CNTfOyBKPn2SuaxFBwJlT775h.wa2hbr0F1NAkasEjNcFl46R1jLxuQnWYhagIVb7hU0Zj.0Imh3ltc; __cf_bm=mnHq_LMtLQiYfqfTgMadrjlV2q35pQPUKVmv6hTGiWo-1774247005.9366374-1.0.1.1-Ij3XjQ8nMxwnJKFm2tlsZoGIh7UZ8G1ohvvnOI0hW4uqPm3_VxgLTIcCqaFMvYfNnVHZIUHQL9bh24aMikZ9XJFKWvApqrs9pm91k_Yl.VLAMc0wAY4P6eGctaZVil3x; iabbb_auth_id_token=eyJraWQiOiI5YTg2VE1wS0xJK2kzRUN3ajFSbVhaRFNBSmRYXC9ldGUzME5RWWJKR0hKWT0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJiNDA4OTRiOC0wMGQxLTcwNmUtNGUxNi01MDNhYzQ5Y2U1M2YiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfWkNpMGMxMTNXIiwiY29nbml0bzp1c2VybmFtZSI6ImZiNWIzNzM2LTY1MTUtNDgyOC1hZDQ5LWJmOTU3MGNlMTI5MCIsInByZWZlcnJlZF91c2VybmFtZSI6ImRldmlsdmFuZXZhZEBvdXRsb29rLmNvbSIsImdpdmVuX25hbWUiOiJFdmFkIiwib3JpZ2luX2p0aSI6IjhiNjcwMGJmLTMyNTgtNDk2MC04NWZiLTg2MWEwYjdiOGVjOSIsImF1ZCI6IjdxbDAxczRiZ2ZqNmxobW5zYWVrc2J1YWY2IiwiaWRlbnRpdGllcyI6W3siZGF0ZUNyZWF0ZWQiOiIxNzc0MjQ3MDU3NzcxIiwidXNlcklkIjoiQUFBQUFBQUFBQUFBQUFBQUFBQUFBTlNrOHZtSURHcUZDLWpReVZlcUVMUSIsInByb3ZpZGVyTmFtZSI6Ik1pY3Jvc29mdCIsInByb3ZpZGVyVHlwZSI6Ik9JREMiLCJpc3N1ZXIiOm51bGwsInByaW1hcnkiOiJmYWxzZSJ9XSwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE3NzQyNDcwNTgsImN1c3RvbTp1c2VyYXR0cnMiOiJ7XCJsZ0F0XCI6MCxcInJlZ0NkVmZBdFwiOjAsXCJsZ0NkVmZBdFwiOjAsXCJwd3JzQ2RWZkF0XCI6MCxcInNlbmRQcmVmXCI6XCJlbWFpbFwiLFwibGVnYWN5U3ViXCI6XCJcIixcInJlc2V0XCI6XCJ0cnVlXCIsXCJsb2dpbk1ldGhvZFwiOlwiTUlDUk9TT0ZUX1NTT1wifSIsImV4cCI6MTc3NDI1MDY1OCwiaWF0IjoxNzc0MjQ3MDU4LCJmYW1pbHlfbmFtZSI6IkRldmlsdmFuIiwianRpIjoiMjViYzQ2ZjgtZmJjOS00YzViLWE2OWUtNmJjMmZjYjdlOWNlIiwiZW1haWwiOiJkZXZpbHZhbmV2YWRAb3V0bG9vay5jb20ifQ.QxWEttLIA0ZZQrbzI6cwUhg8KLjsWy9WXOeFuczQmCvVsw4rBjCGWX9q8oQ9Zemco39X3EtRSlBSw61a1Ynj-ToXMOoBSOMRo2Zn373em3Hqu_Murf6mfpk53L0-v7IR2NfW9gs3Towf5Gehk12AcLqyDlyKCUz0XUB-TI4Hjus7TBKgzXbR3DcUUSwe3Jp5skl_jZwEKHvRj0cUJwM4P1Cu81mnhVYyuCknNsOxtidcwxYfJ6faCNm3y5oC0ADklAR6rO7UVfdUvd2POeHVQpeD6_uqQQI4gxtBTdODMZYJezxzHpLd1q0eJ1Z7_shUK-Oj9oKdVbNZibtQzm-w1w; iabbb_auth_refresh_token=eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAifQ.ZVTHTWKOu792dNSSpxqKpvd_62xH15eXwgXn1l52sBGjqY8rI0ApBSeSzmIx4C0hu6t6hftBsjTDRrGX4Hhh-Rqq3qcRbm43oAQt-TybxaSBaM_R7Fmp_o67xKnsZIb9DasVIq1ZgnhCaRG0IKmxO4PyrmHVHD1t6zJBJQESfbpNoXwMeIolrsHOJ7JHnmzCSov-XoisMt5mEoyoSO6L58_VhKKk0EQ61yp9gPOPhcNu39ojAOuVS-VrzxB1x7QUzYBre-ytZC5lhfEZk6u9zc1AWjB41qhSmCub-KjnU5J6C6Ma5SmbXqc3ZsJ3XII5Y4wqWGsrX0H1i0hwUVXf9g.dYV3ibseYsXLN6xj.5sS-Y0_OJWRYM3R3szFO4jncknVcbLhDdOo_4sueykY2_s6Xv1I7p_AaCbXCxGLin2NYm-apoU9YtVqJrtcdaW9Ct2zjxLXNzUDQaPRlty7yZjlsAG6dFddoi-ed9Vgk-R2XSSUgdNDoojy7rB9iEWBA0D522nD_RD7o6fW3OBsvpqK2zhT6Eyx9LalfTaJAossdmk910vqTNEv755fTXZn1LrSa5Y5yXj0iVHjN7_JZJWXtfZmBLnVQcjhCpIc9moS3x7L47OwZpJRFKZsV8XQNPs4-Vtq2rhxpoiM6PYLG_23MgFlKfCX4zBKZMpcn-AT9quCNsZk0Nb1qYE2puhJlU9520dULN6CdxWiZ_XfW1hcqcIoyzSfPxjp2QH6VDUZGvWimywM0g7t_cxsw4y1eV-ptBLD5iGVfHN1ofor-h6zONzJ9XqYF5BXv2ctXk-SqBfZMORiy2rMRTOohPfLSMvmqEfn_Y7P9IRzU3PIy0qT40B1ODkwIBUPp_9ic_JxoaDt0iVnVZmMflQApCekjuYaovx9YCXvsnW9Vhh0MYN4CdZ4gWrZ0R9qSfEbBQBxVX4wjiMrqRtvSlV6jXcTUI_Dh_f__Yy1RIZMwAp7F6rnoFBKgemHor-Z5qhBT-CRp4nRTR_eONQM4HhTbI0Uvr2rw4cJ7-DBKR8hdxbcvzbNI_Kc3D_EiXcFn7sRy686TAdsiE4s3duTtWz7z2tQ4HyNpkPNHmjvmvM70vAHoqoO9UEf_FEsOUUf3qYVu51dcabwwtFD1MWHagQ7s-fHoCH0t8VooEL4B54Yj4QFlQcWiZBb9nHpDjGoH0zq1uLnbUR_2edqqFpL5ZQ-QUIIVjqTUTJWDdzIvseY0uqlQt18haFyaIcWWRD--l5tXga787BaJ1gJZHIoEnu6cHse_KFQpqG8F-4qL_1oVDCOw7KMshiO2_TnaUYU-9TH1Eim2Hk0qhPW7bCjgrQR3uA0hQ57nAXl13GCpuSDlqWe-Ogu2Q5h7_ANHs8uf2I0Ffv0sUKFuFWPKGjbmH4xzQvT_ykTfviFjA_ireXeD7SjjPpdUgmqkPGCc1jxDE-GsTTSBc0uX-sSChzk_ZBS3VyceY8vJNMb6bgfTa1bTQVRHd7tFvz7XHdPJkL2s0P-q---Pqk7fI7KE5eCXfHPCmKuO1JUK6qqQlWYiFj_iK356cae6S9uLBoVgchmy692NeCP27WzzGybVh00nqWgy2ZuJBlzEB12Dr7dUZQ.qS4EUkUGajZBO0F8YMAf7A; iabbb_auth_username=fb5b3736-6515-4828-ad49-bf9570ce1290; iabbb_auth_access_token=eyJraWQiOiJsbEFpM3V6VG5jbXltaUFieDR4WFhlVFE3VDQ2SDRCdno0RWFONm90UXhjPSIsImFsZyI6IlJTMjU2In0.eyJvcmlnaW5fanRpIjoiOGI2NzAwYmYtMzI1OC00OTYwLTg1ZmItODYxYTBiN2I4ZWM5Iiwic3ViIjoiYjQwODk0YjgtMDBkMS03MDZlLTRlMTYtNTAzYWM0OWNlNTNmIiwidG9rZW5fdXNlIjoiYWNjZXNzIiwic2NvcGUiOiJhd3MuY29nbml0by5zaWduaW4udXNlci5hZG1pbiIsImF1dGhfdGltZSI6MTc3NDI0NzA1OCwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfWkNpMGMxMTNXIiwiZXhwIjoxNzc0MjUwNjU4LCJpYXQiOjE3NzQyNDcwNTgsImp0aSI6IjRhMTdkOWU1LTczZWQtNGU2Ni05OWRkLTQzMDMxYjg0MDExMCIsImNsaWVudF9pZCI6IjdxbDAxczRiZ2ZqNmxobW5zYWVrc2J1YWY2IiwidXNlcm5hbWUiOiJmYjViMzczNi02NTE1LTQ4MjgtYWQ0OS1iZjk1NzBjZTEyOTAifQ.omCnY1mE4l-r2Mai_QcoqZVm-j1iih55a1Xj5rAuGaMtD8gU1Xm2M5b3DVLfi4_WstdJq4YdWD4up35PM6GCpgZ1z8MgzNHXS0kKES_EI5zCMAoXTkvWt0eWrB28Bnv46Egg5O7SIWaN0lL5A9HSr_iSV3GzBS0HO27nZvGqKczyB2FQGizheHMmRZrAyo61GCKEi98j1wDx-WhfmCXg_pyRHvIv9gcLO9qhTAdZctizODCeToXr0zYCqJ_N8K6Glw-aSO4G4ZiK877mWZhHJV93NKs4L0J3IsVgZOH9PUqHzfMs-RiBaN1dbeyJmbdemN_zTDxPoxAeX833VABu2Q; iabbb_auth_user_data=eyJhbGciOiJIUzI1NiJ9.eyJkYXRhIjp7ImlkIjo2Mzk1MDksImVtYWlsIjoiZGV2aWx2YW5ldmFkQG91dGxvb2suY29tIiwidXNlcnR5cGUiOiJjb25zdW1lciIsIm5hbWUiOiJFdmFkIERldmlsdmFuIiwiY29uZmlybWF0aW9uc3RhdHVzIjoiRVhURVJOQUxfUFJPVklERVIiLCJzdGF0dXMiOiJBQ1RJVkUiLCJhZGRyZXNzIjoiIiwic2Vjb25kYXJ5ZW1haWxzIjpudWxsLCJiaWRzIjpbXX0sImhhc011bHRpcGxlQmlkcyI6ZmFsc2UsImZpcnN0QmlkIjpudWxsLCJpYXQiOjE3NzQyNDcwNTh9.upTIfPhj8kSbd38YGlclLpQv0JtcRrKOFfGGpZ4ANpQ; iabbb_auth_multiple_bids=false; iabbb_auth_login_hint=true; _gcl_au=1.1.272628837.1774233283.415972280.1774247063.1774247063; _ga_MJQ72F5ZG5=GS2.1.s1774246086$o2$g1$t1774247086$j54$l0$h0; s_tp=5290; gpv_PageUrl=https%3A%2F%2Fwww.bbb.org%2Fus%2Fny%2Fnew-york%2Fprofile%2Fmattress%2Feight-0121-166228%2Fcustomer-reviews; _ga_MP6NWVNK4P=GS2.1.s1774246072$o3$g1$t1774247593$j59$l0$h0; _ga_QWV3Q1HBDG=GS2.1.s1774246072$o3$g1$t1774247593$j59$l0$h0; s_ips=1261; CF_Authorization=eyJraWQiOiJkYjM1ZGI4Zjc4MzljNjk2ODE5ZjVhOGE0NWU4N2ZlM2VjNGQwZDFiOTY2YmI4ZDYxYThkMjY4OTAxZDNjYWE5IiwiYWxnIjoiUlMyNTYiLCJ0eXAiOiJKV1QifQ.eyJ0eXBlIjoiYXBwIiwiYXVkIjoiN2M1ZDY4NTRhZGIxNGU4NWFiMWQxZDhlNTUwZmRjZGQ4MDI1MDkyNmQyNjgzNjM2MTcxNGNlZDlkNzMyMTg5ZSIsImV4cCI6MTc3NDMzMzk5MSwiaXNzIjoiaHR0cHM6XC9cL2lhYmJiLmNsb3VkZmxhcmVhY2Nlc3MuY29tIiwiY29tbW9uX25hbWUiOiJhNTBlNTEzYjFjNzM0ZjFhMjJmZDJlN2ZkMjI5ZmI4NC5hY2Nlc3MiLCJpYXQiOjE3NzQyNDc1OTEsInN1YiI6IiJ9.prcdrNiUABy49_Y0mDkkb2vTwSWpwbG9R83ik0duUqUOuMH2mQWj8OVEb1AljkPNYyejtH6Sglf9yujY-SCvp2XPRvOAGnzjd_c_goWuup9dOEb9AShg2a29soKCGAuhHQpXhBfjv4knrtEyH1OoxN-sv3wOy1vbmlCK59npoH-LRO37dqeZkm3ystuzBfdjaqsHOrGbhrZIGtKu7whDfjVgBl0Svu-SpGlS2pjXl-lEHO78RMMn2bYbCMGdk7OGd2e5PnJn7T58JO1lgSs6_Bm2YE6NULg22xPDx9wbM7HiHMYNu3VwogijOKnVD_tLj0Vj8S2PoD0Bnk6BOb97vQ; s_ppv=eight%2520%257C%2520bbb%2520reviews%2520%257C%2520better%2520business%2520bureau%2C100%2C24%2C5290%2C14%2C14; _ga_PKZXBXGJHK=GS2.1.s1774246072$o3$g1$t1774247671$j60$l0$h0; s_nr30=1774247673972-Repeat; s_sq=cbbbproduction%3D%2526c.%2526a.%2526activitymap.%2526page%253Deight%252520%25257C%252520bbb%252520reviews%252520%25257C%252520better%252520business%252520bureau%2526link%253DPage%2525202%2526region%253Dcontent%2526pageIDType%253D1%2526.activitymap%2526.a%2526.c%2526pid%253Deight%252520%25257C%252520bbb%252520reviews%252520%25257C%252520better%252520business%252520bureau%2526pidt%253D1%2526oid%253Dhttps%25253A%25252F%25252Fwww.bbb.org%25252Fus%25252Fny%25252Fnew-york%25252Fprofile%25252Fmattress%25252Feight-0121-166228%25252Fcustomer-reviews%25253Fpage%25253D2%2526ot%253DA%26cbbbstaging%3D%2526c.%2526a.%2526activitymap.%2526page%253Dbbb%252520account%2526link%253DGet%252520a%252520Quote%2526region%253DBODY%2526pageIDType%253D1%2526.activitymap%2526.a%2526.c%2526pid%253Dbbb%252520account%2526pidt%253D1%2526oid%253Dhttps%25253A%25252F%25252Fwww.bbb.org%25252Fget-a-quote%2526ot%253DA");
//            connect.header("Origin", "https://www.bbb.org");
            connect.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0");
            connect.header("Referer", "https://www.bbb.org/us/ny/new-york/profile/mattress/eight-0121-166228/customer-reviews");
            connect.header("sec-fetch-storage-access", "active");
            Element body = null;
            try {
                body = connect.get().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (body != null) {
                Elements elements = body.select(".card");
                if (UEmpty.isNotEmpty(elements)) {
                    for (Element element : elements) {
                        System.out.println(element.text());
                    }
                }
            }
        }
    }

    @Test
    public void testBBBV2() {
        String targetUrl = "";
        for (int i = 1; i <= 2; i++) {
            targetUrl = "https://www.bbb.org/us/ny/new-york/profile/mattress/eight-0121-166228/customer-reviews?page=" + i;
            Connection connect = Jsoup.connect(targetUrl)
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)))
                    .timeout(60000)
                    .maxBodySize(0);
        }
    }
}