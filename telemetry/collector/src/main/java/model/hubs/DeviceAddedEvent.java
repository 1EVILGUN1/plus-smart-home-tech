package model.hubs;

import abstracts.HubEvent;
import enums.HubDeviceType;
import enums.HubEventType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {
    @NotBlank
    private String id;
    private HubDeviceType deviceType;
    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
