# ring-audit-middleware

Ring middleware to facilitate audit requirements.

## Usage

```clojure
(ns my.app
  (:use [ring.audit.middleware.core :only [wrap-audit-middleware]]))

(defroutes main-routes
  (GET "/foo/bar/:id" ...) ;; rely on uri-matchers if provided - default is to audit route
  (POST "/admin" {:audit true :body ... })  ;; explicitly force this route to be audited with the :audit key
  (POST "/users" {:audit false :body ... })) ;; force this route to not be audited even if uri-matchers find a match

(def audit-fn (fn [req] ... )))

(def app
  (-> app (wrap-audit-middleware audit-fn)))

;; privide uri-matchers to determine if the route should be audited
(def app
  (-> app (wrap-audit-middleware audit-fn :uri-matchers [#"foo/bar/[1-9]+" #"admin/*" #"users"])))

;; instruct the middleware to execute audit-fn in a future
(def app
  (-> app (wrap-audit-middleware audit-fn :future true)))
```

## License

Copyright (C) 2013 Kyle Gann

Distributed under the Eclipse Public License, the same as Clojure.
