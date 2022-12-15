var async = require("async");

module.exports = require("../base").extend({

	/**
	 */

	"load": function(next) {
		var self = this;
		async.map(this.source, function(src, next) {
			self._loaders.getLoader(src).load(next);
		}, next);
	}
});

module.exports.test = function(source) {
	return source instanceof Array;
}