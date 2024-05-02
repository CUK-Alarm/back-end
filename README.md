# 🔜 CUK 알리미
**가톨릭대학교 공지사항 알리미입니다.<br>
아침에 전날 공지사항을 다시 한번 알려드립니다. <br>
학교 전체 공지사항은 물론 각 전공에 맞는 공지사항을 알려드립니다!**
<br><br>

# 👨🏻‍💻 Contributors
|  <div align = center>조현태 </div> |
|:----------|
|<div align = center> <img src = "https://oopy.lazyrockets.com/api/v2/notion/image?src=https%3A%2F%2Fnoticon-static.tammolo.com%2Fdgggcrkxq%2Fimage%2Fupload%2Fv1567128822%2Fnoticon%2Fosiivsvhnu4nt8doquo0.png&blockId=865f4b2a-5198-49e8-a173-0f893a4fed45&width=256" width = "17" height = "17"/> [hyuntae99](https://github.com/hyuntae99)| </div> 
<br>

## 📖 Development Tech
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white">
<img src="https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white">
<br><br>

# 💼 Server Architecture
<img src="https://velog.velcdn.com/images/jmjmjmz732002/post/a6c7a7be-ff27-4723-bfe2-d458ed641fab/image.png">
<br><br>

# 🗂️ Directory
```
├── java
│   └── com
│       └── hyunn
│           └── alarm
│               ├── AlarmApplication
│               ├── controller
│               │   ├── KakaoLoginController
│               │   ├── MainController
│               │   └── UserController
│               ├── dto
│               │   ├── Request
│               │   │   ├── UserDepartmentRequest
│               │   │   └── UserEmailRequest
│               │   └── Response
│               │   │   ├── ApiStandardResponse
│               │   │   ├── ErrorResponse
│               │   │   └── UserResponse
│               ├── entity
│               │   │── BaseEntity
│               │   │── Department
│               │   └── User
│               ├── exception
│               │   │── ApiKeyNotValidException
│               │   │── ApiNotFoundException
│               │   │── ErrorStatus
│               │   │── GlobalExceptionHandler
│               │   │── UnauthorizedException
│               │   └── UserNorFoundException
│               ├── repository
│               │   │── DepartmentJpaRepository
│               │   └── UserJpaRepositoty
│               └── service
│                   ├── KakaoLoginService
│                   ├── MessageService
│                   ├── UserService
│                   └── WebCrawlerService
└── resources
    ├── config 
    │   └── application-local.yml
    ├── static
    │   ├── cuk_logo.png
    │   ├── index.html
    │   └── main_background.jpg
    ├── templates
    │   ├── background.jpg
    │   ├── main.html
    │   ├── modify.html
    │   ├── setting.html
    │   └── withdraw.html
    └── application.yml
```

# 📝 Service

**동영상을 GIF로 변환하는 과정에서 부득이하게 화질이 낮아진 점 먼저 양해 구합니다.**

## 1. 로그인 및 회원가입

카카오 SNS 로그인을 제공합니다.
<img src= "https://velog.velcdn.com/images/hyuntae99/post/7b86b083-15d9-441c-946d-de82cd4a840b/image.gif">
<br><br>

## 2. 메인 페이지

현재 로그인된 정보를 알려줍니다. (카카오 로그인 기반 이메일과 전화번호)

<img src="https://github.com/CUK-Alarm/back-end/assets/101180610/5b9be203-bac9-41f8-940b-d96d9d002781" width="800" height="400">
<br><br>

사용 중인 유저의 정보는 알림으로 어떤 전공을 선택했는지도 알 수 있습니다.

<img src="https://github.com/CUK-Alarm/back-end/assets/101180610/b2b29d42-4f58-445e-8d6d-8b2dbadc8c76" width="800" height="400">
<br><br>

## 3. 문자를 통한 인증
문자를 통해서 인증번호를 발송합니다.<br>
인증번호는 인증 버튼을 누를 때마다 임의의 6자리로 생성됩니다. <br>
또한 제한된 시간이 지나면 인증번호는 폐기되며 새로 인증번호를 받아야합니다. <br>
해당 로직은 아래 "알림 설정", "이메일 변경", "회원 탈퇴"에서 사용됩니다.

<img src= "https://velog.velcdn.com/images/hyuntae99/post/c408e2a8-fb75-4eef-ad8f-e4bda782d79c/image.gif" > 
<br>

아래와 같이 핸드폰으로 인증번호를 전송합니다.<br>
<img src = "https://github.com/hyuntae99/CUK-Alarm/assets/101180610/3a217ad8-f7ba-4401-8345-c50339872882" width="400" height="200">
<br><br>

## 4. 알림 설정
기본적으로 학교 전체 공지사항은 기본적으로 모두 알려드립니다. (학사 / 장학 / 일반 / 취창업) <br>
또한 설정을 통해서 각 전공에 맞는 추가 공지사항도 추가로 알려드립니다.

<img src= "https://velog.velcdn.com/images/hyuntae99/post/ba900e03-c4ad-43ad-a322-27457379eaa1/image.gif"> 
<br><br>

## 5. 이메일 변경
카카오 로그인에서 받았던 기본 이메일 대신 유저 원하는 이메일로 변경 가능합니다.

<img src= "https://velog.velcdn.com/images/hyuntae99/post/7d84e5c0-01bd-4136-86b7-3c9ebc5ba6f0/image.gif">
<br><br>

## 6. 회원 탈퇴
유저가 유저 DB에서 삭제됩니다.

<img src= "https://velog.velcdn.com/images/hyuntae99/post/35fafcf0-b1b9-440e-895f-fe9a024bd623/image.gif">
<br><br>

## 7. 서비스 이용 (메일, 문자)
서비스에 등록한 유저는 매일 오전 9시에 전날 공지사항을 받을 수 있습니다. <br>
아래와 같이 어느 파트에서 어떤 공지사항이 있었는지 알 수 있으며 <br>
각 유저 알림 설정에 따라 전날 공지가 하나도 없었다면 알림 자체를 발송하지 않습니다.

<img src="https://github.com/CUK-Alarm/back-end/assets/101180610/d6b7cccd-4dfd-4b71-8d4c-b2d2629b7578" width="400" height="400"> <img src="https://github.com/CUK-Alarm/back-end/assets/101180610/75a1491b-4058-4574-bfb0-a26090db043d" width="400" height="400">
<br><br>

## 8. 개발자 모드 (유료 서비스 이용)
공지사항을 메세지로 보내기에는 API 가격이 부담되어 특정 계정에만 사용하도록 했습니다.<br>
특정 코드를 입력하면 루트 계정으로 변환되어 문자 메세지로도 알림을 받을 수 있습니다.

<img src="https://github.com/CUK-Alarm/back-end/assets/101180610/801f65d3-b6ea-4118-9ea1-590e0a897502" width="800" height="400">

<img src="https://github.com/CUK-Alarm/back-end/assets/101180610/b6e3ad72-8d31-401a-a3b7-f1a751857c72" width="800" height="400">


루트 계정으로 변환되면 플러스 요금제로 변경됩니다. (문자로 알림을 받을 수 있습니다.)

<img src="https://github.com/CUK-Alarm/back-end/assets/101180610/467482b0-0fde-4a15-a0b6-f95a5a6e2bb9" width="800" height="400">
<br<br>

# 🎞️ 느낀점
**1. 지금까지 주로 사용한 인증방식은 유저만 알 수 있는 고유한 정보를 2개 이상 받는 방법이었습니다만 <br>
입력할 정보가 많아져서 불편함을 느낄 수도 있고 개인정보가 알고 있는 타인이 쉽게 API에 접근할 수 있었습니다. <br>
하지만 이번에는 핸드폰이라는 통신 기기를 통해서 인증을 진행했기 때문에 안전성이 높아진 것 같습니다. <br>**

**2. 다른 프로젝트는 프론트 개발자와 함께 하거나 JSP를 거의 사용하지 않는 기본적인 웹페이지만 사용했었는데 <br>
이번 프로젝트는 혼자 진행했기 때문에 이번 기회에 JSP에 대한 이해가 조금 늘었다고 생각합니다. <br>
또한 REST API와 API의 차이점과 Model에 대해서 많이 공부했습니다. <br>**

**3. 제가 개발한 REST API를 사용해서 웹페이지에 나타내는 작업을 통해 프론트 개발자분들의 불편을 알게 되었고 <br>
Response 구조를 이해하기 쉽고 통일되게 만들어야 한다는 것을 다시 한번 생각하게 되었습니다. <br>
특히 API를 만들 때, Request와 Response에 대해서 좀 더 신중하게 고민해야겠다는 반성도 했습니다. <br>**

