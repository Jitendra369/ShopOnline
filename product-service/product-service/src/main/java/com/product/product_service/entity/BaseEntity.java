package com.product.product_service.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

@Getter
@Setter
public abstract class BaseEntity implements Persistable<Long> {

    @Id
    private Long id;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Ids are assigned from a sequence before save, so Spring Data can't use a null id
    // to detect new entities. Without this, auditing never populates createdDate.
    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}
