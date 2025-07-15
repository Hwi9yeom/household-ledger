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

React + TypeScript를 선택한 이유

1. SPA(Single Page Application)의 장점

- 빠른 사용자 경험: 페이지 새로고침 없이 즉각적인 반응
- 로컬 스토리지 활용: 비회원도 데이터를 브라우저에 저장하고 관리
- 오프라인 지원: 인터넷 없이도 기본 기능 사용 가능

2. 간편함

- 컴포넌트 재사용: 입력 폼, 리스트 등을 모듈화
- 상태 관리: React Context로 손쉽게 데이터 흐름 관리 가능
- 타입 안정성: TypeScript로 런타임 에러 방지

3. 확장성

- 모바일 앱 전환: React Native로 쉽게 전환 가능
- PWA 지원: 앱처럼 설치 가능한 웹앱으로 발전 가능
- API 분리: 백엔드와 프론트엔드 독립적 개발/배포

Thymeleaf vs React 비교

Thymeleaf 장점:

-  서버 사이드 렌더링 (SEO 유리)
-  단순한 구조
-  Spring Boot와 통합 용이
-  배포 간편 (하나의 JAR 파일)

React 장점:

-  더 나은 사용자 경험 (빠른 인터랙션)
-  로컬 스토리지 기반 오프라인 모드
-  복잡한 상태 관리 용이
-  모던한 개발 도구 생태계

이 프로젝트에 React 사용 이유

1. 비회원 모드 지원: 로컬 스토리지로 데이터 관리
2. 실시간 업데이트: 수입/지출 추가 시 즉시 반영
3. 복잡한 필터링: 월별, 카테고리별 필터링
4. 차트/그래프: 향후 시각화 기능 추가 용이








## External Learning
| Date     | Event | Key Takeaway | Next Action                                                          |
|----------|-------|-------------|----------------------------------------------------------------------|
| 25-07-05 | 휴먼아시아 인권아카데미<br>“기업과 인권” | 개인정보 보호·노동권이 스타트업 KPI로 부각 | 서비스 암호화 설계시 ESG 체크리스트 반영                                             |
| 25-07-06 | Christian Dior: Designer of Dreams | ‘일관성 속 변주’ 디자인 원칙 | 대시보드 색·테마 토큰 전략 적용                                                   |
| 25-07-07 | 원티드 프리온보딩 백엔드 코스 – NoSQL(MongoDB)·샤딩 아키텍처 | 단일-노드 RDB → 수평 확장 Doc Store 전환 시 **핫스팟 방지 샤드키 설계**·**일관성 Trade-off** 파악 | 지출 집계(Analytics) 모듈을 MongoDB Aggregation + $match 샤드키 실험 해볼 수 있지 않을까 |
