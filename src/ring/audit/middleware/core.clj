(ns ring.audit.middleware.core
  {:author "Kyle Gann"
    :doc "Basic request auditing middleware for ring based applications."}
  (:require [ring.audit.middleware.util :as util])
  (:use clout.core))

(defn wrap-audit-middleware
  [app handler & {:keys [routes future?] :or {routes [] future? false}}]
  (let [compiled-routes (map route-compile routes)]
    (fn [req]
      (when-not (= false (:audit req)) ;; require strict boolean
        (if (or (= true (:audit req))
                (empty? routes) ;; audit all routes
                (some #(route-matches % req) compiled-routes))
          (util/wrap-future future? (handler req)))
      (app req)))))