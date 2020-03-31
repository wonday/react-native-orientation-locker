const path = require('path');
module.exports = {
  resolver: {
    extraNodeModules: new Proxy(
      {},
      {
        get: (_, name) => {
          return path.join(__dirname, `node_modules/${name}`);
        },
      },
    ),
  },
  watchFolders: [path.resolve(__dirname, '..')],
};
