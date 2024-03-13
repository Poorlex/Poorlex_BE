package com.poorlex.poorlex.expenditure.service.dto.response;

import com.poorlex.poorlex.expenditure.domain.Expenditure;
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
        return new BattleExpenditureResponse(
                expenditure.getId(),
                expenditure.getMainImageUrl(),
                expenditure.getImageCounts(),
                own
        );
    }
}
