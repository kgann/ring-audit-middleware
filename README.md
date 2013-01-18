# ring-audit-middleware

Ring middleware to facilitate audit requirements.

Makes use of [clout](https://github.com/weavejester/clout) to match routes and execute an ```fn``` of your choice!

Have it write to a db, send a message to a queue, send an email...

## Compojure example

```clojure
(ns foo.bar
  (:use compojure.core
        [ring.audit.middleware.core :only [wrap-audit-middleware]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes foo-app
  (GET "/foo/bar/:id" ...)
  (POST "/admin/:id/edit" ...)
  (POST "/users" ...))

;; must accept a single argument, the ring request map
(def audit-fn (fn [req] ... ))
```
#### Examples

Audit all routes
- - -
```clojure
(def app
  (-> (handler/site foo-app)
      (wrap-audit-middleware audit-fn)))
```

Provide a collection of routes (see clout documentation) to determine if the request should be audited
- - -
```clojure
(def app
  (-> (handler/site foo-app) ;; audit all admin member routes and all user routes
      (wrap-audit-middleware audit-fn :routes [#"/admin/:id/*" #"/users/*"])))
```

Instruct the middleware to audit routes in a future (useful for long running audits)
- - -
```clojure
(def app
  (-> (handler/site foo-app)
      (wrap-audit-middleware audit-fn :future true)))
```

## License

Copyright (C) 2013 Kyle Gann

Distributed under the Eclipse Public License, the same as Clojure.
