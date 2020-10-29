#pragma once
#include "pch.h"

#include <functional>
#include <string>
#include <NativeModules.h>

using namespace winrt::Microsoft::ReactNative;

namespace OrientationWindows {
    
    struct OrientationConstHolder
    {

        
    };
    
    REACT_MODULE(OrientationLockerModule, L"OrientationLocker");
    struct OrientationLockerModule
    {

        winrt::Windows::Graphics::Display::DisplayInformation displayInfo{ nullptr };
        bool isLocked = false;
        winrt::event_token orientationChangedToken{};
        winrt::Windows::UI::ViewManagement::UIViewSettings viewSettings{ nullptr };
        winrt::Microsoft::ReactNative::ReactContext m_context;

        std::string initialOrientation;

        OrientationLockerModule();

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

        std::string OrientationToString(winrt::Windows::Graphics::Display::DisplayOrientations orientations) noexcept;

        //static void OnOrientationChanged(winrt::Windows::Graphics::Display::DisplayInformation const&, winrt::Windows::Foundation::IInspectable const&) noexcept;

        void SetInitOrientation() noexcept;

        std::string GetInitOrientation() noexcept;

        void GetToken() noexcept;
    };
}

