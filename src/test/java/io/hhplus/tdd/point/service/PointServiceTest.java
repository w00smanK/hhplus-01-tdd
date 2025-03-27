package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    private UserPoint userPoint;

    @Mock
    private UserPointTable userPointTable;
    @Mock
    private PointHistoryTable pointHistoryTable;
    @InjectMocks
    private PointService pointService;
    @BeforeEach
    void setUp() {
        userPoint = new UserPoint(1L, 3000L, System.currentTimeMillis());
    }

    @Nested
    @DisplayName("특정 유저의 포인트 조회 기능")
    class getPointTest {
        @Test
        @DisplayName("존재하는 유저의 포인트를 조회하면 해당 포인트를 반환한다")
        void getPoint_whenUserExists() {
            given(userPointTable.selectById(1L)).willReturn(userPoint);

            UserPoint point = pointService.getPoint(1L);

            assertThat(point).isEqualTo(userPoint);
            assertThat(point.id()).isEqualTo(1L);
            assertThat(point.point()).isEqualTo(3000L);
        }

        @Test
        @DisplayName("포인트 저장 이력이 없는 회원 조회시 포인트 0원으로 조회되는지 테스트")
        void getPoint_whenUserDoesNotExist() {
            given(userPointTable.selectById(2L)).willReturn(UserPoint.empty(2L));

            UserPoint emptyPoint = pointService.getPoint(2L);

            assertThat(emptyPoint.point()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("특정 유저의 포인트 히스토리 조회 기능")
    class getHistoryTest {
        @Test
        @DisplayName("신규 Point History 조회")
        void getEmptyHistory() {
            // given
            given(pointHistoryTable.selectAllByUserId(3L))
                    .willReturn(Collections.emptyList());

            // when
            List<PointHistory> actualPointHistories = pointService.getHistory(3L);

            // then
            assertThat(actualPointHistories).isEmpty();
        }

        @Test
        @DisplayName("특정 유저의 포인트 충전/이용 내역을 조회 성공 테스트" +
                "2충전/1사용 했을 때 정상적으로 히스토리 보여지는지 확인 테스트"
        )
        void getHistory() {
            // given
            insertHistory(3L, 300L, TransactionType.CHARGE);
            insertHistory(3L, 400L, TransactionType.CHARGE);
            insertHistory(3L, 500L, TransactionType.USE);

            List<PointHistory> history = List.of(
                    new PointHistory(1, 3L, 300L, TransactionType.CHARGE, System.currentTimeMillis()),
                    new PointHistory(2, 3L, 400L, TransactionType.CHARGE, System.currentTimeMillis()),
                    new PointHistory(3, 3L, 500L, TransactionType.USE, System.currentTimeMillis())
            );
            given(pointHistoryTable.selectAllByUserId(3L)).willReturn(history);

            // when
            List<PointHistory> result = pointService.getHistory(3L);

            //then
            assertThat(result).hasSize(3);
            assertThat(result).hasSize(3);
            assertThat(result.get(0).amount()).isEqualTo(300L);
            assertThat(result.get(0).type()).isEqualTo(TransactionType.CHARGE);
            assertThat(result.get(1).amount()).isEqualTo(400L);
            assertThat(result.get(1).type()).isEqualTo(TransactionType.CHARGE);
            assertThat(result.get(2).amount()).isEqualTo(500L);
            assertThat(result.get(2).type()).isEqualTo(TransactionType.USE);
        }
    }

    @Nested
    @DisplayName("특정 유저의 포인트 충전")
    class charge{
        @Test
        @DisplayName("정상적인 포인트 충전")
        void charge() {

            //given
            given(userPointTable.selectById(1L))
                    .willReturn(userPoint);

            long chargingPoint = 5000L;
            long newPoint = userPoint.point() + chargingPoint;

            given(userPointTable.insertOrUpdate(1L, newPoint))
                    .willReturn(new UserPoint(1L, 8000L, System.currentTimeMillis()));

            //when
            UserPoint result = pointService.charge(1L, 5000L);

            //then
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.point()).isEqualTo(8000L);
        }
    }

    @Nested
    @DisplayName("특정 유저의 포인트를 사용하는 기능")
     class use {
            @Test
            @DisplayName("정상적인 포인트 사용")
            void use() {

                //given
                given(userPointTable.selectById(1L))
                        .willReturn(userPoint);

                long usingPoint = 2000L;
                long newPoint = userPoint.point() - usingPoint;

                given(userPointTable.insertOrUpdate(1L, newPoint))
                        .willReturn(new UserPoint(1L, 1000L, System.currentTimeMillis()));

                //when
                UserPoint result = pointService.use(1L, 2000L);

                //then
                assertThat(result.id()).isEqualTo(1L);
                assertThat(result.point()).isEqualTo(1000L);
            }

        }

    // insertHistory 메서드는 테스트 코드에서만 사용되는 메서드
    private void insertHistory(long userId, long amount, TransactionType transactionType) {
        pointHistoryTable.insert(userId, amount, transactionType, System.currentTimeMillis());
    }
}