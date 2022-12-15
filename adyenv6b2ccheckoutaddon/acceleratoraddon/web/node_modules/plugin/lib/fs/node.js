module.exports = require('fs');


module.exports.isDirectory = function(fullPath) {
	return module.exports.statSync(fullPath).isDirectory();
}
