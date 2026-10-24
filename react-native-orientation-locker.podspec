require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name           = package['name']
  s.version        = package['version']
  s.summary        = package['summary']
  s.description    = package['description']
  s.author         = package['author']['name']
  s.license        = package['license']
  s.homepage       = package['homepage']
  s.source         = { :git => 'https://github.com/wonday/react-native-orientation-locker.git', :tag => "v#{s.version}" }
  s.ios.deployment_target = '12.4'
  s.tvos.deployment_target = '12.4'
  s.preserve_paths = 'README.md', 'package.json', 'index.js'
  s.source_files   = 'iOS/**/*.{h,m,mm}'

  # install_modules_dependencies wires up the New Architecture
  # (RCT_NEW_ARCH_ENABLED, codegen spec pod RNOrientationLockerSpec and the
  # React dependencies). Fall back to the legacy dependency for older RN.
  if defined?(install_modules_dependencies())
    install_modules_dependencies(s)
  else
    s.dependency 'React-Core'
  end
end
