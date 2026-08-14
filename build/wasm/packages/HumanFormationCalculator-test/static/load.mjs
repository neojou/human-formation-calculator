import * as exports from "../kotlin/HumanFormationCalculator-test.mjs"
try {
    const startUnitTests = "startUnitTests"
    exports[startUnitTests]?.()
    window.__karma__.loaded();
} catch (e) {
    window.__karma__.error("Problem with loading", void 0, void 0, void 0, e)
}
