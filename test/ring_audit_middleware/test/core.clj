(ns ring-audit-middleware.test.core
  (:use ring-audit-middleware.core
        clojure.test
        ring.mock.request))

(def handler-called (atom false))

(def audit-fn (fn [_] (reset! handler-called true)))

(defmacro ^{:private true} defaudittest [name & body]
  `(deftest ~name
     (reset! handler-called false)
     ~@body
     (reset! handler-called false)))

(defmacro ^{:private true} was [expr]
  `(is (= true ~expr)))

(defaudittest audit-handler-without-matcher
  (let [mock-req (request :get "/")
        audit-handler (wrap-audit-middleware mock-req audit-fn)]
    (audit-handler mock-req)
    (was @handler-called)))

(defaudittest audit-handler-with-matcher
  (let [mock-req (request :get "/foo")
        audit-handler (wrap-audit-middleware mock-req audit-fn :uri-matchers [#"foo"])]
    (audit-handler mock-req)
    (was @handler-called)))

(defaudittest audit-handler-with-matchers
  (let [mock-req (request :get "/foo")
        audit-handler (wrap-audit-middleware mock-req audit-fn :uri-matchers [#"bar" #"foo"])]
    (audit-handler mock-req)
    (was @handler-called)))

(defaudittest audit-handler-with-non-matching-matchers
  (let [mock-req (request :get "/foo")
        audit-handler (wrap-audit-middleware mock-req audit-fn :uri-matchers [#"bar" #"baz"])]
    (audit-handler mock-req)
    (is (= false @handler-called))))

(defaudittest force-audit-handler
  (let [mock-req (assoc (request :get "/foo") :audit true)
        audit-handler (wrap-audit-middleware mock-req audit-fn :uri-matchers nil)]
    (audit-handler mock-req)
    (was @handler-called)))

(defaudittest skip-audit-handler
  (let [mock-req (assoc (request :get "/foo") :audit false)
        audit-handler (wrap-audit-middleware mock-req audit-fn)]
    (audit-handler mock-req)
    (is (= false @handler-called))))