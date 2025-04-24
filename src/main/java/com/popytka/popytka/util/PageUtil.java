package com.popytka.popytka.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@UtilityClass
public class PageUtil {

    public Pageable getPageable(Pageable indexedPage) {
        int pageNumber = indexedPage.getPageNumber() == 0 ? 1 : indexedPage.getPageNumber();
        return PageRequest.of(pageNumber - 1, indexedPage.getPageSize(), indexedPage.getSort());
    }
}
