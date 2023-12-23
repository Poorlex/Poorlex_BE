package com.poolex.poolex.expenditure.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expenditure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long memberId;
    @Embedded
    private ExpenditureAmount amount;
    @Column(nullable = false)
    private LocalDate date;
    @Embedded
    private ExpenditureDescription description;
    @Embedded
    private ExpenditureCertificationImageUrls imageUrls;

    public Expenditure(final Long id,
                       final Long memberId,
                       final ExpenditureAmount amount,
                       final LocalDate date,
                       final ExpenditureDescription description,
                       final ExpenditureCertificationImageUrls imageUrls) {
        this.id = id;
        this.memberId = memberId;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.imageUrls = imageUrls;
    }

    public static Expenditure withoutId(final ExpenditureAmount amount,
                                        final Long memberId,
                                        final LocalDate date,
                                        final ExpenditureDescription description,
                                        final ExpenditureCertificationImageUrls imageUrls) {
        final Expenditure instance = new Expenditure(null, memberId, amount, date, description, imageUrls);
        imageUrls.belongTo(instance);
        return instance;
    }

    public Long getId() {
        return id;
    }
}
