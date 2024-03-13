package com.poorlex.poorlex.expenditure.service.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExpenditureImageUnusedEvent {
    private final String imageUrl;

}
