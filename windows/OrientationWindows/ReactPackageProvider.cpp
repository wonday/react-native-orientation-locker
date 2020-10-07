#include "pch.h"
#include "ReactPackageProvider.h"
#include "ReactPackageProvider.g.cpp"

#include <ModuleRegistration.h>
#include "OrientationWindows.h"

// NOTE: You must include the headers of your native modules here in
// order for the AddAttributedModules call below to find them.

namespace winrt::OrientationWindows::implementation
{
    void ReactPackageProvider::CreatePackage(IReactPackageBuilder const& packageBuilder) noexcept
    {
        AddAttributedModules(packageBuilder);
    }
}