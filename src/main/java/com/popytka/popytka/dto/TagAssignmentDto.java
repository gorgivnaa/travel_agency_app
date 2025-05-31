package com.popytka.popytka.dto;

import com.popytka.popytka.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TagAssignmentDto {

    private Tag tag;

    private boolean assigned;

    public Long getId() {
        return tag.getId();
    }
}
