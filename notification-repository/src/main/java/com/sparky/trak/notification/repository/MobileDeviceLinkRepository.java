package com.sparky.trak.notification.repository;

import com.sparky.trak.notification.domain.MobileDeviceLink;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface MobileDeviceLinkRepository extends CrudRepository<MobileDeviceLink, Long> {

    Collection<MobileDeviceLink> findAllByUserId(long userId);

    Optional<MobileDeviceLink> findByDeviceGuid(String deviceGuid);

    Optional<MobileDeviceLink> findByUserIdAndDeviceGuid(long userId, String deviceGuid);
}
