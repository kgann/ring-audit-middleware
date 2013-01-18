(ns ring.audit.middleware.test.core
  (:use ring.audit.middleware.core
        clojure.test
        ring.mock.request))

(def handler-called (atom false))

(def audit-fn (fn [_] (reset! handler-called true)))

(def mock-app (fn [_] {} ))

(defmacro ^{:private true} defaudittest [name & body]
  `(deftest ~name
     (reset! handler-called false)
     ~@body
     (reset! handler-called false)))

(defmacro ^{:private true} was [expr]
  `(is (= true ~expr)))

(defaudittest audit-handler-without-matcher
  (let [mock-req (request :get "/")
        app (wrap-audit-middleware mock-app audit-fn)]
    (app mock-req)
    (was @handler-called)))

(defaudittest audit-handler-with-matcher
  (let [mock-req (request :get "/foo")
        app (wrap-audit-middleware mock-app audit-fn :routes ["/:id"])]
    (app mock-req)
    (was @handler-called)))

(defaudittest audit-handler-with-matchers
  (let [mock-req (request :get "/foo")
        app (wrap-audit-middleware mock-app audit-fn :routes ["/bar/:id" "/foo"])]
    (app mock-req)
    (was @handler-called)))

(defaudittest audit-handler-with-non-matching-matchers
  (let [mock-req (request :get "/foo")
        app (wrap-audit-middleware mock-app audit-fn :routes ["/bar/:id"])]
    (app mock-req)
    (is (= false @handler-called))))

(defaudittest force-audit-handler
  (let [mock-req (assoc (request :get "/foo") :audit true)
        app (wrap-audit-middleware mock-app audit-fn)]
    (app mock-req)
    (was @handler-called)))

(defaudittest skip-audit-handler
  (let [mock-req (assoc (request :get "/foo") :audit false)
        app (wrap-audit-middleware mock-app audit-fn)]
    (app mock-req)
    (is (= false @handler-called))))