package com.poorlex.poorlex.consumption.expenditure.domain;

import com.poorlex.poorlex.common.BaseCreatedAtEntity;
import com.poorlex.poorlex.exception.ApiException;
import com.poorlex.poorlex.exception.ExceptionTag;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Expenditure extends BaseCreatedAtEntity {

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
    @Column(nullable = false)
    private String mainImageUrl;
    private String subImageUrl;

    Expenditure(final Long id,
                final Long memberId,
                final ExpenditureAmount amount,
                final LocalDate date,
                final ExpenditureDescription description,
                final String mainImageUrl,
                final String subImageUrl) {
        this.id = id;
        this.memberId = memberId;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.mainImageUrl = mainImageUrl;
        this.subImageUrl = subImageUrl;
    }

    public static Expenditure withoutId(final ExpenditureAmount amount,
                                        final Long memberId,
                                        final LocalDate date,
                                        final ExpenditureDescription description,
                                        final String mainImageUrl,
                                        final String subImageUrl) {
        return new Expenditure(null, memberId, amount, date, description, mainImageUrl, subImageUrl);
    }

    public void updateMainImage(final String mainImageUrl) {
        if (Objects.isNull(mainImageUrl)) {
            throw new ApiException(ExceptionTag.EXPENDITURE_IMAGE, "메인 이미지가 반드시 존재해야 합니다.");
        }
        this.mainImageUrl = mainImageUrl;
    }

    public void updateSubImage(final String subImageUrl) {
        this.subImageUrl = subImageUrl;
    }

    public void removeSubImage() {
        this.subImageUrl = null;
    }

    public void updateAmount(final ExpenditureAmount amount) {
        this.amount = amount;
    }

    public void updateDescription(final ExpenditureDescription description) {
        this.description = description;
    }

    public boolean owned(final Long memberId) {
        return this.memberId.equals(memberId);
    }

    public int getImageCounts() {
        final int countInCaseSubImageExist = 2;
        final int countInCaseSubImageEmpty = 1;

        if (Objects.isNull(subImageUrl)) {
            return countInCaseSubImageEmpty;
        }
        return countInCaseSubImageExist;
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getAmount() {
        return amount.getValue();
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description.getValue();
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public Optional<String> getSubImageUrl() {
        return Optional.ofNullable(subImageUrl);
    }
}
