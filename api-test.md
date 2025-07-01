# 가계부 API 테스트 가이드

## 구현된 REST API 엔드포인트

### 1. User API (`/api/users`)
- `GET /api/users` - 모든 사용자 조회
- `GET /api/users/{id}` - 특정 사용자 조회
- `GET /api/users/email/{email}` - 이메일로 사용자 조회
- `POST /api/users` - 사용자 생성
- `PUT /api/users/{id}` - 사용자 수정
- `DELETE /api/users/{id}` - 사용자 삭제

### 2. Category API (`/api/categories`)
- `GET /api/categories` - 모든 카테고리 조회
- `GET /api/categories/user/{userId}` - 특정 사용자의 활성 카테고리 조회
- `GET /api/categories/user/{userId}/type/{type}` - 사용자별 타입별 카테고리 조회
- `GET /api/categories/user/{userId}/subtype/{subType}` - 사용자별 서브타입별 카테고리 조회
- `POST /api/categories` - 카테고리 생성
- `PUT /api/categories/{id}` - 카테고리 수정
- `DELETE /api/categories/{id}` - 카테고리 비활성화
- `DELETE /api/categories/{id}/hard` - 카테고리 완전 삭제

### 3. LedgerEntry API (`/api/ledger-entries`)
- `GET /api/ledger-entries` - 모든 거래 내역 조회
- `GET /api/ledger-entries/user/{userId}` - 사용자별 거래 내역 조회
- `GET /api/ledger-entries/user/{userId}/type/{entryType}` - 사용자별 타입별 거래 조회
- `GET /api/ledger-entries/user/{userId}/category/{categoryId}` - 사용자별 카테고리별 거래 조회
- `GET /api/ledger-entries/user/{userId}/range?startDate=&endDate=` - 기간별 거래 조회
- `GET /api/ledger-entries/user/{userId}/type/{entryType}/total?startDate=&endDate=` - 타입별 합계 조회
- `POST /api/ledger-entries` - 거래 내역 생성
- `PUT /api/ledger-entries/{id}` - 거래 내역 수정
- `DELETE /api/ledger-entries/{id}` - 거래 내역 삭제

### 4. Budget API (`/api/budgets`)
- `GET /api/budgets` - 모든 예산 조회
- `GET /api/budgets/user/{userId}` - 사용자별 활성 예산 조회
- `GET /api/budgets/user/{userId}/period/{period}` - 사용자별 기간별 예산 조회
- `GET /api/budgets/user/{userId}/category/{categoryId}` - 사용자별 카테고리별 예산 조회
- `GET /api/budgets/user/{userId}/active?date=` - 특정 날짜의 활성 예산 조회
- `POST /api/budgets` - 예산 생성
- `PUT /api/budgets/{id}` - 예산 수정
- `DELETE /api/budgets/{id}` - 예산 비활성화
- `PUT /api/budgets/{id}/activate` - 예산 활성화

## 테스트 예시

### 1. 사용자 생성
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "name": "테스트 사용자"}'
```

### 2. 카테고리 생성 (수입)
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "name": "월급",
    "type": "INCOME",
    "subType": "FIXED_INCOME",
    "icon": "💰",
    "color": "#4CAF50"
  }'
```

### 3. 카테고리 생성 (지출)
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "name": "식비",
    "type": "EXPENSE",
    "subType": "VARIABLE_EXPENSE",
    "icon": "🍽️",
    "color": "#FF5722"
  }'
```

### 4. 거래 내역 생성 (수입)
```bash
curl -X POST http://localhost:8080/api/ledger-entries \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "entryType": "INCOME",
    "transactionType": "FIXED",
    "categoryId": 1,
    "amount": 3000000,
    "date": "2025-07-01",
    "description": "7월 월급",
    "memo": "정기 급여"
  }'
```

### 5. 거래 내역 생성 (지출)
```bash
curl -X POST http://localhost:8080/api/ledger-entries \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "entryType": "EXPENSE",
    "transactionType": "VARIABLE",
    "categoryId": 2,
    "amount": 15000,
    "date": "2025-07-01",
    "description": "점심 식사",
    "memo": "회사 근처 한식당"
  }'
```

### 6. 예산 생성
```bash
curl -X POST http://localhost:8080/api/budgets \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "categoryId": 2,
    "period": "MONTHLY",
    "amount": 500000,
    "startDate": "2025-07-01",
    "endDate": "2025-07-31"
  }'
```

### 7. 데이터 조회 예시
```bash
# 사용자 거래 내역 조회
curl http://localhost:8080/api/ledger-entries/user/1

# 카테고리별 합계 조회 (2025년 7월)
curl "http://localhost:8080/api/ledger-entries/user/1/category/2/total?startDate=2025-07-01&endDate=2025-07-31"

# 활성 예산 조회
curl http://localhost:8080/api/budgets/user/1/active
```

## 주요 기능

1. **유효성 검증**: 모든 API에서 데이터 유효성 검증 구현
2. **비즈니스 로직**: 예산 중복 방지, 금액 검증, 날짜 검증 등
3. **소프트 삭제**: 카테고리와 예산은 완전 삭제 대신 비활성화
4. **타입 안전성**: Enum을 통한 타입 안전성 보장
5. **날짜 범위 조회**: 기간별 거래 내역 및 집계 기능