#include "pch.h"
#include "OrientationWindows.h"

using namespace winrt::Windows::Graphics::Display;
using namespace winrt::Windows::UI::ViewManagement;

namespace OrientationWindows {
    /*
    static const React::ReactPropertyId<React::ReactNonAbiValue<std::shared_ptr<OrientationConstHolder>>>
        & OrientationConstHolderPropertyId() noexcept {
        static const React::ReactPropertyId<React::ReactNonAbiValue<std::shared_ptr<OrientationConstHolder>>> prop{
            L"ReactNative.OrientationLocker", L"OrientationConstHolder" };
        return prop;
    }*/

    std::string OrientationLockerModule::OrientationToString(DisplayOrientations orientations) noexcept
    {
        std::string result;
        if ((orientations & DisplayOrientations::Landscape) == DisplayOrientations::Landscape)
        {
            // Landscape | LandscapeLeft
            // Landscape 
            result = "LANDSCAPE";
        }
        if ((orientations & DisplayOrientations::Portrait) == DisplayOrientations::Portrait)
        {
            // Portrait
            // Portrait
            result = "PORTRAIT";
        }
        if ((orientations & DisplayOrientations::LandscapeFlipped) == DisplayOrientations::LandscapeFlipped)
        {
            // LandscapeRight
            // LandscapeFlipped
            result = "LANDSCAPE-RIGHT";
        }
        if ((orientations & DisplayOrientations::PortraitFlipped) == DisplayOrientations::PortraitFlipped)
        {
            // Portrait Upside-Down
            // PortraitFlipped
            result = "PORTRAIT-UPSIDEDOWN";
        }
        return result;
    }

    OrientationLockerModule::OrientationLockerModule() {
        //Initialize(reactContext);
        //GetConstants(provider);
    }

    void OrientationLockerModule::Initialize(React::ReactContext const& reactContext) noexcept {
        m_context = reactContext;
    }

    void OnOrientationChanged(winrt::Windows::Graphics::Display::DisplayInformation const& di, winrt::Windows::Foundation::IInspectable const&) noexcept {
        DisplayOrientations currentDeviceOrientation = di.CurrentOrientation();
        OrientationLockerModule m;
        std::string orientation = m.OrientationToString(currentDeviceOrientation);
        m.OrientationDidChange(orientation); // when this line runs app crashes
    }

    void OrientationLockerModule::GetOrientation(std::function<void(std::string)> cb) noexcept {
        viewSettings = UIViewSettings::GetForCurrentView();
        UserInteractionMode mode = viewSettings.UserInteractionMode();
        displayInfo = DisplayInformation::GetForCurrentView();
        DisplayOrientations currentOrientation;

        if (isLocked == true && mode == UserInteractionMode::Touch) {
            currentOrientation = displayInfo.AutoRotationPreferences();
        }
        else {
            currentOrientation = displayInfo.CurrentOrientation();
        }
        cb(OrientationToString(currentOrientation));
    }

    void OrientationLockerModule::GetDeviceOrientation(std::function<void(std::string)> cb) noexcept {
        displayInfo = DisplayInformation::GetForCurrentView();
        DisplayOrientations currentDeviceOrientation = displayInfo.CurrentOrientation();
        cb(OrientationToString(currentDeviceOrientation));
    }

    void OrientationLockerModule::LockToPortrait() noexcept {
        viewSettings = UIViewSettings::GetForCurrentView();
        UserInteractionMode mode = viewSettings.UserInteractionMode();
        if (mode == UserInteractionMode::Touch) {
            isLocked = true;
            DisplayInformation::AutoRotationPreferences(DisplayOrientations::Portrait);
            OrientationDidChange("PORTRAIT");
            LockDidChange("PORTRAIT");
        }
    }

    void OrientationLockerModule::LockToPortraitUpsideDown() noexcept {
        viewSettings = UIViewSettings::GetForCurrentView();
        UserInteractionMode mode = viewSettings.UserInteractionMode();
        if (mode == UserInteractionMode::Touch) {
            isLocked = true;
            DisplayInformation::AutoRotationPreferences(DisplayOrientations::PortraitFlipped);
            OrientationDidChange("PORTRAIT-UPSIDEDOWN");
            LockDidChange("PORTRAIT-UPSIDEDOWN");
        }
    }

