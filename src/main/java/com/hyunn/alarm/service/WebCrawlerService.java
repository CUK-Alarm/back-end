package com.hyunn.alarm.service;

import com.hyunn.alarm.entity.Department;
import com.hyunn.alarm.entity.User;
import com.hyunn.alarm.exception.ApiNotFoundException;
import com.hyunn.alarm.repository.DepartmentJpaRepository;
import com.hyunn.alarm.repository.UserJpaRepository;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WebCrawlerService {

  private final UserJpaRepository userJpaRepository;
  private final DepartmentJpaRepository departmentJpaRepository;
  private final MessageService messageService;

  @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul") // 오전 9시에 실행
  public void crawler() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 시간 형식
    ZoneId koreaZoneId = ZoneId.of("Asia/Seoul"); // 대한민국 시간대
    ZonedDateTime yesterdayDateTime = ZonedDateTime.now(koreaZoneId).minusDays(1); // 전날 날짜
    String yesterdayDateString = yesterdayDateTime.format(formatter); // 시간 형식 적용
    System.out.println(yesterdayDateString);

    // 각 학과에 대해서 웹크롤링 실시
    List<Department> departments = departmentJpaRepository.findAll();
    for (Department department : departments) {
      try {
        crawlWebsite(department, yesterdayDateString);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // 서비스 설정을 완료한 유저들에게 메세지 전송
    List<User> users = userJpaRepository.findAll();
    for (User user : users) {
      try {
        if (!user.getMajor().equals("null")) {
          messageService.sendMessage(user);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 학교 내 모든 공지사항 정보 크롤링
   */
  @Transactional
  public void crawlWebsite(Department department, String currentDateTime) throws IOException {
    String uri = department.getUri();
    department.resetNotification(); // 공지사항 초기화
    departmentJpaRepository.save(department); // 저장
    int currentDate = parseStringDate(currentDateTime); // 현재 시간 가져오기

    // Jsoup을 활용한 크롤링 설정
    Document doc = Jsoup.connect(uri).get();
    Elements elements = doc.select("li:not(:has(div.flag.top))"); // 관련 정보 크롤링
    Elements times = elements.select("div.info_line"); // 업로드 시간 크롤링

    // 크롤링에 실패할 경우
    if (elements.size() == 0) {
      throw new ApiNotFoundException("API 응답이 비어 있습니다.");
    }

    // 크롤링한 모든 원소에 대해서 실행
    for (int i = 0; i < elements.size(); i++) {
      Elements titles = elements.select("div.title_line > div.title > div.text"); // 제목들만 파싱
      String currentTitle = titles.get(i).textNodes().get(0).text().trim(); // 그 중 의미있는 데이터만 파싱
      int date = parseElementDate(times.get(i)); // 시간 데이터 파싱

      // 전날에 대한 공지사항을 모두 취합하여 저장
      if (date <= currentDate + 1) {
        if (date == currentDate + 1) {
          continue;
        } else if (date == currentDate) {
          department.addNotification(currentTitle + "\n");
          departmentJpaRepository.save(department);
        } else {
          break;
        }
      }
    }
  }

  /**
   * String 날짜를 Int로 변환
   */
  public int parseStringDate(String date) {
    if (date == null) {
      throw new ApiNotFoundException("날짜를 알 수 없는 게시글입니다.");
    }

    String[] parts = date.split("[^0-9]+");
    String currentTime = "";
    for (String part : parts) {
      currentTime += part;
    }
    return Integer.parseInt(currentTime.substring(0, 8));
  }

  /**
   * Element 날짜를 Int로 변환
   */
  public int parseElementDate(Element infoLineElement) {
    if (infoLineElement == null) {
      throw new ApiNotFoundException("날짜를 알 수 없는 게시글입니다.");
    }

    String dateText = infoLineElement.selectFirst("div:contains(작성일)").text();
    int index = dateText.indexOf("작성일 : ");
    String date = dateText.substring(index + "작성일 : ".length()).trim();
    String[] parts = date.split("[^0-9]+");
    StringBuilder currentTime = new StringBuilder();
    for (String part : parts) {
      currentTime.append(part);
    }
    return Integer.parseInt(currentTime.substring(0, 8));
  }

}
