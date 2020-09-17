#pragma once

#include "Class.g.h"

namespace winrt::OrientationWindows::implementation
{
    struct Class : ClassT<Class>
    {
        Class() = default;

        int32_t MyProperty();
        void MyProperty(int32_t value);
    };
}

namespace winrt::OrientationWindows::factory_implementation
{
    struct Class : ClassT<Class, implementation::Class>
    {
    };
}
