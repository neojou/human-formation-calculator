// Project Pages live under /human-formation-calculator/, not the domain root.
// "auto" resolves wasm/js chunks relative to composeApp.js.
config.output = config.output || {};
config.output.publicPath = "auto";
