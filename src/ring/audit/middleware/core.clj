(ns ring.audit.middleware.core
  {:author "Kyle Gann"
    :doc "Basic request auditing middleware for ring based applications."}
  (:require [ring.audit.middleware.util :as util]))

(defn wrap-audit-middleware
  [app audit-handler & {:keys [uri-matchers future?]
                        :or {uri-matchers nil future? false}}]
  (fn [req]
    (if (= true (:audit req))
      (util/wrap-future future? (audit-handler req))
      (if (and (not (= false (:audit req)))
               (or (not uri-matchers)
                   (and uri-matchers (some #(re-find % (:uri req)) uri-matchers))))
          (util/wrap-future future? (audit-handler req))))
    (app req)))