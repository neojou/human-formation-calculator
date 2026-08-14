
module.exports = function(config) {

config.set({
  "singleRun": true,
  "autoWatch": false,
  "basePath": "/Users/neojou/Github/human-formation-calculator/build/wasm/packages/HumanFormationCalculator-test",
  "files": [
    "/Users/neojou/.kotlin/kotlin-npm-tooling/yarn/6c95dff60f26527795591f73adc8d145/node_modules/kotlin-web-helpers/dist/kotlin-test-karma-runner.js",
    "/Users/neojou/Github/human-formation-calculator/build/wasm/packages/HumanFormationCalculator-test/static/load.mjs"
  ],
  "frameworks": [
    "webpack-output",
    "mocha",
    "webpack"
  ],
  "client": {
    "args": []
  },
  "browsers": [
    "ChromeHeadless"
  ],
  "customLaunchers": {},
  "customContextFile": "/Users/neojou/.kotlin/kotlin-npm-tooling/yarn/6c95dff60f26527795591f73adc8d145/node_modules/kotlin-web-helpers/dist/static/context.html",
  "customDebugFile": "/Users/neojou/.kotlin/kotlin-npm-tooling/yarn/6c95dff60f26527795591f73adc8d145/node_modules/kotlin-web-helpers/dist/static/debug.html",
  "failOnFailingTestSuite": false,
  "failOnEmptyTestSuite": false,
  "reporters": [
    "karma-kotlin-reporter"
  ],
  "preprocessors": {
    "/Users/neojou/.kotlin/kotlin-npm-tooling/yarn/6c95dff60f26527795591f73adc8d145/node_modules/kotlin-web-helpers/dist/kotlin-test-karma-runner.js": [
      "webpack",
      "sourcemap"
    ],
    "/Users/neojou/Github/human-formation-calculator/build/wasm/packages/HumanFormationCalculator-test/static/load.mjs": [
      "webpack",
      "sourcemap"
    ]
  },
  "proxies": {},
  "webpackCopy": []
});
config.plugins = config.plugins || [];
config.plugins.push('kotlin-web-helpers/dist/karma-kotlin-reporter.js');

config.loggers = [
    {
        type: 'kotlin-web-helpers/dist/tc-log-appender.js',
        //default layout
        layout: { type: 'pattern', pattern: '%[%d{DATETIME}:%p [%c]: %]%m' }
    }
]
config.plugins = config.plugins || [];
config.plugins.push('kotlin-web-helpers/dist/karma-webpack-output.js');

// webpack config
function createWebpackConfig() {
                    let config = {
                      mode: 'development',
                      resolve: {
                        modules: [
                          "node_modules"
                        ]
                      },
                      plugins: [],
                      module: {
                        rules: []
                      },
                      resolveLoader: {
  modules: ['node_modules', process.env['KOTLIN_TOOLING_DIR']]
}
                    };
                    
// source maps
config.module.rules.push({
        test: /\.m?js$/,
        use: ["source-map-loader"],
        enforce: "pre"
});
config.devtool = false;
config.ignoreWarnings = [
    /Failed to parse source map/,
    /Accessing import\.meta directly is unsupported \(only property access or destructuring is supported\)/
]

                // optimization
                config.optimization = config.optimization || {
  "splitChunks": false
};
// noinspection JSUnnecessarySemicolon
;(function(config) {
    const tcErrorPlugin = require('kotlin-web-helpers/dist/tc-log-error-webpack');
    config.plugins.push(new tcErrorPlugin())
    config.stats = config.stats || {}
    Object.assign(config.stats, config.stats, {
        warnings: false,
        errors: false
    })
})(config);
config.experiments = {
    topLevelAwait: true,
}
// noinspection JSUnnecessarySemicolon
;(function(config) {
    const webpack = require('webpack');

    // https://github.com/webpack/webpack/issues/12951
    const PatchSourceMapSource = require('kotlin-web-helpers/dist/webpack-5-debug');
    config.plugins.push(new PatchSourceMapSource())
    
    config.plugins.push(new webpack.SourceMapDevToolPlugin({
        moduleFilenameTemplate: "[absolute-resource-path]"
    }))
})(config);
   return config;
}

config.set({webpack: createWebpackConfig()});


}
