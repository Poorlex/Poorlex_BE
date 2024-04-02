package com.poorlex.poorlex.batch.scheduler;

import com.poorlex.poorlex.consumption.expenditure.domain.Expenditure;
import com.poorlex.poorlex.consumption.expenditure.domain.ExpenditureRepository;
import com.poorlex.poorlex.user.point.domain.MemberPoint;
import com.poorlex.poorlex.user.point.domain.MemberPointRepository;
import com.poorlex.poorlex.user.point.domain.Point;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ExpenditureScheduler {

    private static final Point ZERO_EXPENDITURE_POINT = new Point(3);
    private static final Point EXPENDITURE_POINT = new Point(1);
    private static final boolean POINT_NOT_PAID_STATUS = false;

    private final ExpenditureRepository expenditureRepository;
    private final MemberPointRepository memberPointRepository;

    @Scheduled(cron = "${schedules.expenditure-pay-point.cron}")
    @Transactional
    public void payExpenditurePoint() {
        final List<Expenditure> expenditures = expenditureRepository.findAllByPointPaid(POINT_NOT_PAID_STATUS);

        expenditures.forEach(expenditure -> {
            memberPointRepository.save(getMemberPoint(expenditure));
            expenditure.payPoint();
        });
    }

    private MemberPoint getMemberPoint(final Expenditure expenditure) {
        if (expenditure.isZeroExpenditure()) {
            return MemberPoint.withoutId(ZERO_EXPENDITURE_POINT, expenditure.getMemberId());
        }
        return MemberPoint.withoutId(EXPENDITURE_POINT, expenditure.getMemberId());
    }
}
