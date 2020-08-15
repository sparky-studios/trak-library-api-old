package com.traklibrary.notification.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
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

    @Version
    @Column(name = "op_lock_version")
    private Long version;
}
