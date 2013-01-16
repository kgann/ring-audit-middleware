(ns ring-audit-middleware.util
  {:author "Kyle Gann"
    :doc "Utilities for ring-audit-middleware"})

(defmacro wrap-future [future? & body]
  `(if future?
     (future (do ~@body))
     (do ~@body)))