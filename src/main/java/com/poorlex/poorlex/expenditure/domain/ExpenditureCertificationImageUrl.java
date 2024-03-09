package com.poorlex.poorlex.expenditure.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Entity
@Table(name = "expenditure_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExpenditureCertificationImageUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "url", columnDefinition = "text", nullable = false)
    private String value;
    @ManyToOne
    @JoinColumn(name = "expenditure_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Expenditure expenditure;

    public ExpenditureCertificationImageUrl(final Long id, final String value, final Expenditure expenditure) {
        validate(value);
        this.id = id;
        this.value = value;
        this.expenditure = expenditure;
    }

    public static ExpenditureCertificationImageUrl withoutId(final String value, final Expenditure expenditure) {
        return new ExpenditureCertificationImageUrl(null, value, expenditure);
    }

    public static ExpenditureCertificationImageUrl withoutIdAndExpenditure(final String value) {
        return new ExpenditureCertificationImageUrl(null, value, null);
    }

    private void validate(final String value) {
        if (!StringUtils.hasText(value)) {
            throw new IllegalArgumentException("지출 이미지 URL 이 비어있습니다.");
        }
    }

    protected void belongTo(final Expenditure expenditure) {
        if (Objects.nonNull(this.expenditure)) {
            throw new IllegalArgumentException("지출 이미지가 추가될 지출이 존재하지 않습니다.");
        }
        this.expenditure = expenditure;
    }

    public String getValue() {
        return value;
    }

    public Expenditure getExpenditure() {
        return expenditure;
    }
}
