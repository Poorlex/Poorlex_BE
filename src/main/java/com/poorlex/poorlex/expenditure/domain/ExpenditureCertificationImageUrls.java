package com.poorlex.poorlex.expenditure.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenditureCertificationImageUrls {

    public static final int MAX_IMAGE_COUNT = 2;

    @OneToMany(
        mappedBy = "expenditure",
        cascade = CascadeType.PERSIST,
        orphanRemoval = true
    )
    private List<ExpenditureCertificationImageUrl> imageUrls;

    public ExpenditureCertificationImageUrls(final List<ExpenditureCertificationImageUrl> imageUrls) {
        if (imageUrls.size() > MAX_IMAGE_COUNT) {
            throw new IllegalArgumentException(
                String.format("지출 이미지는 최대 %d개 입니다. ( 입력 이미지 갯수 : %d )", MAX_IMAGE_COUNT, imageUrls.size())
            );
        }
        this.imageUrls = imageUrls;
    }

    public void addImageUrl(final ExpenditureCertificationImageUrl imageUrl) {
        if (imageUrls.size() >= MAX_IMAGE_COUNT) {
            throw new IllegalArgumentException(
                String.format("지출 이미지는 최대 %d개 입니다 더 이상 추가할 수 없습니다. ( 현재 이미지 갯수 : %d )", MAX_IMAGE_COUNT, imageUrls.size())
            );
        }
        imageUrls.add(imageUrl);
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
