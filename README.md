# household-ledger

[CI](https://github.com/Hwi9yeom/household-ledger/actions/workflows/ci.yml/badge.svg)

가계부 프로젝트

Temurin-Java 21, Spring Boot 3.5.3, MySQL

레이어 구조 선택이유. 
- Spring Boot 다시 공부하는 취지라
- 공식 가이드나 여러 튜토리얼들이 레이어 구조라
- 작은 규모 프로젝트라 도메인이 비교적 단순해서
- 아직까지는 각 레이어의 책임이 명확하게 분리되어서
- 레이어별 AOP 적용이 간단해서

나중에 구현하는 기능 더 많아지면 바꿔볼까 고민 해봐야겠다.

Day1. 
성공: Health API, Repo
실패: Green CI

Day2.
성공: Green CI, 도메인 정리, 테스트 코드 작성

Day3.
Github card

Day 4.
Created empty test skeleton for monthly query

Day 5. Monthly query endpoint (GET /entries?month=YYYY-MM) green

Day 9. JWT auth

Day 10. Auth-guard test 작성(401 기대) 







## External Learning
| Date     | Event | Key Takeaway | Next Action                                                          |
|----------|-------|-------------|----------------------------------------------------------------------|
| 25-07-05 | 휴먼아시아 인권아카데미<br>“기업과 인권” | 개인정보 보호·노동권이 스타트업 KPI로 부각 | 서비스 암호화 설계시 ESG 체크리스트 반영                                             |
| 25-07-06 | Christian Dior: Designer of Dreams | ‘일관성 속 변주’ 디자인 원칙 | 대시보드 색·테마 토큰 전략 적용                                                   |
| 25-07-07 | 원티드 프리온보딩 백엔드 코스 – NoSQL(MongoDB)·샤딩 아키텍처 | 단일-노드 RDB → 수평 확장 Doc Store 전환 시 **핫스팟 방지 샤드키 설계**·**일관성 Trade-off** 파악 | 지출 집계(Analytics) 모듈을 MongoDB Aggregation + $match 샤드키 실험 해볼 수 있지 않을까 |
