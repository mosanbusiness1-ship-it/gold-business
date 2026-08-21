package com.mo.core.factories;

import com.mo.core.dtos.userNeedsDTO.AbstractUserNeedDto;
import com.mo.core.model.needs.AbstractUserNeed;

public interface UserNeedFactory {
    AbstractUserNeed create(AbstractUserNeedDto dto);
}
