#include "pch.h"
#include "OrientationWindows.h"

using namespace winrt::Windows::Graphics::Display;
using namespace winrt::Windows::UI::ViewManagement;
using namespace winrt::Windows::Devices::Sensors;

namespace OrientationWindows {

    void OrientationLockerModule::Initialize(React::ReactContext const& reactContext) noexcept {
        m_context = reactContext;
        m_deviceOrientationSensor = SimpleOrientationSensor::GetDefault();
        if (m_deviceOrientationSensor != nullptr)
        {
            m_deviceOrientationChangedToken = m_deviceOrientationSensor.OrientationChanged({ this, &OrientationLockerModule::OnDeviceOrientationChanged });
        }
    }

    void OrientationLockerModule::addListener(std::string) noexcept
    {
        // Keep: Required for RN built in Event Emitter Calls.
    }

    void OrientationLockerModule::removeListeners(int64_t) noexcept
    {
        // Keep: Required for RN built in Event Emitter Calls.
    }

    void OrientationLockerModule::GetOrientation(std::function<void(std::string)> cb) noexcept {
        m_context.UIDispatcher().Post([weakThis = weak_from_this(), this, cb]() {
            if (auto strongThis = weakThis.lock())
            {
                const auto currentOrientation = m_displayInfo.CurrentOrientation();
                cb(OrientationToString(currentOrientation));
                return;
            }
            cb("UNKNOWN");
        });
    }

    void OrientationLockerModule::GetDeviceOrientation(std::function<void(std::string)> cb) noexcept {
        m_context.UIDispatcher().Post([weakThis = weak_from_this(), this, cb]() {
            if (auto strongThis = weakThis.lock())
            {
                if (m_deviceOrientationSensor != nullptr)
                {
                    const auto currentDeviceOrientation = m_deviceOrientationSensor.GetCurrentOrientation();
                    cb(DeviceOrientationToString(currentDeviceOrientation));
                    return;
                }
            }
            // No Orientation Sensor found on device
            cb("UNKNOWN");
        });
    }

    void OrientationLockerModule::LockToPortrait() noexcept {
        const auto mode = m_viewSettings.UserInteractionMode();
        if (mode == UserInteractionMode::Touch) {
            if (m_displayInfo.AutoRotationPreferences() != DisplayOrientations::Portrait) {
                DisplayInformation::AutoRotationPreferences(DisplayOrientations::Portrait);
                LockDidChange("PORTRAIT");
            }
        }
    }

    void OrientationLockerModule::LockToPortraitUpsideDown() noexcept {
        const auto mode = m_viewSettings.UserInteractionMode();
        if (mode == UserInteractionMode::Touch) {
            if (m_displayInfo.AutoRotationPreferences() != DisplayOrientations::PortraitFlipped) {
                DisplayInformation::AutoRotationPreferences(DisplayOrientations::PortraitFlipped);
                LockDidChange("PORTRAIT-UPSIDEDOWN");
            }
        }
    }

    void OrientationLockerModule::LockToLandscape() noexcept {
        const auto mode = m_viewSettings.UserInteractionMode();
        if (mode == UserInteractionMode::Touch) {
            if (m_displayInfo.AutoRotationPreferences() != DisplayOrientations::Landscape) {
                DisplayInformation::AutoRotationPreferences(DisplayOrientations::Landscape);
                LockDidChange("LANDSCAPE");
            }
        }
    }

    void OrientationLockerModule::LockToLandscapeRight() noexcept {
        const auto mode = m_viewSettings.UserInteractionMode();
        if (mode == UserInteractionMode::Touch) {
            if (m_displayInfo.AutoRotationPreferences() != DisplayOrientations::LandscapeFlipped) {
                DisplayInformation::AutoRotationPreferences(DisplayOrientations::LandscapeFlipped);
                LockDidChange("LANDSCAPE-RIGHT");
            }
        }
    }

    void OrientationLockerModule::LockToLandscapeLeft() noexcept {
        const auto mode = m_viewSettings.UserInteractionMode();
        if (mode == UserInteractionMode::Touch) {
            if (m_displayInfo.AutoRotationPreferences() != DisplayOrientations::Landscape) {
                DisplayInformation::AutoRotationPreferences(DisplayOrientations::Landscape);
                LockDidChange("LANDSCAPE-LEFT");
            }
        }
    }

