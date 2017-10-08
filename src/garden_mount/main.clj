(ns garden-mount.main
  (:gen-class)
  (:require [garden-mount.watcher :as watcher]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (watcher/compile-garden-namespaces (watcher/default-config)))
