module.exports = {
  dependency: {
    platforms: {
      windows: {
        sourceDir: 'windows',
        solutionFile: 'OrientationWindows.sln',
        projects: [
          {
            projectFile: 'OrientationWindows/OrientationWindows.vcxproj',
            directDependency: true,
          }
        ],
      },
	},
  },
};
