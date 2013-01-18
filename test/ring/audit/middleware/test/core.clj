(ns ring.audit.middleware.test.core
  (:use ring.audit.middleware.core
        clojure.test
        ring.mock.request))

(def handler-called (atom false))

(def audit-fn (fn [_ _] (reset! handler-called true)))

(def mock-app (fn [_] {} ))

(defmacro ^{:private true} def-audit-test [name & body]
  `(deftest ~name
     (reset! handler-called false)
     ~@body
     (reset! handler-called false)))

(defmacro ^{:private true} was [expr]
  `(is (= true ~expr)))

(defmacro ^{:private true} was-not [expr]
  `(is (= false ~expr)))

(def-audit-test audit-handler-without-matcher
  (let [mock-req (request :get "/")
        app (wrap-audit-middleware mock-app audit-fn)]
    (app mock-req)
    (was @handler-called)))

(def-audit-test audit-handler-with-matcher
  (let [mock-req (request :get "/foo")
        app (wrap-audit-middleware mock-app audit-fn :routes ["/:id"])]
    (app mock-req)
    (was @handler-called)))

(def-audit-test audit-handler-with-matchers
  (let [mock-req (request :get "/foo")
        app (wrap-audit-middleware mock-app audit-fn :routes ["/bar/:id" "/foo"])]
    (app mock-req)
    (was @handler-called)))

(def-audit-test audit-handler-with-non-matching-matchers
  (let [mock-req (request :get "/foo")
        app (wrap-audit-middleware mock-app audit-fn :routes ["/bar/:id"])]
    (app mock-req)
    (was-not @handler-called)))