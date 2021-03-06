# ring-audit-middleware

Ring middleware to facilitate audit requirements.

Makes use of [clout](https://github.com/weavejester/clout) to match routes and execute an ```fn``` of your choice!

Have it write to a db, place a message on a queue, send an email...

## Installation

    [ring-audit-middleware "0.1.0"]

## Compojure example

```clojure
(ns foo.bar
  (:use compojure.core
        ring.audit.middleware.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes foo-app
  (GET "/foo/bar/:id" ...)
  (POST "/admin/:id/edit" ...)
  (POST "/users" ...))

(defn audit-fn [req params] ... )

(def app
  (-> (handler/site foo-app)
      (wrap-audit-middleware audit-fn)))
```

#### The audit ```fn```

Must accept two arguments
  1. ring request map
  2. request parameters from clout (see clout documentation)

##### Note - the audit ```fn``` is for side-effects only. If the request map is modified, it will not be persisted through the remaining middleware stack

```clojure
(defn audit-fn [req params] ... )
```

#### Options for ```wrap-audit-middleware```

- - -
###### Audit all routes

```clojure
(def app
  (-> (handler/site foo-app)
      (wrap-audit-middleware audit-fn)))
```

- - -
###### Provide a collection of routes (see clout documentation) to determine if the request should be audited

```clojure
(def app
  (-> (handler/site foo-app) ;; audit all admin member routes and all user routes
      (wrap-audit-middleware audit-fn :routes ["/admin/:id/*" "/users/*"])))
```

- - -
###### Instruct the middleware to audit routes in a future (useful for long running audits)

```clojure
(def app
  (-> (handler/site foo-app)
      (wrap-audit-middleware audit-fn :future? true)))
```

- - -
###### Why not both?

```clojure
(def app
  (-> (handler/site foo-app)
      (wrap-audit-middleware audit-fn :routes ["/admin/:id/*"] :future? true)))
```

- - -
###### Easily create as many audit middleware pieces as you need

```clojure
(def app
  (-> (handler/site foo-app)
      (wrap-audit-middleware some-fn-for-admin-routes :routes ["/admin/:id/*"])
      (wrap-audit-middleware some-fn-for-user-routes :routes ["/user/*"] :future? true)))
```

## Todo

Right now, all audit ```fn```'s are executed for their side-effects and they can not modify the request map

  1. allow for request map modification as well as side-effects (middleware could handle authentication)

## License

Copyright (C) 2013 Kyle Gann

Distributed under the Eclipse Public License, the same as Clojure.
