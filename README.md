# hhplus-01-tdd

## 1. TDD (Test-Driven Development)

> 테스트를 먼저 작성하고 나서 실제 코드를 작성하는 개발 방법론
> 

### 핵심 아이디어

1. **테스트 먼저 작성**한다 (실패할 수밖에 없음)
2. 테스트를 **통과시키기 위한 최소한의 코드** 작성
3. 코드 리팩터링 (구조 개선, 중복 제거 등)
4. 다시 테스트 → 순환

🌀 이것을 **Red → Green → Refactor** 사이클이라고 부른다.

### 장점

- 요구사항을 코드로 명확히 표현
- 테스트 커버리지가 자연스럽게 확보됨
- 리팩터링이 쉬움 (테스트가 안전망 역할)

## 2. Testable Code (테스트 가능한 코드)

> 테스트 작성이 쉽고, 테스트가 잘 돌아갈 수 있도록 구성된 코드
> 

### 특징

- **의존성이 적거나**, 의존성을 **주입(DI)** 받을 수 있음
- **결정적(Deterministic)** → 입력이 같으면 결과도 항상 같음
- **하드코딩 X**, 외부 시스템(DB, API)과는 느슨하게 연결
- **작은 단위로 나뉘어 있음** → 함수/메서드가 작고 명확

```jsx

예시 (좋은 Testable Code):
public int add(int a, int b) {
    return a + b;
}

반대로 아래처럼 외부 의존성이 많으면 테스트 어렵고 불안정:
public int getTotalPrice() {
    return httpClient.call("http://외부API") + db.read("price");
}

```

## 3. Test Code (테스트 코드)

> 실제로 테스트를 수행하는 JUnit, Mockito 등으로 작성된 코드
> 

### 종류

- **단위 테스트(Unit Test)**: 함수, 클래스 단위로 작은 범위 테스트
- **통합 테스트(Integration Test)**: 여러 컴포넌트를 함께 테스트
- **E2E 테스트 (End-to-End)**: 사용자 관점 전체 흐름 테스트
