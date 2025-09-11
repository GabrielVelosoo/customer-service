package io.github.gabrielvelosoo.customerservice.application.validator.group;

import jakarta.validation.GroupSequence;

@GroupSequence({ ValidateNotBlank.class, ValidateOthers.class })
public interface ValidationOrder {
}
