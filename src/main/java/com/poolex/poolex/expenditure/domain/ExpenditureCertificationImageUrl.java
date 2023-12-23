package com.poolex.poolex.expenditure.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "url", nullable = false)
    private String value;
    @ManyToOne
    @JoinColumn(name = "expenditure_id", nullable = false)
    private Expenditure expenditure;

    public ExpenditureCertificationImageUrl(final String value, final Expenditure expenditure) {
        validate(value);
        this.value = value;
        this.expenditure = expenditure;
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
}
