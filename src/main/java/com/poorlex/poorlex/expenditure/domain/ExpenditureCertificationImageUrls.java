package com.poorlex.poorlex.expenditure.domain;

import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenditureCertificationImageUrls {

    private static final int MAX_IMAGE_COUNT = 2;

    @OneToMany(
        mappedBy = "expenditure",
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<ExpenditureCertificationImageUrl> imageUrls;

    public ExpenditureCertificationImageUrls(final List<ExpenditureCertificationImageUrl> imageUrls) {
        if (Collections.isEmpty(imageUrls) || imageUrls.size() > MAX_IMAGE_COUNT) {
            throw new IllegalArgumentException();
        }
        this.imageUrls = imageUrls;
    }

    protected void belongTo(final Expenditure expenditure) {
        for (final ExpenditureCertificationImageUrl imageUrl : imageUrls) {
            imageUrl.belongTo(expenditure);
        }
    }

    public List<ExpenditureCertificationImageUrl> getUrls() {
        return new ArrayList<>(imageUrls);
    }
}
