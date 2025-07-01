# 가계부 애플리케이션 도메인 모델 설계

## 1. 핵심 도메인 엔티티

### 1.1 User (사용자)
```
- id: Long
- email: String
- name: String
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

### 1.2 LedgerEntry (가계부 항목)
```
- id: Long
- userId: Long
- entryType: EntryType (INCOME, EXPENSE, SAVING_INVESTMENT)
- transactionType: TransactionType (FIXED, VARIABLE)
- categoryId: Long
- amount: BigDecimal
- date: LocalDate
- description: String
- memo: String
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

### 1.3 Category (카테고리)
```
- id: Long
- userId: Long
- name: String
- type: CategoryType (INCOME, EXPENSE, SAVING_INVESTMENT)
- subType: SubCategoryType
- icon: String
- color: String
- isActive: Boolean
- createdAt: LocalDateTime
```

#### SubCategoryType 상세
- **수입 카테고리**
  - FIXED_INCOME: 고정수입 (월급, 주급, 배당금, 예금이자)
  - VARIABLE_INCOME: 변동수입 (앱테크, 중고거래, 기타)
  
- **지출 카테고리**
  - FIXED_EXPENSE: 고정지출 (통신비, 보험료, 구독료, 월세)
  - VARIABLE_EXPENSE: 변동지출 (쇼핑, 식비, 교통비, 문화)

- **저축/투자 카테고리**
  - SAVING: 저축 (예금, 적금, 비상금)
  - INVESTMENT: 투자 (주식, 비트코인, 부동산, 정기투자)

### 1.4 Budget (예산)
```
- id: Long
- userId: Long
- categoryId: Long (nullable - 전체 예산일 경우)
- period: BudgetPeriod (DAILY, WEEKLY, MONTHLY, YEARLY)
- amount: BigDecimal
- startDate: LocalDate
- endDate: LocalDate
- isActive: Boolean
- createdAt: LocalDateTime
```

### 1.5 RecurringTransaction (정기 거래)
```
- id: Long
- userId: Long
- categoryId: Long
- name: String
- amount: BigDecimal
- frequency: Frequency (DAILY, WEEKLY, MONTHLY, YEARLY)
- dayOfPeriod: Integer (1-31 for monthly, 1-7 for weekly)
- startDate: LocalDate
- endDate: LocalDate (nullable)
- isActive: Boolean
- lastProcessedDate: LocalDate
- nextScheduledDate: LocalDate
```

### 1.6 FinancialGoal (재무 목표)
```
- id: Long
- userId: Long
- goalType: GoalType (SAVING, INVESTMENT, DEBT_PAYMENT)
- name: String
- targetAmount: BigDecimal
- currentAmount: BigDecimal
- targetDate: LocalDate
- monthlyContribution: BigDecimal
- priority: Integer
- isActive: Boolean
- createdAt: LocalDateTime
```

### 1.7 BudgetAlert (예산 알림)
```
- id: Long
- userId: Long
- budgetId: Long
- alertType: AlertType (WARNING, EXCEEDED)
- threshold: Integer (percentage)
- message: String
- isRead: Boolean
- createdAt: LocalDateTime
```

## 2. 핵심 기능

### 2.1 수입 예측
- 고정수입 기반으로 향후 1년간 수입 예측
- 주기별(일/주/월/년) 예상 수입 계산

### 2.2 예산 관리
- 카테고리별/전체 예산 설정
- 주기별(일/주/월/년) 예산 설정
- 실시간 예산 소진율 계산
- 예산 초과 경고 알림

### 2.3 저축/투자 계획
- 수입 대비 저축 가능 금액 자동 계산
- 목표 저축액 설정 시 필요 예산 역산
- 우선순위별 재무 목표 관리

### 2.4 통계 및 분석
- 수입/지출/저축 트렌드 분석
- 카테고리별 지출 패턴 분석
- 예산 대비 실제 지출 비교

## 3. 도메인 규칙

### 3.1 거래 규칙
- 모든 거래는 반드시 카테고리를 가져야 함
- 거래 날짜는 미래 날짜 불가 (정기 거래 제외)
- 금액은 0보다 커야 함

### 3.2 예산 규칙
- 동일 기간에 중복 예산 설정 불가
- 하위 카테고리 예산의 합은 상위 예산을 초과할 수 없음
- 예산 기간은 최소 1일, 최대 1년

### 3.3 정기 거래 규칙
- 종료일은 시작일보다 이후여야 함
- 비활성화된 정기 거래는 자동 생성되지 않음
- 정기 거래 수정 시 이미 생성된 거래는 영향받지 않음

### 3.4 재무 목표 규칙
- 목표 금액은 현재 금액보다 커야 함
- 목표 날짜는 현재보다 미래여야 함
- 우선순위는 1부터 시작하며 중복 불가

## 4. 다음 단계
1. JPA 엔티티 클래스 구현
2. Repository 인터페이스 정의
3. 서비스 레이어 구현
4. REST API 엔드포인트 설계
5. 테스트 케이스 작성