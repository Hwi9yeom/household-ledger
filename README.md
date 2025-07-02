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