    void OrientationLockerModule::UnlockAllOrientations() noexcept {
        const auto mode = m_viewSettings.UserInteractionMode();
        if (mode == UserInteractionMode::Touch) {
            DisplayOrientations allOrientations = DisplayOrientations::Landscape | DisplayOrientations::LandscapeFlipped | DisplayOrientations::Portrait | DisplayOrientations::PortraitFlipped;
            if (m_displayInfo.AutoRotationPreferences() != allOrientations) {
                DisplayInformation::AutoRotationPreferences(allOrientations);
                GetOrientation(LockDidChange);
            }
        }
    }

    void OrientationLockerModule::GetConstants(React::ReactConstantProvider& provider) noexcept {
        if (!m_context.UIDispatcher().HasThreadAccess()) {
            const auto ghSetConstantsEvent = CreateEvent(
                nullptr,                       // default security attributes
                TRUE,                       // manual-reset event
                FALSE,                      // initial state is nonsignaled
                L"OrientationLockerModule_SetConstantsEvent"   // object name
            );
            if (ghSetConstantsEvent == nullptr)
            {
                assert(false);
            }
            m_context.UIDispatcher().Post([&provider, ghSetConstantsEvent, this]() {
                InitConstants();
                provider.Add(L"initialOrientation", GetInitOrientation());
                if (!SetEvent(ghSetConstantsEvent))
                {
                    assert(false);
                }
                });
            const auto dwWaitResult = WaitForSingleObject(
                ghSetConstantsEvent,
                INFINITE);                  // wait period
            switch (dwWaitResult)
            {
            case WAIT_OBJECT_0:
                break;
            default:
                assert(false);
            }
            CloseHandle(ghSetConstantsEvent);
        }
        else {
            InitConstants();
            provider.Add(L"initialOrientation", GetInitOrientation());
        }
    }

    std::string OrientationLockerModule::OrientationToString(DisplayOrientations orientation) noexcept {
        std::string result;
        if (orientation == DisplayOrientations::Landscape)
        {
            result = "LANDSCAPE";
        }
        else if (orientation == DisplayOrientations::Portrait)
        {
            result = "PORTRAIT";
        }
        else if (orientation == DisplayOrientations::LandscapeFlipped)
        {
            result = "LANDSCAPE-RIGHT";
        }
        else if (orientation == DisplayOrientations::PortraitFlipped)
        {
            result = "PORTRAIT-UPSIDEDOWN";
        }
        return result;
    }

    std::string OrientationLockerModule::DeviceOrientationToString(SimpleOrientation orientation) noexcept {
        std::string result;
        if (orientation == SimpleOrientation::Facedown)
        {
            result = "FACE-DOWN";
        }
        else if (orientation == SimpleOrientation::Faceup)
        {
            result = "FACE-UP";
        }
        else if (orientation == SimpleOrientation::NotRotated)
        {
            result = "LANDSCAPE";
        }
        else if (orientation == SimpleOrientation::Rotated90DegreesCounterclockwise)
        {
            result = "PORTRAIT-UPSIDEDOWN";
        }
        else if (orientation == SimpleOrientation::Rotated180DegreesCounterclockwise)
        {
            result = "LANDSCAPE-RIGHT";
        }
        else if (orientation == SimpleOrientation::Rotated270DegreesCounterclockwise)
        {
            result = "PORTRAIT";
        }
        return result;
    }

    void OrientationLockerModule::OnOrientationChanged(DisplayInformation const&, winrt::Windows::Foundation::IInspectable const&) noexcept {
        GetOrientation(OrientationDidChange);
    }

    void OrientationLockerModule::OnDeviceOrientationChanged(SimpleOrientationSensor const&, SimpleOrientationSensorOrientationChangedEventArgs const&) noexcept {
        GetDeviceOrientation(DeviceOrientationDidChange);
    }

    void OrientationLockerModule::InitConstants() noexcept {
        m_displayInfo = DisplayInformation::GetForCurrentView();
        m_viewSettings = UIViewSettings::GetForCurrentView();
        m_initialOrientation = OrientationToString(m_displayInfo.CurrentOrientation());
        m_orientationChangedToken = m_displayInfo.OrientationChanged({ this, &OrientationLockerModule::OnOrientationChanged });
    }

    std::string OrientationLockerModule::GetInitOrientation() noexcept {
        return m_initialOrientation;
    }
}
