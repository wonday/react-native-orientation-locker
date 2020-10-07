#include "pch.h"
#include "OrientationWindows.h"

namespace OrientationWindows {
    void OrientationLockerModule::GetOrientation(std::function<void(std::string)> cb) noexcept {
        cb("HIT ORIENTATION WINDOWS");
    }

    void OrientationLockerModule::GetDeviceOrientation(std::function<void(std::string)> cb) noexcept {
        cb("HIT ORIENTATION WINDOWS");
    }

    void OrientationLockerModule::LockToPortrait() noexcept {}

    void OrientationLockerModule::LockToPortraitUpsideDown() noexcept {}

    void OrientationLockerModule::LockToLandscape() noexcept {}

    void OrientationLockerModule::LockToLandscapeRight() noexcept {}

    void OrientationLockerModule::UnlockAllOrientations() noexcept {}

 }