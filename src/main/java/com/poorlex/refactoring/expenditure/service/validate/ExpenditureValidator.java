package com.poorlex.refactoring.expenditure.service.validate;

import com.poorlex.refactoring.expenditure.domain.Expenditure;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ExpenditureValidator {

    public void imageExist(final List<MultipartFile> images) {
        if (Objects.isNull(images) || images.isEmpty()) {
            throw new ExpenditureException.ExpenditureImageNotExistException();
        }
    }

    public void isOwner(final Expenditure expenditure, final Long editorMemberId) {
        if (!expenditure.isCreatedBy(editorMemberId)) {
            throw new ExpenditureException.NoGrantForUpdateExpenditureException();
        }
    }
}
