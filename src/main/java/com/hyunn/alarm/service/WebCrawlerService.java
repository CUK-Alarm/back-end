package com.hyunn.alarm.service;

import com.hyunn.alarm.entity.Department;
import com.hyunn.alarm.entity.User;
import com.hyunn.alarm.exception.ApiNotFoundException;
import com.hyunn.alarm.repository.DepartmentJpaRepository;
import com.hyunn.alarm.repository.UserJpaRepository;
import java.io.IOException;
import java.net.SocketException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jsoup.HttpStatusException;
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

  @Scheduled(cron = "* * * * * *", zone = "Asia/Seoul") // 오전 9시에 실행
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
        System.out.println("---- " + department.getMajor() + " ----");
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
    int currentDate = parseStringDate(currentDateTime); // 전날 시간 가져오기

    // Jsoup을 활용한 크롤링 설정
    try {
      Document doc = Jsoup.connect(uri)
          .userAgent(
              "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
          .get();
      Elements tbody = doc.select("tr");

      // <th> 태그가 있는 첫 번째 행을 제거
      if (!tbody.isEmpty() && tbody.first().select("th").size() > 0) {
        tbody.remove(0);
      }

      // 크롤링 후 남은 데이터가 없는 경우는 제외
      if (tbody != null) {
        // 크롤링한 모든 원소에 대해서 실행
        for (Element row : tbody) {
          // 고정 공지사항은 제외
          if (!row.hasClass("b-cate-notice")) {
            String currentTitle = row.select("td.b-td-title a.b-title").text().trim(); // 제목 파싱
            String dateText = "";
            if (department.getMajor().equals("일반") || department.getMajor().equals("학사")
                || department.getMajor().equals("장학") || department.getMajor().equals("취/창업")) {
              dateText = row.select("td:nth-child(4)").text().trim(); // 날짜 파싱
            } else {
              dateText = row.select("td:nth-child(3)").text().trim();
            }
            System.out.println("제목 : " + currentTitle);
            System.out.println("날짜 : " + dateText);

            int date = parseStringDate(dateText); // 날짜 변환

            // 전날에 대한 공지사항을 모두 취합하여 저장
            if (date <= currentDate + 1) {
              // 오늘 공지사항은 내일 아침에 공지
              if (date == currentDate + 1) {
                continue;
              } else if (date == currentDate) { // 전날 공지사항은 추가
                department.addNotification(currentTitle + "\n");
                departmentJpaRepository.save(department);
              } else {
                break;
              }
            }
          }
        }
      }
    } catch (HttpStatusException e) {
      System.err.println("HTTP 오류: " + e.getStatusCode() + " - " + e.getMessage());
    } catch (SocketException e) {
      System.err.println("소켓 오류: " + e.getMessage());
    } catch (IOException e) {
      System.err.println("IO 오류: " + e.getMessage());
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
}