package com.poorlex.refactoring.expenditure.service.dto.response;

import com.poorlex.refactoring.expenditure.domain.Expenditure;
import com.poorlex.refactoring.expenditure.domain.ExpenditureCertificationImageUrl;
import java.util.List;
import lombok.Getter;

@Getter
public class BattleExpenditureResponse {

    private final Long id;
    private final String imageUrl;
    private final int imageCount;
    private final boolean own;

    public BattleExpenditureResponse(final Long id,
                                     final String imageUrl,
                                     final int imageCount,
                                     final boolean own) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.imageCount = imageCount;
        this.own = own;
    }

    public static BattleExpenditureResponse from(final Expenditure expenditure, final boolean own) {
        final List<ExpenditureCertificationImageUrl> imageUrls = expenditure.getImageUrls().getUrls();

        return new BattleExpenditureResponse(
            expenditure.getId(),
            imageUrls.get(0).getValue(),
            imageUrls.size(),
            own
        );
    }
}
