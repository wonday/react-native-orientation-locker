#pragma once
#include "pch.h"

#include <functional>
#include <string>
#include <NativeModules.h>

using namespace winrt::Microsoft::ReactNative;

namespace OrientationWindows {

    REACT_MODULE(OrientationLockerModule, L"OrientationLocker");
    struct OrientationLockerModule
    {
        REACT_INIT(Initialize)
        void Initialize(React::ReactContext const& reactContext) noexcept;

        REACT_METHOD(GetOrientation, L"getOrientation");
        void GetOrientation(std::function<void(std::string)> cb) noexcept;

        REACT_METHOD(GetDeviceOrientation, L"getDeviceOrientation");
        void GetDeviceOrientation(std::function<void(std::string)> cb) noexcept;

        REACT_METHOD(LockToPortrait, L"lockToPortrait");
        void LockToPortrait() noexcept;

        REACT_METHOD(LockToPortraitUpsideDown, L"lockToPortraitUpsideDown");
        void LockToPortraitUpsideDown() noexcept;

        REACT_METHOD(LockToLandscape, L"lockToLandscape");
        void LockToLandscape() noexcept;

        REACT_METHOD(LockToLandscapeRight, L"lockToLandscapeRight");
        void LockToLandscapeRight() noexcept;

        REACT_METHOD(LockToLandscapeLeft, L"lockToLandscapeLeft");
        void LockToLandscapeLeft() noexcept;

        REACT_METHOD(UnlockAllOrientations, L"unlockAllOrientations");
        void UnlockAllOrientations() noexcept;

        REACT_EVENT(OrientationDidChange, L"orientationDidChange");
        std::function<void(std::string)> OrientationDidChange;

        REACT_EVENT(DeviceOrientationDidChange, L"deviceOrientationDidChange");
        std::function<void(std::string)> DeviceOrientationDidChange;

        REACT_EVENT(LockDidChange, L"lockDidChange");
        std::function<void(std::string)> LockDidChange;

        REACT_CONSTANT_PROVIDER(GetConstants)
        void GetConstants(React::ReactConstantProvider& provider) noexcept;

    private:

        static std::string OrientationToString(winrt::Windows::Graphics::Display::DisplayOrientations orientations) noexcept;

        static std::string DeviceOrientationToString(winrt::Windows::Devices::Sensors::SimpleOrientation orientation) noexcept;

        void OnOrientationChanged(winrt::Windows::Graphics::Display::DisplayInformation const&, winrt::Windows::Foundation::IInspectable const&) noexcept;

        void OnDeviceOrientationChanged(winrt::Windows::Devices::Sensors::SimpleOrientationSensor const&, winrt::Windows::Devices::Sensors::SimpleOrientationSensorOrientationChangedEventArgs const&) noexcept;

        void InitConstants() noexcept;

        std::string GetInitOrientation() noexcept;

        winrt::Windows::Graphics::Display::DisplayInformation m_displayInfo{ nullptr };

        winrt::event_token m_orientationChangedToken{};

        winrt::Windows::UI::ViewManagement::UIViewSettings m_viewSettings{ nullptr };

        winrt::Microsoft::ReactNative::ReactContext m_context;

        std::string m_initialOrientation;

        winrt::Windows::Devices::Sensors::SimpleOrientationSensor m_deviceOrientationSensor{ nullptr };

        winrt::event_token m_deviceOrientationChangedToken{};
    };
}
