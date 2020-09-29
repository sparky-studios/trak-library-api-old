package com.sparkystudios.traklibrary.notification.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "mobile_device_link")
public class MobileDeviceLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private long id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private long userId;

    @Column(name = "device_guid", nullable = false, updatable = false)
    private String deviceGuid;

    @Column(name = "linked_date", nullable = false, updatable = false)
    private LocalDateTime linkedDate;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "endpoint_arn", nullable = false, unique = true)
    private String endpointArn;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "op_lock_version")
    private Long version;
}
