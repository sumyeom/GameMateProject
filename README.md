
<br>

# 👀  ![image](https://github.com/user-attachments/assets/e3bfae8c-8698-4b6f-ab7a-5fe5d3ccd9e8) 👀

### 다양한 게임 정보와 리뷰를 공유하고 게임을 같이 할 친구를 찾을 수 있는 커뮤니티 사이트
<br>

## 👨‍👩‍👧‍👦 Our Team

|이예지|전수연|고강혁|양제훈|
|:---:|:---:|:---:|:---:|
|[@yeji-world](https://github.com/yeji-world)|[@sumyeom](https://github.com/sumyeom)|[@Newbiekk](https://github.com/Newbiekk-kkh)|[@89JHoon](https://github.com/89JHoon)|
|BE|BE|BE|BE|

<br>

## 프로젝트 기능

### 🛡 OAuth2 소셜로그인 (kakao, google)

> * Kakao와 google 통한 간편 로그인이 가능합니다.

### 📧 이메일 인증

> * 이메일 인증 기능을 지원합니다.


### 👥 게임 매칭 시스템 제공
 
> * 현재 '리그오브레전드' 라는 게임에 대해 매칭 시스템을 통해 친구를 구할 수 있습니다.
> * 내 정보를 입력하고, 원하는 상대방의 조건을 입력하여 매칭 로직을 통해 최대 5인의 추천을 받을 수 있습니다.


### 🎮 게임 정보 확인
 
> * 다양한 게임에 대한 정보를 얻을 수 있고, 각 게임에 대해 사용자들이 작성한 리뷰를 확인 할 수 있습니다.


### ❗️ 게임 추천
 
> * 내가 원하는 성향을 가진 게임을 추천하는 서비스를 제공합니다.
> * Gemini API 사용해 사용자가 입력한 내용을 기반으로 게임을 추천해줍니다.


### 📖 게임 커뮤니티
 
> * 게임에 대한 게시글을 작성하고, 게시글에 댓글 대댓글을 작성할 수 있습니다.
> * 조회수 상위 5개의 게시글을 오늘의 게시글로 선정해 사용자에게 제공합니다.


### 👨‍👩‍👦 소셜 기능

> * 사용자간의 팔로우 기능을 제공합니다.
> * 게시물 / 게임 리뷰에 '좋아요' 또는 '싫어요'를 달 수 있습니다.

### 🔔 실시간 알림 기능

> * 알림은 SSE를 통해 실시간으로 제공됩니다.
> * 매칭 / 팔로우 / 댓글 / 좋아요 등의 이벤트에 대해 알림이 제공됩니다.

### 🎫 쿠폰 기능

> * 자체 쿠폰을 발급해, 사용자들에게 여러 혜택들을 제공합니다.


<br><br>


## 📚 적용 기술

| 분야 | **기술 및 도구** | **목적** |
| --- | --- | --- |
| **애플리케이션 개발** | JDK 17<br>Spring Boot 3.4.1<br>IntelliJ IDEA | 1️⃣ **JDK 17**<br> - 성능, 보안, 개발 효율성을 위한 안정적인 운영 환경<br>2️⃣ **Spring Boot 3.4.1**<br> - 프로덕션 환경에서의 안정성과 클라우드 네이티브 기능 강화<br>3️⃣ **IntelliJ IDEA**<br> - 개발자 생산성을 극대화하는 강력한 통합 개발 환경(IDE) |
| **인증 / 인가** | Spring Security<br>JWT<br>OAuth 2.0 | 1️⃣ **Spring Security**<br> - 웹 애플리케이션 보안 통합 관리<br>2️⃣ **JWT**<br> - 무상태(Stateless) 인증 구현<br>3️⃣ **OAuth 2.0**<br> - 제3자 서비스의 안전한 자원 접근 관리 |
| **협업** | Git<br>GitHub<br>Slack<br>Notion<br>GitHub Project / Issue<br>Trello<br>WBS | 1️⃣ **Git & GitHub**<br> - 코드 협업과 버전 관리<br>2️⃣ **Slack & Notion**<br> - 진행 사항에 대해 소통하고, 문서화<br>3️⃣ **GitHub Project / Issue & Trello & WBS**<br> - 요구 사항에 대한 일정 및 우선순위 파악, 정리<br> - 각 일정에 대한 진행 사항 체크 |
| **Database** | RDS(MySQL)<br>Redis | 1️⃣ **RDS(MySQL)**<br> - RDS를 통해 DB를 위한 인프라 구축<br> - 안정적인 관계형 DB로 데이터 관리<br>2️⃣ **Redis**<br> - 게시글 조회수 카운팅을 캐시를 활용하여 효과적으로 관리<br> - Redisson 기반 분산 락을 통해 데이터 일관성 유지<br> - Redis Stream을 사용해 다중 WAS 환경에서도 알림이 유실되지 않고 전달되도록 관리 |
| **파일 첨부** | AWS S3 | 1️⃣ **AWS S3**<br> - 대용량 데이터를 안정적으로 유지 관리 |
| **CI/CD** | Docker<br>GitHub Action<br>AWS EC2<br>AWS | 1️⃣ **Docker**<br> - 일관된 개발 환경 제공<br> - 배포 및 확장이 용이<br>2️⃣ **GitHub Action**<br> - CI/CD 자동화를 구현하고, 배포 프로세스를 효율적으로 관리<br>3️⃣ **AWS**<br> - 클라우드 환경에서 안정적인 서비스 운영 |
| **외부 API** | Gemini API<br>Google OAuth 2.0 API<br>Kakao OAuth 2.0 API<br>JavaMail | 1️⃣ **Gemini API**<br> - 신뢰성 있는 게임 추천을 위한 API<br>2️⃣ **OAuth 2.0 API (Google & Kakao)**<br> - 소셜 로그인을 위한 API<br>3️⃣ **JavaMail**<br> - 메일 발송을 위한 API |

<br><br>

## 📝 기술적 의사 결정

<details>
<summary> Gemini AI 활용 방안 </summary>

## 1. 배경
- 게임 추천 시스템은 단순한 게임 선택을 넘어 **사용자 경험 설계**부터 **윤리적 검증**까지 종합적인 접근이 필요함.
- 플레이어의 **세션 데이터**를 지속적으로 학습에 반영하면서도, 추천의 **근거를 투명하게 제시**하는 것이 장기적 신뢰 확보의 핵심.
- AI를 이용해 게임을 추천하려면 다음 요소들을 종합적으로 분석해야 함:
  - **사용자 취향** (장르, 플레이 스타일)
  - **보유 기기** (PC, 콘솔, 모바일)
  - **최신 트렌드**
  - **소셜 데이터** (친구, 스트리머 추천)
  - **추천 알고리즘** (콘텐츠 기반, 협업 필터링)
  - **할인 정보, 연령 등급** 등의 요소 고려
- 또한, **사용자 피드백을 반영**하여 지속적으로 추천 시스템을 개선하는 것이 중요함.

## 2. 선택 이유
- **구글의 Gemini AI**는 **멀티모달 기능**(텍스트, 이미지, 음성 등)을 지원하며, 강력한 **자연어 처리(NLP)**와 **데이터 분석 기능**을 제공.
- Gemini AI를 활용하면 단순한 게임 추천이 아니라 **사용자와 대화하며 취향을 파악하고 맞춤형 추천을 제공하는 AI 시스템**을 구축 가능.
- 특히, **Google Cloud의 API**와 결합하면 더 정교한 추천 모델을 구축할 수 있어 지속적인 발전이 기대됨.
- 결론적으로, **Gemini AI를 활용하면 더욱 정밀하고 개인화된 게임 추천 시스템 구축**을 기대할 수 있음.

## 3. 대안 비교
| 대안 | 장점 | 단점 |
| --- | --- | --- |
| **Microsoft Azure OpenAI + Game Pass 데이터 연계** | - **Xbox Game Pass 데이터**와 연계 가능<br>- **클라우드 기반 확장성** 우수 | - **Microsoft 생태계** 내에서 활용도가 높음<br>- **범용적인 추천 시스템 구축에는 한계** |
| **Claude (Anthropic)** | - **긴 문맥을 유지하는 대화 능력** 우수<br>- **친환경적이고 안전한 AI 설계** | - **게임 추천 알고리즘 구축에는 다소 한계** |
</details>

<details>
<summary> AWS S3 활용 방안 </summary>

## 1. 배경
- 커뮤니티에서 **첨부파일(이미지) 관리**는 여러 측면에서 중요함:
  - **사용자 경험(UX) 향상**: 이미지가 포함된 게시글은 가독성을 높이고 몰입감을 제공.
  - **서버 성능 최적화**: 저장 공간과 로딩 속도를 고려한 최적화 필요.
  - **보안 강화**: 불법 콘텐츠 필터링 및 악성 코드 방지를 위한 보안 조치 필수.
  - **검색 최적화**: SEO 최적화를 통해 접근성을 높일 수 있음.
  - **모바일 대응**: 다양한 네트워크 환경에서도 원활한 사용 가능.
- 효과적인 이미지 관리를 위해 **이미지 압축, AI 필터링, CDN 활용** 등의 전략이 필요하며, 이를 잘 적용하여 **원활한 커뮤니티 운영**을 목표로 함.

## 2. 선택 이유
1. **대용량 이미지 저장 가능**  
2. **빠른 이미지 로딩 속도**  
   - 전 세계 어디에서든 **빠르게 로딩 가능**  
   - 모바일 및 저속 인터넷 환경에서도 **원활한 서비스 제공**  
3. **데이터 손실 안전 및 보안 강화**  
   - **자동 데이터 복제**: 여러 데이터 센터에 복제 저장하여 **데이터 손실 방지**  
   - **접근 제어 가능**: 퍼블릭/프라이빗 설정을 통해 특정 사용자만 접근 가능  
4. **비용 절감 효과**  
   - **사용량 기반 과금**으로 불필요한 비용 절감  
5. **결론**  
   - S3를 사용하면 **이미지 저장이 편리하고, 로딩 속도가 빠르며, 데이터 손실 위험이 낮고, 보안이 뛰어나며, 비용 절감 가능**  
   - 특히, **많은 사용자가 이미지를 업로드하고 조회하는 커뮤니티 게시판에 적절한 솔루션**  

## 3. 대안 비교
| 대안 | 장점 | 단점 |
| --- | --- | --- |
| **Azure Blob Storage** | - 마이크로소프트 환경과 연계 용이 | - AWS 대비 **확장성과 글로벌 커버리지 부족**<br>- 관리 콘솔이 다소 복잡 |
| **Firebase Storage** | - **모바일 앱과의 연동 최적화** | - **대량 이미지 저장보다는 모바일 앱 중심**<br>- **데이터 관리 기능이 제한적** |
</details>

<details>
 <summary>SSE(Server-Sent Events) 기반의 실시간 알림 시스템</summary>
 
## 1. SSE 도입 배경
- 초기 알림 기능은 **스케줄러를 활용하여 5분마다 미확인된 알림을 이메일로 전송**하는 방식으로 구현됨.  
- 그러나 이러한 방식은 **실시간성이 부족하여 즉각적인 알림 제공이 불가능**했음.  
- 이를 개선하기 위해 **SSE(Server-Sent Events) 기반의 실시간 알림 시스템**을 도입하게 됨.

## 2. SSE 선택 이유
- 실시간 알림을 구현하기 위해 **Short Polling, Long Polling, SSE, WebSocket** 등 다양한 기술을 검토한 결과, **SSE가 가장 적합**하다고 판단됨.
- **SSE(Server-Sent Events) 장점**  
  ✅ **단방향(one-way) 연결**을 통해 **서버에서 클라이언트로 실시간 알림 전송**  
  ✅ 기존 **HTTP 프로토콜**을 활용하므로 **설정이 간편**  
  ✅ WebSocket과 달리 **재연결(자동 복구) 기능 내장**  
  ✅ 알림과 같이 **단방향 전송이 주를 이루는 서비스에 적합**  

## 3. 대안 비교

| 기술 | 동작 방식 | 장점 | 단점 |
| --- | --- | --- | --- |
| **Short Polling** | 클라이언트가 일정 주기마다 서버에 요청 | 구현이 간단함 | 불필요한 요청 증가로 리소스 낭비 |
| **Long Polling** | 서버가 새로운 데이터가 있을 때까지 응답을 지연 | 실시간성 개선 | 다수의 연결 유지 시 서버 부담 증가 |
| **WebSocket** | 클라이언트-서버 간 **양방향 연결 유지** | 쌍방향 통신 가능 | 설정이 복잡하며, HTTP/2 환경에서 오버헤드 증가 가능 |

## 4. 결론
- 알림 서비스는 **서버에서 클라이언트로 단방향 메시지 전송이 주된 역할**을 함.  
- WebSocket의 **양방향 연결 기능이 불필요**하며, 구현이 간단한 **SSE가 최적의 선택**이었음.  
</details>
<details>
<summary> 알림 시스템에 Redis Stream 적용</summary>

## 메시지 영속성과 신뢰성 확보

* 기존 SSE만 사용할 경우 네트워크 문제나 서버 장애 시 알림이 유실될 수 있음
* Redis Stream은 알림 메시지를 일시적으로 저장하고 관리하여 메시지 전달의 신뢰성을 높임

## 장애 상황 대응

* 사용자의 네트워크 연결이 끊기거나 서버에 문제가 생겼을 때도 Stream에 메시지가 보관되어 있어 재접속 시 미전송된 알림을 처리할 수 있음

## 시스템 확장성

* 향후 시스템이 확장되어 다중 WAS 환경에서 알림을 처리해야 할 경우 Redis Stream을 통해 메시지 큐 역할을 수행하여 분산 시스템에서도 안정적인 알림 처리가 가능

## 대안 비교

| 기술 | 동작 방식 | 장점 | 단점 |
|------|-----------|------|------|
| **Redis Pub/Sub** | 구독자가 있으면 실시간으로 메시지 전송 | 빠른 실시간 메시징 | 구독자가 없으면 메시지 유실 |
| **Kafka** | 로그 기반 스트리밍 | 강력한 메시지 보장, 대량 데이터 처리 가능 | 설정이 복잡하고 운영 비용이 높음 |
| **Redis Stream** | 메시지 저장 + 스트리밍 | 메시지 유실 방지, 소비자 그룹 지원 | Pub/Sub보다 약간의 설정 필요 |

## 결론

알림 서비스에서는 **메시지 유실 방지가 중요**하므로, 단순 Pub/Sub보다 **Redis Stream이 적합**했습니다. Kafka는 강력한 기능을 제공하지만, 운영 복잡성과 비용 문제 및 대량 데이터 처리가 현재 레벨에서 필요하지 않을 것으로 예상되어 오버 엔지니어링 같았습니다. 이러한 이유들로 Redis Stream을 선택했습니다.
<br><br>
</details>


## 🚨 Trouble Shooting

<details>
 <summary> 게임추천- 프롬프트 관련 보안 위험 </summary>

 ## 문제인식

게임 추천 기능중 사용자에게 데이터를 응답 받는 중 SQL 인젝션 위험이 예상됨

-   **보안 위험 분석**

    ```java
    String prompt = String.format("...추가적인 요청은 %s 야", 
    userGamePreference.getExtraRequest());
    ```

    -   **문제점**: **`extraRequest`**가 프롬프트에 직접 삽입되어 악성 코드 실행 가능
    -   **위험**: 프롬프트 조작을 통한 시스템 명령어 주입, 데이터 유출 등의 공격 가능성

-   **개선 방안**

    -   **OWASP 인코딩 라이브러리 적용**

        ```java
        import org.owasp.encoder.Encode;

        String safeExtraRequest = Encode.forJava(extraRequest);
        ```

        -   **기능**: 특수 문자 자동 이스케이프
        -   **예방 공격**: XSS, 명령어 주입
    -   **Bean Validation 통합**

        ```java
        public class UserGamePreferenceRequestDto {

            @Size(max = 100, message = "추가 요청은 100자 이내로 입력해주세요")
            @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "특수 문자는 사용할 수 없습니다")
            private String extraRequest;
        }

        public class GameRecommendContorller {
         @PostMapping
            public ResponseEntity<UserGamePreferenceResponseDto> createUserGamePreference(
                    @Valid @RequestBody UserGamePreferenceRequestDto requestDto,
                    @AuthenticationPrincipal CustomUserDetails customUserDetails
            ) {
        ```

        -   **장점**: 선언적 검증 규칙 관리

-   **기대 효과**

    -  입력 데이터의 무결성과 유효성이 크게 향상되어, 악의적인 데이터 주입 시도를 사전에 차단할 수 있음
    -  SQL Injection과 같은 데이터베이스 공격 위험이 현저히 감소하여, 데이터베이스의 보안성이 향상
    -  사용자 경험 측면에서도 개선이 이루어져, 유효하지 않은 데이터 입력에 대한 즉각적인 피드백을 제공함으로써 사용자 친화적인 인터페이스를 구현할 수 있음

</details>

<details>
 <summary> 특정 유저의 팔로워 조회 시 성능 최적화 </summary>

 ## 🔍 기존 코드 문제점

```java
public List<FollowFindResponseDto> findFollowers(String email) {
    User followee = userRepository.findByEmail(email)
        .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

    if (followee.getUserStatus() == UserStatus.WITHDRAW) {
        throw new ApiException(ErrorCode.IS_WITHDRAWN_USER);
    } // 탈퇴한 회원 예외 처리

    List<Follow> followListByFollowee = followRepository.findByFollowee(followee);

    List<User> followersByFollowee = followListByFollowee.stream()
        .map(Follow::getFollower)
        .filter(follower -> follower.getUserStatus() != UserStatus.WITHDRAW)
        .toList();

    return followersByFollowee.stream()
        .map(FollowFindResponseDto::toDto)
        .toList();
}
```

위 코드의 **문제점은 FetchType.LAZY로 인해 N+1 문제가 발생**한다는 것입니다.

- `Follow` 엔티티에서 `getFollower()`를 호출할 때, **각 팔로워에 대한 별도의 쿼리**가 실행됨
- 결과적으로 팔로워가 1,000명일 경우 **총 1,002개의 쿼리**가 발생 (1개의 사용자 조회 + 1개의 팔로우 리스트 조회 + 1,000개의 팔로워 조회)

---

## 💡 해결 방법: JPQL을 활용한 성능 최적화

이 문제를 해결하기 위해 **JPQL을 사용하여 한 번의 쿼리로 필요한 데이터를 가져오도록 수정**하였습니다.

```java
@Query("SELECT NEW com.example.gamemate.domain.follow.dto.FollowFindResponseDto(f.follower.id, f.follower.nickname) " +
       "FROM Follow f " +
       "JOIN f.follower " +
       "WHERE f.followee.email = :email " +
       "AND f.follower.userStatus != 'WITHDRAW'")
List<FollowFindResponseDto> findFollowersDtoByFolloweeEmail(@Param("email") String email);
```

✅ **최적화된 코드의 장점**

- **단 2개의 쿼리만 실행됨** → 기존 1,002개 → 2개
- **N+1 문제 해결** → `JOIN`을 통해 한 번에 데이터 조회
- **DTO로 직접 매핑** → 엔티티를 불필요하게 생성하지 않고 바로 DTO로 변환

---

## 📊 성능 테스트 결과

![image (7)](https://github.com/user-attachments/assets/0ca21e4a-33ce-4e8a-9e6c-ea3161099f99)

| 테스트 환경 | 기존 코드 | 최적화된 코드 | 성능 개선율 |
| --- | --- | --- | --- |
| 팔로워 1,000명 기준 | **752ms** (1,002개 쿼리) | **7ms** (2개 쿼리) | **99% 성능 개선** 🚀 |

---

## 🔎 결론

JPQL을 활용한 최적화를 통해:

- N+1 문제를 해결하여 **실행 시간을 752ms에서 7ms로 99% 단축**
- 불필요한 쿼리를 제거하여 **DB 부하를 대폭 감소** (1,002개 → 2개)
- DTO 직접 매핑으로 **메모리 사용량 최적화**

이러한 성능 개선을 통해 팔로워가 많은 사용자의 프로필 조회시에도 빠른 응답 속도를 보장할 수 있게 되었습니다.

</details>

<br><br>


## [📋 ERD Diagram]
## ![📋 ERD Diagram](https://github.com/user-attachments/assets/90506e5f-ecbc-4a9c-b748-02767a68140d)


<br>

## 🌐 Architecture

![image (8)](https://github.com/user-attachments/assets/fa1c41cc-58e4-418d-94aa-02330e0e0ba6)



## 📆 일정 관리 (WBS)
![image](https://github.com/user-attachments/assets/3cbe94f2-3236-470e-8e43-31ceacb65367)


<br>

## 📝 Technologies & Tools 📝

#### Java 17 | SpringBoot 3.4.1 | MySql 8.0 | QueryDSL 5.0

<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/> <img src="https://img.shields.io/badge/SpringSecurity-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white"/> <img src="https://img.shields.io/badge/JSONWebToken-000000?style=for-the-badge&logo=JSONWebTokens&logoColor=white"/> 

<img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"/> <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white"/> <img src="https://img.shields.io/badge/LINUX-FCC624?style=for-the-badge&logo=linux&logoColor=black"/>  <img src="https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=Ubuntu&logoColor=white"/>

<img src="https://img.shields.io/badge/AmazonEC2-FF9900?style=for-the-badge&logo=AmazonEC2&logoColor=white"/> <img src="https://img.shields.io/badge/AmazonS3-569A31?style=for-the-badge&logo=AmazonS3&logoColor=white"/>  

<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"/> <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"/> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"/>  <img src="https://img.shields.io/badge/GithubActions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white"/>  

<img src="https://img.shields.io/badge/IntelliJIDEA-000000?style=for-the-badge&logo=IntelliJIDEA&logoColor=white"/>  <img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white"/> <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white"/> <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"/> <img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white"/>

<br><br><br><br>
