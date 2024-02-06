package com.poorlex.poorlex.expenditure.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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
    private LocalDateTime dateTime;
    @Embedded
    private ExpenditureDescription description;
    @Embedded
    private ExpenditureCertificationImageUrls imageUrls;

    protected Expenditure(final Long id,
                          @NonNull final Long memberId,
                          @NonNull final ExpenditureAmount amount,
                          @NonNull final LocalDateTime dateTime,
                          @NonNull final ExpenditureDescription description,
                          @NonNull final ExpenditureCertificationImageUrls imageUrls) {
        this.id = id;
        this.memberId = memberId;
        this.amount = amount;
        this.dateTime = dateTime;
        this.description = description;
        this.imageUrls = imageUrls;
    }

    public static Expenditure withoutId(final ExpenditureAmount amount,
                                        final Long memberId,
                                        final LocalDateTime dateTime,
                                        final ExpenditureDescription description,
                                        final ExpenditureCertificationImageUrls imageUrls) {
        final Expenditure instance = new Expenditure(null, memberId, amount, dateTime, description, imageUrls);
        imageUrls.belongTo(instance);
        return instance;
    }

    public boolean hasSameMemberId(final Long memberId) {
        return this.memberId.equals(memberId);
    }

    public void pasteAmountAndDescriptionAndImageUrls(final Expenditure other) {
        this.amount = other.amount;
        this.description = other.description;
        this.imageUrls = other.imageUrls;
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public long getAmount() {
        return amount.getValue();
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description.getValue();
    }

    public ExpenditureCertificationImageUrls getImageUrls() {
        return imageUrls;
    }
}
