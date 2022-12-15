# Plugin.js

## Features

- ability to load entire directories
- add remote plugins (dnode)

## Example

bootstrap.js

```javascript
var plugin = require("plugin"),
express = require("express"),

plugin(express()).
params({
	http: { port: 8080 }
}).
require(__dirname + "/config.js").
require(__dirname + "/someRoutes.js").
load();
```

config.js

```javascript
exports.plugin = function(server, loader) {
	server.listen(loader.params("http.port") || 80);
}
```

someRoutes.js
```javascript
module.exports = function(server) {
    server.get("/hello", function(req, res) {
        res.end("world!");
    })
}
```


## Plugin API

### loader .plugin(plugInto, ...)

Initializes the loader for local / remote plugins. `plugInto` is passed into the first parameter when calling `.plugin()` on each required module.

### loader.use(extension)

Extends the loader. This allows you to customize how modules are loaded into your sandbox.

```javascript
require("plugin")().
use(require("plugin-dnode")).
require("dnode://localhost").
load();
```

### loader.params(keyOrParams, value)

Get / set params

bootstrap.js

```javascript
require("plugin")().
params("some.message", 8080).
require(__dirname + "/hello.js").
load();
```

server.js
```javascript
exports.plugin = function(loader) {
	console.log(loader.params("some.message"));
}
```

### loader.require(source, ...)

path to the plugins

```javascript
loader.
require(__dirname + "/plugin.js").
require(__dirname + "/someDirectory.js").
require("multiple", "plugins").
require("dnode://localhost").
load();
```

### loader.load(callback)

loads the required dependencies

### loader.module(search)

Returns one loaded module based on the search query. Note that calling this method
may load the given module if it hasn't already. Here's a real-world example:

bootstrap.js

```javascript
require("plugin")().
params("http.port", 8080).
require(__dirname + "/server.js").
require(__dirname + "/routes.js").
load();
```

server.js

```javascript
exports.isHttpServer = true; //not needed - just used for searching
exports.plugin = function(loader) {
	var server = express();
	server.listen(loader.params("http.port"));
	return server;
}
```

routes.js

```javascript
exports.plugin = function(loader) {
	var server = loader.module("server");
	server.get("/hello", function(req, res) {
		res.end("hello world!");
	})
}
```

Also note that you can search based on attributes. Here's `routes.js` again:

```javascript
exports.plugin = function(loader) {
	var server = loader.module({ isHttpServer: true });
	//do stuff here
}
```

### loader.modules(search)

Just like `loader.module(search)`, but returns multiple modules.

### loader.exports

This is all the plugins combined, and it's set once everything's loaded.

