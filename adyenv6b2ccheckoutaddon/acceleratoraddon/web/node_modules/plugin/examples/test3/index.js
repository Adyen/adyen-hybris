var plugin = require("../../");

plugin().
params({
	http: {
		port: 8080
	}
}).
require({
	name: "hello",
	plugin: function(loader) {
		return loader.module("world");
	}
}).
require({
	name: "world", 
	plugin: function(loader) {
		return "world";
	}
}).
require(__dirname + "/plugin").
require(__dirname + "/plugins").
load(function(err, exports) {
	console.log(exports);
});