    void OrientationLockerModule::LockToLandscape() noexcept {
        viewSettings = UIViewSettings::GetForCurrentView();
        UserInteractionMode mode = viewSettings.UserInteractionMode();
        if (mode == UserInteractionMode::Touch) {
            isLocked = true;
            DisplayInformation::AutoRotationPreferences(DisplayOrientations::Landscape);
            OrientationDidChange("LANDSCAPE");
            LockDidChange("LANDSCAPE");
        }
    }

    void OrientationLockerModule::LockToLandscapeRight() noexcept {
        viewSettings = UIViewSettings::GetForCurrentView();
        UserInteractionMode mode = viewSettings.UserInteractionMode();
        if (mode == UserInteractionMode::Touch) {
            isLocked = true;
            DisplayInformation::AutoRotationPreferences(DisplayOrientations::LandscapeFlipped);
            OrientationDidChange("LANDSCAPE-RIGHT");
            LockDidChange("LANDSCAPE-RIGHT");
        }
    }

    void OrientationLockerModule::LockToLandscapeLeft() noexcept {
        viewSettings = UIViewSettings::GetForCurrentView();
        UserInteractionMode mode = viewSettings.UserInteractionMode();
        if (mode == UserInteractionMode::Touch) {
            isLocked = true;
            DisplayInformation::AutoRotationPreferences(DisplayOrientations::Landscape);
            OrientationDidChange("LANDSCAPE-LEFT");
            LockDidChange("LANDSCAPE-LEFT");
        }
    }

    void OrientationLockerModule::UnlockAllOrientations() noexcept {
        viewSettings = UIViewSettings::GetForCurrentView();
        UserInteractionMode mode = viewSettings.UserInteractionMode();
        if (mode == UserInteractionMode::Touch) {
            DisplayOrientations allOrientations = DisplayOrientations::Landscape | DisplayOrientations::LandscapeFlipped | DisplayOrientations::Portrait | DisplayOrientations::PortraitFlipped;
            DisplayInformation::AutoRotationPreferences(allOrientations);
            isLocked = false;
            GetOrientation(OrientationDidChange);
            GetOrientation(LockDidChange);
        }
    }

    void OrientationLockerModule::GetConstants(React::ReactConstantProvider& provider) noexcept {
        if (!m_context.UIDispatcher().HasThreadAccess()) {
            HANDLE ghSetConstantsEvent;
            ghSetConstantsEvent = CreateEvent(
                NULL,               // default security attributes
                TRUE,               // manual-reset event
                FALSE,              // initial state is nonsignaled
                TEXT("SetConstantsEvent")  // object name
            );
            if (ghSetConstantsEvent == NULL)
            {
                printf("CreateEvent failed (%d)\n", GetLastError());
                return;
            }
            // create a event, call it "done" 
            m_context.UIDispatcher().Post([&provider, ghSetConstantsEvent, this]() {
                SetInitOrientation();
                provider.Add(L"initialOrientation",GetInitOrientation());
                GetToken();
                if (!SetEvent(ghSetConstantsEvent))
                {
                    printf("SetEvent failed (%d)\n", GetLastError());
                    return;
                }
                });
            DWORD dwWaitResult;
            dwWaitResult = WaitForSingleObject(
                ghSetConstantsEvent, // event handle
                INFINITE);    // indefinite wait
            switch (dwWaitResult)
            {
                // Event object was signaled
            case WAIT_OBJECT_0:
                break;
                // An error occurred
            default:
                printf("Wait error (%d)\n", GetLastError());
                break;
            }
            CloseHandle(ghSetConstantsEvent);
            // wait for the workitem to be executed
        }
        else {
            SetInitOrientation();
            provider.Add(L"initialOrientation", GetInitOrientation());
            GetToken();
        }
    }

    void OrientationLockerModule::SetInitOrientation() noexcept {
        initialOrientation = OrientationToString(DisplayInformation::GetForCurrentView().CurrentOrientation());
    }

    std::string OrientationLockerModule::GetInitOrientation() noexcept {
        try
        {
            return initialOrientation;
        }
        catch (winrt::hresult_error& err)
        {
            return "unknown";
        }
    }
   void OrientationLockerModule::GetToken() noexcept {
        try
        {
            orientationChangedToken = DisplayInformation::GetForCurrentView().OrientationChanged(&OnOrientationChanged);
        }
        catch (winrt::hresult_error& err)
        {
        }
    }

 }