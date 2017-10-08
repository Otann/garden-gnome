(ns garden-gnome.compile
  (:gen-class)
  (:require [garden-gnome.watcher :as watcher]))

(defn -main [& args]
  (watcher/reload-and-compile! (watcher/default-config)))
