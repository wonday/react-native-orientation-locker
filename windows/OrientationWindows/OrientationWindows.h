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

        REACT_METHOD(UnlockAllOrientations, L"UnlockAllOrientations");
        void UnlockAllOrientations() noexcept;

        REACT_CONSTANT(InitialOrientation, L"initialOrientation");
        const std::string InitialOrientation = "PORTRAIT_WINDOWS";
    };
}

