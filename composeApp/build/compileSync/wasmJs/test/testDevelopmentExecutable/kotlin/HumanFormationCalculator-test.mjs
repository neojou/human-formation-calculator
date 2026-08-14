
import { instantiate } from './HumanFormationCalculator-test.uninstantiated.mjs';
import "./custom-formatters.js"

const exports = (await instantiate({
})).exports;

export const {
memory,
_initialize,
startUnitTests
} = exports


