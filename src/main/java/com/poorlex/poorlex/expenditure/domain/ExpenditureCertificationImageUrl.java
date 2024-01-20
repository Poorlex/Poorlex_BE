package com.poorlex.poorlex.expenditure.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

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
            throw new IllegalArgumentException();
        }
    }

    protected void belongTo(final Expenditure expenditure) {
        if (Objects.nonNull(this.expenditure)) {
            throw new IllegalArgumentException();
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
