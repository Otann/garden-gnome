(ns garden-gnome.watch
  (:gen-class)
  (:require [garden-gnome.watcher :as watcher]))

(defn -main [& args]
  (watcher/start! (watcher/default-config)))
