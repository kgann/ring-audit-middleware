(ns ring.audit.middleware.core
  {:author "Kyle Gann"
    :doc "Basic request auditing middleware for ring based applications."}
  (:require [ring.audit.middleware.util :as util])
  (:use clout.core))

(defn wrap-audit-middleware
  [app handler & {:keys [routes future?] :or {routes [] future? false}}]
  (let [compiled-routes (map route-compile routes)]
    (fn [req]
      (let [params (some #(route-matches % req) compiled-routes)]
        (if (or (empty? routes) params) ;; params is truthy if a match is found
          (util/wrap-future future? (handler req params))))
      (app req))))